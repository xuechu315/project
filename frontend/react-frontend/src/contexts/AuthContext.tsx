import React, { createContext, useContext, useState, ReactNode } from 'react';

interface User {
  id: string;
  username: string;
  name: string;
  userType: string;
  phone: string;
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isDoctor: boolean;
  isFamily: boolean;
  isElder: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  updateUserInfo: () => void;
  getToken: () => string | null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

function loadUserFromSession(): User | null {
  const userId = sessionStorage.getItem('userId');
  if (!userId) return null;
  return {
    id: userId,
    username: sessionStorage.getItem('username') || '',
    name: sessionStorage.getItem('name') || '',
    userType: sessionStorage.getItem('userType') || '',
    phone: sessionStorage.getItem('phone') || '',
  };
}

function saveUserToSession(u: any, token?: string) {
  sessionStorage.setItem('userId', String(u.id));
  sessionStorage.setItem('username', u.username || '');
  sessionStorage.setItem('name', u.name || '');
  sessionStorage.setItem('userType', u.userType || '');
  sessionStorage.setItem('phone', u.phone || '');
  if (token) {
    sessionStorage.setItem('token', token);
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(loadUserFromSession);

  const isAuthenticated = user !== null;
  const isAdmin = user?.userType === 'admin';
  const isDoctor = user?.userType === 'doctor';
  const isFamily = user?.userType === 'family';
  const isElder = user?.userType === 'elder';

  const login = async (username: string, password: string) => {
    const response = await fetch('http://localhost:8080/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });
    const result = await response.json();
    if (result.code !== 200) throw new Error(result.message || '登录失败');

    const u = result.data;
    // 保存 token 和用户信息
    saveUserToSession(u, u.token);
    setUser({ id: u.id, username: u.username, name: u.name, userType: u.userType, phone: u.phone || '' });
  };

  const logout = () => {
    sessionStorage.clear();
    setUser(null);
  };

  const updateUserInfo = () => {
    setUser(loadUserFromSession);
  };

  const getToken = (): string | null => {
    return sessionStorage.getItem('token');
  };

  return (
    <AuthContext.Provider value={{
      user, isAuthenticated, isAdmin, isDoctor, isFamily, isElder,
      login, logout, updateUserInfo, getToken
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
