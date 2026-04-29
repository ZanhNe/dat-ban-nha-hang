import React from 'react';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";
import RoleLayoutShell from '../../components/layout/RoleLayoutShell';

function WaiterLayout() {
    const user = useAtomValue(userAtom);

    return (
        <RoleLayoutShell
            title="Waiter POS"
            subtitle={user?.workplace?.name || 'Nhà hàng hiện tại'}
            userLabel={user?.fullName ? `Phục vụ: ${user.fullName}` : 'Waiter'}
            navItems={[{ to: '/waiter', label: 'Phân công Bàn', end: true }]}
        />
    );
}

export default WaiterLayout;