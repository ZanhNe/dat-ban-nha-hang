import { useState, useEffect, useCallback } from 'react';
import { managerService } from '../../services/managerService';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";
import { formatApiError } from '../../services/apiShape';

export const useRestaurantInfo = () => {
    const user = useAtomValue(userAtom);

    const [info, setInfo] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState('');

    // GET: Fetch thông tin
    const fetchInfo = useCallback(async () => {
        setIsLoading(true);
        setError('');
        try {
            const res = await managerService.getRestaurantInfo();
            if (res.status === 200) {
                setInfo(res.data);
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể tải thông tin nhà hàng.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        if (user) fetchInfo();
    }, [fetchInfo, user]);

    // PUT: Cập nhật
    const updateInfo = async (formData) => {
        setIsSaving(true);
        setError('');
        try {
            const res = await managerService.updateRestaurantInfo(formData);
            if (res.status === 200) {
                setInfo(res.data);
                return true;
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể cập nhật thông tin nhà hàng.').displayMessage);
        } finally {
            setIsSaving(false);
        }
        return false;
    };

    return { info, isLoading, isSaving, error, updateInfo };
};