import React, { useState, useEffect, useCallback } from 'react';
import { Icon } from '@iconify/react';

const API_BASE = 'http://localhost:8080';
function apiFetch(url: string, options: RequestInit = {}) {
  const opts = { ...options };
  const token = sessionStorage.getItem('token');
  const headers: Record<string, string> = { ...(opts.headers as Record<string, string> || {}) };
  if (token) {
    headers['Authorization'] = 'Bearer ' + token;
  }
  if (!opts.method || opts.method === 'GET') {
    opts.headers = headers;
  } else {
    headers['Content-Type'] = 'application/json';
    opts.headers = headers;
  }
  return fetch(API_BASE + url, opts).then(r => r.json());
}

function esc(str: string | null | undefined): string {
  if (!str) return '';
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

interface LogEntry {
  id: number;
  operator: string;
  operation: string;
  createdAt: string;
  level?: string;
}

export default function OperationLogsPage() {
  const [logs, setLogs] = useState<LogEntry[]>([]);
  const [searchQ, setSearchQ] = useState('');
  const [levelFilter, setLevelFilter] = useState('all');
  // 广播频道
  useEffect(() => {
    if ('BroadcastChannel' in window) {
      const channel = new BroadcastChannel('admin_channel');
      channel.onmessage = (ev) => {
        if (ev.data?.type === 'log' || ev.data?.type === 'logsCleared') loadLogs();
      };
      return () => channel.close();
    }
  }, []);

  const loadLogs = useCallback(async () => {
    try {
      const res = await apiFetch('/api/admin/logs');
      if (res.code === 200) setLogs(res.data || []);
    } catch (_) { }
  }, []);

  useEffect(() => { loadLogs(); }, [loadLogs]);

  const filteredLogs = logs.filter(l => {
    if (levelFilter !== 'all' && (l.level || 'info') !== levelFilter) return false;
    const text = (l.operator || '') + ' ' + (l.operation || '') + ' ' + (l.createdAt || '');
    return !searchQ || text.toLowerCase().includes(searchQ.toLowerCase());
  });

  const clearLogs = async () => {
    if (!confirm('确定清空所有日志？')) return;
    try {
      const res = await apiFetch('/api/admin/logs', { method: 'DELETE' });
      if (res.code === 200) loadLogs();
    } catch (err: any) { alert('清空失败: ' + err.message); }
  };

  return (
    <>
      <header className="bg-white/80 backdrop-blur-md sticky top-0 z-10 border-b border-slate-100 px-8 py-4 flex items-center justify-between">
        <h2 className="text-xl font-bold">系统操作日志</h2>
        <div className="flex items-center gap-4">
          <button className="px-4 py-2 bg-white border rounded-xl" onClick={clearLogs}>清空日志</button>
        </div>
      </header>
      <div className="p-8 space-y-6">
        <section className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm">
          <div className="flex items-center gap-3 mb-4">
            <input className="px-3 py-2 border rounded-lg flex-1" placeholder="按用户/操作/时间检索" value={searchQ} onChange={e => setSearchQ(e.target.value)} />
            <select className="px-3 py-2 border rounded-lg" value={levelFilter} onChange={e => setLevelFilter(e.target.value)}>
              <option value="all">全部类型</option>
              <option value="info">信息</option>
              <option value="warn">警告</option>
              <option value="error">错误</option>
            </select>
          </div>
          <div className="max-h-[60vh] overflow-auto text-sm text-slate-700">
            {filteredLogs.length === 0 ? (
              <div className="text-center text-slate-400 py-12">暂无操作日志</div>
            ) : filteredLogs.map(l => (
              <div key={l.id} className="p-3 border-b">
                <div className="flex items-center gap-3">
                  <span className="text-xs font-bold text-blue-600 bg-blue-50 px-2 py-0.5 rounded">{esc(l.operator)}</span>
                  <span className="text-sm">{esc(l.operation)}</span>
                </div>
                <div className="text-[12px] text-slate-400 mt-1">{new Date(l.createdAt).toLocaleString()}</div>
              </div>
            ))}
          </div>
        </section>
      </div>

    </>
  );
}
