import React, { useState, useEffect } from 'react';
import { notificationService } from '../../services/notificationService';
import './NotificationBell.css';

function NotificationBell() {
    const [open, setOpen] = useState(false);
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);

    const fetchData = async () => {
        try {
            const [countRes, listRes] = await Promise.all([
                notificationService.getUnreadCount(),
                notificationService.getNotifications()
            ]);
            setUnreadCount(countRes.data?.unreadCount || 0);
            setNotifications(listRes.data || []);
        } catch (err) {
            console.error('Lỗi tải thông báo:', err);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleToggle = () => {
        setOpen(prev => !prev);
    };

    const handleMarkAsRead = async (id) => {
        try {
            await notificationService.markAsRead(id);
            setNotifications(prev =>
                prev.map(n => n.notificationId === id ? { ...n, isRead: true } : n)
            );
            setUnreadCount(prev => Math.max(0, prev - 1));
        } catch (err) {
            console.error('Lỗi đánh dấu:', err);
        }
    };

    const handleMarkAllAsRead = async () => {
        try {
            await notificationService.markAllAsRead();
            setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
            setUnreadCount(0);
        } catch (err) {
            console.error('Lỗi đánh dấu tất cả:', err);
        }
    };

    const timeAgo = (dateStr) => {
        const now = new Date();
        const d = new Date(dateStr);
        const diff = Math.floor((now - d) / 60000);
        if (diff < 1) return 'Vừa xong';
        if (diff < 60) return `${diff} phút trước`;
        if (diff < 1440) return `${Math.floor(diff / 60)} giờ trước`;
        return `${Math.floor(diff / 1440)} ngày trước`;
    };

    return (
        <div className="notification-wrapper">
            <button className="bell-btn" onClick={handleToggle}>
                🔔
                {unreadCount > 0 && <span className="bell-badge">{unreadCount}</span>}
            </button>

            {open && (
                <>
                    <div className="notification-overlay" onClick={() => setOpen(false)} />
                    <div className="notification-dropdown">
                        <div className="dropdown-header">
                            <h4>Thông báo</h4>
                            {unreadCount > 0 && (
                                <button className="btn-mark-all" onClick={handleMarkAllAsRead}>
                                    Đọc tất cả
                                </button>
                            )}
                        </div>
                        <div className="notification-list">
                            {notifications.length === 0 ? (
                                <div className="empty-notifications">Không có thông báo</div>
                            ) : (
                                notifications.map(n => (
                                    <div
                                        key={n.notificationId}
                                        className={`notification-item ${!n.isRead ? 'unread' : ''}`}
                                        onClick={() => !n.isRead && handleMarkAsRead(n.notificationId)}
                                    >
                                        <div className="notif-title">{n.title}</div>
                                        <div className="notif-msg">{n.message}</div>
                                        <div className="notif-time">{timeAgo(n.createdAt)}</div>
                                    </div>
                                ))
                            )}
                        </div>
                    </div>
                </>
            )}
        </div>
    );
}

export default NotificationBell;
