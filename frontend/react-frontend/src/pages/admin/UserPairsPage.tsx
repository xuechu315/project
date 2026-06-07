import React, { useState, useEffect, useCallback } from 'react';
import { Icon } from '@iconify/react';
import { useAuth } from '../../contexts/AuthContext';

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

export default function UserPairsPage() {
  const [elders, setElders] = useState<any[]>([]);
  const [families, setFamilies] = useState<any[]>([]);
  const [doctors, setDoctors] = useState<any[]>([]);
  const [filteredElders, setFilteredElders] = useState<any[]>([]);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [bindModalOpen, setBindModalOpen] = useState(false);
  const [bindElderId, setBindElderId] = useState<number | null>(null);
  const [bindElderName, setBindElderName] = useState('');
  const [bindSelectedFamilies, setBindSelectedFamilies] = useState<number[]>([]);
  const [bindSelectedDoctor, setBindSelectedDoctor] = useState('');
  const { user } = useAuth();
  const [successMsg, setSuccessMsg] = useState('');

  // 操作成功提示（2秒自动消失）
  useEffect(() => {
    if (successMsg) {
      const t = setTimeout(() => setSuccessMsg(''), 2000);
      return () => clearTimeout(t);
    }
  }, [successMsg]);

  const loadData = useCallback(async () => {
    try {
      const res = await apiFetch('/api/admin/users');
      if (res.code === 200) {
        setElders(res.data.elders || []);
        setFamilies(res.data.families || []);
        setDoctors(res.data.doctors || []);
        setFilteredElders(res.data.elders || []);
        setPage(1);
      }
    } catch (_) { }
  }, []);

  useEffect(() => { loadData(); }, [loadData]);

  // 广播频道
  useEffect(() => {
    if ('BroadcastChannel' in window) {
      const channel = new BroadcastChannel('admin_channel');
      channel.onmessage = (ev) => { if (ev.data?.type === 'userPairUpdate') loadData(); };
      return () => channel.close();
    }
  }, [loadData]);

  const doSearch = () => {
    const kw = searchKeyword.trim().toLowerCase();
    if (!kw) { setFilteredElders(elders); } else {
      setFilteredElders(elders.filter((e: any) => {
        if (e.name.toLowerCase().includes(kw)) return true;
        const fams = families.filter((f: any) => f.elderId === e.elderId);
        if (fams.some((f: any) => f.name.toLowerCase().includes(kw))) return true;
        const relations = e.doctorRelations || [];
        return relations.some((r: any) => {
          const doc = doctors.find((d: any) => d.id === r.doctorId);
          return doc && doc.name.toLowerCase().includes(kw);
        });
      }));
    }
    setPage(1);
  };

  const start = (page - 1) * pageSize;
  const pageData = filteredElders.slice(start, start + pageSize);
  const totalPages = Math.ceil(filteredElders.length / pageSize);

  const openBindModal = (elder: any) => {
    setBindElderId(elder.elderId);
    setBindElderName(elder.name);
    const boundFams = families.filter((f: any) => f.elderId === elder.elderId);
    setBindSelectedFamilies(boundFams.map((f: any) => f.userId));
    const relations = elder.doctorRelations || [];
    setBindSelectedDoctor(relations.length > 0 ? String(relations[0].doctorId) : '');
    setBindModalOpen(true);
  };

  const saveBind = async (e: React.FormEvent) => {
    e.preventDefault();
    if (bindElderId === null) return;
    try {
      const boundToElder = families.filter(f => f.elderId === bindElderId);
      const uniqueFamilies = families.filter((f, i, arr) => arr.findIndex(x => x.userId === f.userId) === i);
      for (const userId of bindSelectedFamilies) {
        if (!boundToElder.some(f => f.userId === userId)) {
          const famPerson = uniqueFamilies.find(f => f.userId === userId);
          if (famPerson) await apiFetch('/api/family-members', { method: 'POST', body: JSON.stringify({ userId, elderId: bindElderId, name: famPerson.name }) });
        }
      }
      for (const bf of boundToElder) {
        if (!bindSelectedFamilies.includes(bf.userId)) await apiFetch('/api/family-members/' + bf.id, { method: 'DELETE' });
      }
      const elderObj = elders.find(x => x.elderId === bindElderId);
      const currentRelations = elderObj?.doctorRelations || [];
      for (const r of currentRelations) await apiFetch('/api/elder-doctor?elderId=' + bindElderId + '&doctorId=' + r.doctorId, { method: 'DELETE' });
      if (bindSelectedDoctor) await apiFetch('/api/elder-doctor', { method: 'POST', body: JSON.stringify({ elderId: bindElderId, doctorId: Number(bindSelectedDoctor) }) });

      const operator = user?.username || 'admin';
      const famNames = bindSelectedFamilies.map(id => {
        const f = uniqueFamilies.find(f => f.userId === id);
        return f ? f.name : String(id);
      }).join(', ');
      const doc = doctors.find(d => d.id === Number(bindSelectedDoctor));
      const docName = doc ? doc.name : '';
      let detail = bindElderName;
      if (famNames) detail += ' 家属=' + famNames;
      if (docName) detail += ' 医生=' + docName;
      await apiFetch('/api/admin/logs', { method: 'POST', body: JSON.stringify({ operator, operation: detail }) });
      setBindModalOpen(false);
      loadData();
      setSuccessMsg('关系编辑成功');
    } catch (err: any) { alert('保存失败: ' + (err.message || err)); }
  };

  const unbindAll = async (elderId: number) => {
    if (!confirm('确认解绑该老人所有关系？')) return;
    try {
      const bound = families.filter(f => f.elderId === elderId);
      for (const b of bound) await apiFetch('/api/family-members/' + b.id, { method: 'DELETE' });
      const elderObj = elders.find(x => x.elderId === elderId);
      const relations = elderObj?.doctorRelations || [];
      for (const r of relations) await apiFetch('/api/elder-doctor?elderId=' + elderId + '&doctorId=' + r.doctorId, { method: 'DELETE' });
      const operator = user?.username || 'admin';
      await apiFetch('/api/admin/logs', { method: 'POST', body: JSON.stringify({ operator, operation: `解绑: ${elderObj?.name || elderId}` }) });
      loadData();
      setSuccessMsg('解绑成功');
    } catch (err: any) { alert('解绑失败: ' + (err.message || err)); }
  };

  const uniqueFamilies = families.filter((f, i, arr) => arr.findIndex(x => x.userId === f.userId) === i);

  return (
    <>
      <header className="bg-white/80 backdrop-blur-md sticky top-0 z-10 border-b border-slate-100 px-8 py-4 flex items-center justify-between">
        <h2 className="text-xl font-bold">用户关系配对管理</h2>
      </header>
      <div className="p-8 space-y-6">
        <section className="bg-white p-4 rounded-2xl border border-slate-100 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <input className="px-3 py-2 border rounded-lg w-64" placeholder="搜索老人姓名" value={searchKeyword} onChange={e => setSearchKeyword(e.target.value)} onKeyDown={e => { if (e.key === 'Enter') doSearch(); }} />
              <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm" onClick={doSearch}>搜索</button>
              {searchKeyword && <button className="px-4 py-2 border rounded-lg hover:bg-slate-50 text-sm" onClick={() => { setSearchKeyword(''); setFilteredElders(elders); setPage(1); }}>显示全部</button>}
            </div>

          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead><tr className="text-sm text-slate-500 border-b"><th className="p-3">老人</th><th>家属</th><th>医生</th><th>状态</th><th>操作</th></tr></thead>
              <tbody>
                {pageData.length === 0 ? (
                  <tr><td colSpan={5} className="p-12 text-center text-slate-400">暂无数据</td></tr>
                ) : pageData.map((e: any) => {
                  const fams = families.filter((f: any) => f.elderId === e.elderId);
                  const famHtml = fams.length ? fams.map((f: any) => f.name).join(' / ') : '<span class="text-slate-400">未绑定</span>';
                  const relations = e.doctorRelations || [];
                  const docHtml = relations.length ? relations.map((r: any) => { const d = doctors.find((x: any) => x.id === r.doctorId); return d ? d.name : r.doctorId; }).join(' / ') : '<span class="text-slate-400">未绑定</span>';
                  const hasFam = fams.length > 0;
                  const hasDoc = relations.length > 0;
                  const status = hasFam && hasDoc
                    ? '<span class="px-2 py-0.5 bg-green-50 text-green-600 rounded-full text-xs font-bold">已绑定</span>'
                    : hasFam || hasDoc
                      ? '<span class="px-2 py-0.5 bg-amber-50 text-amber-600 rounded-full text-xs font-bold">部分绑定</span>'
                      : '<span class="px-2 py-0.5 bg-slate-50 text-slate-400 rounded-full text-xs font-bold">未绑定</span>';
                  return (
                    <tr key={e.elderId} className="border-b hover:bg-slate-50">
                      <td className="p-3 font-bold">{e.name}</td>
                      <td className="p-3" dangerouslySetInnerHTML={{ __html: famHtml }} />
                      <td className="p-3" dangerouslySetInnerHTML={{ __html: docHtml }} />
                      <td className="p-3" dangerouslySetInnerHTML={{ __html: status }} />
                      <td className="p-3">
                        <button className="px-3 py-1 bg-blue-600 text-white rounded-xl mr-1" onClick={() => openBindModal(e)}>编辑</button>
                        <button className="px-3 py-1 bg-red-50 text-red-600 rounded-xl" onClick={() => unbindAll(e.elderId)}>解绑</button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
          {/* 分页 */}
          <div className={`${filteredElders.length === 0 ? 'hidden' : ''} flex items-center justify-between px-3 py-3 border-t border-slate-100 bg-slate-50 text-sm mt-3`}>
            <div className="text-slate-500">共 <span>{filteredElders.length}</span> 位老人</div>
            <div className="flex items-center gap-2">
              <button className="px-3 py-1 rounded-lg border hover:bg-white disabled:opacity-40 disabled:cursor-not-allowed" disabled={page <= 1} onClick={() => setPage(page - 1)}>上一页</button>
              <span className="text-slate-600">第 <span>{page}</span> / <span>{totalPages}</span> 页</span>
              <button className="px-3 py-1 rounded-lg border hover:bg-white disabled:opacity-40 disabled:cursor-not-allowed" disabled={page >= totalPages} onClick={() => setPage(page + 1)}>下一页</button>
              <select className="ml-2 px-2 py-1 border rounded-lg text-xs" value={pageSize} onChange={e => { setPageSize(Number(e.target.value)); setPage(1); }}>
                <option value={10}>10条/页</option>
                <option value={20}>20条/页</option>
                <option value={50}>50条/页</option>
              </select>
            </div>
          </div>
          <div className="mt-4 text-right text-[12px] text-slate-500">提示：在此页面可为老人绑定家属和责任医生。</div>
        </section>
      </div>

      {/* 绑定弹窗 */}
      {bindModalOpen && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50" onClick={e => { if (e.target === e.currentTarget) setBindModalOpen(false); }}>
          <div className="bg-white w-[720px] rounded-2xl p-6" onClick={e => e.stopPropagation()}>
            <h3 className="text-lg font-bold mb-3">编辑绑定关系</h3>
            <form className="space-y-4" onSubmit={saveBind}>
              <div className="grid grid-cols-2 gap-4">
                <div><label className="text-sm text-slate-500">老人</label><div className="mt-1 font-bold">{bindElderName}</div></div>
                <div>
                  <label className="text-sm text-slate-500">选择家属（可多选）</label>
                  <div className="mt-1 max-h-48 overflow-y-auto border rounded-lg p-2 space-y-1">
                    {uniqueFamilies.length === 0 ? (
                      <div className="text-slate-400 text-sm py-2">暂无家属用户</div>
                    ) : uniqueFamilies.map((f: any) => (
                      <label key={f.userId} className="flex items-center gap-2 px-2 py-1 rounded hover:bg-slate-50 cursor-pointer">
                        <input type="checkbox" checked={bindSelectedFamilies.includes(f.userId)} onChange={e => {
                          if (e.target.checked) setBindSelectedFamilies([...bindSelectedFamilies, f.userId]);
                          else setBindSelectedFamilies(bindSelectedFamilies.filter(id => id !== f.userId));
                        }} />
                        <span>{f.name}</span>
                      </label>
                    ))}
                  </div>
                </div>
              </div>
              <div>
                <label className="text-sm text-slate-500">选择医生</label>
                <select className="w-full px-3 py-2 border rounded-lg mt-1" value={bindSelectedDoctor} onChange={e => setBindSelectedDoctor(e.target.value)}>
                  <option value="">-- 清除医生绑定 --</option>
                  {doctors.map((d: any) => <option key={d.id} value={d.id}>{d.name}</option>)}
                </select>
              </div>
              <div className="flex justify-end gap-3">
                <button type="button" className="px-4 py-2 rounded-xl" onClick={() => setBindModalOpen(false)}>取消</button>
                <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded-xl">保存绑定</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 操作成功提示 */}
      {successMsg && (
        <div className="fixed top-6 right-6 z-[100] animate-fade-in">
          <div className="bg-green-50 border border-green-200 rounded-xl px-5 py-3 shadow-lg flex items-center gap-2">
            <span className="text-green-600 font-bold">✓</span>
            <span className="text-green-700 font-medium text-sm">{successMsg}</span>
          </div>
        </div>
      )}
    </>
  );
}
