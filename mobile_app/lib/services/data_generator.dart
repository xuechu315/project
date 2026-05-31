import 'dart:async';
import 'dart:math';

/// 传感器数据生成器服务
/// 用于模拟心率、血压和加速度传感器数据
class DataGenerator {
  static final Random _random = Random();

  // 默认基准值（适合老年人的健康范围）
  static const int defaultHeartRate = 72;
  static const int defaultSystolic = 125;
  static const int defaultDiastolic = 80;

  /// 生成模拟心率数据（bpm）
  /// 老年人正常范围：60-100，这里模拟可能的波动
  static int generateHeartRate() {
    // 在基准值附近波动 ±15
    int variation = _random.nextInt(31) - 15; // -15 到 +15
    int heartRate = defaultHeartRate + variation;

    // 确保在合理范围内（45-140）
    return max(45, min(140, heartRate));
  }

  /// 生成模拟收缩压（mmHg）
  static int generateSystolic() {
    // 在基准值附近波动 ±15
    int variation = _random.nextInt(31) - 15;
    int systolic = defaultSystolic + variation;

    // 确保在合理范围内（85-165）
    return max(85, min(165, systolic));
  }

  /// 生成模拟舒张压（mmHg）
  static int generateDiastolic() {
    // 在基准值附近波动 ±10
    int variation = _random.nextInt(21) - 10;
    int diastolic = defaultDiastolic + variation;

    // 确保在合理范围内（55-100）
    return max(55, min(100, diastolic));
  }

  /// 生成模拟加速度数据
  /// 返回三维加速度值（x, y, z），单位 m/s²
  static Map<String, double> generateAcceleration() {
    // 模拟轻微运动时的加速度变化
    return {
      'x': (_random.nextDouble() - 0.5) * 2, // -1 到 +1
      'y': (_random.nextDouble() - 0.5) * 2,
      'z': 9.8 + (_random.nextDouble() - 0.5) * 0.5, // 重力加速度附近
    };
  }

  /// 生成完整的健康数据
  static Map<String, dynamic> generateHealthData(int userId) {
    return {
      'userId': userId,
      'heartRate': generateHeartRate(),
      'systolic': generateSystolic(),
      'diastolic': generateDiastolic(),
      'steps': 4000 + _random.nextInt(3000),
      'bloodOxygen': 92 + _random.nextInt(8), // 92-99%
      'uploadTime': DateTime.now().toIso8601String(),
    };
  }

  /// 创建心率实时更新流
  static Stream<int> heartRateStream(
      {Duration interval = const Duration(seconds: 3)}) {
    return Stream.periodic(interval, (_) => generateHeartRate());
  }

  /// 创建血压实时更新流
  static Stream<Map<String, int>> bloodPressureStream(
      {Duration interval = const Duration(seconds: 10)}) {
    return Stream.periodic(
        interval,
        (_) => {
              'systolic': generateSystolic(),
              'diastolic': generateDiastolic(),
            });
  }

  /// 创建加速度实时更新流
  static Stream<Map<String, double>> accelerationStream(
      {Duration interval = const Duration(milliseconds: 100)}) {
    return Stream.periodic(interval, (_) => generateAcceleration());
  }
}
