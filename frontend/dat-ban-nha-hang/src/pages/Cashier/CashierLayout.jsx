import React from 'react';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";
import RoleLayoutShell from '../../components/layout/RoleLayoutShell';

function CashierLayout() {
    const user = useAtomValue(userAtom);

    return (
        <RoleLayoutShell
            title="Cashier"
            subtitle={user?.workplace?.name || 'Nhà hàng hiện tại'}
            userLabel={user?.fullName ? `Thu ngân: ${user.fullName}` : 'Cashier'}
            navItems={[
                { to: '/cashier', label: 'Chờ thanh toán', end: true },
                { to: '/cashier/paying', label: 'Đang thanh toán' },
            ]}
        />
    );
}

export default CashierLayout;
