# 应急响应模块 API 接口文档

## 概述

应急响应模块提供风险评估、路线规划、应急调度等功能，支持多智能体协同应急响应场景。

**基础路径**: `/api/emergency`

---

## 1. 风险评估接口

### POST `/api/emergency/assess`

根据心率、血压、加速度等健康数据，调用DeepSeek AI进行风险评估，判断紧急程度。

**请求方式**: POST

**Content-Type**: application/json

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Integer | 否 | 用户ID |
| heartRate | Integer | 否 | 心率 (bpm) |
| systolic | Integer | 否 | 收缩压 (mmHg) |
| diastolic | Integer | 否 | 舒张压 (mmHg) |
| accelerationX | Double | 否 | X轴加速度 (m/s²) |
| accelerationY | Double | 否 | Y轴加速度 (m/s²) |
| accelerationZ | Double | 否 | Z轴加速度 (m/s²) |
| behaviorNote | String | 否 | 行为备注 |

**请求示例**:

```json
{
    "userId": 1,
    "heartRate": 118,
    "systolic": 185,
    "diastolic": 110,
    "accelerationX": 0.5,
    "accelerationY": -0.3,
    "accelerationZ": 4.2,
    "behaviorNote": "疑似跌倒"
}
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 (200=成功) |
| message | String | 消息 |
| data | Object | 风险评估结果 |
| data.riskLevel | String | 风险等级 ("轻微" / "紧急") |
| data.riskScore | Double | 风险评分 (0-1) |
| data.analysis | String | AI分析说明 |
| data.recommendation | String | 建议措施 |
| data.needEmergency | Boolean | 是否需要紧急救援 |

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "riskLevel": "紧急",
        "riskScore": 0.85,
        "analysis": "心率过快、血压严重偏高、加速度异常，疑似跌倒",
        "recommendation": "建议立即采取应急措施，派遣救援小组",
        "needEmergency": true
    }
}
```

**curl示例**:

```bash
curl -X POST "http://localhost:8080/api/emergency/assess" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "heartRate": 118,
    "systolic": 185,
    "diastolic": 110,
    "accelerationX": 0.5,
    "accelerationY": -0.3,
    "accelerationZ": 4.2,
    "behaviorNote": "疑似跌倒"
  }'
```

---

## 2. 路线规划接口

### POST `/api/emergency/route`

根据起点和终点坐标，计算距离、预估时间，生成导航路线。

**请求方式**: POST

**Content-Type**: application/json

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| originLat | Double | 是 | 起点纬度 |
| originLng | Double | 是 | 起点经度 |
| destLat | Double | 是 | 终点纬度 |
| destLng | Double | 是 | 终点经度 |
| transportMode | String | 否 | 交通方式 ("driving"/"walking"/"cycling")，默认driving |

**请求示例**:

```json
{
    "originLat": 31.2304,
    "originLng": 121.4737,
    "destLat": 31.2404,
    "destLng": 121.4837,
    "transportMode": "driving"
}
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 消息 |
| data | Object | 路线规划结果 |
| data.distanceKm | Double | 距离 (公里) |
| data.estimatedMinutes | Integer | 预估时间 (分钟) |
| data.transportMode | String | 交通方式 |
| data.steps | Array | 导航步骤列表 |
| data.polyline | String | 路线编码字符串 |

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "distanceKm": 1.45,
        "estimatedMinutes": 3,
        "transportMode": "driving",
        "steps": [
            {"stepIndex": 1, "instruction": "从起点出发", "distanceKm": 0.0, "durationSeconds": 0},
            {"stepIndex": 2, "instruction": "沿规划路线行驶", "distanceKm": 1.45, "durationSeconds": 180},
            {"stepIndex": 3, "instruction": "到达目的地", "distanceKm": 0.0, "durationSeconds": 0}
        ],
        "polyline": "origin:(31.2304,121.4737)->dest:(31.2404,121.4837)"
    }
}
```

**curl示例**:

```bash
curl -X POST "http://localhost:8080/api/emergency/route" \
  -H "Content-Type: application/json" \
  -d '{
    "originLat": 31.2304,
    "originLng": 121.4737,
    "destLat": 31.2404,
    "destLng": 121.4837,
    "transportMode": "driving"
  }'
```

---

## 3. 应急调度接口

### POST `/api/emergency/dispatch`

创建应急任务，派遣救援小组前往现场。

**请求方式**: POST

**Content-Type**: application/json

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Integer | 是 | 用户ID |
| userLat | Double | 是 | 用户位置纬度 |
| userLng | Double | 是 | 用户位置经度 |
| emergencyType | String | 否 | 应急类型 |
| emergencyLevel | String | 否 | 应急等级 ("轻微"/"紧急") |
| address | String | 否 | 详细地址 |
| contactPhone | String | 否 | 联系电话 |

**请求示例**:

