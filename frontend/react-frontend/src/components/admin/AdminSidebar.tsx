import React from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';
import { Icon } from '@iconify/react';

export default function AdminSidebar() {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const navItems = [
    { icon: 'solar:users-group-rounded-bold', label: '用户管理', href: '/admin/users' },
    { icon: 'solar:link-bold', label: '用户关系配对', href: '/admin/pairs' },
    { icon: 'solar:document-bold', label: '系统操作日志', href: '/admin/logs' },
  ];

  return (
    <aside className="w-64 bg-white border-r border-slate-200 flex flex-col shrink-0">
      <div className="p-6 flex items-center gap-3">
        <div className="w-10 h-10 bg-blue-600 rounded-xl flex items-center justify-center text-white shadow-lg shadow-blue-100">
          <Icon className="text-2xl" icon="solar:health-bold" />
        </div>
        <div className="flex flex-col leading-tight">
          <span className="text-lg font-bold text-slate-800">多银龄守护系统</span>
          <span className="text-xs font-semibold text-blue-600">管理员中心</span>
        </div>
      </div>
      <nav className="flex-1 px-4 space-y-1 overflow-y-auto mt-4">
        <div className="pt-8 pb-4">
          <p className="px-4 text-[10px] font-bold text-slate-400 uppercase tracking-widest">系统管理</p>
        </div>
        {navItems.map((item, i) => {
          const isActive = location.pathname === item.href;
          return (
            <a
              key={i}
              className={`flex items-center gap-3 px-4 py-3 rounded-xl transition-all cursor-pointer ${
                isActive ? 'bg-blue-50 text-blue-600 font-bold' : 'text-slate-500 hover:bg-slate-50 hover:text-slate-800'
              }`}
              onClick={() => navigate(item.href)}
            >
              <Icon className="text-xl" icon={item.icon} />
              <span>{item.label}</span>
            </a>
          );
        })}
      </nav>
      <div className="p-4 border-t border-slate-100">
        <a
          className="flex items-center gap-3 px-4 py-3 text-red-500 hover:bg-red-50 rounded-xl transition-all cursor-pointer"
          onClick={() => { logout(); navigate('/login'); }}
        >
          <Icon className="text-xl" icon="solar:logout-bold" />
          <span className="font-semibold">退出系统</span>
        </a>
      </div>
    </aside>
  );
}
