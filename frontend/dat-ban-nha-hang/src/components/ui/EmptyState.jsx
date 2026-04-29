import React from 'react';

function EmptyState({ message = 'Không có dữ liệu để hiển thị.' }) {
    return <div className="ui-state">{message}</div>;
}

export default EmptyState;
