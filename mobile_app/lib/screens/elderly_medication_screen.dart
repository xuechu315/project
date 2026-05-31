import 'package:flutter/material.dart';

class ElderlyMedicationScreen extends StatefulWidget {
  const ElderlyMedicationScreen({super.key});

  @override
  State<ElderlyMedicationScreen> createState() =>
      _ElderlyMedicationScreenState();
}

class _ElderlyMedicationScreenState extends State<ElderlyMedicationScreen> {
  final List<Map<String, dynamic>> medications = [
    {
      'name': '阿司匹林',
      'description': '用于保护心血管',
      'dosage': '1 片',
      'frequency': '饭后服用',
      'isTaken': false,
      'icon': Icons.medication_rounded,
      'color': const Color(0xFF3B82F6),
    },
    {
      'name': '降压宝',
      'description': '每天两次 每次2片',
      'dosage': '2 片',
      'frequency': '温水送服',
      'isTaken': false,
      'icon': Icons.medication,
      'color': const Color(0xFF8B5CF6),
    },
  ];

  void _toggleTaken(int index) {
    setState(() {
      medications[index]['isTaken'] = !medications[index]['isTaken'];
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Column(
        children: [
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
            decoration: const BoxDecoration(
              color: Color(0xFFEFF6FF),
              border: Border(
                  bottom: BorderSide(color: Color(0xFFBFDBFE), width: 2)),
            ),
            child: Row(
              children: [
                GestureDetector(
                  onTap: () => Navigator.pop(context),
                  child: Container(
                    width: 64,
                    height: 64,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(32),
                      border:
                          Border.all(color: const Color(0xFF93C5FD), width: 4),
                    ),
                    child: const Icon(Icons.chevron_left,
                        size: 48, color: Color(0xFF1E40AF)),
                  ),
                ),
                const SizedBox(width: 16),
                const Text(
                  '吃药时间表',
                  style: TextStyle(
                    fontSize: 32,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF1E40AF),
                  ),
                ),
              ],
            ),
          ),
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24),
              child: Column(
                children: [
                  const Align(
                    alignment: Alignment.centerLeft,
                    child: Text(
                      '今日药单',
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF1F2937),
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  ...medications.asMap().entries.map((entry) {
                    final index = entry.key;
                    final med = entry.value;
                    final isTaken = med['isTaken'];
                    return Container(
                      margin: const EdgeInsets.only(bottom: 24),
                      padding: const EdgeInsets.all(24),
                      decoration: BoxDecoration(
                        color: isTaken ? const Color(0xFFF0FDF4) : Colors.white,
                        borderRadius: BorderRadius.circular(32),
                        border: Border.all(
                          color: isTaken
                              ? const Color(0xFF86EFAC)
                              : const Color(0xFFE5E7EB),
                          width: 4,
                        ),
                        boxShadow: const [
                          BoxShadow(
                            color: Color(0x08000000),
                            blurRadius: 6,
                            offset: Offset(0, 4),
                          ),
                        ],
                      ),
                      child: Column(
                        children: [
                          Row(
                            children: [
                              Container(
                                width: 64,
                                height: 64,
                                decoration: BoxDecoration(
                                  color: med['color'].withOpacity(0.1),
                                  borderRadius: BorderRadius.circular(24),
                                ),
                                child: Icon(
                                  med['icon'],
                                  size: 40,
                                  color: med['color'],
                                ),
                              ),
                              const SizedBox(width: 16),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      med['name'],
                                      style: TextStyle(
                                        fontSize: 28,
                                        fontWeight: FontWeight.bold,
                                        color: med['color'],
                                      ),
                                    ),
                                    Text(
                                      med['description'],
                                      style: const TextStyle(
                                        fontSize: 16,
                                        fontWeight: FontWeight.bold,
                                        color: Color(0xFF6B7280),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 16),
                          Row(
                            children: [
                              Container(
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 16,
                                  vertical: 8,
                                ),
                                decoration: BoxDecoration(
                                  color: const Color(0xFFF3F4F6),
                                  borderRadius: BorderRadius.circular(24),
                                ),
                                child: Text(
                                  med['dosage'],
                                  style: const TextStyle(
                                    fontSize: 20,
                                    fontWeight: FontWeight.bold,
                                    color: Color(0xFF374151),
                                  ),
                                ),
                              ),
                              const SizedBox(width: 12),
                              Container(
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 16,
                                  vertical: 8,
                                ),
                                decoration: BoxDecoration(
                                  color: const Color(0xFFF3F4F6),
                                  borderRadius: BorderRadius.circular(24),
                                ),
                                child: Text(
                                  med['frequency'],
                                  style: const TextStyle(
                                    fontSize: 20,
                                    fontWeight: FontWeight.bold,
                                    color: Color(0xFF374151),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 16),
                          ElevatedButton(
                            onPressed: () => _toggleTaken(index),
                            style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.white,
                              foregroundColor: isTaken
                                  ? Colors.green
                                  : const Color(0xFF6B7280),
                              padding: const EdgeInsets.symmetric(vertical: 16),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(40),
                                side: BorderSide(
                                  color: isTaken
                                      ? Colors.green
                                      : const Color(0xFFD1D5DB),
                                  width: 4,
                                ),
                              ),
                            ),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Icon(
                                  isTaken
                                      ? Icons.check_circle
                                      : Icons.check_circle_outline,
                                  size: 32,
                                ),
                                const SizedBox(width: 12),
                                Text(
                                  isTaken ? '已于今天服用' : '点击确认已服用',
                                  style: const TextStyle(
                                    fontSize: 24,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    );
                  }).toList(),
                  const SizedBox(height: 40),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
