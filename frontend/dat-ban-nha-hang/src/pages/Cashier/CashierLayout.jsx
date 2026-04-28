import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAtomValue, useSetAtom } from "jotai";
import { userAtom } from "../../store/authStore";
import NotificationBell from "../../components/Notification/NotificationBell";
import './CashierLayout.css';

function CashierLayout() {
    const navigate = useNavigate();
    const user = useAtomValue(userAtom);
    const setUser = useSetAtom(userAtom);

    const handleLogout = () => {
        localStorage.clear();
        setUser(null);
        navigate('/login');
    };

    return (
        <div className="cashier-layout">
            <aside className="cashier-sidebar">
                <div className="sidebar-brand">
                    <span className="icon">💰</span>
                    <h2>CASHIER</h2>
                </div>
                <nav className="cashier-nav">
                    <NavLink to="/cashier" end className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Chờ thanh toán
                    </NavLink>
                    <NavLink to="/cashier/paying" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Đang thanh toán
                    </NavLink>
                </nav>
            </aside>

            <main className="cashier-main">
                <header className="cashier-header">
                    <div className="header-info">
                        <h3>{user?.workplace?.name || "Nhà hàng hiện tại"}</h3>
                    </div>
                    <div className="header-user">
                        <NotificationBell />
                        <span>Thu ngân: <strong>{user?.fullName}</strong></span>
                        <button onClick={handleLogout} className="btn-logout-outline">Đăng xuất</button>
                    </div>
                </header>
                <section className="cashier-content">
                    <Outlet />
                </section>
            </main>
        </div>
    );
}

export default CashierLayout;
