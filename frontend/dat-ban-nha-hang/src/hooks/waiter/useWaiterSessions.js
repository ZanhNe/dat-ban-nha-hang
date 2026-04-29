import { useState, useEffect, useCallback } from 'react';
import { waiterService } from '../../services/waiterService';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";
import { formatApiError, unwrapData, unwrapMeta } from '../../services/apiShape';

export const useWaiterSessions = () => {
    const user = useAtomValue(userAtom);

    const [activeSessions, setActiveSessions] = useState([]); // Bàn đang chờ nhận
    const [mySessions, setMySessions] = useState([]); // Bàn mình đang phục vụ
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');

    const [activePage, setActivePage] = useState(0);
    const [myPage, setMyPage] = useState(0);
    const [activeLimit, setActiveLimit] = useState(10);
    const [myLimit, setMyLimit] = useState(10);
    const [activeMeta, setActiveMeta] = useState({ page: 0, limit: 10, totalItems: 0, totalPages: 1 });
    const [myMeta, setMyMeta] = useState({ page: 0, limit: 10, totalItems: 0, totalPages: 1 });

    const fetchSessions = useCallback(async () => {
        if (!user) return;
        setIsLoading(true);
        setError('');
        try {
            const [activeRes, myRes] = await Promise.all([
                waiterService.getSessions({ status: 'ACTIVE', unassigned: true, page: activePage, limit: activeLimit }),
                waiterService.getMySessions(myPage, myLimit)
            ]);
            if (activeRes.status === 200) {
                setActiveSessions(unwrapData(activeRes) || []);
                setActiveMeta((prev) => unwrapMeta(activeRes) || prev);
            }
            if (myRes.status === 200) {
                setMySessions(unwrapData(myRes) || []);
                setMyMeta((prev) => unwrapMeta(myRes) || prev);
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể tải danh sách phiên bàn.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    }, [user, activePage, myPage, activeLimit, myLimit]);

    useEffect(() => { fetchSessions(); }, [fetchSessions]);

    const handleAssign = async (sessionId) => {
        try {
            setError('');
            const res = await waiterService.assignSession(sessionId);
            if (res.status === 200) {
                fetchSessions(); // Load lại danh sách sau khi nhận bàn
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể nhận bàn.').displayMessage);
        }
    };

    const handleServeComplete = async (sessionId) => {
        try {
            setError('');
            const res = await waiterService.serveComplete(sessionId);
            if (res.status === 200) {
                fetchSessions();
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể hoàn tất phiên phục vụ.').displayMessage);
        }
    };

    return {
        activeSessions,
        mySessions,
        isLoading,
        error,
        activeMeta,
        myMeta,
        activePage,
        myPage,
        setActivePage,
        setMyPage,
        activeLimit,
        myLimit,
        setActiveLimit,
        setMyLimit,
        handleAssign,
        handleServeComplete,
        fetchSessions
    };
};