import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Icon } from '@iconify/react';

export default function LoginPage() {
  const { login } = useAuth();
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('123456');
  const [loading, setLoading] = useState(false);
  const [showPwd, setShowPwd] = useState(false);

  const handleLogin = async () => {
    if (!username.trim() || !password.trim()) {
      alert('请输入用户名和密码');
      return;
    }
    setLoading(true);
    try {
      // 使用 Context 的 login 方法（会自动保存 Token + 用户信息）
      await login(username.trim(), password.trim());

      const userType = sessionStorage.getItem('userType');
      let targetUrl = '';
      switch (userType) {
        case 'admin': targetUrl = '/admin/users'; break;
        case 'doctor': targetUrl = '/doctor/dashboard'; break;
        case 'family': targetUrl = '/family'; break;
        case 'elder': targetUrl = '/elder'; break;
        default: alert('未知的用户类型，请联系管理员'); setLoading(false); return;
      }
      window.location.href = targetUrl;
    } catch (error: any) {
      const detail = error.name === 'TypeError' && error.message.includes('fetch')
        ? '后端服务未启动或网络不可达'
        : (error.message || '网络错误');
      alert('登录失败: ' + detail);
      setLoading(false);
    }
  };

  return (
    <div className="bg-slate-50 flex items-center justify-center min-h-screen font-sans">
      <div className="max-w-4xl w-full flex bg-white rounded-2xl shadow-2xl overflow-hidden m-4">
        <div className="hidden md:flex md:w-1/2 bg-blue-600 p-12 text-white flex-col justify-between">
          <div>
            <div className="flex items-center gap-2 mb-8">
              <Icon className="text-4xl" icon="solar:health-bold" />
              <span className="text-2xl font-bold tracking-tight">多银龄守护</span>
            </div>
            <h1 className="text-3xl font-bold mb-4 leading-tight">智慧监护，<br />多智能体协同守护。</h1>
            <p className="text-blue-100 leading-relaxed">基于 CrewAI 框架，集成健康监测、行为分析、冲突解决与应急响应，为老人提供全天候的安全保障。</p>
          </div>
        </div>
        <div className="w-full md:w-1/2 p-8 md:p-12">
          <div className="mb-8">
            <h2 className="text-2xl font-bold text-slate-800">欢迎登录</h2>
            <p className="text-slate-500 mt-1">请输入您的账号信息</p>
          </div>
          <form className="space-y-5" onSubmit={e => { e.preventDefault(); handleLogin(); }}>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">账号</label>
              <div className="relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <Icon icon="solar:user-circle-bold" />
                </span>
                <input
                  className="block w-full pl-10 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                  placeholder="请输入您的账号" type="text" value={username}
                  onChange={e => setUsername(e.target.value)}
                  onKeyDown={e => { if (e.key === 'Enter') e.currentTarget.form?.querySelector<HTMLInputElement>('input[type="password"]')?.focus(); }}
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">登录密码</label>
              <div className="relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <Icon icon="solar:lock-password-bold" />
                </span>
                <input
                  className="block w-full pl-10 pr-10 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                  placeholder="请输入密码" type={showPwd ? 'text' : 'password'} value={password}
                  onChange={e => setPassword(e.target.value)}
                  onKeyDown={e => { if (e.key === 'Enter') handleLogin(); }}
                />
                <span
                  className="absolute inset-y-0 right-0 flex items-center pr-3 cursor-pointer text-slate-400"
                  onClick={() => setShowPwd(!showPwd)}
                >
                  <Icon icon={showPwd ? 'solar:eye-bold' : 'solar:eye-closed-bold'} />
                </span>
              </div>
            </div>
            <button
              type="submit"
              disabled={loading}
              className="block w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-xl shadow-lg shadow-blue-200 text-center transform transition-active active:scale-95 disabled:opacity-70"
            >
              {loading ? '登录中...' : '立即登录'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
