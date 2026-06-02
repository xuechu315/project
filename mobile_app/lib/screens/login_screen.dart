import 'package:flutter/material.dart';
import 'package:elderly_care/screens/family_home_screen.dart';
import 'package:elderly_care/screens/elderly_home_screen.dart';
import 'package:elderly_care/services/api_service.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController _usernameController =
      TextEditingController(text: 'elderly1');
  final TextEditingController _passwordController =
      TextEditingController(text: '123456');
  bool _isLoading = false;

  void _handleLogin() async {
    setState(() => _isLoading = true);

    try {
      // 调用后端登录API
      final response = await ApiService.login(
        _usernameController.text.trim(),
        _passwordController.text.trim(),
      );

      // 安全获取用户数据（防止 response['data'] 为 null）
      final userData = response['data'];
      if (userData == null) {
        throw Exception('登录响应异常：用户数据为空');
      }

      // 根据返回的用户类型跳转
      String userType = userData['userType'] ?? '';
      String userName = userData['name'] ?? '用户';
      if (userType == 'elder') {
        // 老人账号，跳转到老人界面
        setState(() => _isLoading = false);
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
              builder: (context) => ElderlyHomeScreen(userName: userName)),
        );
      } else if (userType == 'family') {
        // 家属账号，跳转到家属界面
        setState(() => _isLoading = false);
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => FamilyHomeScreen(userName: userName)),
        );
      } else {
        throw Exception('未知的用户类型: $userType');
      }
    } catch (e) {
      // 登录失败，显示错误提示
      setState(() => _isLoading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('登录失败: ${e.toString()}')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      body: LayoutBuilder(
        builder: (context, constraints) {
          // 判断是否为小屏幕手机（宽度小于 600）
          bool isSmallScreen = constraints.maxWidth < 600;

          return SingleChildScrollView(
            child: isSmallScreen ? _buildMobileLayout() : _buildDesktopLayout(),
          );
        },
      ),
    );
  }

  // 桌面端布局 - 左右分栏
  Widget _buildDesktopLayout() {
    return Container(
      height: MediaQuery.of(context).size.height,
      child: Row(
        children: [
          Expanded(
            flex: 1,
            child: Container(
              color: const Color(0xFF1E40AF),
              padding: const EdgeInsets.all(48),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text(
                    '多银龄守护',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                      letterSpacing: -0.5,
                    ),
                  ),
                  const SizedBox(height: 32),
                  const Text(
                    '智慧监护，\n多智能体协同守护。',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 32,
                      fontWeight: FontWeight.bold,
                      height: 1.3,
                    ),
                  ),
                  const SizedBox(height: 16),
                  const Text(
                    '基于 CrewAI 框架，集成健康监测、行为分析、\n冲突解决与应急响应，为老人提供全天候的安全保障。',
                    style: TextStyle(
                      color: Color(0xFFDBEAFE),
                      fontSize: 14,
                      height: 1.6,
                    ),
                  ),
                ],
              ),
            ),
          ),
          Expanded(
            flex: 1,
            child: Container(
              color: Colors.white,
              padding: const EdgeInsets.all(48),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text(
                    '欢迎登录',
                    style: TextStyle(
                      color: Color(0xFF1E293B),
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  const Text(
                    '请输入您的账号信息',
                    style: TextStyle(
                      color: Color(0xFF64748B),
                      fontSize: 14,
                    ),
                  ),
                  const SizedBox(height: 48),
                  _buildInputField(
                    controller: _usernameController,
                    label: '账号 / 手机号',
                    hintText: '请输入您的账号',
                    icon: Icons.person_outline,
                  ),
                  const SizedBox(height: 20),
                  _buildInputField(
                    controller: _passwordController,
                    label: '登录密码',
                    hintText: '请输入密码',
                    icon: Icons.lock_outline,
                    isPassword: true,
                  ),
                  const SizedBox(height: 32),
                  Container(
                    width: double.infinity,
                    height: 50,
                    decoration: BoxDecoration(
                      color: const Color(0xFF1E40AF),
                      borderRadius: BorderRadius.circular(12),
                      boxShadow: [
                        BoxShadow(
                          color: const Color(0xFF1E40AF).withOpacity(0.3),
                          blurRadius: 10,
                          offset: const Offset(0, 4),
                        ),
                      ],
                    ),
                    child: TextButton(
                      onPressed: _isLoading ? null : _handleLogin,
                      child: _isLoading
                          ? const CircularProgressIndicator(color: Colors.white)
                          : const Text(
                              '立即登录',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 16,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  // 移动端布局 - 垂直堆叠
  Widget _buildMobileLayout() {
    return Column(
      children: [
        // 顶部蓝色区域
        Container(
          width: double.infinity,
          color: const Color(0xFF1E40AF),
          padding: const EdgeInsets.all(32),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                '多银龄守护',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  letterSpacing: -0.5,
                ),
              ),
              const SizedBox(height: 20),
              const Text(
                '智慧监护，多智能体协同守护。',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  height: 1.3,
                ),
              ),
            ],
          ),
        ),
        // 底部白色登录区域
        Container(
          width: double.infinity,
          color: Colors.white,
          padding: const EdgeInsets.all(24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                '欢迎登录',
                style: TextStyle(
                  color: Color(0xFF1E293B),
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              const Text(
                '请输入您的账号信息',
                style: TextStyle(
                  color: Color(0xFF64748B),
                  fontSize: 14,
                ),
              ),
              const SizedBox(height: 32),
              _buildInputField(
                controller: _usernameController,
                label: '账号 / 手机号',
                hintText: '请输入您的账号',
                icon: Icons.person_outline,
              ),
              const SizedBox(height: 16),
              _buildInputField(
                controller: _passwordController,
                label: '登录密码',
                hintText: '请输入密码',
                icon: Icons.lock_outline,
                isPassword: true,
              ),
              const SizedBox(height: 24),
              Container(
                width: double.infinity,
                height: 50,
                decoration: BoxDecoration(
                  color: const Color(0xFF1E40AF),
                  borderRadius: BorderRadius.circular(12),
                  boxShadow: [
                    BoxShadow(
                      color: const Color(0xFF1E40AF).withOpacity(0.3),
                      blurRadius: 10,
                      offset: const Offset(0, 4),
                    ),
                  ],
                ),
                child: TextButton(
                  onPressed: _isLoading ? null : _handleLogin,
                  child: _isLoading
                      ? const CircularProgressIndicator(color: Colors.white)
                      : const Text(
                          '立即登录',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                ),
              ),
              const SizedBox(height: 20),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildInputField({
    required TextEditingController controller,
    required String label,
    required String hintText,
    required IconData icon,
    bool isPassword = false,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: const TextStyle(
            color: Color(0xFF475569),
            fontSize: 14,
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(height: 6),
        Container(
          decoration: BoxDecoration(
            color: const Color(0xFFF1F5F9),
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: const Color(0xFFE2E8F0)),
          ),
          child: TextField(
            controller: controller,
            obscureText: isPassword,
            decoration: InputDecoration(
              prefixIcon: Icon(
                icon,
                color: const Color(0xFF94A3B8),
              ),
              hintText: hintText,
              border: InputBorder.none,
              contentPadding: const EdgeInsets.symmetric(vertical: 14),
            ),
            style: const TextStyle(fontSize: 14),
          ),
        ),
      ],
    );
  }
}
