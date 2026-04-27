import React from 'react';
import { useWaiterSessions } from '../../hooks/waiter/useWaiterSessions';
import { useNavigate } from 'react-router-dom';
import './WaiterDashboard.css';

function WaiterDashboard() {
    const { activeSessions, mySessions, isLoading, handleAssign } = useWaiterSessions();
    const navigate = useNavigate();

    if (isLoading) return <div className="waiter-loading">Đang tải dữ liệu phiên bàn...</div>;

    return (
        <div className="waiter-dashboard">
            <header className="page-header">
                <h2>Quản lý Phiên bàn</h2>
                <p>Theo dõi và nhận phục vụ các bàn có khách</p>
            </header>

            <div className="dashboard-2cols">
                {/* CỘT 1: BÀN CHỜ NHẬN (ACTIVE) */}
                <div className="session-column">
                    <div className="column-header">
                        <h3>Bàn chờ nhận phục vụ ({activeSessions.length})</h3>
                        <span className="pulse-indicator"></span>
                    </div>
                    <div className="session-grid">
                        {activeSessions.map(session => (
                            <div key={session.sessionId} className="session-card new-session">
                                <div className="card-top">
                                    <h4>{session.tableNames}</h4>
                                    <span className="guest-badge">👥 {session.guestCount}</span>
                                </div>
                                <div className="card-bottom">
                                    <button className="btn-assign" onClick={() => handleAssign(session.sessionId)}>
                                        Nhận Bàn
                                    </button>
                                </div>
                            </div>
                        ))}
                        {activeSessions.length === 0 && (
                            <div className="empty-box">Hiện không có khách chờ nhận bàn.</div>
                        )}
                    </div>
                </div>

                {/* CỘT 2: BÀN ĐANG PHỤC VỤ CỦA MÌNH (SERVING) */}
                <div className="session-column">
                    <div className="column-header">
                        <h3>Bàn bạn đang phục vụ ({mySessions.length})</h3>
                    </div>
                    <div className="session-grid">
                        {mySessions.map(session => (
                            <div key={session.sessionId} className="session-card my-session">
                                <div className="card-top">
                                    <h4>{session.tableNames}</h4>
                                    <span className="guest-badge">👥 {session.guestCount}</span>
                                </div>
                                <div className="card-bottom card-actions-flex">
                                    <button className="btn-pos" onClick={() => navigate(`/waiter/pos/${session.sessionId}`)}>
                                        Ghi Món (POS)


                                    </button>
                                    <button className="btn-status" onClick={() => navigate(`/waiter/orders/${session.sessionId}`)}> Trạng thái</button>
                                </div>
                            </div>
                        ))}
                        {mySessions.length === 0 && (
                            <div className="empty-box">Bạn chưa nhận phục vụ bàn nào.</div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default WaiterDashboard;