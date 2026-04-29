import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import NotificationBell from '../Notification/NotificationBell';
import useLogout from '../../hooks/useLogout';

function RoleLayoutShell({ title, subtitle, navItems, userLabel }) {
    const logout = useLogout();

    return (
        <div className="min-h-screen flex bg-(--ui-bg)">
            <aside className="w-64 border-r border-(--ui-border) bg-white p-4 flex flex-col">
                <div className="mb-4">
                    <h2 className="text-lg font-bold">{title}</h2>
                    {subtitle ? <p className="text-sm text-(--ui-subtext) mt-1">{subtitle}</p> : null}
                </div>
                <nav className="flex-1 space-y-1">
                    {navItems.map((item) => (
                        <NavLink
                            key={item.to}
                            to={item.to}
                            end={item.end}
                            className={({ isActive }) =>
                                `block rounded-md px-3 py-2 text-sm font-medium ${
                                    isActive
                                        ? 'bg-blue-50 text-blue-700 border border-blue-100'
                                        : 'text-gray-700 hover:bg-gray-50 border border-transparent'
                                }`
                            }
                        >
                            {item.label}
                        </NavLink>
                    ))}
                </nav>
                <button className="ui-btn mt-4" onClick={logout}>
                    Đăng xuất
                </button>
            </aside>

            <main className="flex-1 min-w-0">
                <header className="h-14 border-b border-(--ui-border) bg-white px-5 flex items-center justify-between">
                    <span className="text-sm text-(--ui-subtext)">{userLabel}</span>
                    <NotificationBell />
                </header>
                <section className="ui-page">
                    <Outlet />
                </section>
            </main>
        </div>
    );
}

export default RoleLayoutShell;
