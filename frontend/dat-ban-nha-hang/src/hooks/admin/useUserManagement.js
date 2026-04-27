import { useState, useEffect, useCallback } from 'react';
import adminService from '../../services/adminService';

export const useUserManagement = () => {
    const [users, setUsers] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);
    const [filters, setFilters] = useState({ page: 0, limit: 10, role: '', search: '', status: '' });
    const [meta, setMeta] = useState(null);

    const fetchUsers = useCallback(async () => {
        setIsLoading(true);
        try {
            const res = await adminService.getAllUsers(filters);
            if (res.status === 200) {
                setUsers(res.data);
                setMeta(res.meta);
            }
        } catch (error) {
            console.error("Lỗi lấy danh sách user:", error);
        } finally {
            setIsLoading(false);
        }
    }, [filters]);

    useEffect(() => {
        fetchUsers();
    }, [fetchUsers]);

    const handleSaveUser = async (payload, userId) => {
        setIsActionLoading(true);
        const res = await adminService.saveUser(payload, userId);
        setIsActionLoading(false);
        if (res.status === 200 || res.status === 201) {
            alert(res.message);
            fetchUsers();
            return true;
        }
        alert(res.message);
        return false;
    };

    const handleAssignWorkplace = async (userId, restaurantId, roleId) => {
        setIsActionLoading(true);
        const res = await adminService.assignWorkplace(userId, restaurantId, roleId);
        setIsActionLoading(false);
        if (res.status === 200) {
            alert(res.message);
            fetchUsers();
            return true;
        }
        return false;
    };

    return { users, isLoading, isActionLoading, filters, setFilters, meta, handleSaveUser, handleAssignWorkplace };
};