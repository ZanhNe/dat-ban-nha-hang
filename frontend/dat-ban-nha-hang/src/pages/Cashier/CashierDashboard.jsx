import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { cashierService } from '../../services/cashierService';
import './CashierDashboard.css';

function CashierDashboard() {
    const [activeTab, setActiveTab] = useState('SERVED');
    const [sessions, setSessions] = useState([]);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const fetchSessions = async () => {
        setLoading(true);
        try {
            let result;
            if (activeTab === 'SERVED') {
                result = await cashierService.getServedSessions();
            } else {
                result = await cashierService.getPayingSessions();
            }
            setSessions(result.data || []);
        } catch (err) {
            console.error('Lỗi tải dữ liệu:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSessions();
    }, [activeTab]);

    const handleCardClick = (sessionId) => {
        navigate(`/cashier/payment/${sessionId}`);
    };

    return (
        <div className="cashier-dashboard">
            <h2>Quản lý thanh toán</h2>

            <div className="cashier-tabs">
                <button
                    className={`tab-btn ${activeTab === 'SERVED' ? 'active' : ''}`}
                    onClick={() => setActiveTab('SERVED')}
                >
                    Chờ thanh toán
                </button>
                <button
                    className={`tab-btn ${activeTab === 'PAYING' ? 'active' : ''}`}
                    onClick={() => setActiveTab('PAYING')}
                >
                    Đang thanh toán
                </button>
            </div>

            {loading ? (
                <div className="loading-state">Đang tải...</div>
            ) : sessions.length === 0 ? (
                <div className="empty-state">
                    <div className="empty-icon">📋</div>
                    <p>{activeTab === 'SERVED' ? 'Không có phiên bàn nào chờ thanh toán' : 'Không có phiên bàn nào đang thanh toán'}</p>
                </div>
            ) : (
                <div className="session-grid">
                    {sessions.map(session => (
                        <div
                            key={session.sessionId}
                            className={`session-card ${session.status === 'PAYING' ? 'paying' : ''}`}
                            onClick={() => handleCardClick(session.sessionId)}
                        >
                            <div className="card-header">
                                <h3>Phiên #{session.sessionId}</h3>
                                <span className={`badge ${session.status.toLowerCase()}`}>
                                    {session.status === 'SERVED' ? 'Chờ thanh toán' : 'Đang thanh toán'}
                                </span>
                            </div>
                            <div className="card-body">
                                <p><strong>{session.customerName}</strong></p>
                                <p>Số khách: {session.numberOfPeople} người</p>
                                <div className="tables-list">
                                    {session.tableLabels?.map((label, i) => (
                                        <span key={i} className="table-tag">{label}</span>
                                    ))}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default CashierDashboard;
