// d:\software\mobile_app\lib\screens\elderly_sos_screen.dart
import 'package:flutter/material.dart';

class ElderlySosScreen extends StatefulWidget {
  const ElderlySosScreen({super.key});

  @override
  State<ElderlySosScreen> createState() => _ElderlySosScreenState();
}

class _ElderlySosScreenState extends State<ElderlySosScreen>
    with TickerProviderStateMixin {
  late AnimationController _animationController;
  late AnimationController _countdownController;
  double _countdownProgress = 1.0;
  int _countdownSeconds = 5;
  bool _isCancelling = false;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat();

    // 5秒倒计时动画
    _countdownController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 5),
    );
    _countdownController.addListener(() {
      setState(() {
        _countdownProgress = 1.0 - _countdownController.value;
        _countdownSeconds = (5 * (1.0 - _countdownController.value)).ceil();
      });
    });
    _countdownController.forward();
  }

  @override
  void dispose() {
    _animationController.dispose();
    _countdownController.dispose();
    super.dispose();
  }

  void _handleLongPressStart(LongPressStartDetails details) {
    setState(() {
      _isCancelling = true;
    });
  }

  void _handleLongPressEnd(LongPressEndDetails details) {
    setState(() {
      _isCancelling = false;
    });
  }

  void _handleLongPressCancel() {
    Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFDC2626),
      body: SafeArea(
        child: Stack(
          children: [
            Container(
              width: double.infinity,
              height: MediaQuery.of(context).size.height,
              child: Center(
                child: Stack(
                  alignment: Alignment.center,
                  children: [
                    AnimatedBuilder(
                      animation: _animationController,
                      builder: (context, child) {
                        return Container(
                          width: 200 + _animationController.value * 60,
                          height: 200 + _animationController.value * 60,
                          decoration: BoxDecoration(
                            color: Colors.white.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(120),
                          ),
                        );
                      },
                    ),
                    AnimatedBuilder(
                      animation: _animationController,
                      builder: (context, child) {
                        return Container(
                          width: 140 + _animationController.value * 40,
                          height: 140 + _animationController.value * 40,
                          decoration: BoxDecoration(
                            color: Colors.white.withOpacity(0.2),
                            borderRadius: BorderRadius.circular(80),
                          ),
                        );
                      },
                    ),
                  ],
                ),
              ),
            ),
            SingleChildScrollView(
              child: Column(
                children: [
                  const SizedBox(height: 20),
                  Container(
                    width: 80,
                    height: 80,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(40),
                      boxShadow: const [
                        BoxShadow(
                          color: Color(0x88DC2626),
                          blurRadius: 12,
                          offset: Offset(0, 6),
                        ),
                      ],
                    ),
                    child: const Icon(
                      Icons.warning,
                      size: 50,
                      color: Color(0xFFDC2626),
                    ),
                  ),
                  const SizedBox(height: 12),
                  const Text(
                    '紧急求助中',
                    style: TextStyle(
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                      letterSpacing: 1,
                    ),
                  ),
                  const SizedBox(height: 8),
                  const Text(
                    '系统已识别到您的求救信号\n正在连接紧急服务...',
                    style: TextStyle(
                      fontSize: 15,
                      fontWeight: FontWeight.bold,
                      color: Color(0xE6FFFFFF),
                      height: 1.5,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 15),
                  Container(
                    margin: const EdgeInsets.symmetric(horizontal: 16),
                    padding: const EdgeInsets.all(15),
                    decoration: BoxDecoration(
                      color: const Color(0xFFB91C1C),
                      borderRadius: BorderRadius.circular(18),
                      border:
                          Border.all(color: const Color(0xFFFCA5A5), width: 2),
                    ),
                    child: Column(
                      children: [
                        Row(
                          children: const [
                            Icon(
                              Icons.check_circle,
                              size: 24,
                              color: Colors.green,
                            ),
                            SizedBox(width: 8),
                            Text(
                              '110/120 报警中',
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                                color: Colors.white,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 10),
                        Row(
                          children: const [
                            Icon(
                              Icons.phone,
                              size: 24,
                              color: Colors.green,
                            ),
                            SizedBox(width: 8),
                            Expanded(
                              child: Text(
                                '已通知家属：张小明',
                                style: TextStyle(
                                  fontSize: 18,
                                  fontWeight: FontWeight.bold,
                                  color: Colors.white,
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 10),
                        Row(
                          children: const [
                            Icon(
                              Icons.location_on,
                              size: 24,
                              color: Colors.yellow,
                            ),
                            SizedBox(width: 8),
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    '当前位置已发送',
                                    style: TextStyle(
                                      fontSize: 16,
                                      fontWeight: FontWeight.bold,
                                      color: Colors.yellow,
                                    ),
                                  ),
                                  Text(
                                    '朝阳区幸福社区3号楼...',
                                    style: TextStyle(
                                      fontSize: 13,
                                      fontWeight: FontWeight.bold,
                                      color: Color(0xCCFFFFFF),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 15),
                  Container(
                    margin: const EdgeInsets.symmetric(horizontal: 16),
                    child: Column(
                      children: [
                        Text(
                          '如果您不需要帮助，请在 ${_countdownSeconds}s 内长按取消',
                          style: const TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                            color: Colors.white,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Container(
                          height: 10,
                          decoration: BoxDecoration(
                            color: const Color(0xFFB91C1C),
                            borderRadius: BorderRadius.circular(5),
                            border: Border.all(
                                color: const Color(0xFFFCA5A5), width: 2),
                          ),
                          child: AnimatedContainer(
                            duration: const Duration(milliseconds: 100),
                            width: MediaQuery.of(context).size.width *
                                0.85 *
                                _countdownProgress,
                            decoration: BoxDecoration(
                              color: Colors.yellow,
                              borderRadius: BorderRadius.circular(5),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 15),
                  Container(
                    margin: const EdgeInsets.symmetric(horizontal: 16),
                    child: GestureDetector(
                      onLongPressStart: _handleLongPressStart,
                      onLongPressEnd: _handleLongPressEnd,
                      onLongPress: _handleLongPressCancel,
                      child: Container(
                        padding: const EdgeInsets.symmetric(vertical: 16),
                        decoration: BoxDecoration(
                          color: _isCancelling
                              ? Colors.white.withOpacity(0.4)
                              : Colors.white.withOpacity(0.2),
                          borderRadius: BorderRadius.circular(28),
                          border: Border.all(
                            color: Colors.white,
                            width: 2.0,
                          ),
                        ),
                        child: Center(
                          child: Text(
                            _isCancelling ? '松开取消求助' : '长按取消',
                            style: const TextStyle(
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                              color: Colors.white,
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 8),
                  const Text(
                    '防止误触，请务必长按',
                    style: TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                      color: Color(0xCCFFFFFF),
                    ),
                  ),
                  const SizedBox(height: 15),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
