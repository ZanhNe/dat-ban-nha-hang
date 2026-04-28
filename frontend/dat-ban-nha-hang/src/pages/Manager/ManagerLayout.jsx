import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAtomValue, useSetAtom } from "jotai";
import { userAtom } from "../../store/authStore";
import NotificationBell from "../../components/Notification/NotificationBell";
import './ManagerLayout.css';
function ManagerLayout() {
    const navigate = useNavigate();


    const user = useAtomValue(userAtom);
    const setUser = useSetAtom(userAtom);
    const handleLogout = () => {
        localStorage.removeItem('user');
        setUser(null);
        navigate('/login');
    };
    return (
        <div className="manager-layout">
            {/* SIDEBAR BÊN TRÁI */}
            <aside className="manager-sidebar">
                <div className="sidebar-logo">
                    <h2>POS Manager</h2>
                </div>
                <nav className="sidebar-nav">
                    <NavLink to="/manager" end className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Tổng quan (Dashboard)
                    </NavLink>
                    <NavLink to="/manager/info" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Thông tin nhà hàng
                    </NavLink>
                    <NavLink to="/manager/staff" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Quản lý Nhân sự
                    </NavLink>
                    <NavLink to="/manager/menu" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Quản lý Thực đơn
                    </NavLink>
                    <NavLink to="/manager/tables" className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Sơ đồ bàn
                    </NavLink>
                </nav>
            </aside>

            <div className="manager-main">
                <header className="manager-header">
                    <div className="header-left">

                        <span>Khu vực Quản trị Nhà hàng</span>
                    </div>
                    <div className="header-right">
                        <NotificationBell />
                        <div className="user-profile">
                            <span className="user-name">Xin chào, <strong>{user.fullName}</strong></span>
                            <button className="btn-logout" onClick={handleLogout}>Đăng xuất</button>
                        </div>
                    </div>
                </header>


                <main className="manager-content">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}

export default ManagerLayout;