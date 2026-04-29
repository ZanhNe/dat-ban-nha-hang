import React from 'react';

function SectionCard({ children, className = '' }) {
    return <div className={`ui-card ${className}`.trim()}>{children}</div>;
}

export default SectionCard;
