// src/layouts/ReceptionistLayout.jsx
import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAtomValue, useSetAtom } from "jotai";
import { userAtom } from "../../store/authStore";
import NotificationBell from "../../components/Notification/NotificationBell";
import './ReceptionistLayout.css';

function ReceptionistLayout() {
    const navigate = useNavigate();
    const user = useAtomValue(userAtom);
    const setUser = useSetAtom(userAtom);

    const handleLogout = () => {
        localStorage.clear();
        setUser(null);
        navigate('/login');
    };

    return (
        <div className="reception-layout">
            <aside className="reception-sidebar">
                <div className="sidebar-brand">
                    <span className="icon">🛎️</span>
                    <h2>RECEPTION</h2>
                </div>
                <nav className="reception-nav">
                    <NavLink to="/receptionist" end className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Yêu cầu mới
                    </NavLink>
                    <NavLink to="/receptionist/check-in" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Đón khách (Check-in)
                    </NavLink>
                    <NavLink to="/receptionist/table-map" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Sơ đồ bàn Live
                    </NavLink>
                </nav>
            </aside>

            <main className="reception-main">
                <header className="reception-header">
                    <div className="header-info">
                        <h3>{user?.workplace?.name || "Nhà hàng hiện tại"}</h3>
                    </div>
                    <div className="header-user">
                        <NotificationBell />
                        <span>Nhân viên: <strong>{user?.fullName}</strong></span>
                        <button onClick={handleLogout} className="btn-logout-minimal">Đăng xuất</button>
                    </div>
                </header>
                <section className="reception-content">
                    <Outlet />
                </section>
            </main>
        </div>
    );
}

export default ReceptionistLayout;