```json
{
    "userId": 1,
    "userLat": 31.2304,
    "userLng": 121.4737,
    "emergencyType": "跌倒",
    "emergencyLevel": "紧急",
    "address": "上海市浦东新区张江高科技园区",
    "contactPhone": "13800138000"
}
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 消息 |
| data | Object | 调度结果 |
| data.taskId | Integer | 任务ID |
| data.status | String | 任务状态 |
| data.emergencyLevel | String | 应急等级 |
| data.createdAt | String | 创建时间 |
| data.estimatedArrivalTime | String | 预计到达时间 |
| data.distanceKm | Double | 距离 (公里) |
| data.estimatedMinutes | Integer | 预估时间 (分钟) |
| data.assignedTeam | String | 指派救援小组 |
| data.message | String | 调度消息 |

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "taskId": 1001,
        "status": "已派发",
        "emergencyLevel": "紧急",
        "createdAt": "2026-05-31T21:10:00",
        "estimatedArrivalTime": "2026-05-31T21:15:00",
        "distanceKm": 2.5,
        "estimatedMinutes": 5,
        "assignedTeam": "急救先锋 2 号小组",
        "message": "应急任务已派发，救援小组正在赶往现场"
    }
}
```

**curl示例**:

```bash
curl -X POST "http://localhost:8080/api/emergency/dispatch" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "userLat": 31.2304,
    "userLng": 121.4737,
    "emergencyType": "跌倒",
    "emergencyLevel": "紧急",
    "address": "上海市浦东新区张江高科技园区",
    "contactPhone": "13800138000"
  }'
```

---

## 4. 更新任务状态接口

### PUT `/api/emergency/task/{taskId}/status`

更新应急任务的状态。

**请求方式**: PUT

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| taskId | Integer | 是 | 任务ID |

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | String | 是 | 新状态 (如: "已到达"/"处理中"/"已完成") |

**请求示例**:

```
PUT /api/emergency/task/1001/status?status=已到达
```

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": "任务状态已更新"
}
```

**curl示例**:

```bash
curl -X PUT "http://localhost:8080/api/emergency/task/1001/status?status=已到达"
```

---

## 5. 计算距离接口

### GET `/api/emergency/distance`

使用Haversine公式计算两点之间的直线距离。

**请求方式**: GET

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| lat1 | Double | 是 | 起点纬度 |
| lng1 | Double | 是 | 起点经度 |
| lat2 | Double | 是 | 终点纬度 |
| lng2 | Double | 是 | 终点经度 |

**请求示例**:

```
GET /api/emergency/distance?lat1=31.2304&lng1=121.4737&lat2=31.2404&lng2=121.4837
```

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": 1.45
}
```

**curl示例**:

```bash
curl "http://localhost:8080/api/emergency/distance?lat1=31.2304&lng1=121.4737&lat2=31.2404&lng2=121.4837"
```

---

## 6. 估算行程时间接口

### GET `/api/emergency/eta`

根据距离和交通方式估算行程时间。

**请求方式**: GET

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| distanceKm | Double | 是 | 距离 (公里) |
| transportMode | String | 否 | 交通方式，默认driving |

**交通方式说明**:

| 方式 | 速度 |
|------|------|
| walking | 5 km/h |
| cycling | 15 km/h |
| driving | 40 km/h |

**请求示例**:

```
GET /api/emergency/eta?distanceKm=10&transportMode=driving
```

**响应示例**:

```json
{
    "code": 200,
    "message": "success",
    "data": 15
}
```

**curl示例**:

```bash
curl "http://localhost:8080/api/emergency/eta?distanceKm=10&transportMode=driving"
```

---

## 接口汇总表

| 序号 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 1 | POST | `/api/emergency/assess` | 风险评估 |
| 2 | POST | `/api/emergency/route` | 路线规划 |
| 3 | POST | `/api/emergency/dispatch` | 应急调度 |
| 4 | PUT | `/api/emergency/task/{taskId}/status` | 更新任务状态 |
| 5 | GET | `/api/emergency/distance` | 计算距离 |
| 6 | GET | `/api/emergency/eta` | 估算行程时间 |

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |

---

## 技术说明

### 风险评估算法

风险评估调用DeepSeek AI API，综合分析以下数据：
- 心率数据：正常范围60-100 bpm
- 血压数据：正常范围收缩压90-140，舒张压60-90
- 加速度数据：检测跌倒等异常行为

AI返回的风险等级分为：
- **轻微**：风险评分 < 0.5，无需紧急救援
- **紧急**：风险评分 >= 0.5，需要立即派遣救援

### 距离计算

使用Haversine公式计算地球表面两点间的大圆距离：

```
a = sin²(Δlat/2) + cos(lat1) × cos(lat2) × sin²(Δlng/2)
c = 2 × atan2(√a, √(1-a))
distance = R × c
```

其中 R = 6371 km（地球半径）

### 时间估算

根据交通方式和距离估算行程时间：
- 步行：5 km/h
- 骑行：15 km/h
- 驾车：40 km/h（城市道路平均速度）
