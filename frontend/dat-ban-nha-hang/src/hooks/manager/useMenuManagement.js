import { useState, useEffect, useCallback } from 'react';
import { managerService } from '../../services/managerService';

export const useMenuManagement = () => {
    const [menus, setMenus] = useState([]);
    const [activeMenuId, setActiveMenuId] = useState(null);
    const [foodGroups, setFoodGroups] = useState([]);

    const [foodsByGroup, setFoodsByGroup] = useState({});

    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);

    const fetchMenus = useCallback(async () => {
        setIsLoading(true);
        const res = await managerService.getMenus();
        if (res.status === 200) {
            setMenus(res.data);
            if (res.data.length > 0 && !activeMenuId) setActiveMenuId(res.data[0].menuId);
        }
        setIsLoading(false);
    }, [activeMenuId]);

    useEffect(() => { fetchMenus(); }, [fetchMenus]);

    useEffect(() => {
        const fetchGroups = async () => {
            if (!activeMenuId) return;
            const res = await managerService.getFoodGroups(activeMenuId);
            if (res.status === 200) setFoodGroups(res.data);
        };
        fetchGroups();
    }, [activeMenuId]);

    const fetchFoodsForGroup = async (groupId) => {
        const res = await managerService.getFoods(groupId);
        if (res.status === 200) {
            setFoodsByGroup(prev => ({ ...prev, [groupId]: res.data }));
        }
    };

    const handleSave = async (type, payload, id = null, parentId = null) => {
        setIsActionLoading(true);
        let res;
        try {
            if (type === 'MENU') res = await managerService.saveMenu(payload, id);
            if (type === 'GROUP') res = await managerService.saveFoodGroup(parentId, payload, id);
            if (type === 'FOOD') res = await managerService.saveFood(parentId, payload, id);

            if (res.status === 200 || res.status === 201) {
                alert(" " + res.message);
                if (type === 'MENU') fetchMenus();
                if (type === 'GROUP') {
                    const groupRes = await managerService.getFoodGroups(activeMenuId);
                    setFoodGroups(groupRes.data);
                }
                if (type === 'FOOD') fetchFoodsForGroup(parentId);
                return true;
            }
        } catch (e) {
            alert(" Có lỗi xảy ra!");
        } finally {
            setIsActionLoading(false);
        }
        return false;
    };

    const handleDelete = async (type, id, parentId = null) => {
        if (!window.confirm("Bạn có chắc chắn muốn xóa mục này?")) return;
        setIsActionLoading(true);
        let res;
        if (type === 'MENU') res = await managerService.deleteMenu(id);
        if (type === 'GROUP') res = await managerService.deleteFoodGroup(id);
        if (type === 'FOOD') res = await managerService.deleteFood(id);

        setIsActionLoading(false);
        if (res?.status === 200) {
            if (type === 'MENU') { setActiveMenuId(null); fetchMenus(); }
            if (type === 'GROUP') { setFoodGroups(prev => prev.filter(g => g.groupId !== id)); }
            if (type === 'FOOD') { fetchFoodsForGroup(parentId); }
        }
    };

    return {
        menus, activeMenuId, setActiveMenuId,
        foodGroups, foodsByGroup, fetchFoodsForGroup,
        isLoading, isActionLoading,
        handleSave, handleDelete
    };
};