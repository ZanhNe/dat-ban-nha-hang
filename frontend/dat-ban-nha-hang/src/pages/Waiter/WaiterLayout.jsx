import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAtomValue, useSetAtom } from "jotai";
import { userAtom } from "../../store/authStore";
import './WaiterLayout.css';

function WaiterLayout() {
    const navigate = useNavigate();
    const user = useAtomValue(userAtom);
    const setUser = useSetAtom(userAtom);

    const handleLogout = () => {
        localStorage.clear();
        setUser(null);
        navigate('/login');
    };

    return (
        <div className="waiter-desktop-layout">
            {/* SIDEBAR BÊN TRÁI */}
            <aside className="waiter-sidebar">
                <div className="sidebar-brand">
                    <span className="icon"></span>
                    <h2>WAITER POS</h2>
                </div>
                <nav className="waiter-nav">
                    <NavLink to="/waiter" end className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}>
                        Phân công Bàn
                    </NavLink>


                </nav>
            </aside>

            {/* KHU VỰC NỘI DUNG CHÍNH */}
            <main className="waiter-main">
                <header className="waiter-header">
                    <div className="header-info">
                        <h3>{user?.workplace?.name || "Nhà hàng hiện tại"}</h3>
                    </div>
                    <div className="header-user">
                        <span>Phục vụ: <strong>{user?.fullName}</strong></span>
                        <button onClick={handleLogout} className="btn-logout-outline">Đăng xuất</button>
                    </div>
                </header>
                <section className="waiter-content">
                    <Outlet />
                </section>
            </main>
        </div>
    );
}

export default WaiterLayout;