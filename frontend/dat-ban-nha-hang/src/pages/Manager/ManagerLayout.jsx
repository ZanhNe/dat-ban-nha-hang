import React from 'react';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";
import RoleLayoutShell from '../../components/layout/RoleLayoutShell';

function ManagerLayout() {
    const user = useAtomValue(userAtom);
    return (
        <RoleLayoutShell
            title="POS Manager"
            subtitle={user?.workplace?.name || 'Khu vực Quản trị Nhà hàng'}
            userLabel={user?.fullName ? `Xin chào, ${user.fullName}` : 'Manager'}
            navItems={[
                { to: '/manager', label: 'Tổng quan', end: true },
                { to: '/manager/info', label: 'Thông tin nhà hàng' },
                { to: '/manager/staff', label: 'Quản lý Nhân sự' },
                { to: '/manager/menu', label: 'Quản lý Thực đơn' },
                { to: '/manager/tables', label: 'Sơ đồ bàn' },
            ]}
        />
    );
}

export default ManagerLayout;