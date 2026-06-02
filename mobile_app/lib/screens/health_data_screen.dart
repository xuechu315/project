import 'package:flutter/material.dart';
import 'package:elderly_care/services/api_service.dart';

class HealthDataScreen extends StatefulWidget {
  final int userId;
  final bool isElderly;

  const HealthDataScreen({
    super.key,
    required this.userId,
    this.isElderly = true,
  });

  @override
  State<HealthDataScreen> createState() => _HealthDataScreenState();
}

class _HealthDataScreenState extends State<HealthDataScreen> {
  Map<String, dynamic> _latestData = {};
  List<dynamic> _historyData = [];
  Map<String, dynamic> _statistics = {};
  bool _isLoading = true;
  int _currentTab = 0; // 0: 最新数据, 1: 历史记录, 2: 统计

  // 模拟数据
  final Map<String, dynamic> _mockLatestData = {
    'heartRate': 72,
    'systolic': 125,
    'diastolic': 80,
    'steps': 5238,
    'uploadTime': '2026-05-26 14:30:00',
    'bloodOxygen': 98,
  };

  final List<dynamic> _mockHistoryData = [
    {
      'date': '2026-05-26',
      'heartRate': 72,
      'systolic': 125,
      'diastolic': 80,
      'steps': 5238
    },
    {
      'date': '2026-05-25',
      'heartRate': 75,
      'systolic': 130,
      'diastolic': 85,
      'steps': 4820
    },
    {
      'date': '2026-05-24',
      'heartRate': 68,
      'systolic': 128,
      'diastolic': 78,
      'steps': 6102
    },
    {
      'date': '2026-05-23',
      'heartRate': 70,
      'systolic': 132,
      'diastolic': 82,
      'steps': 5670
    },
    {
      'date': '2026-05-22',
      'heartRate': 74,
      'systolic': 126,
      'diastolic': 80,
      'steps': 4235
    },
    {
      'date': '2026-05-21',
      'heartRate': 71,
      'systolic': 124,
      'diastolic': 76,
      'steps': 5890
    },
    {
      'date': '2026-05-20',
      'heartRate': 73,
      'systolic': 129,
      'diastolic': 81,
      'steps': 6540
    },
  ];

