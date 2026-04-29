import React from 'react';

function PaginationBar({ page = 0, totalPages = 1, totalItems, onPageChange, disabled = false }) {
    if (totalPages <= 1) return null;

    return (
        <div className="flex items-center justify-between mt-4">
            <span className="text-sm text-gray-600">
                Trang {page + 1} / {totalPages}
                {totalItems !== undefined && ` - Tổng: ${totalItems}`}
            </span>
            <div className="flex gap-2">
                <button
                    className="ui-btn btn-secondary"
                    disabled={disabled || page <= 0}
                    onClick={() => onPageChange(page - 1)}
                >
                    Trước
                </button>
                <button
                    className="ui-btn btn-secondary"
                    disabled={disabled || page + 1 >= totalPages}
                    onClick={() => onPageChange(page + 1)}
                >
                    Sau
                </button>
            </div>
        </div>
    );
}

export default PaginationBar;
