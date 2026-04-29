import { useState, useEffect, useCallback } from 'react';
import adminService from '../../services/adminService';
import { formatApiError } from '../../services/apiShape';

const normalizeUser = (user) => ({
    ...user,
    roles: Array.isArray(user?.roles) ? user.roles : [],
    primaryRoleId: user?.roles?.[0]?.id ?? null,
    primaryRoleName: user?.roles?.[0]?.name ?? 'ROLE_CUSTOMER'
});

export const useUserManagement = () => {
    const [users, setUsers] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);
    const [filters, setFilters] = useState({ page: 0, limit: 10, role: '', search: '', status: '' });
    const [meta, setMeta] = useState(null);
    const [error, setError] = useState('');

    const fetchUsers = useCallback(async () => {
        setIsLoading(true);
        setError('');
        try {
            const res = await adminService.getAllUsers(filters);
            if (res.status === 200) {
                const filteredUsers = (res.data || []).filter(user => !user.roles.some(role => role.name === 'ROLE_ADMIN'));
                setUsers(filteredUsers.map(normalizeUser));
                setMeta(res.meta);
            }
        } catch (error) {
            console.error("Lỗi lấy danh sách user:", error);
            setError(formatApiError(error, 'Không thể tải danh sách người dùng.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    }, [filters]);

    useEffect(() => {
        fetchUsers();
    }, [fetchUsers]);

    const handleSaveUser = async (payload, userId) => {
        setIsActionLoading(true);
        setError('');
        try {
            const res = await adminService.saveUser(payload, userId);
            if (res.status === 200 || res.status === 201) {
                fetchUsers();
                return { success: true, message: res.message };
            }
            return { success: false, message: res.message || 'Lưu người dùng thất bại.' };
        } catch (error) {
            const apiError = formatApiError(error, 'Không thể lưu người dùng.');
            setError(apiError.displayMessage);
            return { success: false, message: apiError.displayMessage };
        } finally {
            setIsActionLoading(false);
        }
    };

    const handleAssignWorkplace = async (userId, restaurantId, roleId) => {
        setIsActionLoading(true);
        setError('');
        try {
            const res = await adminService.assignWorkplace(userId, restaurantId, roleId);
            if (res.status === 200) {
                fetchUsers();
                return { success: true, message: res.message, data: res.data };
            }
            return { success: false, message: res.message || 'Điều chuyển workplace thất bại.' };
        } catch (error) {
            const apiError = formatApiError(error, 'Không thể điều chuyển workplace.');
            setError(apiError.displayMessage);
            return { success: false, message: apiError.displayMessage };
        } finally {
            setIsActionLoading(false);
        }
    };

    return { users, isLoading, isActionLoading, filters, setFilters, meta, error, handleSaveUser, handleAssignWorkplace };
};