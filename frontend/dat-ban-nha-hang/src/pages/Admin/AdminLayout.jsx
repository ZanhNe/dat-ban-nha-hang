import React from 'react';
import { userAtom } from "../../store/authStore";
import { useAtomValue } from "jotai";
import RoleLayoutShell from '../../components/layout/RoleLayoutShell';

function AdminLayout() {
    const user = useAtomValue(userAtom);
    return (
        <RoleLayoutShell
            title="Bảng quản trị"
            subtitle={user?.fullName ? `Xin chào, ${user.fullName}` : ''}
            userLabel="Khu vực Admin"
            navItems={[
                { to: '/admin', label: 'Dashboard', end: true },
                { to: '/admin/approvals', label: 'Duyệt nhà hàng' },
                { to: '/admin/restaurants', label: 'Quản lý nhà hàng' },
                { to: '/admin/cuisines', label: 'Danh mục ẩm thực' },
                { to: '/admin/broadcast', label: 'Gửi thông báo' },
                { to: '/admin/user-management', label: 'Quản lý người dùng' },
                { to: '/admin/commission', label: 'Hoa hồng' },
            ]}
        />
    );
}

export default AdminLayout;