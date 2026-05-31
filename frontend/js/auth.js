/**
 * 用户认证工具类
 * 用于管理用户登录状态和权限验证
 */

const UserAuth = {
    // API 基础地址
    API_BASE_URL: 'http://localhost:8080/api',

    /**
     * 检查用户是否已登录
     */
    isAuthenticated() {
        return sessionStorage.getItem('userId') !== null && 
               sessionStorage.getItem('userType') !== null;
    },

    /**
     * 获取当前用户信息
     */
    getCurrentUser() {
        if (!this.isAuthenticated()) {
            return null;
        }
        
        return {
            id: sessionStorage.getItem('userId'),
            username: sessionStorage.getItem('username'),
            name: sessionStorage.getItem('name'),
            userType: sessionStorage.getItem('userType'),
            phone: sessionStorage.getItem('phone')
        };
    },

    /**
     * 获取用户类型
     */
    getUserType() {
        return sessionStorage.getItem('userType');
    },

    /**
     * 检查是否为管理员
     */
    isAdmin() {
        return this.getUserType() === 'admin';
    },

    /**
     * 检查是否为医生
     */
    isDoctor() {
        return this.getUserType() === 'doctor';
    },

    /**
     * 检查是否为家属
     */
    isFamily() {
        return this.getUserType() === 'family';
    },

    /**
     * 检查是否为老人
     */
    isElder() {
        return this.getUserType() === 'elder';
    },

    /**
     * 登出
     */
    logout() {
        sessionStorage.clear();
        window.location.href = '../login.html';
    },

    /**
     * 验证页面访问权限
     * @param {string[]} allowedTypes - 允许访问的用户类型数组
     */
    requireAuth(allowedTypes) {
        if (!this.isAuthenticated()) {
            alert('请先登录');
            window.location.href = '../login.html';
            return false;
        }

        const userType = this.getUserType();
        if (allowedTypes && !allowedTypes.includes(userType)) {
            alert('您没有权限访问此页面');
            this.redirectToDashboard();
            return false;
        }

        return true;
    },

    /**
     * 根据用户类型跳转到对应首页
     */
    redirectToDashboard() {
        const userType = this.getUserType();
        let targetUrl = '../login.html';

        switch(userType) {
            case 'admin':
                targetUrl = '../admin/admin_user_pairs.html';
                break;
            case 'doctor':
                targetUrl = '../doctor/doctor_dashboard.html';
                break;
            case 'family':
                targetUrl = '../family/family_index.html';
                break;
            case 'elder':
                targetUrl = '../elder/elder_index.html';
                break;
        }

        window.location.href = targetUrl;
    },

    /**
     * 更新页面用户信息显示
     */
    updateUserInfo() {
        const user = this.getCurrentUser();
        if (user) {
            // 更新用户名显示
            const userNameElement = document.getElementById('user-name');
            if (userNameElement) {
                userNameElement.textContent = user.name || user.username;
            }

            // 更新日期时间
            this.updateDateTime();
        }
    },

    /**
     * 更新日期时间显示
     */
    updateDateTime() {
        const dateElement = document.getElementById('current-date');
        const timeElement = document.getElementById('current-time');

        if (dateElement || timeElement) {
            const now = new Date();
            
            if (dateElement) {
                const dateStr = now.toLocaleDateString('zh-CN', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    weekday: 'long'
                });
                dateElement.textContent = dateStr;
            }

            if (timeElement) {
                const timeStr = now.toLocaleTimeString('zh-CN', {
                    hour: '2-digit',
                    minute: '2-digit',
                    second: '2-digit'
                });
                timeElement.textContent = timeStr;
            }

            // 每秒更新时间
            setInterval(() => {
                const now = new Date();
                if (timeElement) {
                    timeElement.textContent = now.toLocaleTimeString('zh-CN', {
                        hour: '2-digit',
                        minute: '2-digit',
                        second: '2-digit'
                    });
                }
            }, 1000);
        }
    },

    /**
     * 发起API请求
     * @param {string} endpoint - API端点
     * @param {Object} options - fetch选项
     */
    async apiRequest(endpoint, options = {}) {
        const url = `${this.API_BASE_URL}${endpoint}`;
        
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        };

        try {
            const response = await fetch(url, { ...defaultOptions, ...options });
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.message || '请求失败');
            }

            return data;
        } catch (error) {
            console.error('API请求失败:', error);
            throw error;
        }
    }
};

// 页面加载时自动更新用户信息
document.addEventListener('DOMContentLoaded', function() {
    if (UserAuth.isAuthenticated()) {
        UserAuth.updateUserInfo();
    }
});
