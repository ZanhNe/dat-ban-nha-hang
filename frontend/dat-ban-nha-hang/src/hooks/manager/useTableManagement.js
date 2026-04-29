import { useState, useEffect, useCallback } from 'react';
import { managerService } from '../../services/managerService';
import { formatApiError } from '../../services/apiShape';

export const useTableManagement = () => {
    const [areas, setAreas] = useState([]);
    const [activeAreaId, setActiveAreaId] = useState(null);
    const [tables, setTables] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);
    const [error, setError] = useState('');

    // Fetch Areas
    const fetchAreas = useCallback(async () => {
        setIsLoading(true);
        setError('');
        try {
            const res = await managerService.getTableAreas();
            if (res.status === 200) {
                setAreas(res.data);
                if (res.data.length > 0 && !activeAreaId) setActiveAreaId(res.data[0].tableAreaId);
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể tải sơ đồ khu vực bàn.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    }, [activeAreaId]);

    useEffect(() => {
        const t = setTimeout(() => {
            fetchAreas();
        }, 0);
        return () => clearTimeout(t);
    }, [fetchAreas]);

    useEffect(() => {
        const fetchTables = async () => {
            if (!activeAreaId) return;
            setError('');
            try {
                const res = await managerService.getTablesByArea(activeAreaId);
                if (res.status === 200) setTables(res.data);
            } catch (error) {
                setError(formatApiError(error, 'Không thể tải danh sách bàn trong khu vực.').displayMessage);
            }
        };
        fetchTables();
    }, [activeAreaId]);

    const onSaveArea = async (payload, id = null) => {
        setIsActionLoading(true);
        setError('');
        try {
            const res = await managerService.saveTableArea(payload, id);
            if (res.status === 200 || res.status === 201) {
                fetchAreas();
                return true;
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể lưu khu vực bàn.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
        return false;
    };

    const onDeleteArea = async (id) => {
        if (!window.confirm("Xóa khu vực sẽ ảnh hưởng đến các bàn bên trong. Tiếp tục?")) return;
        setIsActionLoading(true);
        setError('');
        try {
            const res = await managerService.deleteTableArea(id);
            if (res.status === 200) {
                setActiveAreaId(null);
                fetchAreas();
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể xóa khu vực bàn.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
    };

    const onSaveTable = async (payload, tableId = null) => {
        setIsActionLoading(true);
        setError('');
        try {
            const res = await managerService.saveTable(activeAreaId, payload, tableId);
            if (res.status === 200 || res.status === 201) {
                const tableRes = await managerService.getTablesByArea(activeAreaId);
                setTables(tableRes.data);
                return true;
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể lưu bàn ăn.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
        return false;
    };

    const onDeleteTable = async (tableId) => {
        if (!window.confirm("Xóa bàn này?")) return;
        setIsActionLoading(true);
        setError('');
        try {
            const res = await managerService.deleteTable(tableId);
            if (res.status === 200) {
                setTables(prev => prev.filter(t => t.tableId !== tableId));
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể xóa bàn ăn.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
    };

    return {
        areas, activeAreaId, setActiveAreaId, tables,
        isLoading, isActionLoading, error,
        onSaveArea, onDeleteArea, onSaveTable, onDeleteTable
    };
};