import React from 'react';

function PageHeader({ title, subtitle, rightSlot }) {
    return (
        <div className="ui-page-header">
            <div>
                <h2 className="ui-page-title">{title}</h2>
                {subtitle ? <p className="ui-page-subtitle">{subtitle}</p> : null}
            </div>
            {rightSlot || null}
        </div>
    );
}

export default PageHeader;
