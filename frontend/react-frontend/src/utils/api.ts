const API_BASE = 'http://localhost:8080';

/** 从 sessionStorage 获取 Token */
export function getToken(): string | null {
  return sessionStorage.getItem('token');
}

/** 构建带认证头的 headers */
export function authHeaders(): Record<string, string> {
  const token = getToken();
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token) {
    headers['Authorization'] = 'Bearer ' + token;
  }
  return headers;
}

/**
 * 通用 API 请求（自动携带 Token）
 */
export async function fetchApi<T = any>(path: string, options: RequestInit = {}): Promise<{ code: number; message?: string; data?: T }> {
  const url = API_BASE + path;
  try {
    const response = await fetch(url, {
      headers: { ...authHeaders(), ...(options.headers as Record<string, string> || {}) },
      ...options,
    });
    if (response.status === 401 || response.status === 403) {
      // Token 无效或权限不足，清除登录状态
      sessionStorage.clear();
      window.location.href = '/login';
      throw new Error('未登录或权限不足');
    }
    if (!response.ok) throw new Error('HTTP ' + response.status);
    return await response.json();
  } catch (error) {
    console.error('API请求失败 [' + path + ']:', (error as Error).message);
    throw error;
  }
}

/**
 * 兼容旧接口的 admin fetch（自动携带 Token）
 */
export async function apiFetchAdmin(url: string, options: RequestInit = {}) {
  const opts = { ...options };
  const token = getToken();
  const headers: Record<string, string> = { ...(opts.headers as Record<string, string> || {}) };
  if (token) {
    headers['Authorization'] = 'Bearer ' + token;
  }
  if (!opts.method || opts.method === 'GET') {
    opts.headers = { ...headers };
  } else {
    opts.headers = { 'Content-Type': 'application/json', ...headers };
  }
  const resp = await fetch(API_BASE + url, opts);
  if (resp.status === 401 || resp.status === 403) {
    sessionStorage.clear();
    window.location.href = '/login';
    throw new Error('未登录或权限不足');
  }
  return resp.json();
}

export default API_BASE;
