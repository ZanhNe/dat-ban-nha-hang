import { useState, useEffect, useCallback } from 'react';
import { receptionistService } from '../../services/receptionistService';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";
import { formatApiError } from '../../services/apiShape';

export const useCheckIn = () => {
    const user = useAtomValue(userAtom);

    const [bookings, setBookings] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isActionLoading, setIsActionLoading] = useState(false);
    const [error, setError] = useState('');
    const [page, setPage] = useState(0);
    const [meta, setMeta] = useState(null);

    const fetchConfirmed = useCallback(async (targetPage = 0) => {
        if (!user) return;
        setIsLoading(true);
        setError('');
        try {
            const res = await receptionistService.getConfirmedBookings(targetPage, 10);
            if (res.status === 200) {
                setBookings(res.data);
                setMeta(res.meta);
                setPage(targetPage);
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể tải danh sách booking đã xác nhận.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    }, [user]);

    useEffect(() => {
        fetchConfirmed(0);
    }, [fetchConfirmed]);

    const handleCheckIn = async (bookingId) => {
        setIsActionLoading(true);
        setError('');
        try {
            const res = await receptionistService.checkInBooking(bookingId);
            if (res.status === 200) {
                setBookings(prev => prev.filter(b => b.bookingId !== bookingId));
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể check-in booking này.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
    };

    return { bookings, isLoading, isActionLoading, error, page, setPage, meta, handleCheckIn, fetchConfirmed };
};