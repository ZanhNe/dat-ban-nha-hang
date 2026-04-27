import { useState, useEffect, useCallback } from 'react';
import { managerService } from '../../services/managerService';

export const useTableManagement = () => {
    const [areas, setAreas] = useState([]);
    const [activeAreaId, setActiveAreaId] = useState(null);
    const [tables, setTables] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);

    // Fetch Areas
    const fetchAreas = useCallback(async () => {
        setIsLoading(true);
        const res = await managerService.getTableAreas();
        if (res.status === 200) {
            setAreas(res.data);
            if (res.data.length > 0 && !activeAreaId) setActiveAreaId(res.data[0].tableAreaId);
        }
        setIsLoading(false);
    }, [activeAreaId]);

    useEffect(() => { fetchAreas(); }, [fetchAreas]);

    // Fetch Tables when Area changes
    useEffect(() => {
        const fetchTables = async () => {
            if (!activeAreaId) return;
            const res = await managerService.getTablesByArea(activeAreaId);
            if (res.status === 200) setTables(res.data);
        };
        fetchTables();
    }, [activeAreaId]);

    const onSaveArea = async (payload, id = null) => {
        setIsActionLoading(true);
        const res = await managerService.saveTableArea(payload, id);
        if (res.status === 200 || res.status === 201) {
            fetchAreas();
            return true;
        }
        return false;
    };

    const onDeleteArea = async (id) => {
        if (!window.confirm("Xóa khu vực sẽ ảnh hưởng đến các bàn bên trong. Tiếp tục?")) return;
        const res = await managerService.deleteTableArea(id);
        if (res.status === 200) {
            setActiveAreaId(null);
            fetchAreas();
        }
    };

    const onSaveTable = async (payload, tableId = null) => {
        setIsActionLoading(true);
        const res = await managerService.saveTable(activeAreaId, payload, tableId);
        setIsActionLoading(false);
        if (res.status === 200 || res.status === 201) {
            const tableRes = await managerService.getTablesByArea(activeAreaId);
            setTables(tableRes.data);
            return true;
        }
        return false;
    };

    const onDeleteTable = async (tableId) => {
        if (!window.confirm("Xóa bàn này?")) return;
        const res = await managerService.deleteTable(tableId);
        if (res.status === 200) {
            setTables(prev => prev.filter(t => t.tableId !== tableId));
        }
    };

    return {
        areas, activeAreaId, setActiveAreaId, tables,
        isLoading, isActionLoading,
        onSaveArea, onDeleteArea, onSaveTable, onDeleteTable
    };
};