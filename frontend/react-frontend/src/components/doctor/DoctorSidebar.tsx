import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

interface DoctorSidebarProps {
  activeKey?: string;
}

export default function DoctorSidebar({ activeKey = 'dashboard' }: DoctorSidebarProps) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [firstElderId, setFirstElderId] = useState<string | null>(
    sessionStorage.getItem('activeElderId')
  );
  const [loadingElderId, setLoadingElderId] = useState(false);

  // 首次挂载时，如果还没有 activeElderId，自动从 API 获取该医生第一个签约老人
  useEffect(() => {
    if (!firstElderId && !loadingElderId) {
      loadFirstElder();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function loadFirstElder() {
    setLoadingElderId(true);
    const doctorId = sessionStorage.getItem('userId');
    if (!doctorId) { setLoadingElderId(false); return; }
    const token = sessionStorage.getItem('token');
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = 'Bearer ' + token;
    try {
      const res = await fetch(`http://localhost:8080/api/elders/doctor/${doctorId}`, { headers });
      const result = await res.json();
      const list = result?.data || [];
      if (list.length > 0) {
        const id = String(list[0].id);
        sessionStorage.setItem('activeElderId', id);
        setFirstElderId(id);
      }
    } catch (_) {
      console.warn('获取签约老人列表失败');
    } finally {
      setLoadingElderId(false);
    }
  }

  const getItems = () => {
    return [
      { icon: '📊', label: '我的监护老人', href: '/doctor/dashboard', key: 'dashboard' },
      { icon: '👥', label: '老人详情', href: firstElderId ? `/doctor/elder/${firstElderId}` : null, key: 'elderDetail' },
    ];
  };

  const items = getItems();

  // 判断当前菜单项是否高亮
  const isActive = (item: { key: string; href: string | null }) => {
    if (item.key === 'elderDetail') {
      // 所有 /doctor/elder/{id} 路径都高亮"老人详情"
      return location.pathname.startsWith('/doctor/elder/');
    }
    return location.pathname === item.href;
  };

  // 导航到目标页面（SPA 无刷新导航）
  const navigateTo = (item: { key: string; href: string | null }) => {
    if (item.key === 'elderDetail') {
      if (firstElderId) {
        navigate(`/doctor/elder/${firstElderId}`);
      } else if (!loadingElderId) {
        // 还没加载到 elderId，重新加载再导航
        loadFirstElder().then(() => {
          const id = sessionStorage.getItem('activeElderId');
          if (id) navigate(`/doctor/elder/${id}`);
        });
      }
    } else if (item.href) {
      navigate(item.href);
    }
  };

  return (
    <aside className="w-64 bg-[#1e40af] text-white flex flex-col fixed h-full z-40">
      <div className="p-6 flex items-center gap-3">
        <div className="w-10 h-10 bg-white rounded-lg flex items-center justify-center">
          <span>🏥</span>
        </div>
        <span className="text-xl font-bold tracking-tight">多银龄守护</span>
      </div>
      <nav className="flex-1 px-4 space-y-2 mt-4">
        {items.map((item, index) => {
          const active = isActive(item);
          return (
            <button
              key={index}
              className={`w-full flex items-center gap-3 px-4 py-3 transition-all rounded-xl text-left ${
                active
                  ? 'bg-white/10 rounded-xl border-l-4 border-white shadow-sm font-medium'
                  : 'text-white/70 hover:bg-white/5 rounded-xl'
              }`}
              onClick={() => navigateTo(item)}
            >
              <span>{item.icon}</span>
              <span>{item.label}</span>
            </button>
          );
        })}
      </nav>
      <div className="p-6 border-t border-white/10">
        <div className="flex items-center gap-3 mb-6">
          <img
            alt="Doctor Avatar"
            className="w-10 h-10 rounded-full border-2 border-white/20"
            src="https://modao.cc/agent-py/media/generated_images/2026-04-28/5babafdd480d4f388443a64e1a0dd8f0.jpg"
          />
          <div>
            <p className="text-sm font-bold text-white" id="doctorName">{user?.name || '医生'} 医生</p>
            <p className="text-xs text-white/60">中心医院/全科</p>
          </div>
        </div>
        <button
          className="w-full py-2 bg-white hover:bg-red-50 text-red-500 rounded-lg flex items-center justify-center gap-2 transition-all border border-red-200"
          onClick={() => { logout(); navigate('/login'); }}
        >
          <span>🚪</span>
          <span className="text-sm">退出登录</span>
        </button>
      </div>
    </aside>
  );
}
