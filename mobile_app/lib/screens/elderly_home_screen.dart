import 'package:flutter/material.dart';
import 'package:elderly_care/screens/elderly_monitor_screen.dart';
import 'package:elderly_care/screens/elderly_medication_screen.dart';
import 'package:elderly_care/screens/elderly_sos_screen.dart';

class ElderlyHomeScreen extends StatefulWidget {
  final String? userName;

  const ElderlyHomeScreen({super.key, this.userName});

  @override
  State<ElderlyHomeScreen> createState() => _ElderlyHomeScreenState();
}

class _ElderlyHomeScreenState extends State<ElderlyHomeScreen> {
  String get _userName => widget.userName ?? '用户';

  // 获取当前日期（手动格式化，不依赖intl包）
  String get _currentDate {
    DateTime now = DateTime.now();
    String year = now.year.toString();
    String month = now.month.toString().padLeft(2, '0');
    String day = now.day.toString().padLeft(2, '0');
    return '$year年$month月$day日';
  }

  final List<Map<String, dynamic>> features = [
    {
      'title': '健康监测',
      'icon': Icons.favorite,
      'bgColor': const Color(0xFFFF7043),
      'cardBgColor': const Color(0xFFFFF3E0),
      'borderColor': const Color(0xFFFFAB91),
      'textColor': const Color(0xFFE65100),
      'screen': const ElderlyMonitorScreen(),
    },
    {
      'title': '吃药提醒',
      'icon': Icons.medical_services,
      'bgColor': const Color(0xFF2196F3),
      'cardBgColor': const Color(0xFFE3F2FD),
      'borderColor': const Color(0xFF90CAF9),
      'textColor': const Color(0xFF0D47A1),
      'screen': const ElderlyMedicationScreen(),
    },
  ];

  void _navigateToScreen(Widget screen) {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => screen),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF3F4F6),
      body: SafeArea(
        child: Column(
          children: [
            // 顶部问候与日期
            Container(
              width: double.infinity,
              margin: const EdgeInsets.fromLTRB(16, 16, 16, 0),
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
              decoration: const BoxDecoration(
                color: Color(0xFF1E40AF),
                borderRadius: BorderRadius.all(Radius.circular(20)),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '$_userName，您好！',
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '今天是 $_currentDate',
                    style: const TextStyle(
                      color: Color(0xFFE0E7FF),
                      fontSize: 16,
                    ),
                  ),
                ],
              ),
            ),
            // 主功能宫格
            Expanded(
              child: GridView.builder(
                padding: const EdgeInsets.all(16),
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  childAspectRatio: 1.0,
                  crossAxisSpacing: 12,
                  mainAxisSpacing: 12,
                ),
                itemCount: features.length,
                itemBuilder: (context, index) {
                  final feature = features[index];
                  return GestureDetector(
                    onTap: () => _navigateToScreen(feature['screen']),
                    child: Container(
                      decoration: BoxDecoration(
                        color: feature['cardBgColor'],
                        borderRadius: BorderRadius.circular(24),
                        border: Border.all(
                          color: feature['borderColor'],
                          width: 3,
                        ),
                      ),
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Container(
                            width: 60,
                            height: 60,
                            decoration: BoxDecoration(
                              color: feature['bgColor'],
                              borderRadius: BorderRadius.circular(30),
                              boxShadow: [
                                BoxShadow(
                                  color: feature['bgColor'].withOpacity(0.3),
                                  blurRadius: 10,
                                  offset: const Offset(0, 4),
                                ),
                              ],
                            ),
                            child: Icon(
                              feature['icon'],
                              size: 32,
                              color: Colors.white,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            feature['title'],
                            style: TextStyle(
                              color: feature['textColor'],
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                },
              ),
            ),
            // 底部 SOS 区域
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
              decoration: const BoxDecoration(
                color: Color(0xFFFAFAFA),
                border: Border(top: BorderSide(color: Color(0xFFE5E7EB))),
              ),
              child: Column(
                children: [
                  GestureDetector(
                    onTap: () => _navigateToScreen(const ElderlySosScreen()),
                    child: Container(
                      height: 72,
                      decoration: BoxDecoration(
                        color: const Color(0xFFDC2626),
                        borderRadius: BorderRadius.circular(36),
                        boxShadow: [
                          BoxShadow(
                            color: const Color(0xFFDC2626).withOpacity(0.4),
                            blurRadius: 15,
                            offset: const Offset(0, 6),
                          ),
                        ],
                      ),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: const [
                          Icon(
                            Icons.error,
                            size: 32,
                            color: Colors.white,
                          ),
                          SizedBox(width: 8),
                          Text(
                            '紧急求助 SOS',
                            style: TextStyle(
                              color: Colors.white,
                              fontSize: 22,
                              fontWeight: FontWeight.bold,
                              letterSpacing: 2,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 8),
                  const Text(
                    '遇紧急情况，请点击此红色按钮',
                    style: TextStyle(
                      color: Color(0xFF6B7280),
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
