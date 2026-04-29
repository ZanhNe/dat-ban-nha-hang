// src/layouts/ReceptionistLayout.jsx
import React from 'react';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";
import RoleLayoutShell from '../../components/layout/RoleLayoutShell';

function ReceptionistLayout() {
    const user = useAtomValue(userAtom);

    return (
        <RoleLayoutShell
            title="Reception"
            subtitle={user?.workplace?.name || 'Nhà hàng hiện tại'}
            userLabel={user?.fullName ? `Nhân viên: ${user.fullName}` : 'Receptionist'}
            navItems={[
                { to: '/receptionist', label: 'Yêu cầu mới', end: true },
                { to: '/receptionist/check-in', label: 'Đón khách (Check-in)' },
            ]}
        />
    );
}

export default ReceptionistLayout;