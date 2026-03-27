import React from 'react';
import { useAtom, useAtomValue } from 'jotai';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { pageAtom, totalPagesAtom, totalItemsAtom, limitAtom } from '../../store/mapStore';

export default function PaginationControl() {
  const [page, setPage] = useAtom(pageAtom);
  const totalPages = useAtomValue(totalPagesAtom);
  const totalItems = useAtomValue(totalItemsAtom);
  const limit = useAtomValue(limitAtom);

  if (totalPages <= 1) return null;

  const handlePrev = () => {
    if (page > 0) setPage(page - 1);
  };

  const handleNext = () => {
    if (page < totalPages - 1) setPage(page + 1);
  };

  // Tính số thứ tự hiển thị
  const startIdx = page * limit + 1;
  const endIdx = Math.min((page + 1) * limit, totalItems);

  return (
    <div className="flex items-center justify-between border-t py-3 bg-white mt-4">
      <span className="text-sm text-gray-600">
        Hiển thị <span className="font-medium text-gray-900">{startIdx}-{endIdx}</span> trong <span className="font-medium text-gray-900">{totalItems}</span>
      </span>
      
      <div className="flex items-center gap-2">
        <button
          onClick={handlePrev}
          disabled={page === 0}
          className="p-1 rounded-md text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>
        <span className="text-sm font-medium text-gray-800">
          Trang {page + 1} / {totalPages}
        </span>
        <button
          onClick={handleNext}
          disabled={page >= totalPages - 1}
          className="p-1 rounded-md text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <ChevronRight className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
}
