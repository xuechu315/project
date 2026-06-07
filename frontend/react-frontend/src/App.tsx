import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import LoginPage from './pages/LoginPage';
import DoctorLayout from './layouts/DoctorLayout';
import AdminLayout from './layouts/AdminLayout';
import DoctorDashboardPage from './pages/doctor/DashboardPage';
import ElderDetailPage from './pages/doctor/ElderDetailPage';

import UserManagementPage from './pages/admin/UserManagementPage';
import UserPairsPage from './pages/admin/UserPairsPage';
import OperationLogsPage from './pages/admin/OperationLogsPage';

/** 路由守卫：检查登录 + 角色 */
function ProtectedRoute({ children, roles }: { children: React.ReactNode; roles?: string[] }) {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (roles && user && !roles.includes(user.userType)) {
    // 角色不匹配，重定向到对应角色首页
    const roleHome: Record<string, string> = {
      admin: '/admin/users',
      doctor: '/doctor/dashboard',
      family: '/family',
      elder: '/elder',
    };
    return <Navigate to={roleHome[user.userType] || '/login'} replace />;
  }

  return <>{children}</>;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* 医生端路由 — 仅 doctor 角色 */}
      <Route path="/doctor" element={
        <ProtectedRoute roles={['doctor']}>
          <DoctorLayout />
        </ProtectedRoute>
      }>
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<DoctorDashboardPage />} />
        <Route path="elder/:elderId" element={<ElderDetailPage />} />
      </Route>

      {/* 管理员端路由 — 仅 admin 角色 */}
      <Route path="/admin" element={
        <ProtectedRoute roles={['admin']}>
          <AdminLayout />
        </ProtectedRoute>
      }>
        <Route index element={<Navigate to="users" replace />} />
        <Route path="users" element={<UserManagementPage />} />
        <Route path="pairs" element={<UserPairsPage />} />
        <Route path="logs" element={<OperationLogsPage />} />
      </Route>

      {/* 家属端和老人端 — 占位，要求登录 */}
      <Route path="/family" element={
        <ProtectedRoute roles={['family']}>
          <div className="p-8 text-center text-gray-500 text-lg">家属端页面开发中...</div>
        </ProtectedRoute>
      } />
      <Route path="/elder" element={
        <ProtectedRoute roles={['elder']}>
          <div className="p-8 text-center text-gray-500 text-lg">老人端页面开发中...</div>
        </ProtectedRoute>
      } />

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