  final Map<String, dynamic> _mockStatistics = {
    'avgHeartRate': 72,
    'maxHeartRate': 75,
    'minHeartRate': 68,
    'avgSystolic': 128,
    'avgDiastolic': 80,
    'avgSteps': 5499,
    'totalSteps': 38495,
    'days': [
      {'date': '周一', 'heartRate': 72, 'steps': 5238},
      {'date': '周二', 'heartRate': 75, 'steps': 4820},
      {'date': '周三', 'heartRate': 68, 'steps': 6102},
      {'date': '周四', 'heartRate': 70, 'steps': 5670},
      {'date': '周五', 'heartRate': 74, 'steps': 4235},
      {'date': '周六', 'heartRate': 71, 'steps': 5890},
      {'date': '周日', 'heartRate': 73, 'steps': 6540},
    ],
  };

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      // 使用模拟数据
      setState(() {
        _latestData = _mockLatestData;
        _historyData = _mockHistoryData;
        _statistics = _mockStatistics;
      });
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('加载数据失败: $e')),
      );
    }
    setState(() => _isLoading = false);
  }

  // 上传模拟数据
  Future<void> _uploadMockData() async {
    final mockData = {
      'userId': widget.userId,
      'heartRate': 65 + (DateTime.now().second % 20),
      'systolic': 120 + (DateTime.now().minute % 20),
      'diastolic': 75 + (DateTime.now().minute % 10),
      'steps': 4000 + (DateTime.now().second * 100),
      'bloodOxygen': 95 + (DateTime.now().second % 5),
    };

    try {
      await ApiService.uploadHealthData(mockData);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('数据上传成功')),
      );
      await _loadData();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('上传失败: $e')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('健康数据'),
        backgroundColor: const Color(0xFF1E40AF),
        actions: widget.isElderly
            ? [
                IconButton(
                  icon: const Icon(Icons.upload),
                  onPressed: _uploadMockData,
                ),
              ]
            : [],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Column(
              children: [
                // 顶部标签页
                Container(
                  color: Colors.white,
                  child: Row(
                    children: [
                      _buildTab(0, '最新数据'),
                      _buildTab(1, '历史记录'),
                      _buildTab(2, '周统计'),
                    ],
                  ),
                ),
                // 内容区域
                Expanded(
                  child: IndexedStack(
                    index: _currentTab,
                    children: [
                      _buildLatestData(),
                      _buildHistoryData(),
                      _buildStatistics(),
                    ],
                  ),
                ),
              ],
            ),
    );
  }

  Widget _buildTab(int index, String label) {
    return Expanded(
      child: InkWell(
        onTap: () => setState(() => _currentTab = index),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 16),
          decoration: BoxDecoration(
            border: Border(
              bottom: BorderSide(
                color: _currentTab == index
                    ? const Color(0xFF1E40AF)
                    : Colors.transparent,
                width: 3,
              ),
            ),
          ),
          child: Text(
            label,
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 14,
              fontWeight:
                  _currentTab == index ? FontWeight.bold : FontWeight.normal,
              color: _currentTab == index
                  ? const Color(0xFF1E40AF)
                  : const Color(0xFF94A3B8),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildLatestData() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          // 健康数据卡片
          _buildHealthCard(
            icon: Icons.favorite,
            iconColor: const Color(0xFFEF4444),
            bgColor: const Color(0xFFFEF2F2),
            title: '心率',
            value: '${_latestData['heartRate'] ?? '-'}',
            unit: 'bpm',
            status: _getHeartRateStatus(_latestData['heartRate'] ?? 0),
          ),
          const SizedBox(height: 12),
          _buildHealthCard(
            icon: Icons.waves,
            iconColor: const Color(0xFFF59E0B),
            bgColor: const Color(0xFFFEF3C7),
            title: '血压',
            value:
                '${_latestData['systolic'] ?? '-'}/${_latestData['diastolic'] ?? '-'}',
            unit: 'mmHg',
            status: _getBloodPressureStatus(
              _latestData['systolic'] ?? 0,
              _latestData['diastolic'] ?? 0,
            ),
          ),
          const SizedBox(height: 12),
          _buildHealthCard(
            icon: Icons.directions_walk,
            iconColor: const Color(0xFF3B82F6),
            bgColor: const Color(0xFFEFF6FF),
            title: '今日步数',
            value: '${_latestData['steps'] ?? '-'}',
            unit: '步',
            status: '',
          ),
          const SizedBox(height: 12),
          _buildHealthCard(
            icon: Icons.cloud,
            iconColor: const Color(0xFF10B981),
            bgColor: const Color(0xFFECFDF5),
            title: '血氧',
            value: '${_latestData['bloodOxygen'] ?? '-'}',
            unit: '%',
            status: '',
          ),
          const SizedBox(height: 16),
          // 上传时间
          Container(
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(12),
            ),
            padding: const EdgeInsets.all(16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  '最后更新时间',
                  style: TextStyle(
                    fontSize: 14,
                    color: Color(0xFF64748B),
                  ),
                ),
                Text(
                  _latestData['uploadTime'] ?? '未知',
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF1E293B),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 80),
        ],
      ),
    );
  }

  Widget _buildHealthCard({
    required IconData icon,
    required Color iconColor,
    required Color bgColor,
    required String title,
    required String value,
    required String unit,
    required String status,
  }) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF1E293B).withOpacity(0.05),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          Container(
            width: 50,
            height: 50,
            decoration: BoxDecoration(
              color: bgColor,
              borderRadius: BorderRadius.circular(16),
            ),
            child: Icon(icon, color: iconColor, size: 24),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: const TextStyle(
                    fontSize: 12,
                    color: Color(0xFF94A3B8),
                  ),
                ),
                const SizedBox(height: 4),
                Row(
                  children: [
                    Text(
                      value,
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                        color: iconColor,
                      ),
                    ),
                    const SizedBox(width: 4),
                    Text(
                      unit,
                      style: const TextStyle(
                        fontSize: 12,
                        color: Color(0xFF94A3B8),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
          if (status.isNotEmpty)
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: status == '正常'
                    ? const Color(0xFFDCFCE7)
                    : const Color(0xFFFEF2F2),
                borderRadius: BorderRadius.circular(10),
              ),
              child: Text(
                status,
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  color: status == '正常'
                      ? const Color(0xFF16A34A)
                      : const Color(0xFFDC2626),
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildHistoryData() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          ..._historyData.map((item) => _buildHistoryItem(item)).toList(),
          const SizedBox(height: 80),
        ],
      ),
    );
  }

  Widget _buildHistoryItem(Map<String, dynamic> item) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF1E293B).withOpacity(0.05),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          // 日期
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                item['date'] ?? '',
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF1E293B),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          // 数据
          Row(
            children: [
              Expanded(
                child: Column(
                  children: [
                    const Text(
                      '心率',
                      style: TextStyle(
                        fontSize: 12,
                        color: Color(0xFF94A3B8),
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '${item['heartRate']} bpm',
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF1E293B),
                      ),
                    ),
                  ],
                ),
              ),
              Expanded(
                child: Column(
                  children: [
                    const Text(
                      '血压',
                      style: TextStyle(
                        fontSize: 12,
                        color: Color(0xFF94A3B8),
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '${item['systolic']}/${item['diastolic']}',
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF1E293B),
                      ),
                    ),
                  ],
                ),
              ),
              Expanded(
                child: Column(
                  children: [
                    const Text(
                      '步数',
                      style: TextStyle(
                        fontSize: 12,
                        color: Color(0xFF94A3B8),
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '${item['steps']}',
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF1E293B),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStatistics() {
    List<dynamic> days = _statistics['days'] ?? [];

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          // 统计卡片
          Container(
            decoration: BoxDecoration(
              color: const Color(0xFF1E40AF),
              borderRadius: BorderRadius.circular(20),
              boxShadow: [
                BoxShadow(
                  color: const Color(0xFF1E40AF).withOpacity(0.3),
                  blurRadius: 12,
                  offset: const Offset(0, 4),
                ),
              ],
            ),
            padding: const EdgeInsets.all(20),
            child: Column(
              children: [
                const Text(
                  '本周数据概览',
                  style: TextStyle(
                    fontSize: 16,
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Expanded(
                      child: Column(
                        children: [
                          Text(
                            '${_statistics['avgHeartRate']}',
                            style: const TextStyle(
                              fontSize: 28,
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const Text(
                            '平均心率 (bpm)',
                            style: TextStyle(
                              fontSize: 12,
                              color: Color(0xFFBFDBFE),
                            ),
                          ),
                        ],
                      ),
                    ),
                    Expanded(
                      child: Column(
                        children: [
                          Text(
                            '${_statistics['totalSteps']}',
                            style: const TextStyle(
                              fontSize: 28,
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const Text(
                            '总步数',
                            style: TextStyle(
                              fontSize: 12,
                              color: Color(0xFFBFDBFE),
                            ),
                          ),
                        ],
                      ),
                    ),
                    Expanded(
                      child: Column(
                        children: [
                          Text(
                            '${_statistics['avgSystolic']}/${_statistics['avgDiastolic']}',
                            style: const TextStyle(
                              fontSize: 20,
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const Text(
                            '平均血压',
                            style: TextStyle(
                              fontSize: 12,
                              color: Color(0xFFBFDBFE),
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
          const SizedBox(height: 16),
          // 心率趋势
          Container(
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              boxShadow: [
                BoxShadow(
                  color: const Color(0xFF1E293B).withOpacity(0.05),
                  blurRadius: 4,
                  offset: const Offset(0, 2),
                ),
              ],
            ),
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  '心率趋势',
                  style: TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF1E293B),
                  ),
                ),
                const SizedBox(height: 16),
                _buildBarChart(
                  data: days.map((d) => d['heartRate']).toList(),
                  labels: days.map((d) => d['date']).toList(),
                  color: const Color(0xFFEF4444),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          // 步数趋势
          Container(
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              boxShadow: [
                BoxShadow(
                  color: const Color(0xFF1E293B).withOpacity(0.05),
                  blurRadius: 4,
                  offset: const Offset(0, 2),
                ),
              ],
            ),
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  '步数趋势',
                  style: TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF1E293B),
                  ),
                ),
                const SizedBox(height: 16),
                _buildBarChart(
                  data: days.map((d) => d['steps']).toList(),
                  labels: days.map((d) => d['date']).toList(),
                  color: const Color(0xFF3B82F6),
                  maxValue: 8000,
                ),
              ],
            ),
          ),
          const SizedBox(height: 80),
        ],
      ),
    );
  }

  Widget _buildBarChart({
    required List<dynamic> data,
    required List<dynamic> labels,
    required Color color,
    int? maxValue,
  }) {
    int max = maxValue ??
        (data.isNotEmpty ? data.reduce((a, b) => a > b ? a : b) : 100);
    max = (max * 1.2).toInt();

    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: List.generate(data.length, (index) {
        double heightPercent = (data[index] / max) * 100;
        return Expanded(
          child: Column(
            children: [
              Container(
                height: 80,
                child: Align(
                  alignment: Alignment.bottomCenter,
                  child: Container(
                    width: 20,
                    height: heightPercent,
                    decoration: BoxDecoration(
                      color: color,
                      borderRadius: const BorderRadius.only(
                        topLeft: Radius.circular(4),
                        topRight: Radius.circular(4),
                      ),
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 8),
              Text(
                labels[index] ?? '',
                style: const TextStyle(
                  fontSize: 10,
                  color: Color(0xFF94A3B8),
                ),
              ),
            ],
          ),
        );
      }),
    );
  }

  String _getHeartRateStatus(int heartRate) {
    if (heartRate >= 60 && heartRate <= 100) return '正常';
    return '异常';
  }

  String _getBloodPressureStatus(int systolic, int diastolic) {
    if (systolic < 120 && diastolic < 80) return '正常';
    if (systolic < 140 && diastolic < 90) return '正常高值';
    return '异常';
  }
}
