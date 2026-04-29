import React, { useCallback, useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { cashierService } from '../../services/cashierService';
import { formatApiError, unwrapData, unwrapMeta } from '../../services/apiShape';
import PageHeader from '../../components/ui/PageHeader';
import LoadingState from '../../components/ui/LoadingState';
import EmptyState from '../../components/ui/EmptyState';
import SectionCard from '../../components/ui/SectionCard';
import PaginationBar from '../../components/ui/PaginationBar';

function CashierDashboard() {
    const location = useLocation();
    const [activeTab, setActiveTab] = useState(location.pathname.includes('/cashier/paying') ? 'PAYING' : 'SERVED');
    const [sessions, setSessions] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [processingSessionId, setProcessingSessionId] = useState(null);
    const [meta, setMeta] = useState({ page: 0, limit: 10, totalItems: 0, totalPages: 1 });
    const navigate = useNavigate();

    const fetchSessions = useCallback(async (targetPage = meta.page) => {
        setLoading(true);
        setError('');
        try {
            let result;
            if (activeTab === 'SERVED') {
                result = await cashierService.getServedSessions({ page: targetPage, limit: meta.limit, status: 'SERVED' });
            } else {
                result = await cashierService.getPayingSessions({ page: targetPage, limit: meta.limit });
            }
            setSessions(unwrapData(result) || []);
            setMeta((prev) => unwrapMeta(result) || prev);
        } catch (err) {
            setError(formatApiError(err, 'Không thể tải danh sách phiên bàn.').displayMessage);
        } finally {
            setLoading(false);
        }
    }, [activeTab, meta.limit, meta.page]);

    useEffect(() => {
        setActiveTab(location.pathname.includes('/cashier/paying') ? 'PAYING' : 'SERVED');
        setMeta((prev) => ({ ...prev, page: 0 }));
    }, [location.pathname]);

    useEffect(() => {
        fetchSessions();
    }, [fetchSessions]);

    const handleCardClick = (sessionId) => {
        navigate(`/cashier/payment/${sessionId}`);
    };

    const handleInitiatePayment = async (sessionId) => {
        setProcessingSessionId(sessionId);
        setError('');
        try {
            await cashierService.initiatePayment(sessionId);
            if (activeTab === 'SERVED') {
                navigate('/cashier/paying');
            }
            await fetchSessions();
        } catch (err) {
            setError(formatApiError(err, 'Không thể khởi tạo thanh toán cho phiên bàn này.').displayMessage);
        } finally {
            setProcessingSessionId(null);
        }
    };

    return (
        <div>
            <PageHeader title="Quản lý thanh toán" />
            {error && (
                <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700 mb-4">
                    {error}
                </div>
            )}

            <div className="flex gap-2 mb-4">
                <button
                    className={`ui-btn ${activeTab === 'SERVED' ? 'ui-btn-primary' : ''}`}
                    onClick={() => navigate('/cashier')}
                >
                    Chờ thanh toán
                </button>
                <button
                    className={`ui-btn ${activeTab === 'PAYING' ? 'ui-btn-primary' : ''}`}
                    onClick={() => navigate('/cashier/paying')}
                >
                    Đang thanh toán
                </button>
            </div>

            {loading ? (
                <LoadingState message="Đang tải danh sách phiên bàn..." />
            ) : sessions.length === 0 ? (
                <EmptyState message={activeTab === 'SERVED' ? 'Không có phiên bàn nào chờ thanh toán' : 'Không có phiên bàn nào đang thanh toán'} />
            ) : (
                <div className="grid md:grid-cols-2 gap-4">
                    {sessions.map(session => (
                        <SectionCard
                            key={session.sessionId}
                            className="p-4 cursor-pointer hover:border-blue-200"
                            onClick={() => handleCardClick(session.sessionId)}
                        >
                            <div className="flex justify-between items-center mb-2">
                                <h3 className="font-semibold">Phiên #{session.sessionId}</h3>
                                <span className="text-xs px-2 py-1 rounded border border-gray-200 text-gray-600">
                                    {session.status === 'SERVED' ? 'Chờ thanh toán' : 'Đang thanh toán'}
                                </span>
                            </div>
                            <div className="text-sm text-gray-700 space-y-1">
                                <p className="font-medium">{session.customerName}</p>
                                <p>Số khách: {session.numberOfPeople} người</p>
                                <div className="flex gap-1 flex-wrap">
                                    {session.tableLabels?.map((label, i) => (
                                        <span key={i} className="px-2 py-1 text-xs bg-gray-100 rounded">{label}</span>
                                    ))}
                                </div>
                                <div className="mt-3 flex gap-2">
                                    <button
                                        className="ui-btn"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            handleCardClick(session.sessionId);
                                        }}
                                    >
                                        Xem chi tiết
                                    </button>
                                    {activeTab === 'SERVED' && (
                                        <button
                                            className="ui-btn ui-btn-primary"
                                            disabled={processingSessionId === session.sessionId}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleInitiatePayment(session.sessionId);
                                            }}
                                        >
                                            {processingSessionId === session.sessionId ? 'Đang khởi tạo...' : 'Khởi tạo thanh toán'}
                                        </button>
                                    )}
                                </div>
                            </div>
                        </SectionCard>
                    ))}
                </div>
            )}
            {meta.totalPages > 1 && (
                <PaginationBar
                    page={meta.page}
                    totalPages={meta.totalPages}
                    totalItems={meta.totalItems}
                    onPageChange={fetchSessions}
                    disabled={loading}
                />
            )}
        </div>
    );
}

export default CashierDashboard;
