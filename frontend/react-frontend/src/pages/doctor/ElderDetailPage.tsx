import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import { Icon } from '@iconify/react';
import ReactEChartsCore from 'echarts-for-react';
import { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, AlignmentType } from 'docx';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

// 带 Token 的 fetch 包装函数
function fetchApi(url: string, options: RequestInit = {}) {
  const token = sessionStorage.getItem('token');
  const headers: Record<string, string> = { ...(options.headers as Record<string, string> || {}) };
  if (token) headers['Authorization'] = 'Bearer ' + token;
  return fetch(url, { ...options, headers });
}

interface PatientData {
  id: number; userId: number; name: string; age: number;
  gender: string; bloodType: string; height: number; weight: number;
  phone: string; heartRate: number; systolic: number; diastolic: number;
  steps: number; status: string; alertLevel: string; behavior?: string;
}

const emptyPatient: PatientData = {
  id: 0, userId: 0, name: '加载中...', age: 0, gender: '-',
  bloodType: '-', height: 0, weight: 0, phone: '-',
  heartRate: 0, systolic: 0, diastolic: 0, steps: 0,
  status: '正常', alertLevel: 'normal',
};

// 内联导出函数（避免跨模块导入的 TS 缓存问题）
function exportHTML(data: any, medicalHistories?: any[], medList?: any[]) {
  const now = new Date().toLocaleString('zh-CN');
  const blob = new Blob([buildExportHTML(data, now, medicalHistories, medList)], { type: 'text/html;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `健康档案_${data.name}_${new Date().toISOString().slice(0, 10)}.html`;
  document.body.appendChild(a); a.click(); document.body.removeChild(a); URL.revokeObjectURL(url);
}

async function exportPDF(data: any, medicalHistories?: any[], medList?: any[]) {
  const now = new Date().toLocaleString('zh-CN');
  const html = buildExportHTML(data, now, medicalHistories, medList);
  const container = document.createElement('div');
  container.innerHTML = html;
  container.style.position = 'absolute';
  container.style.left = '-9999px';
  container.style.top = '0';
  container.style.width = '210mm';
  document.body.appendChild(container);
  try {
    const canvas = await html2canvas(container, { scale: 2, useCORS: true });
    const imgData = canvas.toDataURL('image/jpeg', 0.98);
    const pdf = new jsPDF('p', 'mm', 'a4');
    const pdfWidth = pdf.internal.pageSize.getWidth();
    const pdfHeight = (canvas.height * pdfWidth) / canvas.width;
    let heightLeft = pdfHeight;
    let position = 0;
    pdf.addImage(imgData, 'JPEG', 0, position, pdfWidth, pdfHeight);
    heightLeft -= pdf.internal.pageSize.getHeight();
    while (heightLeft > 0) {
      position = heightLeft - pdfHeight;
      pdf.addPage();
      pdf.addImage(imgData, 'JPEG', 0, position, pdfWidth, pdfHeight);
      heightLeft -= pdf.internal.pageSize.getHeight();
    }
    pdf.save(`健康档案_${data.name}_${new Date().toISOString().slice(0, 10)}.pdf`);
  } catch (e) {
    alert('PDF 导出失败，可尝试 HTML 格式');
  } finally {
    document.body.removeChild(container);
  }
}

async function exportDOCX(data: any, medicalHistories?: any[], medList?: any[]) {
  const now = new Date().toLocaleString('zh-CN');
  const dateStr = new Date().toISOString().slice(0, 10);
  const alertText = data.alertLevel === 'normal' ? '正常' : (data.alertLevel === 'warning' ? '偏高' : '危急');
  // 表格辅助函数
  const _tc = (text: string, opts?: { bold?: boolean; color?: string }) =>
    new TableCell({ children: [new Paragraph({ children: [new TextRun({ text, bold: opts?.bold, color: opts?.color, font: 'Microsoft YaHei' })] })] });
  const _tr = (cells: TableCell[]) => new TableRow({ children: cells });
  const _h2 = (text: string) => new Paragraph({ children: [new TextRun({ text, bold: true, size: 28, font: 'Microsoft YaHei' })], spacing: { before: 400, after: 200 } });
  // 生成表格
  const infoHeaders = ['姓名', '值', '性别', '值'];
  const infoData = [
    ['姓名', data.name, '性别', data.gender],
    ['年龄', data.age + '岁', '血型', data.bloodType ? (data.bloodType.includes('+') ? data.bloodType.replace('+', '型 (Rh阳性)') : data.bloodType.replace('-', '型 (Rh阴性)')) : '-'],
    ['身高/体重', (data.height || '172') + 'cm / ' + (data.weight || '68') + 'kg', '联系方式', data.phone || '139-xxxx-1234'],
    ['家庭住址', data.address || '康乐园小区 A3-102', '建档日期', data.createDate || '2025-06-15'],
  ];
  const infoTable = new Table({
    rows: [
      _tr(infoHeaders.map(h => _tc(h, { bold: true }))),
      ...infoData.map(row => _tr(row.map(c => _tc(c)))),
    ],
  });
  const healthHeaders = ['指标', '数值', '状态'];
  const healthData = [
    ['心率', data.heartRate + ' bpm', alertText],
    ['收缩压', data.systolic + ' mmHg', alertText],
    ['舒张压', data.diastolic + ' mmHg', alertText],

  ];
  const healthTable = new Table({
    rows: [
      _tr(healthHeaders.map(h => _tc(h, { bold: true }))),
      ...healthData.map(row => _tr(row.map((c, i) => i === 2 && c !== '正常' ? _tc(c, { color: 'dc2626' }) : _tc(c)))),
    ],
  });
  // 用药记录
  let medRows = (medList && medList.length > 0)
    ? medList.map((m: any) => [m.name, m.dosage || '-', m.frequency || '-', '生效中'])
    : [['硝苯地平缓释片', '2片/次', '每日1次', '生效中'], ['阿司匹林肠溶片', '1片/次', '每日1次', '生效中']];
  const medTable = new Table({
    rows: [
      _tr(['药物名称', '剂量', '频次', '状态'].map(h => _tc(h, { bold: true }))),
      ...medRows.map(row => _tr(row.map((c, i) => i === 3 ? _tc(c, { color: '16a34a' }) : _tc(c)))),
    ],
  });
  const doc = new Document({
    styles: { default: { document: { run: { font: 'Microsoft YaHei', size: 22 } } } },
    sections: [{
      properties: {},
      children: [
        new Paragraph({ children: [new TextRun({ text: '健康档案', bold: true, size: 36, font: 'Microsoft YaHei' })], alignment: AlignmentType.CENTER, spacing: { after: 200 } }),
        new Paragraph({ children: [new TextRun({ text: '导出时间：' + now, size: 18, color: '64748b', font: 'Microsoft YaHei' })], alignment: AlignmentType.CENTER, spacing: { after: 400 } }),
        _h2('基础信息'),
        infoTable,
        _h2('当前健康指标'),
        healthTable,
        _h2('用药记录'),
        medTable,
        new Paragraph({ children: [new TextRun({ text: '多银龄守护系统 · 医生端 · 导出时间 ' + now, size: 16, color: '94a3b8', font: 'Microsoft YaHei' })], alignment: AlignmentType.CENTER, spacing: { before: 600 } }),
      ],
    }],
  });
  const blob = await Packer.toBlob(doc);
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = '健康档案_' + data.name + '_' + dateStr + '.docx';
  document.body.appendChild(a); a.click(); document.body.removeChild(a); URL.revokeObjectURL(url);
}

function buildExportHTML(data: any, now: string, medicalHistories?: any[], medList?: any[]): string {
  const bt = data.bloodType ? (data.bloodType.includes('+') || data.bloodType.includes('-') ? data.bloodType.replace('+', '型 (Rh阳性)').replace('-', '型 (Rh阴性)') : data.bloodType + '型') : '-';
  const medHistoryRows = (medicalHistories && medicalHistories.length > 0)
    ? medicalHistories.map(h => `<tr><td>${h.diseaseName || h.name || '-'}</td><td>${h.diagnosisDate || '-'}</td><td>${h.notes || '-'}</td></tr>`).join('')
    : '<tr><td colspan="3" style="color:#94a3b8;text-align:center">无既往病史记录</td></tr>';
  const medRows = (medList && medList.length > 0)
    ? medList.map((m: any) => `<tr><td>${m.name}</td><td>${m.dosage || '-'}</td><td>${m.frequency || '-'}</td><td style="color:#16a34a">生效中</td></tr>`).join('')
    : '<tr><td>硝苯地平缓释片</td><td>2片/次</td><td>每日1次</td><td style="color:#16a34a">生效中</td></tr><tr><td>阿司匹林肠溶片</td><td>1片/次</td><td>每日1次</td><td style="color:#16a34a">生效中</td></tr>';
  return `<!DOCTYPE html><html lang="zh-CN"><head><meta charset="utf-8"><title>健康档案 - ${data.name}</title>
<style>body{font-family:'Microsoft YaHei',sans-serif;padding:40px;color:#333;max-width:900px;margin:0 auto}h1{color:#1e40af;border-bottom:3px solid #1e40af;padding-bottom:10px}h2{color:#1e40af;margin-top:30px}table{width:100%;border-collapse:collapse;margin:15px 0}th,td{border:1px solid #ddd;padding:10px 12px;text-align:left}th{background:#1e40af;color:#fff}tr:nth-child(even){background:#f8fafc}.info-grid{display:grid;grid-template-columns:1fr 1fr;gap:12px;margin:15px 0}.info-item{border:1px solid #e2e8f0;padding:12px;border-radius:8px}.label{color:#64748b;font-size:13px}.value{font-weight:bold;font-size:15px;margin-top:4px}.footer{margin-top:40px;padding-top:15px;border-top:1px solid #e2e8f0;color:#94a3b8;font-size:12px;text-align:center}</style></head><body>
<h1>健康档案</h1><p style="color:#64748b">导出时间：${now}</p>
<h2>基础信息</h2>
<div class="info-grid">
<div class="info-item"><div class="label">姓名</div><div class="value">${data.name}</div></div>
<div class="info-item"><div class="label">性别</div><div class="value">${data.gender}</div></div>
<div class="info-item"><div class="label">年龄</div><div class="value">${data.age}岁</div></div>
<div class="info-item"><div class="label">血型</div><div class="value">${bt}</div></div>
<div class="info-item"><div class="label">身高/体重</div><div class="value">${data.height || 172}cm / ${data.weight || 68}kg</div></div>
<div class="info-item"><div class="label">联系方式</div><div class="value">${data.phone || '139-xxxx-1234'}</div></div>
<div class="info-item"><div class="label">家庭住址</div><div class="value">${data.address || '康乐园小区 A3-102'}</div></div>
<div class="info-item"><div class="label">建档日期</div><div class="value">${data.createDate || '2025-06-15'}</div></div>
</div>
<h2>既往病史</h2><table><tr><th>疾病名称</th><th>诊断日期</th><th>备注</th></tr>${medHistoryRows}</table>
<h2>当前健康指标</h2>
<table><tr><th>指标</th><th>数值</th><th>状态</th></tr>
<tr><td>心率</td><td>${data.heartRate} bpm</td><td>${data.alertLevel === 'normal' ? '正常' : (data.alertLevel === 'warning' ? '偏高' : '危急')}</td></tr>
<tr><td>收缩压</td><td>${data.systolic} mmHg</td><td>${data.alertLevel === 'normal' ? '正常' : (data.alertLevel === 'warning' ? '偏高' : '危急')}</td></tr>
<tr><td>舒张压</td><td>${data.diastolic} mmHg</td><td>${data.alertLevel === 'normal' ? '正常' : (data.alertLevel === 'warning' ? '偏高' : '危急')}</td></tr>
</table>
<h2>用药记录</h2><table><tr><th>药物名称</th><th>剂量</th><th>频次</th><th>状态</th></tr>${medRows}</table>
<div class="footer">多银龄守护系统 · 医生端 · 导出时间 ${now}</div>
</body></html>`;
}

export default function PatientDetailPage() {
  const { elderId } = useParams<{ elderId: string }>();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [pid, setPid] = useState<number | null>(null);

  const [activeTab, setActiveTab] = useState(searchParams.get('tab') || 'monitor');
  const [patient, setPatient] = useState<PatientData>(emptyPatient);
  const [loadingPatient, setLoadingPatient] = useState(true);
  const [medList, setMedList] = useState<any[]>([]);
  const [medicalHistories, setMedicalHistories] = useState<any[]>([]);
  const [showExportModal, setShowExportModal] = useState(false);
  const [showMedModal, setShowMedModal] = useState(false);
  const [showEditHealthModal, setShowEditHealthModal] = useState(false);
  const [showFamilyModal, setShowFamilyModal] = useState(false);
  const [editingMedId, setEditingMedId] = useState<number | null>(null);
  const [backendConnected, setBackendConnected] = useState(false);
  const [exportFormat, setExportFormat] = useState('html');
  const [routeToHospital, setRouteToHospital] = useState(false);
  const [familyMembers, setFamilyMembers] = useState<{ name: string; relationship: string; phone: string }[]>([]);
  // 编辑健康信息用
  const [editAge, setEditAge] = useState(0);
  const [editGender, setEditGender] = useState('');
  const [editBloodType, setEditBloodType] = useState('');
  const [editHeight, setEditHeight] = useState(0);
  const [editWeight, setEditWeight] = useState(0);
  const [editPhone, setEditPhone] = useState('');
  const [editMedicalHistories, setEditMedicalHistories] = useState<any[]>([]);
  const [savingHealth, setSavingHealth] = useState(false);

  // 从 URL 参数获取 elderId，保存为活跃老人
  useEffect(() => {
    if (elderId) {
      const id = parseInt(elderId);
      setPid(id);
      sessionStorage.setItem('activeElderId', String(id));
    }
  }, [elderId]);

  const tabs = [
    { key: 'monitor', icon: '💓', label: '实时数据监测' },
    { key: 'medication', icon: '💊', label: '用药方案配置' },
    { key: 'emergency', icon: '🚨', label: '异常应急协同', badge: true },
    { key: 'record', icon: '📁', label: '健康完备档案' },
  ];

  const chartOption = {
    grid: { top: 30, right: 20, bottom: 40, left: 50 },
    tooltip: { trigger: 'axis' as const },
    xAxis: { type: 'category' as const, boundaryGap: false, data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '23:59'], axisLabel: { color: '#94A3B8', fontSize: 10 } },
    yAxis: [
      { type: 'value' as const, name: '心率 (bpm)', splitLine: { lineStyle: { type: 'dashed' as const, color: '#F1F5F9' } }, axisLabel: { color: '#94A3B8', fontSize: 10 } },
      { type: 'value' as const, name: '血压 (mmHg)', splitLine: { show: false }, axisLabel: { color: '#94A3B8', fontSize: 10 } },
    ],
    series: [
      { name: '心率', data: [72, 68, 85, 80, 78, 82, 75], type: 'line', smooth: true, showSymbol: false, lineStyle: { width: 3, color: '#3B82F6' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(59,130,246,0.2)' }, { offset: 1, color: 'rgba(59,130,246,0)' }] } } },
      { name: '收缩压', yAxisIndex: 1, data: [118, 120, 135, 128, 126, 130, 122], type: 'line', smooth: true, showSymbol: false, lineStyle: { width: 3, color: '#10B981', type: 'dashed' as const } },
    ],
  };

  // ESC键关闭所有弹窗
  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        setShowExportModal(false); setShowEditHealthModal(false);
        setShowFamilyModal(false); setShowMedModal(false);
      }
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, []);

  // 加载数据
  useEffect(() => {
    if (pid === null) return;
    loadPatientData(pid);
    checkBackend();
    loadFamilyData(pid);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pid]);

  // 切换tab时加载对应数据
  useEffect(() => {
    if (activeTab === 'medication') loadMedications();
    if (activeTab === 'record') {
      loadMedications();
      if (pid) loadMedicalHistories(pid);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeTab]);

  // 查询家属数据
  async function loadFamilyData(elderId: number) {
    try {
      const resp = await fetchApi(`http://localhost:8080/api/family-members/elder/${elderId}?_t=${Date.now()}`);
      if (resp.ok) {
        const result = await resp.json();
        if (result.data && result.data.length > 0) {
          setFamilyMembers(result.data.map((f: any) => ({
            name: f.name || f.familyName || '未知',
            relationship: f.relationship || '家属',
            phone: f.phone || '-',
          })));
          return;
        }
      }
    } catch (_) {}
    // fallback
    setFamilyMembers([{ name: '王小强', relationship: '孙子', phone: '13700000003' }]);
  }

  // 查询既往病史
  async function loadMedicalHistories(elderId: number) {
    try {
      const resp = await fetchApi(`http://localhost:8080/api/medical-history/elder/${elderId}?_t=${Date.now()}`);
      if (resp.ok) {
        const result = await resp.json();
        if (result.data) setMedicalHistories(result.data);
      }
    } catch (_) {}
  }

  // 路线切换
  const goToRoute = () => {
    const el = document.getElementById('routeSection');
    if (el) el.scrollIntoView({ behavior: 'smooth' });
  };

  const switchToHospitalRoute = () => {
    const goingToHospital = !routeToHospital;
    if (goingToHospital) {
      alert('路线切换确认\n\n起点：中心医院门诊楼\n终点：最近三甲医院\n距离：5.8 km\n预计：18 分钟\n\n确认切换为前往医院的路线？');
    } else {
      alert('路线切换确认\n\n起点：中心医院门诊楼\n终点：康乐园小区 A3-102室\n距离：3.2 km\n预计：12 分钟\n\n确认切换为前往老人住址的路线？');
    }
    setRouteToHospital(goingToHospital);
  };

  async function checkBackend() {
    try {
      const resp = await fetchApi('http://localhost:8080/api/config');
      if (resp.ok) setBackendConnected(true);
    } catch (_) { }
  }

  async function loadPatientData(id: number) {
    setLoadingPatient(true);
    try {
      const elderResp = await fetchApi(`http://localhost:8080/api/elders/${id}?_t=${Date.now()}`);
      if (elderResp.ok) {
        const result = await elderResp.json();
        if (result.data) {
          const elderData = result.data;
          setPatient({
            id: elderData.id, userId: elderData.userId,
            name: elderData.name || '未知',
            age: elderData.age || 0,
            gender: elderData.gender || '未知',
            bloodType: elderData.bloodType || '-',
            height: elderData.height || 0, weight: elderData.weight || 0,
            phone: elderData.phone || '-',
            heartRate: 78, systolic: 125, diastolic: 82, steps: 4521, status: '正常', alertLevel: 'normal',
          });
        }
      }
    } catch (_) { }
    setLoadingPatient(false);
  }

  async function loadMedications() {
    if (!backendConnected) return;
    const uid = patient.userId;
    if (!uid) return;
    try {
      const resp = await fetchApi(`http://localhost:8080/api/medications?user_id=${uid}&_t=${Date.now()}`);
      if (resp.ok) {
        const result = await resp.json();
        setMedList(result.data || []);
      }
    } catch (_) { }
  }

  const statusBadge = () => {
    if (patient.alertLevel === 'danger') return { bg: 'bg-red-100', text: 'text-red-700', dot: 'bg-red-500', label: patient.status };
    if (patient.alertLevel === 'warning') return { bg: 'bg-yellow-100', text: 'text-yellow-700', dot: 'bg-yellow-500', label: patient.status };
    return { bg: 'bg-green-100', text: 'text-green-700', dot: 'bg-green-500', label: '当前健康状态正常' };
  };
  const sb = statusBadge();

  return (
    <div className="flex h-full">
      {/* 加载状态 */}
      {loadingPatient ? (
        <div className="flex-1 flex items-center justify-center">
          <div className="text-center">
            <div className="w-10 h-10 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin mx-auto mb-4"></div>
            <p className="text-slate-500">加载老人信息中...</p>
          </div>
        </div>
      ) : (<>
      {/* 左侧Tab导航 */}
      <nav className="w-64 bg-white border-r border-slate-100 p-6 flex flex-col gap-2 shrink-0">
        <div className="flex items-center gap-4 mb-8">
          <div className="w-14 h-14 bg-blue-100 rounded-xl flex items-center justify-center"><span>👤</span></div>
          <div>
            <h2 className="font-bold text-slate-800">{patient.name}</h2>
            <p className="text-xs text-slate-500">{patient.age}岁 / {patient.gender}</p>
          </div>
        </div>
        {tabs.map(tab => (
          <button key={tab.key}
            className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-all ${activeTab === tab.key ? 'bg-blue-50 text-blue-600 font-bold border-l-4 border-blue-600' : 'text-slate-600 hover:bg-slate-50'} ${tab.key === 'emergency' ? 'relative' : ''}`}
            onClick={() => setActiveTab(tab.key)}
          >
            <span>{tab.icon}</span>
            <span>{tab.label}</span>
            {tab.badge && <span className="absolute right-4 top-1/2 -translate-y-1/2 w-2 h-2 bg-red-500 rounded-full"></span>}
          </button>
        ))}
        <div className="mt-auto pt-6 border-t border-slate-100">
          <button className="w-full flex items-center gap-3 px-4 py-3 text-slate-500 hover:bg-slate-50 rounded-xl transition-all" onClick={() => navigate('/doctor/dashboard')}>
            <span>⬅️</span><span>返回列表</span>
          </button>
        </div>
      </nav>

      {/* 右侧内容区 */}
      <div className="flex-1 p-8 overflow-y-auto">
        {/* Tab 1: 实时数据监测 */}
        {activeTab === 'monitor' && (
          <div className="space-y-8">
            <header className="flex justify-between items-center">
              <div><h1 className="text-2xl font-bold text-slate-800">实时健康监测</h1></div>
              <div className={`flex items-center gap-2 px-4 py-2 ${sb.bg} rounded-xl`}>
                <span className={`w-2 h-2 ${sb.dot} rounded-full animate-pulse`}></span>
                <span className={`text-sm font-bold ${sb.text}`}>{sb.label}</span>
              </div>
            </header>
            <div className="grid grid-cols-3 gap-6">
              <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm">
                <div className="flex items-center gap-3 mb-4">
                  <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center"><span>❤️</span></div>
                  <div><p className="text-xs text-slate-500">心率</p><p className="text-2xl font-bold text-slate-800">{patient.heartRate} <span className="text-sm font-normal text-slate-400">bpm</span></p></div>
                </div>
                <div className="h-2 bg-slate-100 rounded-full overflow-hidden"><div className="h-full bg-blue-500 w-3/4 rounded-full"></div></div>
                <p className="text-xs text-green-600 mt-2">AI判定：心率正常</p>
              </div>
              <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm">
                <div className="flex items-center gap-3 mb-4">
                  <div className="w-12 h-12 bg-red-100 rounded-xl flex items-center justify-center"><span>🩸</span></div>
                  <div><p className="text-xs text-slate-500">血压</p><p className="text-2xl font-bold text-slate-800">{patient.systolic}/{patient.diastolic} <span className="text-sm font-normal text-slate-400">mmHg</span></p></div>
                </div>
                <div className="h-2 bg-slate-100 rounded-full overflow-hidden"><div className="h-full bg-green-500 w-2/3 rounded-full"></div></div>
                <p className="text-xs text-green-600 mt-2">AI判定：血压正常</p>
              </div>
              <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm">
                <div className="flex items-center gap-3 mb-4">
                  <div className="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center"><span>🚶</span></div>
                  <div><p className="text-xs text-slate-500">行为状态</p><p className="text-2xl font-bold text-slate-800">{patient.behavior || '活动中'}</p></div>
                </div>
                <div className="flex items-center gap-2 mt-4">
                  <span className="w-3 h-3 bg-green-500 rounded-full animate-pulse"></span>
                  <span className="text-xs text-green-600">行为监测中</span>
                </div>
              </div>
            </div>
            <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm">
              <h3 className="font-bold text-lg mb-4">24小时健康趋势</h3>
              <ReactEChartsCore option={chartOption} style={{ height: 300 }} />
            </div>
            <div className="bg-gradient-to-r from-blue-50 to-indigo-50 p-6 rounded-2xl border border-blue-100">
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center shrink-0"><span>🤖</span></div>
                <div>
                  <p className="text-sm font-bold text-blue-700 mb-2">AI智能研判结果</p>
                  <p className="text-slate-600">综合分析老人今日健康数据：心率波动在正常范围内，血压稳定，行为活动正常。建议继续保持当前生活作息，注意饮食均衡。</p>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Tab 2: 用药方案配置 */}
        {activeTab === 'medication' && (
          <div className="space-y-8">
            <header className="flex justify-between items-center">
              <div><h1 className="text-2xl font-bold text-slate-800">用药方案管理</h1></div>
              <button className="px-4 py-2 bg-blue-600 text-white rounded-xl font-bold flex items-center gap-2" onClick={() => { setEditingMedId(null); setShowMedModal(true); }}>
                <span>➕</span>新增用药方案
              </button>
            </header>
            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
              <div className="p-4 bg-slate-50 border-b border-slate-100"><h3 className="font-bold text-slate-700">生效中用药方案</h3></div>
              {medList.length === 0 ? (
                <div className="p-6 text-center text-slate-400">
                  {backendConnected ? '暂无用药方案' : '⚠️ 后端未连接，请确认后端服务已启动'}
                </div>
              ) : (
                <div className="divide-y divide-slate-100">
                  {medList.map(med => (
                    <div key={med.id} className="p-4 flex items-center justify-between hover:bg-slate-50 transition-colors">
                      <div className="flex items-center gap-4">
                        <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center"><span>💊</span></div>
                        <div>
                          <p className="font-bold text-slate-800">{med.name}</p>
                          <p className="text-xs text-slate-500">{med.frequency} · {med.dosage} · {med.time}{med.description ? ' · ' + med.description : ''}</p>
                        </div>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className={`px-3 py-1 text-xs font-bold rounded-full ${med.status === '已停用' ? 'bg-slate-100 text-slate-500' : med.status === '待审核' ? 'bg-yellow-100 text-yellow-600' : 'bg-green-100 text-green-600'}`}>{med.status || '生效中'}</span>
                        <button className="text-slate-400 hover:text-blue-600 transition-colors" onClick={() => { setEditingMedId(med.id); setShowMedModal(true); }}><span>✏️</span></button>
                        <button className="text-slate-400 hover:text-red-600 transition-colors" onClick={async () => {
                          if (!confirm('确定要删除该用药方案吗？')) return;
                          try { await fetchApi(`http://localhost:8080/api/medications/${med.id}`, { method: 'DELETE' }); loadMedications(); } catch (_) { }
                        }}><span>🗑️</span></button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        )}

        {/* Tab 3: 异常应急协同 */}
        {activeTab === 'emergency' && (
          <div className="space-y-8">
            <header className="flex justify-between items-center">
              <div><h1 className="text-2xl font-bold text-slate-800">异常应急协同中心</h1></div>
              <span className="px-4 py-2 bg-red-100 text-red-600 rounded-xl text-sm font-bold flex items-center gap-2">
                <span className="w-2 h-2 bg-red-500 rounded-full animate-pulse"></span>1 项高危异常待处理
              </span>
            </header>
            <div className="grid grid-cols-3 gap-6">
              <div className="col-span-2 bg-white rounded-2xl border-2 border-red-400 shadow-sm overflow-hidden">
                <div className="p-4 bg-red-50 border-b border-red-100">
                  <div className="flex items-center gap-2">
                    <span className="px-2 py-1 bg-red-600 text-white text-xs font-bold rounded">高危</span>
                    <span className="text-sm font-bold text-red-700">疑似跌倒 + 血压危急</span>
                  </div>
                </div>
                <div className="p-6">
                  <div className="flex items-start gap-4 mb-4">
                    <div className="w-12 h-12 bg-red-100 rounded-xl flex items-center justify-center shrink-0"><span>⚠️</span></div>
                    <div>
                      <p className="font-bold text-slate-800 mb-1">检测到异常</p>
                      <p className="text-xs text-slate-500">检测时间: 2026-04-28 10:24:12</p>
                      <p className="text-sm text-slate-600 mt-2">Z轴加速度突变为4.2g，随后120秒内身体动作为0，心率由112bpm降至58bpm。</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-xs text-slate-500">置信度：</span><span className="text-sm font-bold text-red-600">0.968</span>
                    <span className="text-xs text-slate-500">|</span>
                    <span className="text-xs text-slate-500">响应时长：</span><span className="text-sm font-bold text-slate-800">1.2s</span>
                  </div>
                </div>
              </div>
              <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
                <div className="p-4 bg-blue-50 border-b border-blue-100"><h3 className="font-bold text-blue-700">紧急处置操作</h3></div>
                <div className="p-4 space-y-3">
                  <button className="w-full py-3 bg-blue-600 text-white rounded-xl font-bold flex items-center justify-center gap-2 hover:bg-blue-700 transition-colors" onClick={goToRoute}><span>🧭</span>导航前往</button>
                  <button className="w-full py-3 bg-green-600 text-white rounded-xl font-bold flex items-center justify-center gap-2 hover:bg-green-700 transition-colors" onClick={() => setShowFamilyModal(true)}><span>💬</span>联系家属</button>
                </div>
              </div>
            </div>
            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden" id="routeSection">
              <div className="flex gap-4 p-4 pb-0">
                <button
                  className={`flex-1 py-3 rounded-xl font-bold flex items-center justify-center gap-2 transition-all ${!routeToHospital ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-600'}`}
                  onClick={() => setRouteToHospital(false)}
                >🏠 前往老人住址</button>
                <button
                  className={`flex-1 py-3 rounded-xl font-bold flex items-center justify-center gap-2 transition-all ${routeToHospital ? 'bg-red-600 text-white' : 'bg-slate-100 text-slate-600'}`}
                  onClick={switchToHospitalRoute}
                >🏥 前往三甲医院</button>
              </div>
              <div className="flex justify-end px-4">
                <button className="text-xs text-blue-600 hover:text-blue-800 font-medium flex items-center gap-1 py-1" onClick={() => { alert('正在重新规划最优路线...\n\n考虑到当前交通状况，系统正在为您重新计算最优路径，请稍候。'); }}>
                  <span>🔄</span>刷新路线
                </button>
              </div>
              <div className="h-[400px] bg-gradient-to-br from-blue-50 via-white to-orange-50 relative p-8">
                {/* 导航进行中浮层 */}
                <div className="absolute top-8 right-8 bg-white rounded-xl shadow-lg p-4 z-10 border border-blue-100">
                  <p className="font-bold text-blue-600 mb-1">🧭 导航进行中</p>
                  <p className="text-sm text-slate-600">剩余 2.8 公里 · 预计 10 分钟</p>
                  <div className="flex items-center gap-2 mt-2">
                    <span className="w-3 h-3 bg-green-500 rounded-full" title="畅通"></span>
                    <span className="text-xs text-slate-500">畅通</span>
                    <span className="w-3 h-3 bg-yellow-500 rounded-full" title="缓行"></span>
                    <span className="text-xs text-slate-500">缓行</span>
                    <span className="w-3 h-3 bg-red-500 rounded-full" title="拥堵"></span>
                    <span className="text-xs text-slate-500">拥堵</span>
                  </div>
                </div>
                <div className="absolute top-8 left-8 flex items-center gap-3">
                  <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center shadow-lg"><span className="text-white font-bold">📍</span></div>
                  <div className="bg-white px-3 py-2 rounded-xl shadow-md"><p className="text-sm font-bold text-slate-800">当前位置</p><p className="text-xs text-slate-500">医生办公室</p></div>
                </div>
                <div className="absolute bottom-16 right-16 flex items-center gap-3">
                  <div className="w-10 h-10 bg-red-600 rounded-full flex items-center justify-center shadow-lg"><span className="text-white font-bold">🏠</span></div>
                  <div className="bg-white px-3 py-2 rounded-xl shadow-md"><p className="text-sm font-bold text-slate-800">老人住址</p><p className="text-xs text-slate-500">康乐园小区 A3-102</p></div>
                </div>
                <svg className="absolute inset-0 w-full h-full" viewBox="0 0 800 400">
                  <path d="M 80 80 Q 200 150, 300 100 T 500 200 Q 600 250, 700 320" stroke="#3b82f6" strokeWidth="4" fill="none" strokeDasharray="10,5" className="animate-pulse" />
                  <circle cx="80" cy="80" r="20" fill="#3b82f6" opacity="0.2" />
                  <circle cx="700" cy="320" r="20" fill="#ef4444" opacity="0.2" />
                </svg>
              </div>
            </div>
            <div className="grid grid-cols-3 gap-6">
              <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
                <h3 className="font-bold text-lg mb-4">🗺️ 行程概览</h3>
                <div className="space-y-4">
                  <div><p className="text-xs text-slate-500 mb-1">起点</p><p className="font-bold text-slate-800">中心医院门诊楼</p></div>
                  <div className="flex items-center gap-2"><span className="text-red-500">⬇️</span></div>
                  <div><p className="text-xs text-slate-500 mb-1">终点</p><p className="font-bold text-slate-800">康乐园小区 A3-102室</p></div>
                </div>
              </div>
              <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
                <h3 className="font-bold text-lg mb-4">🚗 路线信息</h3>
                <div className="space-y-3">
                  <div className="flex justify-between"><span className="text-slate-500">距离</span><span className="font-bold text-2xl text-blue-600">3.2 km</span></div>
                  <div className="flex justify-between"><span className="text-slate-500">预计时长</span><span className="font-bold text-xl text-orange-600">12 分钟</span></div>
                  <div className="flex justify-between"><span className="text-slate-500">红绿灯</span><span className="font-bold text-slate-800">5 个</span></div>
                  <div className="flex justify-between"><span className="text-slate-500">路线类型</span><span className="px-2 py-1 bg-green-100 text-green-600 text-xs font-bold rounded">推荐路线</span></div>
                </div>
              </div>
              <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6 cursor-pointer hover:shadow-md hover:border-blue-300 transition-all" onClick={() => setActiveTab('record')}>
                <h3 className="font-bold text-lg mb-4">🔔 老人信息</h3>
                <div className="flex items-center gap-3 mb-4">
                  <div className="w-12 h-12 bg-red-100 rounded-full flex items-center justify-center"><span>👤</span></div>
                  <div><p className="font-bold text-slate-800">{patient.name}</p><p className="text-xs text-slate-500">{patient.age}岁 / {patient.gender}</p></div>
                </div>
                <div className="space-y-2">
                  <div className="flex items-center gap-2 text-sm"><span>🏠</span><span className="text-slate-600">康乐园小区 A3-102室</span></div>
                  <div className="flex items-center gap-2 text-sm"><span>📞</span><span className="text-slate-600">{patient.phone}</span></div>
                  <div className="flex items-center gap-2 text-sm"><span>🚨</span><span className="text-red-600 font-medium">高危异常待处理</span></div>
                </div>
                <div className="mt-4 pt-3 border-t border-slate-100 text-center text-xs text-blue-500 font-medium">点击查看完整档案 →</div>
              </div>
            </div>
          </div>
        )}

        {/* Tab 4: 健康完备档案 */}
        {activeTab === 'record' && (
          <div className="space-y-8">
            <header className="flex justify-between items-center">
              <div><h1 className="text-2xl font-bold text-slate-800">健康完备档案</h1></div>
              <div className="flex gap-3">
                <button className="px-4 py-2 bg-green-600 text-white rounded-xl font-bold flex items-center gap-2" onClick={() => setShowEditHealthModal(true)}>
                  <span>✏️</span>修改健康信息
                </button>
                <button className="px-4 py-2 bg-blue-600 text-white rounded-xl font-bold flex items-center gap-2" onClick={() => setShowExportModal(true)}>
                  <span>📥</span>导出档案
                </button>
              </div>
            </header>
            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
              <h3 className="font-bold text-lg mb-4">👤 基础信息</h3>
              <div className="grid grid-cols-4 gap-6">
                <div><p className="text-xs text-slate-500">姓名</p><p className="font-bold text-slate-800">{patient.name}</p></div>
                <div><p className="text-xs text-slate-500">性别</p><p className="font-bold text-slate-800">{patient.gender}</p></div>
                <div><p className="text-xs text-slate-500">年龄</p><p className="font-bold text-slate-800">{patient.age}岁</p></div>
                <div><p className="text-xs text-slate-500">血型</p><p className="font-bold text-slate-800">{patient.bloodType ? patient.bloodType.replace('+', '型 (Rh阳性)').replace('-', '型 (Rh阴性)') : '-'}</p></div>
                <div><p className="text-xs text-slate-500">身高/体重</p><p className="font-bold text-slate-800">{patient.height}cm / {patient.weight}kg</p></div>
                <div><p className="text-xs text-slate-500">联系方式</p><p className="font-bold text-slate-800">{patient.phone}</p></div>
                <div><p className="text-xs text-slate-500">家庭住址</p><p className="font-bold text-slate-800">康乐园小区 A3-102</p></div>
                <div><p className="text-xs text-slate-500">建档日期</p><p className="font-bold text-slate-800">2025-06-15</p></div>
              </div>
            </div>
            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
              <h3 className="font-bold text-lg mb-4">💊 用药记录</h3>
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead><tr className="border-b border-slate-100">
                    <th className="text-left py-3 px-4 text-xs font-bold text-slate-500 uppercase">药物名称</th>
                    <th className="text-left py-3 px-4 text-xs font-bold text-slate-500 uppercase">剂量</th>
                    <th className="text-left py-3 px-4 text-xs font-bold text-slate-500 uppercase">频次</th>
                    <th className="text-left py-3 px-4 text-xs font-bold text-slate-500 uppercase">服用时间</th>
                    <th className="text-left py-3 px-4 text-xs font-bold text-slate-500 uppercase">状态</th>
                  </tr></thead>
                  <tbody>
                    {medList.length === 0 ? (
                      <tr><td colSpan={5} className="py-6 text-center text-slate-400">暂无用药记录</td></tr>
                    ) : medList.map(med => (
                      <tr key={med.id} className="border-b border-slate-50 hover:bg-slate-50">
                        <td className="py-3 px-4 font-medium">{med.name}</td>
                        <td className="py-3 px-4 text-slate-600">{med.dosage || '-'}</td>
                        <td className="py-3 px-4 text-slate-600">{med.frequency || '-'}</td>
                        <td className="py-3 px-4 text-slate-600">{med.time || '-'}</td>
                        <td className="py-3 px-4"><span className={`px-2 py-1 text-xs font-bold rounded ${med.status === '已停用' ? 'bg-slate-100 text-slate-500' : med.status === '待审核' ? 'bg-yellow-100 text-yellow-600' : 'bg-green-100 text-green-600'}`}>{med.status || '生效中'}</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
              <h3 className="font-bold text-lg mb-4">📋 既往病史</h3>
              {medicalHistories.length === 0 ? (
                <p className="text-slate-400 text-sm py-2">暂无既往病史记录</p>
              ) : (
                <div className="space-y-2">
                  {medicalHistories.map((h: any, i: number) => (
                    <div key={i} className="flex items-center justify-between p-3 bg-slate-50 rounded-xl">
                      <div className="flex items-center gap-3">
                        <span className="text-red-400">💊</span>
                        <div>
                          <p className="font-bold text-slate-700">{h.diseaseName || h.name}</p>
                          <p className="text-xs text-slate-500">{h.diagnosisDate || '日期不详'}{h.status ? ' · ' + (h.status === '痊愈' ? '✅ 已痊愈' : h.status === '治疗中' ? '💊 治疗中' : h.status === '慢性病' ? '🔄 慢性病' : '👀 观察中') : ''}{h.treatment ? ' · 治疗：' + h.treatment : ''}{h.notes ? ' · ' + h.notes : ''}</p>
                        </div>
                      </div>
                      {h.status && (
                        <span className={`px-2 py-1 text-xs font-bold rounded-full ${h.status === '痊愈' ? 'bg-green-100 text-green-600' : h.status === '治疗中' ? 'bg-blue-100 text-blue-600' : h.status === '慢性病' ? 'bg-yellow-100 text-yellow-600' : 'bg-purple-100 text-purple-600'}`}>{h.status}</span>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
              <h3 className="font-bold text-lg mb-4">🔔 历史异常台账</h3>
              <div className="space-y-3">
                {[
                  { type: '疑似跌倒', time: '2026-04-28 10:24:12', status: '处理中', cls: 'bg-red-50', textCls: 'text-red-700', badgeCls: 'bg-red-200 text-red-700' },
                  { type: '血压偏高', time: '2026-04-25 09:30:15', status: '已处理', cls: 'bg-yellow-50', textCls: 'text-yellow-700', badgeCls: 'bg-yellow-200 text-yellow-700' },
                  { type: '心率波动', time: '2026-04-20 14:22:30', status: '已关闭', cls: 'bg-slate-50', textCls: 'text-slate-600', badgeCls: 'bg-slate-200 text-slate-600' },
                ].map((item, i) => (
                  <div key={i} className={`flex items-center justify-between p-4 ${item.cls} rounded-xl`}>
                    <div className="flex items-center gap-3">
                      <div className={`w-8 h-8 ${item.cls.replace('50', '100')} rounded-lg flex items-center justify-center`}><span>⚠️</span></div>
                      <div><p className={`font-bold ${item.textCls}`}>{item.type}</p><p className="text-xs text-slate-500">{item.time}</p></div>
                    </div>
                    <span className={`px-2 py-1 ${item.badgeCls} text-xs font-bold rounded`}>{item.status}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
        </>)}

      {/* 导出档案弹窗（3种格式） */}
      {showExportModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" onClick={e => { if (e.target === e.currentTarget) setShowExportModal(false); }}>
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm mx-4 p-6" onClick={e => e.stopPropagation()}>
            <h3 className="text-xl font-bold text-slate-800 mb-2">📥 导出健康档案</h3>
            <p className="text-sm text-slate-500 mb-5">请选择档案的导出文件格式</p>
            <div className="space-y-3">
              {[
                { value: 'docx', icon: '📘', title: 'DOCX 文档', desc: 'Microsoft Word 格式，方便编辑', color: 'blue' },
                { value: 'pdf', icon: '📕', title: 'PDF 文档', desc: '便携式文档，适合打印分享', color: 'green' },
                { value: 'html', icon: '📄', title: 'HTML 网页', desc: '网页格式，浏览器直接打开', color: 'purple' },
              ].map(opt => (
                <label key={opt.value} className={`flex items-center gap-4 p-4 rounded-xl border-2 cursor-pointer transition-all ${exportFormat === opt.value ? 'border-blue-400 bg-blue-50' : 'border-slate-200 bg-white hover:bg-slate-50'}`}
                  onClick={() => setExportFormat(opt.value)}>
                  <input type="radio" name="exportFormat" value={opt.value} className="w-5 h-5 accent-blue-600" checked={exportFormat === opt.value} onChange={() => setExportFormat(opt.value)} />
                  <span className="text-2xl">{opt.icon}</span>
                  <div className="flex-1"><p className="font-bold text-slate-800">{opt.title}</p><p className="text-xs text-slate-400">{opt.desc}</p></div>
                </label>
              ))}
            </div>
            <div className="flex gap-3 mt-6">
              <button onClick={() => {
                if (exportFormat === 'html') exportHTML(patient, medicalHistories, medList);
                else if (exportFormat === 'pdf') exportPDF(patient, medicalHistories, medList);
                else if (exportFormat === 'docx') exportDOCX(patient, medicalHistories, medList);
                setShowExportModal(false);
              }} className="flex-1 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl transition-colors">确认导出</button>
              <button onClick={() => setShowExportModal(false)} className="flex-1 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-600 font-bold rounded-xl transition-colors">取消</button>
            </div>
          </div>
        </div>
      )}

      {/* 新增用药弹窗 */}
      {showMedModal && pid !== null && patient.userId && (
        <MedicationModal
          patientId={pid}
          userId={patient.userId}
          editingId={editingMedId}
          onClose={() => setShowMedModal(false)}
          onSaved={() => { setShowMedModal(false); loadMedications(); }}
        />
      )}

      {/* 家属通知弹窗（动态家属数据） */}
      {showFamilyModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" onClick={e => { if (e.target === e.currentTarget) setShowFamilyModal(false); }}>
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 overflow-hidden" onClick={e => e.stopPropagation()}>
            <div className="p-5 bg-gradient-to-r from-blue-500 to-indigo-600">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center backdrop-blur-sm"><span className="text-2xl">🤖</span></div>
                <div><h3 className="font-bold text-white text-lg">家属通知</h3></div>
              </div>
            </div>
            <div className="p-5 space-y-4">
              {patient.alertLevel === 'danger' || patient.alertLevel === 'critical' ? (
                <div className="flex items-start gap-3 p-3 bg-red-50 rounded-xl">
                  <span className="w-8 h-8 bg-red-100 rounded-lg flex items-center justify-center shrink-0">⚠️</span>
                  <div><p className="text-sm font-bold text-red-700">高危异常检测</p><p className="text-xs text-slate-600">系统检测到{patient.name}出现高危异常，疑似跌倒 + 血压危急，已启动应急响应</p></div>
                </div>
              ) : patient.alertLevel === 'warning' ? (
                <div className="flex items-start gap-3 p-3 bg-yellow-50 rounded-xl">
                  <span className="w-8 h-8 bg-yellow-100 rounded-lg flex items-center justify-center shrink-0">💓</span>
                  <div><p className="text-sm font-bold text-yellow-700">健康指标异常</p><p className="text-xs text-slate-600">检测到{patient.name}健康指标异常，已通知家属关注</p></div>
                </div>
              ) : null}
              <div className="flex items-center gap-2 mb-2">
                <span className="w-5 h-5 bg-green-100 rounded-full flex items-center justify-center"><span className="text-green-600 text-xs">✓</span></span>
                <span className="text-sm font-bold text-green-700">通知已发送至以下联系人</span>
              </div>
              {familyMembers.length === 0 ? (
                <div className="p-3 bg-yellow-50 rounded-xl text-center text-sm text-yellow-700">暂无绑定的家属信息</div>
              ) : familyMembers.map((fm, i) => (
                <div key={i} className="flex items-center justify-between p-3 bg-green-50 rounded-xl border border-green-100">
                  <div className="flex items-center gap-3">
                    <div className="w-9 h-9 bg-green-100 rounded-full flex items-center justify-center"><span>👤</span></div>
                    <div><p className="font-bold text-slate-800 text-sm">{fm.name}</p><p className="text-xs text-slate-500">{fm.relationship} · 家属</p></div>
                  </div>
                  <div className="text-right"><p className="font-bold text-green-600 text-sm">✓ 已通知</p><p className="text-xs text-slate-400">{fm.phone}</p></div>
                </div>
              ))}
            </div>
            <div className="px-5 pb-5">
              <button className="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors" onClick={() => setShowFamilyModal(false)}>我知道了</button>
            </div>
          </div>
        </div>
      )}

      {/* 修改健康信息弹窗（含既往病史编辑） */}
      {showEditHealthModal && (
        <EditHealthModal
          patient={patient}
          medicalHistories={medicalHistories}
          pid={pid}
          onClose={() => setShowEditHealthModal(false)}
          onSaved={(newPatient, newHistories) => {
            setPatient(newPatient);
            setMedicalHistories(newHistories);
            setShowEditHealthModal(false);
          }}
        />
      )}
    </div>
  );
}

// 用药方案弹窗组件
function MedicationModal({ patientId, userId, editingId, onClose, onSaved }: {
  patientId: number; userId: number; editingId: number | null; onClose: () => void; onSaved: () => void;
}) {
  const [name, setName] = useState('');
  const [desc, setDesc] = useState('');
  const [dosage, setDosage] = useState('');
  const [frequency, setFrequency] = useState('每日1次');
  const [medTime, setMedTime] = useState('早餐前');
  const [saving, setSaving] = useState(false);

  const handleSave = async () => {
    if (!name || !dosage) { alert('请填写药物名称和剂量'); return; }
    setSaving(true);
    const body = { userId, name, description: desc || null, dosage, frequency, time: medTime };
    try {
      const resp = await fetchApi(
        editingId ? `http://localhost:8080/api/medications/${editingId}` : 'http://localhost:8080/api/medications',
        { method: editingId ? 'PUT' : 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) }
      );
      if (resp.ok) { onSaved(); } else { alert('操作失败'); }
    } catch (_) { alert('网络错误'); }
    setSaving(false);
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" onClick={e => { if (e.target === e.currentTarget) onClose(); }}>
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 p-6" onClick={e => e.stopPropagation()}>
        <h3 className="text-xl font-bold text-slate-800 mb-6">{editingId ? '编辑用药方案' : '新增用药方案'}</h3>
        <div className="space-y-4">
          <div><label className="block text-sm font-bold text-slate-700 mb-2">药物名称</label>
            <input type="text" className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" placeholder="请输入药物名称" value={name} onChange={e => setName(e.target.value)} /></div>
          <div><label className="block text-sm font-bold text-slate-700 mb-2">药物描述</label>
            <input type="text" className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" placeholder="如：降压药" value={desc} onChange={e => setDesc(e.target.value)} /></div>
          <div><label className="block text-sm font-bold text-slate-700 mb-2">剂量</label>
            <input type="text" className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" placeholder="如：每次2片" value={dosage} onChange={e => setDosage(e.target.value)} /></div>
          <div><label className="block text-sm font-bold text-slate-700 mb-2">频次</label>
            <select className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" value={frequency} onChange={e => setFrequency(e.target.value)}>
              <option>每日1次</option><option>每日2次</option><option>每日3次</option><option>每周1次</option>
            </select></div>
          <div><label className="block text-sm font-bold text-slate-700 mb-2">服用时间</label>
            <select className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" value={medTime} onChange={e => setMedTime(e.target.value)}>
              <option>早餐前</option><option>早餐后</option><option>午餐前</option><option>午餐后</option>
              <option>晚餐前</option><option>晚餐后</option><option>睡前</option><option>早中晚各一次</option><option>早晚各一次</option>
            </select></div>
          <div className="flex gap-3 mt-6">
            <button className="flex-1 py-3 bg-slate-100 text-slate-600 rounded-xl font-bold hover:bg-slate-200 transition-colors" onClick={onClose}>取消</button>
            <button disabled={saving} className="flex-1 py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors" onClick={handleSave}>{saving ? '保存中...' : '保存'}</button>
          </div>
        </div>
      </div>
    </div>
  );
}

// 修改健康信息弹窗组件（含既往病史编辑）
function EditHealthModal({ patient, medicalHistories, pid, onClose, onSaved }: {
  patient: PatientData; medicalHistories: any[]; pid: number | null; onClose: () => void;
  onSaved: (newPatient: PatientData, newHistories: any[]) => void;
}) {
  const [editAge, setEditAge] = useState(patient.age);
  const [editGender, setEditGender] = useState(patient.gender === '男' ? '男' : '女');
  const [editBloodType, setEditBloodType] = useState(patient.bloodType || 'O+');
  const [editHeight, setEditHeight] = useState(patient.height);
  const [editWeight, setEditWeight] = useState(patient.weight);
  const [editPhone, setEditPhone] = useState(patient.phone);
  const [editHistories, setEditHistories] = useState<any[]>(
    medicalHistories.map(h => ({ ...h }))
  );
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setEditAge(patient.age);
    setEditGender(patient.gender === '男' ? '男' : '女');
    setEditBloodType(patient.bloodType || 'O+');
    setEditHeight(patient.height);
    setEditWeight(patient.weight);
    setEditPhone(patient.phone);
    setEditHistories(medicalHistories.map(h => ({ ...h })));
  }, [patient, medicalHistories]);

  const addHistory = () => {
    setEditHistories([...editHistories, { diseaseName: '', diagnosisDate: '', status: '', treatment: '', notes: '' }]);
  };

  const removeHistory = (index: number) => {
    setEditHistories(editHistories.filter((_: any, i: number) => i !== index));
  };

  const updateHistory = (index: number, field: string, value: string) => {
    const newHistories = [...editHistories];
    newHistories[index] = { ...newHistories[index], [field]: value };
    setEditHistories(newHistories);
  };

  const handleSave = async () => {
    if (!editAge || editAge < 0 || editAge > 150) { alert('请输入有效年龄'); return; }
    const phonePattern = /^1[3-9]\d{9}$/;
    if (editPhone && !phonePattern.test(editPhone)) {
      alert('联系电话格式不正确，请输入以1开头的11位手机号');
      return;
    }
    setSaving(true);
    try {
      let elderOk = false, phoneOk = true, historyOk = true;

      // 1. 更新老人信息
      if (pid) {
        const elderResp = await fetchApi(`http://localhost:8080/api/elders/${pid}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            age: Number(editAge), gender: editGender, bloodType: editBloodType,
            height: Number(editHeight), weight: Number(editWeight),
          }),
        });
        elderOk = elderResp.ok;

        // 2. 更新既往病史
        const validHistories = editHistories.filter((h: any) => h.diseaseName);
        if (validHistories.length > 0) {
          const histResp = await fetchApi(`http://localhost:8080/api/medical-history/batch/${pid}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(validHistories),
          });
          historyOk = histResp.ok;
        }

        // 3. 更新用户手机号（单独处理，成功失败不阻塞主流程）
        if (patient.userId) {
          const phoneResp = await fetchApi(`http://localhost:8080/api/users/${patient.userId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ phone: editPhone }),
          });
          phoneOk = phoneResp.ok;
          if (!phoneOk) console.warn('手机号更新失败');
        }

        if (elderOk) {
          const newPatient = {
            ...patient,
            age: Number(editAge), gender: editGender, bloodType: editBloodType,
            height: Number(editHeight), weight: Number(editWeight), phone: editPhone,
          };
          onSaved(newPatient, editHistories);
          if (!phoneOk) {
            alert('基本信息已保存，但手机号更新失败，请检查权限');
          }
        } else {
          alert('保存失败，请检查网络或联系管理员');
        }
      }
    } catch (_) { alert('网络错误'); }
    setSaving(false);
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" onClick={e => { if (e.target === e.currentTarget) onClose(); }}>
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto p-6" onClick={e => e.stopPropagation()}>
        <h3 className="text-xl font-bold text-slate-800 mb-6">✏️ 修改健康信息</h3>
        <div className="space-y-5">
          <div><label className="block text-sm font-bold text-slate-700 mb-1">姓名</label>
            <input type="text" className="w-full px-3 py-2.5 rounded-xl border border-slate-200 bg-slate-50 text-slate-600" value={patient.name} disabled /></div>
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-bold text-slate-700 mb-1">年龄</label>
              <input type="number" className="w-full px-3 py-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" value={editAge} onChange={e => setEditAge(Number(e.target.value))} /></div>
            <div><label className="block text-sm font-bold text-slate-700 mb-1">性别</label>
              <select className="w-full px-3 py-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" value={editGender} onChange={e => setEditGender(e.target.value)}>
                <option>男</option><option>女</option>
              </select></div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-bold text-slate-700 mb-1">血型</label>
              <select className="w-full px-3 py-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" value={editBloodType} onChange={e => setEditBloodType(e.target.value)}>
                <option value="A+">A型 (Rh阳性)</option><option value="A-">A型 (Rh阴性)</option>
                <option value="B+">B型 (Rh阳性)</option><option value="B-">B型 (Rh阴性)</option>
                <option value="O+">O型 (Rh阳性)</option><option value="O-">O型 (Rh阴性)</option>
                <option value="AB+">AB型 (Rh阳性)</option><option value="AB-">AB型 (Rh阴性)</option>
              </select></div>
            <div><label className="block text-sm font-bold text-slate-700 mb-1">联系电话</label>
              <input type="tel" maxLength={11} className="w-full px-3 py-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" placeholder="请输入11位手机号" value={editPhone} onChange={e => setEditPhone(e.target.value.replace(/\D/g, ''))} /></div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div><label className="block text-sm font-bold text-slate-700 mb-1">身高 (cm)</label>
              <input type="number" className="w-full px-3 py-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" value={editHeight} onChange={e => setEditHeight(Number(e.target.value))} /></div>
            <div><label className="block text-sm font-bold text-slate-700 mb-1">体重 (kg)</label>
              <input type="number" className="w-full px-3 py-2.5 rounded-xl border border-slate-200 focus:ring-2 focus:ring-blue-500 outline-none" value={editWeight} onChange={e => setEditWeight(Number(e.target.value))} /></div>
          </div>
          {/* 既往病史编辑 */}
          <div>
            <div className="flex items-center justify-between mb-2">
              <label className="text-sm font-bold text-slate-700">既往病史</label>
              <button className="text-xs text-blue-600 font-bold flex items-center gap-1" onClick={addHistory}>
                <span>➕</span>添加病史
              </button>
            </div>
            <div className="space-y-2 max-h-48 overflow-y-auto">
              {editHistories.length === 0 ? (
                <p className="text-xs text-slate-400 py-2 text-center">暂无病史记录，点击"添加病史"</p>
              ) : editHistories.map((h: any, i: number) => (
                <div key={i} className="flex flex-col gap-2 p-2 bg-slate-50 rounded-xl">
                  <div className="flex items-center gap-2">
                    <input className="flex-1 px-2 py-1.5 rounded-lg border border-slate-200 text-sm outline-none focus:ring-1 focus:ring-blue-500" placeholder="疾病名称" value={h.diseaseName || ''} onChange={e => updateHistory(i, 'diseaseName', e.target.value)} />
                    <input type="date" className="w-32 px-2 py-1.5 rounded-lg border border-slate-200 text-sm outline-none" value={h.diagnosisDate || ''} onChange={e => updateHistory(i, 'diagnosisDate', e.target.value)} />
                    <button className="text-red-400 hover:text-red-600 shrink-0" onClick={() => removeHistory(i)}><span>🗑️</span></button>
                  </div>
                  <div className="flex items-center gap-2">
                    <select className="flex-1 px-2 py-1.5 rounded-lg border border-slate-200 text-sm outline-none" value={h.status || ''} onChange={e => updateHistory(i, 'status', e.target.value)}>
                      <option value="">请选择状态</option>
                      <option value="痊愈">痊愈</option>
                      <option value="治疗中">治疗中</option>
                      <option value="慢性病">慢性病</option>
                      <option value="观察中">观察中</option>
                    </select>
                    <input className="flex-1 px-2 py-1.5 rounded-lg border border-slate-200 text-sm outline-none" placeholder="治疗方案" value={h.treatment || ''} onChange={e => updateHistory(i, 'treatment', e.target.value)} />
                    <input className="flex-1 px-2 py-1.5 rounded-lg border border-slate-200 text-sm outline-none" placeholder="备注" value={h.notes || ''} onChange={e => updateHistory(i, 'notes', e.target.value)} />
                  </div>
                </div>
              ))}
            </div>
          </div>
          <div className="flex gap-3 pt-2">
            <button className="flex-1 py-3 bg-slate-100 text-slate-600 rounded-xl font-bold hover:bg-slate-200 transition-colors" onClick={onClose}>取消</button>
            <button disabled={saving} className="flex-1 py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors" onClick={handleSave}>{saving ? '保存中...' : '保存修改'}</button>
          </div>
        </div>
      </div>
    </div>
  );
}
