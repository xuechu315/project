import React from 'react';

interface PaginationProps {
  current: number;
  total: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
}

export default function Pagination({ current, total, pageSize, onPageChange, onPageSizeChange }: PaginationProps) {
  const totalPages = Math.ceil(total / pageSize);
  if (total === 0) return null;

  return (
    <div className="flex items-center justify-between px-6 py-3 border-t border-slate-100 bg-slate-50 text-sm">
      <div className="text-slate-500">共 <span>{total}</span> 条</div>
      <div className="flex items-center gap-2">
        <button
          className="px-3 py-1 rounded-lg border hover:bg-white disabled:opacity-40 disabled:cursor-not-allowed"
          disabled={current <= 1}
          onClick={() => onPageChange(current - 1)}
        >
          上一页
        </button>
        <span className="text-slate-600">第 <span>{current}</span> / <span>{totalPages}</span> 页</span>
        <button
          className="px-3 py-1 rounded-lg border hover:bg-white disabled:opacity-40 disabled:cursor-not-allowed"
          disabled={current >= totalPages}
          onClick={() => onPageChange(current + 1)}
        >
          下一页
        </button>
        <select
          className="ml-2 px-2 py-1 border rounded-lg text-xs"
          value={pageSize}
          onChange={e => onPageSizeChange(Number(e.target.value))}
        >
          <option value={10}>10条/页</option>
          <option value={20}>20条/页</option>
          <option value={50}>50条/页</option>
        </select>
      </div>
    </div>
  );
}
