import 'dart:async';
import 'package:flutter/material.dart';
import 'package:elderly_care/services/data_generator.dart';
import 'package:elderly_care/services/ai_service.dart';

class ElderlyMonitorScreen extends StatefulWidget {
  const ElderlyMonitorScreen({super.key});

  @override
  State<ElderlyMonitorScreen> createState() => _ElderlyMonitorScreenState();
}

class _ElderlyMonitorScreenState extends State<ElderlyMonitorScreen> {
  int _heartRate = 72;
  int _systolic = 125;
  int _diastolic = 84;
  String _heartRateAnalysis = '分析中...';
  String _bloodPressureAnalysis = '分析中...';
  bool _isHeartRateAnalyzing = false;
  bool _isBloodPressureAnalyzing = false;

  StreamSubscription<int>? _heartRateSubscription;
  StreamSubscription<Map<String, int>>? _bpSubscription;
  StreamSubscription<Map<String, double>>? _accelerationSubscription;

  @override
  void dispose() {
    // 取消所有流订阅，防止内存泄漏
    _heartRateSubscription?.cancel();
    _bpSubscription?.cancel();
    _accelerationSubscription?.cancel();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    // 初始化时进行一次分析
    _analyzeHeartRate(72);
    _analyzeBloodPressure(125, 84);
    // 使用数据生成器启动实时更新流
    _startHeartRateStream();
    _startBloodPressureStream();
    _startAccelerationReporting();
  }

  // 心率实时更新流：3秒更新一次
  void _startHeartRateStream() {
    _heartRateSubscription =
        DataGenerator.heartRateStream().listen((heartRate) {
      if (mounted) {
        setState(() {
          _heartRate = heartRate;
        });
        // 更新心率后进行AI分析
        _analyzeHeartRate(heartRate);
      }
    });
  }

  // 血压实时更新流：10秒更新一次
  void _startBloodPressureStream() {
    _bpSubscription = DataGenerator.bloodPressureStream().listen((bp) {
      if (mounted) {
        setState(() {
          _systolic = bp['systolic']!;
          _diastolic = bp['diastolic']!;
        });
        // 更新血压后进行AI分析
        _analyzeBloodPressure(bp['systolic']!, bp['diastolic']!);
      }
    });
  }

  // 加速度后台上报：每15秒上报一次，供后端AI分析，不在界面展示
  void _startAccelerationReporting() {
    _accelerationSubscription = DataGenerator.accelerationStream(
      interval: const Duration(seconds: 15),
    ).listen((accel) {
      AIService.reportAcceleration(
        accel['x']!,
        accel['y']!,
        accel['z']!,
      );
    });
  }

  // 使用AI分析心率
  Future<void> _analyzeHeartRate(int heartRate) async {
    if (_isHeartRateAnalyzing) return;
    setState(() {
      _isHeartRateAnalyzing = true;
      _heartRateAnalysis = 'AI分析中...';
    });

    try {
      final result = await AIService.analyzeHeartRate(heartRate);
      if (mounted) {
        setState(() {
          _heartRateAnalysis = result;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _heartRateAnalysis = '分析失败，请稍后重试。';
        });
      }
    }

    if (mounted) {
      setState(() {
        _isHeartRateAnalyzing = false;
      });
    }
  }

  // 使用AI分析血压
  Future<void> _analyzeBloodPressure(int systolic, int diastolic) async {
    if (_isBloodPressureAnalyzing) return;
    setState(() {
      _isBloodPressureAnalyzing = true;
      _bloodPressureAnalysis = 'AI分析中...';
    });

    try {
      final result = await AIService.analyzeBloodPressure(systolic, diastolic);
      if (mounted) {
        setState(() {
          _bloodPressureAnalysis = result;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _bloodPressureAnalysis = '分析失败，请稍后重试。';
        });
      }
    }

    if (mounted) {
      setState(() {
        _isBloodPressureAnalyzing = false;
      });
    }
  }

  // 获取心率状态颜色
  Color _getHeartRateColor(int heartRate) {
    if (heartRate < 50 || heartRate > 120) return const Color(0xFFDC2626);
    if (heartRate < 60 || heartRate > 100) return const Color(0xFFF59E0B);
    return const Color(0xFF10B981);
  }

