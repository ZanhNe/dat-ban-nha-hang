import React from 'react';

function LoadingState({ message = 'Đang tải dữ liệu...' }) {
    return <div className="ui-state">{message}</div>;
}

export default LoadingState;
