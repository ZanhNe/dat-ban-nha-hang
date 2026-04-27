import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import './AdminLayout.css';
import { userAtom } from "../../store/authStore";
import { useAtomValue, useSetAtom } from "jotai";
function AdminLayout() {
    const navigate = useNavigate();

    const user = useAtomValue(userAtom);
    const setUser = useSetAtom(userAtom);
    const handleLogout = () => {
        localStorage.removeItem('user');
        setUser(null);
        navigate('/login');
    };
    console.log(" teenen", user)
    return (

        <div className="admin-layout">

            <aside className="admin-sidebar">
                <div className="sidebar-header">
                    <h3>Bảng quản trị</h3>
                    <p>Xin chào, {user.fullName}</p>
                </div>
                <nav className="sidebar-nav">
                    <NavLink to="/admin" end className={({ isActive }) => isActive ? "active" : ""}>
                        Dashboard
                    </NavLink>
                    <NavLink to="/admin/approvals" className={({ isActive }) => isActive ? "active" : ""}>
                        Duyệt nhà hàng
                    </NavLink>
                    <NavLink to="/admin/restaurants" className={({ isActive }) => isActive ? "active" : ""}>
                        Quản lý nhà hàng
                    </NavLink>
                    <NavLink to="/admin/cuisines" className={({ isActive }) => isActive ? "active" : ""}>
                        Danh mục ẩm thực
                    </NavLink>
                    <NavLink to="/admin/broadcast" className={({ isActive }) => isActive ? "active" : ""}>
                        Gửi thông báo
                    </NavLink>
                    <NavLink to="/admin/user-management" className={({ isActive }) => isActive ? "active" : ""}>
                        Quản lý người dùng
                    </NavLink>
                </nav>
                <div className="sidebar-footer">
                    <button onClick={handleLogout} className="btn-logout-admin">Đăng xuất</button>
                </div>
            </aside>


            <main className="admin-main-content">
                <Outlet />
            </main>
        </div>
    );
}

export default AdminLayout;