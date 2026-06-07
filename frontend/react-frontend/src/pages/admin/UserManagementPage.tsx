import React, { useState, useEffect, useCallback } from 'react';
import { Icon } from '@iconify/react';
import Pagination from '../../components/common/Pagination';
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

interface User {
  id: number; username: string; name: string; userType: string; userTypeDesc?: string;
  phone: string; createdAt: string;
}

function esc(str: string | null | undefined): string {
  if (!str) return '';
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

export default function UserManagementPage() {
  const [allUsers, setAllUsers] = useState<User[]>([]);
  const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [deleteUserId, setDeleteUserId] = useState<number | null>(null);
  const [deleteUsername, setDeleteUsername] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const { user } = useAuth();

  // 表单状态
  const [formRole, setFormRole] = useState('elder');
  const [formName, setFormName] = useState('');
  const [formUsername, setFormUsername] = useState('');
  const [formPassword, setFormPassword] = useState('');
  const [formPhone, setFormPhone] = useState('');
  const [formElderId, setFormElderId] = useState('');
  const [familyList, setFamilyList] = useState<any[]>([]);
  const [doctorOptions, setDoctorOptions] = useState<any[]>([]);
  const [elderOptions, setElderOptions] = useState<any[]>([]);
  const [selectedFamilies, setSelectedFamilies] = useState<number[]>([]);
  const [selectedDoctorId, setSelectedDoctorId] = useState('');
  const [selectedElders, setSelectedElders] = useState<number[]>([]);
  const [showWelcome, setShowWelcome] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');

  // 操作成功提示（2秒自动消失）
  useEffect(() => {
    if (successMsg) {
      const t = setTimeout(() => setSuccessMsg(''), 2000);
      return () => clearTimeout(t);
    }
  }, [successMsg]);

  // 欢迎弹窗（首次进入管理员页面）
  useEffect(() => {
    const welcomed = sessionStorage.getItem('adminWelcomed');
    if (!welcomed) {
      setShowWelcome(true);
      sessionStorage.setItem('adminWelcomed', 'true');
    }
  }, []);

  const loadUsers = useCallback(async () => {
    try {
      const res = await apiFetch('/api/users');
      if (res.code === 200) {
        setAllUsers(res.data || []);
        setFilteredUsers(res.data || []);
        setPage(1);
      }
    } catch (_) { }
  }, []);

  useEffect(() => { loadUsers(); }, [loadUsers]);

  const doSearch = useCallback(() => {
    const kw = searchKeyword.toLowerCase().trim();
    if (!kw) {
      setFilteredUsers(allUsers);
    } else {
      const roleMap: Record<string, string> = { '老人': 'elder', '家属': 'family', '医生': 'doctor', '管理员': 'admin' };
      setFilteredUsers(allUsers.filter(u =>
        (u.username && u.username.toLowerCase().includes(kw)) ||
        (u.name && u.name.toLowerCase().includes(kw)) ||
        (u.userTypeDesc && u.userTypeDesc.includes(kw)) ||
        (u.phone && u.phone.includes(kw)) ||
        (u.userType && u.userType.includes(kw)) ||
        (roleMap[kw] && u.userType === roleMap[kw])
      ));
    }
    setPage(1);
  }, [searchKeyword, allUsers]);

  const start = (page - 1) * pageSize;
  const pageData = filteredUsers.slice(start, start + pageSize);
  const roleBadge: Record<string, string> = { admin: 'bg-purple-100 text-purple-700', elder: 'bg-amber-100 text-amber-700', family: 'bg-emerald-100 text-emerald-700', doctor: 'bg-sky-100 text-sky-700' };

  const resetForm = () => {
    setFormRole('elder'); setFormName(''); setFormUsername(''); setFormPassword('');
    setFormPhone(''); setFormElderId(''); setEditingUser(null);
    setSelectedFamilies([]); setSelectedDoctorId(''); setSelectedElders([]);
  };

  const openEdit = async (user: User) => {
    setEditingUser(user);
    setFormRole(user.userType);
    setFormName(user.name || '');
    setFormUsername(user.username || '');
    setFormPhone(user.phone || '');
    setFormPassword('');
    setFormElderId('');
    await loadAdditionalFields(user.userType, user.id);
    setModalOpen(true);
  };

  const loadAdditionalFields = async (role: string, userId?: number) => {
    if (role === 'elder') {
      try {
        const [famRes, docRes] = await Promise.all([apiFetch('/api/family-members'), apiFetch('/api/doctors')]);
        const families = (famRes.code === 200 ? famRes.data : []).filter((f: any, i: number, arr: any[]) => arr.findIndex((x: any) => x.userId === f.userId) === i);
        setFamilyList(families);
        setDoctorOptions(docRes.code === 200 ? docRes.data : []);
        setSelectedFamilies([]);
        setSelectedDoctorId('');
        if (userId) {
          const elderRes = await apiFetch('/api/elders');
          if (elderRes.code === 200) {
            const elder = elderRes.data.find((e: any) => e.userId === userId);
            if (elder) {
              setFormElderId(String(elder.id));
              const famRes2 = await apiFetch('/api/family-members');
              if (famRes2.code === 200) {
                const boundFams = famRes2.data.filter((f: any) => f.elderId === elder.id);
                setSelectedFamilies(boundFams.map((f: any) => f.userId));
              }
              const docRelRes = await apiFetch('/api/elder-doctor?elderId=' + elder.id);
              if (docRelRes.code === 200 && docRelRes.data.length > 0) {
                setSelectedDoctorId(String(docRelRes.data[0].doctorId));
              }
            }
          }
        }
      } catch (_) { }
    } else if (role === 'family') {
      try {
        const elderRes = await apiFetch('/api/elders');
        setElderOptions(elderRes.code === 200 ? elderRes.data : []);
        setSelectedElders([]);
        if (userId) {
          const famRes = await apiFetch('/api/family-members');
          if (famRes.code === 200) {
            const myBindings = famRes.data.filter((f: any) => f.userId === userId);
            setSelectedElders(myBindings.map((b: any) => b.elderId));
          }
        }
      } catch (_) { }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formName) { alert('请输入姓名'); return; }
    if (!formUsername) { alert('请输入用户名'); return; }
    if (!editingUser && !formPassword) { alert('请输入密码'); return; }
    const phonePattern = /^1[3-9]\d{9}$/;
    if (formPhone && !phonePattern.test(formPhone)) { alert('手机号格式不正确'); return; }

    const payload: any = { role: formRole, username: formUsername, name: formName, phone: formPhone || undefined };
    if (formPassword) payload.password = formPassword;
    if (formRole === 'family' && selectedElders.length > 0) payload.elderIds = selectedElders;
    if (formRole === 'elder') {
      if (selectedFamilies.length > 0) payload.familyUserIds = selectedFamilies;
      if (selectedDoctorId) payload.doctorId = Number(selectedDoctorId);
    }

    try {
      let res;
      if (editingUser) {
        res = await apiFetch('/api/users/' + editingUser.id, { method: 'PUT', body: JSON.stringify(payload) });
      } else {
        res = await apiFetch('/api/users', { method: 'POST', body: JSON.stringify(payload) });
      }
      if (res.code !== 200) throw new Error(res.message || '操作失败');

      // 同步关系
      if (editingUser && formRole === 'elder' && formElderId) {
        const elderId = Number(formElderId);
        const famList = await apiFetch('/api/family-members').then(r => r.code === 200 ? r.data : []);
        const oldBound = famList.filter((f: any) => f.elderId === elderId);
        for (const userId of selectedFamilies) {
          if (!oldBound.some((f: any) => f.userId === userId)) {
            const famPerson = famList.find((f: any) => f.userId === userId);
            if (famPerson) await apiFetch('/api/family-members', { method: 'POST', body: JSON.stringify({ userId, elderId, name: famPerson.name }) });
          }
        }
        for (const bf of oldBound) {
          if (!selectedFamilies.includes(bf.userId)) await apiFetch('/api/family-members/' + bf.id, { method: 'DELETE' });
        }
        const oldRelations = await apiFetch('/api/elder-doctor?elderId=' + elderId).then(r => r.code === 200 ? r.data : []);
        for (const r of oldRelations) await apiFetch('/api/elder-doctor?elderId=' + elderId + '&doctorId=' + r.doctorId, { method: 'DELETE' });
        if (selectedDoctorId) await apiFetch('/api/elder-doctor', { method: 'POST', body: JSON.stringify({ elderId, doctorId: Number(selectedDoctorId) }) });
      }

      setModalOpen(false);
      resetForm();
      loadUsers();
      setSuccessMsg(editingUser ? '用户编辑成功' : '用户新增成功');
      // 写日志
      try {
        const operator = user?.username || 'admin';
        let detail = (editingUser ? '编辑' : '新增') + formRole + '账号: ' + formName;
        if (formRole === 'elder') {
          const famNames = selectedFamilies.map(id => {
            const f = familyList.find((f: any) => f.userId === id);
            return f ? f.name : String(id);
          }).join(', ');
          const doc = doctorOptions.find((d: any) => d.id === Number(selectedDoctorId));
          const docName = doc ? doc.name : '';
          if (famNames) detail += ' 家属=' + famNames;
          if (docName) detail += ' 医生=' + docName;
        }
        await apiFetch('/api/admin/logs', { method: 'POST', body: JSON.stringify({ operator, operation: detail }) });
      } catch (_) { }
    } catch (err: any) {
      alert((editingUser ? '更新' : '创建') + '失败: ' + (err.message || err));
    }
  };

  const openDelete = (userId: number, username: string) => {
    setDeleteUserId(userId);
    setDeleteUsername(username);
    setDeleteModalOpen(true);
  };

  const confirmDelete = async () => {
    if (!deleteUserId) return;
    try {
      const res = await apiFetch('/api/users/' + deleteUserId, { method: 'DELETE' });
      if (res.code !== 200) throw new Error(res.message || '删除失败');
      setDeleteModalOpen(false);
      loadUsers();
      setSuccessMsg('用户删除成功');
      try {
        const operator = user?.username || 'admin';
        await apiFetch('/api/admin/logs', { method: 'POST', body: JSON.stringify({ operator, operation: '删除用户: ' + deleteUsername }) });
      } catch (_) { }
    } catch (err: any) { alert('删除失败: ' + (err.message || err)); }
  };

  return (
    <>
      <header className="bg-white/80 backdrop-blur-md sticky top-0 z-10 border-b border-slate-100 px-8 py-4 flex items-center justify-between">
        <h2 className="text-xl font-bold"><Icon icon="solar:users-group-rounded-bold" className="text-2xl mr-2 align-middle" />用户管理</h2>
        <button className="px-4 py-2 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-all flex items-center gap-2" onClick={() => { resetForm(); loadAdditionalFields('elder'); setModalOpen(true); }}>
          <Icon icon="solar:user-plus-bold" /><span>新增用户</span>
        </button>
      </header>
      <div className="p-8">
        <section className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
          <div className="flex items-center gap-3 px-6 py-4 border-b border-slate-100">
            <input className="px-3 py-2 border rounded-lg w-64 text-sm" placeholder="搜索用户名 / 姓名 / 角色 / 手机号" value={searchKeyword} onChange={e => setSearchKeyword(e.target.value)} onKeyDown={e => { if (e.key === 'Enter') doSearch(); }} />
            <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm" onClick={doSearch}>搜索</button>
            {searchKeyword && <button className="px-4 py-2 border rounded-lg hover:bg-slate-50 text-sm" onClick={() => { setSearchKeyword(''); setFilteredUsers(allUsers); setPage(1); }}>显示全部</button>}
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead className="bg-slate-50 text-slate-500 text-xs uppercase">
                <tr><th className="px-6 py-4 font-semibold">用户名</th><th className="px-6 py-4 font-semibold">姓名</th><th className="px-6 py-4 font-semibold">角色</th><th className="px-6 py-4 font-semibold">手机号</th><th className="px-6 py-4 font-semibold">创建时间</th><th className="px-6 py-4 font-semibold text-center">操作</th></tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {pageData.length === 0 ? (
                  <tr><td colSpan={6} className="px-6 py-12 text-center text-slate-400">暂无用户数据</td></tr>
                ) : pageData.map(u => (
                  <tr key={u.id} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4 font-medium">{esc(u.username)}</td>
                    <td className="px-6 py-4">{esc(u.name || '-')}</td>
                    <td className="px-6 py-4"><span className={`px-2 py-1 rounded-full text-xs font-semibold ${roleBadge[u.userType] || 'bg-slate-100 text-slate-600'}`}>{esc(u.userTypeDesc || u.userType)}</span></td>
                    <td className="px-6 py-4">{esc(u.phone || '-')}</td>
                    <td className="px-6 py-4 text-slate-400 text-xs">{u.createdAt ? u.createdAt.substring(0, 10) : '-'}</td>
                    <td className="px-6 py-4 text-center">
                      <button className="px-3 py-1.5 text-xs bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition-all mr-1" onClick={() => openEdit(u)}><Icon icon="solar:pen-bold" className="align-middle mr-1" />编辑</button>
                      <button className="px-3 py-1.5 text-xs bg-red-50 text-red-500 rounded-lg hover:bg-red-100 transition-all" onClick={() => openDelete(u.id, u.username)}><Icon icon="solar:trash-bin-trash-bold" className="align-middle mr-1" />删除</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <Pagination current={page} total={filteredUsers.length} pageSize={pageSize} onPageChange={setPage} onPageSizeChange={s => { setPageSize(s); setPage(1); }} />
        </section>
      </div>

      {/* 新增/编辑用户弹窗 */}
      {modalOpen && (
        <div className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center" onClick={e => { if (e.target === e.currentTarget) { setModalOpen(false); resetForm(); } }}>
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto" onClick={e => e.stopPropagation()}>
            <div className="flex items-center justify-between p-6 border-b border-slate-100">
              <h3 className="text-lg font-bold">{editingUser ? '编辑用户' : '新增用户'}</h3>
              <button className="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-slate-100 transition-all" onClick={() => { setModalOpen(false); resetForm(); }}>
                <Icon icon="solar:close-circle-bold" className="text-xl text-slate-400" />
              </button>
            </div>
            <form className="p-6 space-y-4" onSubmit={handleSubmit}>
              <div><label className="block text-sm text-slate-500 mb-1">角色 <span className="text-red-400">*</span></label>
                <select className="w-full px-3 py-2 border rounded-lg" value={formRole} onChange={e => { setFormRole(e.target.value); loadAdditionalFields(e.target.value); }}>
                  <option value="elder">老人</option>
                  <option value="family">家属</option>
                  <option value="doctor">医生</option>
                </select></div>
              <div><label className="block text-sm text-slate-500 mb-1">姓名 <span className="text-red-400">*</span></label>
                <input className="w-full px-3 py-2 border rounded-lg" placeholder="输入姓名" value={formName} onChange={e => setFormName(e.target.value)} required /></div>
              <div><label className="block text-sm text-slate-500 mb-1">用户名 <span className="text-red-400">*</span></label>
                <input className="w-full px-3 py-2 border rounded-lg" placeholder="登录用户名" value={formUsername} onChange={e => setFormUsername(e.target.value)} required /></div>
              <div><label className="block text-sm text-slate-500 mb-1">密码 {!editingUser && <span className="text-red-400">*</span>}</label>
                <input type="password" className="w-full px-3 py-2 border rounded-lg" placeholder={editingUser ? '留空则不改' : '登录密码'} value={formPassword} onChange={e => setFormPassword(e.target.value)} required={!editingUser} /></div>
              <div><label className="block text-sm text-slate-500 mb-1">手机号</label>
                <input className="w-full px-3 py-2 border rounded-lg" maxLength={11} placeholder="输入手机号" value={formPhone} onChange={e => setFormPhone(e.target.value.replace(/\D/g, ''))} /></div>

              {formRole === 'elder' && (
                <>
                  <div><label className="block text-sm text-slate-500 mb-1">绑定家属（可多选）</label>
                    <div className="max-h-40 overflow-y-auto border rounded-lg p-2 space-y-1 mb-3">
                      {familyList.length === 0 ? <div className="text-slate-400 text-sm">暂无家属用户</div> : familyList.map((f: any) => (
                        <label key={f.userId} className="flex items-center gap-2 px-2 py-1 rounded hover:bg-slate-50 cursor-pointer">
                          <input type="checkbox" checked={selectedFamilies.includes(f.userId)} onChange={e => {
                            if (e.target.checked) setSelectedFamilies([...selectedFamilies, f.userId]);
                            else setSelectedFamilies(selectedFamilies.filter(id => id !== f.userId));
                          }} />
                          <span>{f.name}</span>
                        </label>
                      ))}
                    </div></div>
                  <div><label className="block text-sm text-slate-500 mb-1">选择医生</label>
                    <select className="w-full px-3 py-2 border rounded-lg" value={selectedDoctorId} onChange={e => setSelectedDoctorId(e.target.value)}>
                      <option value="">不选</option>
                      {doctorOptions.map((d: any) => <option key={d.id} value={d.id}>{d.name}</option>)}
                    </select></div>
                </>
              )}

              {formRole === 'family' && (
                <div><label className="block text-sm text-slate-500 mb-1">关联老人（可多选）</label>
                  <div className="max-h-40 overflow-y-auto border rounded-lg p-2 space-y-1">
                    {elderOptions.length === 0 ? <div className="text-slate-400 text-sm">暂无老人用户</div> : elderOptions.map((e: any) => (
                      <label key={e.id} className="flex items-center gap-2 px-2 py-1 rounded hover:bg-slate-50 cursor-pointer">
                        <input type="checkbox" checked={selectedElders.includes(e.id)} onChange={chk => {
                          if (chk.target.checked) setSelectedElders([...selectedElders, e.id]);
                          else setSelectedElders(selectedElders.filter(id => id !== e.id));
                        }} />
                        <span>{e.name}</span>
                      </label>
                    ))}
                  </div></div>
              )}

              <div className="flex justify-end gap-3 pt-2">
                <button type="button" className="px-4 py-2 border rounded-xl text-slate-600 hover:bg-slate-50 transition-all" onClick={() => { setModalOpen(false); resetForm(); }}>取消</button>
                <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-all">{editingUser ? '保存修改' : '创建账号'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 删除确认弹窗 */}
      {deleteModalOpen && (
        <div className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center" onClick={e => { if (e.target === e.currentTarget) setDeleteModalOpen(false); }}>
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm mx-4 p-6 text-center">
            <Icon icon="solar:danger-triangle-bold" className="text-4xl text-red-500 mb-3 inline-block" />
            <h3 className="text-lg font-bold mb-2">确认删除</h3>
            <p className="text-sm text-slate-500 mb-6">确定要删除用户 <span className="font-semibold text-slate-800">{esc(deleteUsername)}</span> 吗？此操作不可撤销。</p>
            <div className="flex justify-center gap-3">
              <button className="px-4 py-2 border rounded-xl text-slate-600 hover:bg-slate-50 transition-all" onClick={() => setDeleteModalOpen(false)}>取消</button>
              <button className="px-4 py-2 bg-red-500 text-white rounded-xl hover:bg-red-600 transition-all" onClick={confirmDelete}>确认删除</button>
            </div>
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

      {/* 欢迎弹窗 */}
      {showWelcome && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50" onClick={e => { if (e.target === e.currentTarget) setShowWelcome(false); }}>
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm mx-4 p-8 text-center" onClick={e => e.stopPropagation()}>
            <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-3xl">👋</span>
            </div>
            <h3 className="text-2xl font-bold text-slate-800 mb-6">欢迎，管理员！</h3>
            <button className="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors" onClick={() => setShowWelcome(false)}>开始管理</button>
          </div>
        </div>
      )}
    </>
  );
}
