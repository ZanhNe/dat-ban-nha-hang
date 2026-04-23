import React from 'react';
import { Link, useNavigate } from "react-router-dom";
import { useAtomValue, useSetAtom } from "jotai";
import { userAtom } from "../../store/authStore";
import './Header.css';

function Header() {
    const navigate = useNavigate();

    const user = useAtomValue(userAtom);
    const setUser = useSetAtom(userAtom);

    const handleLogout = () => {

        localStorage.removeItem('user');
        setUser(null);
        navigate('/login');
    };

    return (
        <header className="header">
            <div className="container">

                <div className="logo">
                    <span>Logo</span>
                </div>

                <nav className="nav">
                    <Link to="/">Home</Link>
                    <Link to="/restaurants">Restaurants</Link>
                    <Link to="/lists">Lists</Link>
                    <Link to="/contact">Contact</Link>
                </nav>

                <div className="auth">
                    {user ? (
                        /* Giao diện hiển thị KHI ĐÃ ĐĂNG NHẬP */
                        <div className="user-actions">
                            <span className="greeting">
                                Xin chào, <strong>{user.username}</strong>
                            </span>
                            <button onClick={handleLogout} className="btn-logout">
                                Đăng xuất
                            </button>
                        </div>
                    ) : (
                        /* Giao diện hiển thị KHI CHƯA ĐĂNG NHẬP */
                        <>
                            <Link to="/login" className="login">Login</Link>
                            <Link to="/register" className="signup">Sign up</Link>
                        </>
                    )}
                </div>

            </div>
        </header>
    );
}

export default Header;