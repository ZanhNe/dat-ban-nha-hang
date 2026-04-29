import { useState, useEffect, useCallback } from 'react';
import { managerService } from '../../services/managerService';
import { formatApiError } from '../../services/apiShape';


export const useStaffManagement = () => {
    const [staffs, setStaffs] = useState([]);
    const [meta, setMeta] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isActionLoading, setIsActionLoading] = useState(false);
    const [error, setError] = useState('');

    // Filter phân trang
    const [page, setPage] = useState(0);
    const [limit] = useState(10);

    const fetchStaffs = useCallback(async () => {
        setIsLoading(true);
        setError('');
        try {
            const res = await managerService.getStaffs(page, limit);
            if (res.status === 200) {
                console.log("res", res);
                const filteredStaffs = (res.data || []).filter(staff => !staff.roles.some(role => role.name === 'ROLE_ADMIN' || role.name === 'ROLE_MANAGER'));
                setStaffs(filteredStaffs);
                setMeta(res.meta);
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể tải danh sách nhân viên.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    }, [page, limit]);

    useEffect(() => {
        fetchStaffs();
    }, [fetchStaffs]);

    const handleCreate = async (payload) => {
        setIsActionLoading(true);
        setError('');
        try {
            const res = await managerService.createStaff(payload);
            if (res.status === 201) {
                fetchStaffs();
                return true;
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể tạo nhân viên mới.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
        return false;
    };

    const handleUpdate = async (staffId, payload) => {
        setIsActionLoading(true);
        setError('');
        try {
            const res = await managerService.updateStaff(staffId, payload);
            if (res.status === 200) {
                fetchStaffs();
                return true;
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể cập nhật nhân viên.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
        return false;
    };

    const handleDelete = async (staffId) => {
        if (!window.confirm("CẢNH BÁO: Xóa nhân viên sẽ làm mất dữ liệu lịch sử của họ. Bạn có chắc chắn?")) return;
        setIsActionLoading(true);
        setError('');
        try {
            const res = await managerService.deleteStaff(staffId);
            if (res.status === 200) {
                fetchStaffs();
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể xóa nhân viên.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
    };

    const handleKick = async (staffId) => {
        if (!window.confirm("Bạn muốn đuổi nhân viên này khỏi nhà hàng (Giáng cấp về Customer)? Lịch sử hoạt động sẽ được giữ lại.")) return;
        setIsActionLoading(true);
        setError('');
        try {
            const res = await managerService.kickStaff(staffId);
            if (res.status === 200) {
                fetchStaffs();
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể gỡ nhân viên khỏi nhà hàng.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
    };

    return {
        staffs, meta, isLoading, isActionLoading, error,
        page, setPage,
        handleCreate, handleUpdate, handleDelete, handleKick
    };
};