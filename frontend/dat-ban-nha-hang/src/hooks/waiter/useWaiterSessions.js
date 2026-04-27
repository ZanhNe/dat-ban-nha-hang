import { useState, useEffect, useCallback } from 'react';
import { waiterService } from '../../services/waiterService';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";

export const useWaiterSessions = () => {
    const user = useAtomValue(userAtom);
    const restaurantId = user?.workplace?.restaurantId || 102;

    const [activeSessions, setActiveSessions] = useState([]); // Bàn đang chờ nhận
    const [mySessions, setMySessions] = useState([]); // Bàn mình đang phục vụ
    const [isLoading, setIsLoading] = useState(true);

    const fetchSessions = useCallback(async () => {
        if (!restaurantId) return;
        setIsLoading(true);
        try {
            const [activeRes, myRes] = await Promise.all([
                waiterService.getSessions(restaurantId, 'ACTIVE'),
                waiterService.getSessions(restaurantId, 'SERVING')
            ]);
            if (activeRes.status === 200) setActiveSessions(activeRes.data);
            if (myRes.status === 200) setMySessions(myRes.data);
        } catch (error) {
            console.error("Lỗi fetch sessions:", error);
        } finally {
            setIsLoading(false);
        }
    }, [restaurantId]);

    useEffect(() => { fetchSessions(); }, [fetchSessions]);

    const handleAssign = async (sessionId) => {
        const res = await waiterService.assignSession(sessionId);
        if (res.status === 200) {
            fetchSessions(); // Load lại danh sách sau khi nhận bàn
        }
    };

    return { activeSessions, mySessions, isLoading, handleAssign, fetchSessions };
};