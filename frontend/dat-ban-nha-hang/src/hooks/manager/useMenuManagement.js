import { useState, useEffect, useCallback } from 'react';
import { managerService } from '../../services/managerService';
import { formatApiError } from '../../services/apiShape';

export const useMenuManagement = () => {
    const [menus, setMenus] = useState([]);
    const [activeMenuId, setActiveMenuId] = useState(null);
    const [foodGroups, setFoodGroups] = useState([]);

    const [foodsByGroup, setFoodsByGroup] = useState({});

    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);
    const [error, setError] = useState('');

    const fetchMenus = useCallback(async () => {
        setIsLoading(true);
        setError('');
        try {
            const res = await managerService.getMenus();
            if (res.status === 200) {
                setMenus(res.data);
                if (res.data.length > 0 && !activeMenuId) setActiveMenuId(res.data[0].menuId);
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể tải danh sách menu.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    }, [activeMenuId]);

    useEffect(() => { fetchMenus(); }, [fetchMenus]);

    useEffect(() => {
        const fetchGroups = async () => {
            if (!activeMenuId) return;
            setError('');
            try {
                const res = await managerService.getFoodGroups(activeMenuId);
                if (res.status === 200) setFoodGroups(res.data);
            } catch (error) {
                setError(formatApiError(error, 'Không thể tải nhóm món ăn.').displayMessage);
            }
        };
        fetchGroups();
    }, [activeMenuId]);

    const fetchFoodsForGroup = async (groupId) => {
        setError('');
        try {
            const res = await managerService.getFoods(groupId);
            if (res.status === 200) {
                setFoodsByGroup(prev => ({ ...prev, [groupId]: res.data }));
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể tải danh sách món ăn.').displayMessage);
        }
    };

    const handleSave = async (type, payload, id = null, parentId = null) => {
        setIsActionLoading(true);
        setError('');
        let res;
        try {
            if (type === 'MENU') res = await managerService.saveMenu(payload, id);
            if (type === 'GROUP') res = await managerService.saveFoodGroup(parentId, payload, id);
            if (type === 'FOOD') res = await managerService.saveFood(parentId, payload, id);

            if (res.status === 200 || res.status === 201) {
                if (type === 'MENU') fetchMenus();
                if (type === 'GROUP') {
                    const groupRes = await managerService.getFoodGroups(activeMenuId);
                    setFoodGroups(groupRes.data);
                }
                if (type === 'FOOD') fetchFoodsForGroup(parentId);
                return true;
            }
        } catch (err) {
            setError(formatApiError(err, 'Không thể lưu dữ liệu menu.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
        return false;
    };

    const handleDelete = async (type, id, parentId = null) => {
        if (!window.confirm("Bạn có chắc chắn muốn xóa mục này?")) return;
        setIsActionLoading(true);
        setError('');
        try {
            let res;
            if (type === 'MENU') res = await managerService.deleteMenu(id);
            if (type === 'GROUP') res = await managerService.deleteFoodGroup(id);
            if (type === 'FOOD') res = await managerService.deleteFood(id);

            if (res?.status === 200) {
                if (type === 'MENU') { setActiveMenuId(null); fetchMenus(); }
                if (type === 'GROUP') { setFoodGroups(prev => prev.filter(g => g.groupId !== id)); }
                if (type === 'FOOD') { fetchFoodsForGroup(parentId); }
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể xóa dữ liệu menu.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
    };

    return {
        menus, activeMenuId, setActiveMenuId,
        foodGroups, foodsByGroup, fetchFoodsForGroup,
        isLoading, isActionLoading, error,
        handleSave, handleDelete
    };
};