import React from 'react';
import { Outlet } from 'react-router-dom';
import DoctorSidebar from '../components/doctor/DoctorSidebar';

export default function DoctorLayout() {
  const activeKey = window.location.pathname.includes('/elder/') ? 'detail'
    : window.location.pathname.includes('/consultation') ? 'consultation'
    : 'dashboard';

  return (
    <div className="bg-slate-50 font-sans text-slate-900 min-h-screen">
      <DoctorSidebar activeKey={activeKey} />
      <main className="ml-64 p-8">
        <Outlet />
      </main>
    </div>
  );
}
