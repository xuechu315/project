import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Icon } from '@iconify/react';
// 老人数据类型
interface ElderData {
  id: number;
  name: string;
  age: number;
  gender: string;
  status: string;
  heartRate?: number;
  systolic?: number;
  diastolic?: number;
  steps?: number;
  height?: string;
  weight?: string;
  alertLevel?: string;
  [key: string]: any;
}

export default function DoctorDashboardPage() {
  const navigate = useNavigate();
  const [elderlyList, setElderlyList] = useState<ElderData[]>([]);
  const [currentFilter, setCurrentFilter] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [doctorName, setDoctorName] = useState('');
  const [showWarningModal, setShowWarningModal] = useState(false);
  const [showNotificationModal, setShowNotificationModal] = useState(false);
  const [warningElder, setWarningElder] = useState<ElderData | null>(null);
  const [notificationElder, setNotificationElder] = useState<ElderData | null>(null);

  // ESC键关闭弹窗
  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Escape') { setShowWarningModal(false); setShowNotificationModal(false); }
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, []);

  useEffect(() => {
    const name = sessionStorage.getItem('name');
    setDoctorName(name || '医生');
    loadElderlyList();
  }, []);

  async function loadElderlyList() {
    const doctorId = sessionStorage.getItem('userId');
    if (!doctorId) {
      alert('请先登录');
      navigate('/login');
      return;
    }
    try {
      const token = sessionStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/elders/doctor/${doctorId}`, {
        headers: token ? { 'Authorization': 'Bearer ' + token } : {}
      });
      if (response.ok) {
        const result = await response.json();
        let list: ElderData[] = result.data || [];
        list = list.map(elder => {
          let status = elder.status;
          if (status === '正常') status = 'normal';
          if (status === '轻微异常') status = 'warning';
          if (status === '严重异常') status = 'critical';
          if (!status) status = 'normal';
          return {
            ...elder,
            heartRate: elder.heartRate || 78,
            systolic: elder.systolic || 125,
            diastolic: elder.diastolic || 82,
            steps: elder.steps || 4521,
            status,
            alertLevel: elder.alertLevel || 'normal',
          };
        });
        const alertStatuses = JSON.parse(sessionStorage.getItem('elderAlertStatuses') || '{}');
        list = list.map(elder => {
          if (alertStatuses[elder.id]) {
            return { ...elder, status: alertStatuses[elder.id].status };
          }
          return elder;
        });
        setElderlyList(list);
        // 自动弹出首个严重异常老人的警告弹窗
        const criticalElder = list.find(e => e.status === 'critical');
        if (criticalElder) {
          setWarningElder(criticalElder);
          setShowWarningModal(true);
        }
      } else {
        console.error('获取老人列表失败');
      }
    } catch (error) {
      console.error('加载老人列表失败:', error);
    } finally {
      setLoading(false);
    }
  }

  const filteredList = elderlyList.filter(elder => {
    if (currentFilter !== 'all' && elder.status !== currentFilter) return false;
    if (searchTerm && !elder.name.toLowerCase().includes(searchTerm.toLowerCase())) return false;
    return true;
  });

  const counts = {
    total: elderlyList.length,
    normal: elderlyList.filter(e => e.status === 'normal').length,
    warning: elderlyList.filter(e => e.status === 'warning').length,
    critical: elderlyList.filter(e => e.status === 'critical').length,
  };

  const FilterButton = ({ type, label, count }: { type: string; label: string; count: number }) => {
    const isActive = currentFilter === type;
    let cls = 'px-4 py-2 rounded-xl text-sm font-bold ';
    if (type === 'all') cls += isActive ? 'bg-blue-600 text-white' : 'bg-white text-slate-600 border border-slate-200 hover:border-blue-300';
    else if (type === 'normal') cls += isActive ? 'bg-green-100 text-green-700 border border-green-300' : 'bg-white text-slate-600 border border-slate-200 hover:border-blue-300';
    else if (type === 'warning') cls += isActive ? 'bg-yellow-100 text-yellow-700 border border-yellow-300' : 'bg-yellow-50 text-yellow-600 border border-yellow-200';
    else cls += isActive ? 'bg-red-100 text-red-700 border border-red-300' : 'bg-red-50 text-red-600 border border-red-200';
    return (
      <button className={cls} onClick={() => setCurrentFilter(type)}>
        {label} ({count})
      </button>
    );
  };

  const ElderCard = ({ elder }: { elder: ElderData }) => {
    const genderIcon = elder.gender === '女' ? '👵' : '👴';
    let statusClass = 'bg-green-100 text-green-600';
    let statusText = '正常';
    let borderClass = 'border-slate-100';
    let pulseHtml = null;
    let iconBg = 'bg-blue-100';

    if (elder.status === 'warning') {
      statusClass = 'bg-yellow-100 text-yellow-600'; statusText = '轻微异常'; borderClass = 'border-yellow-300 border-2'; iconBg = 'bg-yellow-100';
      pulseHtml = <span className="w-3 h-3 bg-yellow-400 rounded-full animate-pulse absolute top-4 right-4"></span>;
    } else if (elder.status === 'critical') {
      statusClass = 'bg-red-100 text-red-600'; statusText = '严重异常'; borderClass = 'border-red-400 border-2'; iconBg = 'bg-red-100';
      pulseHtml = <span className="w-3 h-3 bg-red-500 rounded-full animate-pulse absolute top-4 right-4"></span>;
    }

    return (
      <div
        className={`bg-white rounded-2xl shadow-sm p-6 hover:shadow-md transition-shadow cursor-pointer ${borderClass}`}
        style={{ position: pulseHtml ? 'relative' : undefined }}
        onClick={() => { sessionStorage.setItem('activeElderId', String(elder.id)); navigate(`/doctor/elder/${elder.id}`); }}
      >
        {pulseHtml}
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center gap-3">
            <div className={`w-12 h-12 ${iconBg} rounded-full flex items-center justify-center`}>
              <span>{genderIcon}</span>
            </div>
            <div>
              <h3 className="font-bold text-slate-800">{elder.name}</h3>
              <p className="text-xs text-slate-500">{elder.age}岁 / {elder.gender}</p>
            </div>
          </div>
          <span className={`px-2 py-1 ${statusClass} text-[10px] font-bold rounded-full`}>{statusText}</span>
        </div>
        <div className="space-y-2 text-sm">
          <div className="flex justify-between text-slate-500">
            <span>心率</span>
            <span className="text-slate-800 font-medium">{elder.heartRate || '-'}{elder.heartRate ? ' bpm' : ''}</span>
          </div>
          <div className="flex justify-between text-slate-500">
            <span>血压</span>
            <span className="text-slate-800 font-medium">{elder.systolic || '-'}/{elder.diastolic || '-'} mmHg</span>
          </div>
          <div className="flex justify-between text-slate-500">
            <span>身高/体重</span>
            <span className="text-slate-800 font-medium">{elder.height || '-'}/{elder.weight || '-'}</span>
          </div>
        </div>
        <div className="mt-4 pt-4 border-t border-slate-100 flex items-center justify-between">

          <button className="text-xs text-blue-600 font-bold hover:underline" onClick={(e) => { e.stopPropagation(); sessionStorage.setItem('activeElderId', String(elder.id)); navigate(`/doctor/elder/${elder.id}`); }}>查看详情</button>
        </div>
      </div>
    );
  };

  return (
    <>
      <header className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">我的监护老人</h1>
          <p className="text-slate-500 mt-1">今天是 {new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })}</p>
        </div>
        <div className="relative">
          <input
            className="w-72 pl-10 pr-4 py-2 rounded-xl border border-slate-200 focus:ring-2 focus:ring-[#1e40af] outline-none"
            placeholder="搜索老人姓名..."
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
          />
          <span className="absolute left-3 top-1/2 -translate-y-1/2">🔍</span>
        </div>
      </header>

      <div className="flex gap-2 mb-6">
        <FilterButton type="all" label="全部" count={counts.total} />
        <FilterButton type="normal" label="正常" count={counts.normal} />
        <FilterButton type="warning" label="轻微异常" count={counts.warning} />
        <FilterButton type="critical" label="严重异常" count={counts.critical} />
      </div>

      <div className="grid grid-cols-3 gap-6">
        {loading ? (
          <div className="col-span-3 flex items-center justify-center py-12">
            <div className="text-center">
              <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
              <p className="text-slate-500">加载中...</p>
            </div>
          </div>
        ) : filteredList.length === 0 ? (
          <div className="col-span-3 flex flex-col items-center justify-center py-12">
            <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mb-4">
              <span className="text-3xl">📭</span>
            </div>
            <p className="text-slate-500 mb-2">
              {currentFilter === 'all' ? (elderlyList.length === 0 ? '暂未签约老人' : '暂无老人数据') :
                `暂无${currentFilter === 'normal' ? '正常' : currentFilter === 'warning' ? '轻微异常' : '严重异常'}状态的老人`}
            </p>
            <p className="text-xs text-slate-400">切换其他筛选条件查看</p>
          </div>
        ) : (
          filteredList.map(elder => <ElderCard key={elder.id} elder={elder} />)
        )}
      </div>

      {/* 警告弹窗（严重异常老人） */}
      {showWarningModal && warningElder && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" onClick={e => { if (e.target === e.currentTarget) setShowWarningModal(false); }}>
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 overflow-hidden" onClick={e => e.stopPropagation()}>
            <div className="p-5 bg-gradient-to-r from-red-500 to-orange-500">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center backdrop-blur-sm"><span className="text-2xl">🚨</span></div>
                <div><h3 className="font-bold text-white text-lg">⚠️ 异常警告通知</h3><p className="text-xs text-red-100">系统检测到异常</p></div>
              </div>
            </div>
            <div className="p-5 space-y-4">
              <div className="flex items-center gap-3 p-3 bg-red-50 rounded-xl">
                <div className="w-12 h-12 bg-red-100 rounded-full flex items-center justify-center shrink-0"><span>👤</span></div>
                <div>
                  <p className="font-bold text-slate-800">{warningElder.name}</p>
                  <p className="text-xs text-slate-500">{warningElder.age}岁 / {warningElder.gender}</p>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div className="p-3 bg-slate-50 rounded-xl"><p className="text-xs text-slate-500">家庭住址</p><p className="font-bold text-sm text-slate-800">{warningElder.address || '康乐园小区 A3-102'}</p></div>
                <div className="p-3 bg-red-50 rounded-xl"><p className="text-xs text-slate-500">异常类型</p><p className="font-bold text-sm text-red-600">{warningElder.alertType || '疑似跌倒 + 血压危急'}</p></div>
                <div className="p-3 bg-slate-50 rounded-xl"><p className="text-xs text-slate-500">检测时间</p><p className="font-bold text-sm text-slate-800">{new Date().toLocaleString('zh-CN')}</p></div>
                <div className="p-3 bg-slate-50 rounded-xl"><p className="text-xs text-slate-500">置信度</p><p className="font-bold text-sm text-orange-600">0.968</p></div>
              </div>
              <div className="flex gap-3">
                <button className="flex-1 py-3 bg-red-600 text-white rounded-xl font-bold hover:bg-red-700 transition-colors flex items-center justify-center gap-2"
                  onClick={() => { setShowWarningModal(false); sessionStorage.setItem('activeElderId', String(warningElder.id)); navigate(`/doctor/elder/${warningElder.id}?tab=emergency`); }}>
                  <span>🧭</span>确认前往
                </button>
                <button className="flex-1 py-3 bg-slate-100 text-slate-600 rounded-xl font-bold hover:bg-slate-200 transition-colors"
                  onClick={() => setShowWarningModal(false)}>稍后处理</button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 家属通知Agent弹窗 */}
      {showNotificationModal && notificationElder && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" onClick={e => { if (e.target === e.currentTarget) setShowNotificationModal(false); }}>
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 overflow-hidden" onClick={e => e.stopPropagation()}>
            <div className="p-5 bg-gradient-to-r from-blue-500 to-indigo-600">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center backdrop-blur-sm"><span className="text-2xl">🤖</span></div>
                <div><h3 className="font-bold text-white text-lg">家属通知</h3></div>
              </div>
            </div>
            <div className="p-5 space-y-4">
              {notificationElder.alertLevel === 'danger' || notificationElder.alertLevel === 'critical' ? (
                <div className="flex items-start gap-3 p-3 bg-red-50 rounded-xl">
                  <span className="w-8 h-8 bg-red-100 rounded-lg flex items-center justify-center shrink-0">⚠️</span>
                  <div><p className="text-sm font-bold text-red-700">高危异常检测</p><p className="text-xs text-slate-600">系统检测到{notificationElder.name}出现高危异常，疑似跌倒 + 血压危急，已启动应急响应</p></div>
                </div>
              ) : notificationElder.alertLevel === 'warning' ? (
                <div className="flex items-start gap-3 p-3 bg-yellow-50 rounded-xl">
                  <span className="w-8 h-8 bg-yellow-100 rounded-lg flex items-center justify-center shrink-0">💓</span>
                  <div><p className="text-sm font-bold text-yellow-700">健康指标异常</p><p className="text-xs text-slate-600">检测到{notificationElder.name}健康指标异常，已通知家属关注</p></div>
                </div>
              ) : null}
              <div className="flex items-center gap-2 mb-2">
                <span className="w-5 h-5 bg-green-100 rounded-full flex items-center justify-center"><span className="text-green-600 text-xs">✓</span></span>
                <span className="text-sm font-bold text-green-700">通知已发送至家属</span>
              </div>
              <div className="flex items-center justify-between p-3 bg-green-50 rounded-xl border border-green-100">
                <div className="flex items-center gap-3">
                  <div className="w-9 h-9 bg-green-100 rounded-full flex items-center justify-center"><span>👤</span></div>
                  <div><p className="font-bold text-slate-800 text-sm">王小强</p><p className="text-xs text-slate-500">孙子 · 家属</p></div>
                </div>
                <div className="text-right"><p className="font-bold text-green-600 text-sm">✓ 已通知</p><p className="text-xs text-slate-400">13700000003</p></div>
              </div>
            </div>
            <div className="px-5 pb-5">
              <button className="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors" onClick={() => setShowNotificationModal(false)}>我知道了</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
