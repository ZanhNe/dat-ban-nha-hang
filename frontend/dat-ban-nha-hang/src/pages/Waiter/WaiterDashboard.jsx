import React from 'react';
import { useWaiterSessions } from '../../hooks/waiter/useWaiterSessions';
import { useNavigate } from 'react-router-dom';
import PageHeader from '../../components/ui/PageHeader';
import LoadingState from '../../components/ui/LoadingState';
import EmptyState from '../../components/ui/EmptyState';
import SectionCard from '../../components/ui/SectionCard';
import PaginationBar from '../../components/ui/PaginationBar';

function WaiterDashboard() {
    const {
        activeSessions,
        mySessions,
        isLoading,
        error,
        activeMeta,
        myMeta,
        activePage,
        myPage,
        setActivePage,
        setMyPage,
        handleAssign,
        handleServeComplete
    } = useWaiterSessions();
    const navigate = useNavigate();

    if (isLoading) return <LoadingState message="Đang tải dữ liệu phiên bàn..." />;

    return (
        <div>
            <PageHeader title="Quản lý Phiên bàn" subtitle="Theo dõi và nhận phục vụ các bàn có khách" />
            {error && (
                <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700 mb-4">
                    {error}
                </div>
            )}

            <div className="grid lg:grid-cols-2 gap-4">
                <SectionCard className="p-4">
                    <h3 className="font-semibold mb-3">Bàn chờ nhận phục vụ ({activeSessions.length})</h3>
                    <div className="space-y-3">
                        {activeSessions.map(session => (
                            <div key={session.sessionId} className="ui-card p-3 flex justify-between items-center">
                                <div>
                                    <p className="font-medium">{(session.tableLabels || []).join(', ')}</p>
                                    <p className="text-sm text-gray-600">Số khách: {session.numberOfPeople}</p>
                                </div>
                                <button className="ui-btn ui-btn-primary" onClick={() => handleAssign(session.sessionId)}>
                                    Nhận Bàn
                                </button>
                            </div>
                        ))}
                        {activeSessions.length === 0 && <EmptyState message="Hiện không có khách chờ nhận bàn." />}
                    </div>
                    {activeMeta?.totalPages > 1 && (
                        <PaginationBar
                            page={activePage}
                            totalPages={activeMeta.totalPages}
                            totalItems={activeMeta.totalItems}
                            onPageChange={setActivePage}
                        />
                    )}
                </SectionCard>

                <SectionCard className="p-4">
                    <h3 className="font-semibold mb-3">Bàn bạn đang phục vụ ({mySessions.length})</h3>
                    <div className="space-y-3">
                        {mySessions.map(session => (
                            <div key={session.sessionId} className="ui-card p-3">
                                <div className="mb-2">
                                    <p className="font-medium">{(session.tableLabels || []).join(', ')}</p>
                                    <p className="text-sm text-gray-600">Số khách: {session.numberOfPeople}</p>
                                </div>
                                <div className="flex gap-2">
                                    <button className="ui-btn" onClick={() => navigate(`/waiter/pos/${session.sessionId}`)}>
                                        Ghi Món (POS)
                                    </button>
                                    <button className="ui-btn" onClick={() => navigate(`/waiter/orders/${session.sessionId}`)}>Trạng thái</button>
                                    <button className="ui-btn ui-btn-primary" onClick={() => handleServeComplete(session.sessionId)}>
                                        Hoàn tất phục vụ
                                    </button>
                                </div>
                            </div>
                        ))}
                        {mySessions.length === 0 && <EmptyState message="Bạn chưa nhận phục vụ bàn nào." />}
                    </div>
                    {myMeta?.totalPages > 1 && (
                        <PaginationBar
                            page={myPage}
                            totalPages={myMeta.totalPages}
                            totalItems={myMeta.totalItems}
                            onPageChange={setMyPage}
                        />
                    )}
                </SectionCard>
            </div>
        </div>
    );
}

export default WaiterDashboard;