import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  // Android模拟器使用10.0.2.2访问主机
  static const String baseUrl = 'http://10.0.2.2:8080/api';

  // 用户登录
  static Future<Map<String, dynamic>> login(
      String username, String password) async {
    final response = await http.post(
      Uri.parse('$baseUrl/login'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({
        'username': username,
        'password': password,
      }),
    );

    final body = json.decode(response.body) as Map<String, dynamic>;

    // 检查业务状态码（code=200 表示成功），而非 HTTP 状态码
    if (body['code'] == 200) {
      return body;
    } else {
      throw Exception(body['message'] ?? '登录失败');
    }
  }

  // 获取绑定的老人列表
  static Future<List<dynamic>> getBoundElderlyList(int familyId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/family/list'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['data'] ?? [];
    } else {
      throw Exception('Failed to load elderly list');
    }
  }

  // 绑定老人
  static Future<Map<String, dynamic>> bindElderly(
      int familyId, int elderlyId, String relationship) async {
    final response = await http.post(
      Uri.parse('$baseUrl/family/bind'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({
        'familyId': familyId,
        'elderlyId': elderlyId,
        'relationship': relationship,
      }),
    );

    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('Failed to bind elderly');
    }
  }

  // 解绑老人
  static Future<Map<String, dynamic>> unbindElderly(
      int familyId, int elderlyId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/family/unbind'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({
        'familyId': familyId,
        'elderlyId': elderlyId,
      }),
    );

    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('Failed to unbind elderly');
    }
  }

  // 搜索老人（通过手机号或用户名）
  static Future<List<dynamic>> searchElderly(String keyword) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/family/search?keyword=$keyword'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['data'] ?? [];
      } else {
        throw Exception('Failed to search elderly');
      }
    } catch (e) {
      throw Exception('Failed to search elderly: $e');
    }
  }

  // ========== 健康数据相关接口 ==========

  /// 上传健康数据
  static Future<void> uploadHealthData(Map<String, dynamic> data) async {
    final response = await http.post(
      Uri.parse('$baseUrl/health/upload'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(data),
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to upload health data');
    }
  }

  /// 获取最新健康数据
  static Future<Map<String, dynamic>> getLatestHealthData(int userId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/health/latest?userId=$userId'),
      headers: {'Content-Type': 'application/json'},
    );
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['data'] ?? {};
    } else {
      throw Exception('Failed to load latest health data');
    }
  }

  /// 获取历史健康数据
  static Future<List<dynamic>> getHealthHistory(int userId, int days) async {
    final response = await http.get(
      Uri.parse('$baseUrl/health/history?userId=$userId&days=$days'),
      headers: {'Content-Type': 'application/json'},
    );
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['data'] ?? [];
    } else {
      throw Exception('Failed to load health history');
    }
  }

  /// 获取健康统计数据（近7天）
  static Future<Map<String, dynamic>> getHealthStatistics(int userId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/health/statistics?userId=$userId'),
      headers: {'Content-Type': 'application/json'},
    );
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['data'] ?? {};
    } else {
      throw Exception('Failed to load health statistics');
    }
  }

  // ========== AI分析相关接口 ==========

  /// 获取心率AI分析
  static Future<Map<String, dynamic>> getHeartRateAnalysis(
      int heartRate) async {
    final response = await http.get(
      Uri.parse('$baseUrl/health/analyze/heartrate?heartRate=$heartRate'),
      headers: {'Content-Type': 'application/json'},
    );
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['data'] ?? {};
    } else {
      throw Exception('Failed to get heart rate analysis');
    }
  }

  /// 获取血压AI分析
  static Future<Map<String, dynamic>> getBloodPressureAnalysis(
      int systolic, int diastolic) async {
    final response = await http.get(
      Uri.parse(
          '$baseUrl/health/analyze/bloodpressure?systolic=$systolic&diastolic=$diastolic'),
      headers: {'Content-Type': 'application/json'},
    );
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['data'] ?? {};
    } else {
      throw Exception('Failed to get blood pressure analysis');
    }
  }
}
