import { useState, useEffect, useCallback } from 'react';
import { managerService } from '../../services/managerService';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";

export const useStaffManagement = () => {
    const user = useAtomValue(userAtom);
    const restaurantId = user?.workplace?.restaurantId || 101;

    const [staffs, setStaffs] = useState([]);
    const [meta, setMeta] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isActionLoading, setIsActionLoading] = useState(false);

    // Filter phân trang
    const [page, setPage] = useState(0);
    const [size] = useState(10);

    const fetchStaffs = useCallback(async () => {
        if (!restaurantId) return;
        setIsLoading(true);
        try {
            const res = await managerService.getStaffs(restaurantId, page, size);
            if (res.status === 200) {
                setStaffs(res.data);
                setMeta(res.meta);
            }
        } catch (error) {
            console.error("Lỗi lấy danh sách nhân viên:", error);
        } finally {
            setIsLoading(false);
        }
    }, [restaurantId, page, size]);

    useEffect(() => {
        fetchStaffs();
    }, [fetchStaffs]);

    const handleCreate = async (payload) => {
        setIsActionLoading(true);
        const res = await managerService.createStaff(restaurantId, payload);
        setIsActionLoading(false);
        if (res.status === 201) {
            alert("" + res.message);
            fetchStaffs();
            return true;
        }
        alert(" Lỗi: " + res.message);
        return false;
    };

    const handleUpdate = async (staffId, payload) => {
        setIsActionLoading(true);
        const res = await managerService.updateStaff(staffId, payload);
        setIsActionLoading(false);
        if (res.status === 200) {
            alert(" " + res.message);
            fetchStaffs();
            return true;
        }
        alert(" Lỗi: " + res.message);
        return false;
    };

    const handleDelete = async (staffId) => {
        if (!window.confirm("CẢNH BÁO: Xóa nhân viên sẽ làm mất dữ liệu lịch sử của họ. Bạn có chắc chắn?")) return;
        setIsActionLoading(true);
        const res = await managerService.deleteStaff(staffId);
        setIsActionLoading(false);
        if (res.status === 200) {
            alert(" " + res.message);
            fetchStaffs();
        }
    };

    const handleKick = async (staffId) => {
        if (!window.confirm("Bạn muốn đuổi nhân viên này khỏi nhà hàng (Giáng cấp về Customer)? Lịch sử hoạt động sẽ được giữ lại.")) return;
        setIsActionLoading(true);
        const res = await managerService.kickStaff(staffId);
        setIsActionLoading(false);
        if (res.status === 200) {
            alert(" " + res.message);
            fetchStaffs();
        }
    };

    return {
        staffs, meta, isLoading, isActionLoading,
        page, setPage,
        handleCreate, handleUpdate, handleDelete, handleKick
    };
};