  // 获取血压状态颜色
  Color _getBloodPressureColor(int systolic, int diastolic) {
    if (systolic < 90 || diastolic < 60 || systolic > 160 || diastolic > 100) {
      return const Color(0xFFDC2626);
    }
    if (systolic >= 140 || diastolic >= 90) return const Color(0xFFF59E0B);
    return const Color(0xFF10B981);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Column(
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: const BoxDecoration(
                color: Color(0xFFFFFAF0),
                border: Border(
                    bottom: BorderSide(color: Color(0xFFFFE4C4), width: 2)),
              ),
              child: Row(
                children: [
                  GestureDetector(
                    onTap: () => Navigator.pop(context),
                    child: Container(
                      width: 56,
                      height: 56,
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(28),
                        border: Border.all(
                            color: const Color(0xFFFFB366), width: 3),
                      ),
                      child: const Icon(Icons.chevron_left,
                          size: 40, color: Color(0xFF8B4513)),
                    ),
                  ),
                  const SizedBox(width: 12),
                  const Text(
                    '健康监测详情',
                    style: TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF8B4513),
                    ),
                  ),
                ],
              ),
            ),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    Container(
                      padding: const EdgeInsets.all(20),
                      decoration: BoxDecoration(
                        color: const Color(0xFFFFF0F0),
                        borderRadius: BorderRadius.circular(24),
                        border: Border.all(
                            color: const Color(0xFFFFCCCC), width: 3),
                      ),
                      child: Column(
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Row(
                                children: const [
                                  Icon(Icons.favorite,
                                      size: 28, color: Color(0xFFDC2626)),
                                  const SizedBox(width: 8),
                                  const Text(
                                    '实时心率',
                                    style: TextStyle(
                                      fontSize: 20,
                                      fontWeight: FontWeight.bold,
                                      color: Color(0xFF991B1B),
                                    ),
                                  ),
                                ],
                              ),
                              Text(
                                '$_heartRate\n次/分',
                                textAlign: TextAlign.right,
                                style: TextStyle(
                                  fontSize: 32,
                                  fontWeight: FontWeight.bold,
                                  color: _getHeartRateColor(_heartRate),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 12),
                          Container(
                            height: 160,
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.circular(20),
                              border: Border.all(
                                  color: const Color(0xFFFFCCCC), width: 2),
                            ),
                            child: Center(
                              child: Container(
                                width: 120,
                                height: 120,
                                decoration: BoxDecoration(
                                  shape: BoxShape.circle,
                                  color: Colors.transparent,
                                  border: Border.all(
                                      color: _getHeartRateColor(_heartRate),
                                      width: 3),
                                ),
                                child: Center(
                                  child: Icon(Icons.favorite,
                                      size: 52,
                                      color: _getHeartRateColor(_heartRate)),
                                ),
                              ),
                            ),
                          ),
                          const SizedBox(height: 12),
                          Container(
                            padding: const EdgeInsets.all(12),
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.circular(16),
                              border: Border.all(
                                  color: const Color(0xFFFFCCCC), width: 2),
                            ),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  children: [
                                    Icon(
                                      Icons.check_circle,
                                      size: 20,
                                      color: _getHeartRateColor(_heartRate),
                                    ),
                                    const SizedBox(width: 8),
                                    const Text(
                                      'AI医生分析：',
                                      style: TextStyle(
                                        fontSize: 14,
                                        fontWeight: FontWeight.bold,
                                        color: Color(0xFF374151),
                                      ),
                                    ),
                                  ],
                                ),
                                const SizedBox(height: 4),
                                _isHeartRateAnalyzing
                                    ? const Row(
                                        children: [
                                          SizedBox(
                                            width: 16,
                                            height: 16,
                                            child: CircularProgressIndicator(
                                              strokeWidth: 2,
                                            ),
                                          ),
                                          SizedBox(width: 8),
                                          Text('正在分析...'),
                                        ],
                                      )
                                    : Text(
                                        _heartRateAnalysis,
                                        style: TextStyle(
                                          fontSize: 14,
                                          fontWeight: FontWeight.bold,
                                          color: _getHeartRateColor(_heartRate),
                                        ),
                                      ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 20),
                    Container(
                      padding: const EdgeInsets.all(20),
                      decoration: BoxDecoration(
                        color: const Color(0xFFEFF6FF),
                        borderRadius: BorderRadius.circular(24),
                        border: Border.all(
                            color: const Color(0xFFBFDBFE), width: 3),
                      ),
                      child: Column(
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Row(
                                children: const [
                                  Icon(Icons.bloodtype,
                                      size: 28, color: Color(0xFF2563EB)),
                                  const SizedBox(width: 8),
                                  const Text(
                                    '实时血压',
                                    style: TextStyle(
                                      fontSize: 20,
                                      fontWeight: FontWeight.bold,
                                      color: Color(0xFF1E40AF),
                                    ),
                                  ),
                                ],
                              ),
                              Text(
                                '$_systolic/$_diastolic\nmmHg',
                                textAlign: TextAlign.right,
                                style: TextStyle(
                                  fontSize: 28,
                                  fontWeight: FontWeight.bold,
                                  color: _getBloodPressureColor(
                                      _systolic, _diastolic),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 12),
                          Row(
                            children: [
                              Expanded(
                                child: Container(
                                  padding: const EdgeInsets.all(12),
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    borderRadius: BorderRadius.circular(16),
                                  ),
                                  child: Column(
                                    children: [
                                      const Text(
                                        '高压',
                                        style: TextStyle(
                                          fontSize: 14,
                                          fontWeight: FontWeight.bold,
                                          color: Color(0xFF6B7280),
                                        ),
                                      ),
                                      const SizedBox(height: 4),
                                      Text(
                                        '$_systolic',
                                        style: TextStyle(
                                          fontSize: 40,
                                          fontWeight: FontWeight.bold,
                                          color: _getBloodPressureColor(
                                              _systolic, _diastolic),
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                              const SizedBox(width: 12),
                              Expanded(
                                child: Container(
                                  padding: const EdgeInsets.all(12),
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    borderRadius: BorderRadius.circular(16),
                                  ),
                                  child: Column(
                                    children: [
                                      const Text(
                                        '低压',
                                        style: TextStyle(
                                          fontSize: 14,
                                          fontWeight: FontWeight.bold,
                                          color: Color(0xFF6B7280),
                                        ),
                                      ),
                                      const SizedBox(height: 4),
                                      Text(
                                        '$_diastolic',
                                        style: TextStyle(
                                          fontSize: 40,
                                          fontWeight: FontWeight.bold,
                                          color: _getBloodPressureColor(
                                              _systolic, _diastolic),
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 12),
                          Container(
                            padding: const EdgeInsets.all(12),
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.circular(16),
                              border: Border.all(
                                  color: const Color(0xFFBFDBFE), width: 2),
                            ),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  children: [
                                    Icon(
                                      Icons.check_circle,
                                      size: 20,
                                      color: _getBloodPressureColor(
                                          _systolic, _diastolic),
                                    ),
                                    const SizedBox(width: 8),
                                    const Text(
                                      'AI医生分析：',
                                      style: TextStyle(
                                        fontSize: 14,
                                        fontWeight: FontWeight.bold,
                                        color: Color(0xFF374151),
                                      ),
                                    ),
                                  ],
                                ),
                                const SizedBox(height: 4),
                                _isBloodPressureAnalyzing
                                    ? const Row(
                                        children: [
                                          SizedBox(
                                            width: 16,
                                            height: 16,
                                            child: CircularProgressIndicator(
                                              strokeWidth: 2,
                                            ),
                                          ),
                                          SizedBox(width: 8),
                                          Text('正在分析...'),
                                        ],
                                      )
                                    : Text(
                                        _bloodPressureAnalysis,
                                        style: TextStyle(
                                          fontSize: 14,
                                          fontWeight: FontWeight.bold,
                                          color: _getBloodPressureColor(
                                              _systolic, _diastolic),
                                        ),
                                      ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 30),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
