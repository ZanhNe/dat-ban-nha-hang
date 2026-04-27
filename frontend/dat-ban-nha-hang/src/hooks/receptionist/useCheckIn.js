import { useState, useEffect, useCallback } from 'react';
import { receptionistService } from '../../services/receptionistService';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";

export const useCheckIn = () => {
    const user = useAtomValue(userAtom);
    const restaurantId = user?.workplace?.restaurantId;

    const [bookings, setBookings] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isActionLoading, setIsActionLoading] = useState(false);

    const fetchConfirmed = useCallback(async () => {
        if (!restaurantId) return;
        setIsLoading(true);
        const res = await receptionistService.getConfirmedBookings(restaurantId);
        if (res.status === 200) setBookings(res.data);
        setIsLoading(false);
    }, [restaurantId]);

    useEffect(() => {
        fetchConfirmed();
    }, [fetchConfirmed]);

    const handleCheckIn = async (bookingId) => {
        setIsActionLoading(true);
        try {
            const res = await receptionistService.checkInBooking(bookingId);
            if (res.status === 200) {
                alert("  " + res.message);
                // Xóa khỏi danh sách chờ check-in sau khi thành công
                setBookings(prev => prev.filter(b => b.bookingId !== bookingId));
            }
        } catch (error) {
            alert("Lỗi check-in!");
        } finally {
            setIsActionLoading(false);
        }
    };

    return { bookings, isLoading, isActionLoading, handleCheckIn, fetchConfirmed };
};