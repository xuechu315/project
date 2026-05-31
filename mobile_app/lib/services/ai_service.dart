import 'dart:convert';
import 'package:http/http.dart' as http;

/// AI分析服务 - 通过后端服务器调用DeepSeek API
class AIService {
  // 后端服务器地址（Android模拟器使用10.0.2.2访问主机）
  static const String baseUrl = 'http://10.0.2.2:8080/api';

  static const String _analysisFailed = '分析失败，请稍后重试。';
  static const String _serviceUnavailable = 'AI服务暂不可用，请稍后重试。';
  static const String _networkError = '网络连接异常，请稍后重试。';

  /// 分析心率数据 - 调用后端接口
  static Future<String> analyzeHeartRate(int heartRate) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/health/analyze/heartrate?heartRate=$heartRate'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final analysis = data['data']?['analysis'] as String?;
        if (analysis != null && analysis.trim().isNotEmpty) {
          return analysis.trim();
        }
        return _analysisFailed;
      }
      return _serviceUnavailable;
    } catch (e) {
      return _networkError;
    }
  }

  /// 分析血压数据 - 调用后端接口
  static Future<String> analyzeBloodPressure(
      int systolic, int diastolic) async {
    try {
      final response = await http.get(
        Uri.parse(
            '$baseUrl/health/analyze/bloodpressure?systolic=$systolic&diastolic=$diastolic'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final analysis = data['data']?['analysis'] as String?;
        if (analysis != null && analysis.trim().isNotEmpty) {
          return analysis.trim();
        }
        return _analysisFailed;
      }
      return _serviceUnavailable;
    } catch (e) {
      return _networkError;
    }
  }

  /// 上报加速度数据供后端AI分析（静默调用，不在老人端展示）
  static Future<void> reportAcceleration(
      double x, double y, double z) async {
    try {
      await http.get(
        Uri.parse(
            '$baseUrl/health/analyze/acceleration?x=$x&y=$y&z=$z'),
        headers: {'Content-Type': 'application/json'},
      );
    } catch (_) {
      // 静默失败，不影响老人端界面
    }
  }
}
