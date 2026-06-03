# 多银龄守护系统 API 接口文档

**版本：** v1.0.0  
**基础路径：** `http://localhost:8080/api`  
**Swagger 文档：** `http://localhost:8080/swagger-ui.html`

---

## 目录

1. [通用说明](#通用说明)
2. [用户认证模块](#用户认证模块)
3. [用户管理模块](#用户管理模块)
4. [老人管理模块](#老人管理模块)
5. [医生管理模块](#医生管理模块)
6. [家属管理模块](#家属管理模块)
7. [家属绑定模块](#家属绑定模块)
8. [健康数据模块](#健康数据模块)
9. [药品管理模块](#药品管理模块)
10. [SOS求助模块](#sos求助模块)
11. [咨询模块](#咨询模块)
12. [通知模块](#通知模块)
13. [应急响应模块](#应急响应模块)
14. [AI分析模块](#ai分析模块)
15. [首页数据模块](#首页数据模块)
16. [管理员模块](#管理员模块)
17. [老人-医生关联模块](#老人-医生关联模块)

---

## 通用说明

### 响应格式

所有接口统一返回以下 JSON 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200 表示成功 |
| message | String | 响应消息 |
| data | Object | 响应数据 |

### 常见状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/认证失败 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 用户认证模块

### 1. 用户登录

**POST** `/api/login`

**请求体：**
```json
{
  "username": "string",
  "password": "string"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "elder001",
    "name": "张三",
    "userType": "elder",
    "phone": "13800138000"
  }
}
```

---

### 2. 验证用户

**GET** `/api/verify`

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "elder001",
    "name": "张三",
    "userType": "elder"
  }
}
```

---

## 用户管理模块

### 1. 获取当前用户信息

**GET** `/api/user/{userId}`

**路径参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Integer | 是 | 用户ID |

---

### 2. 获取所有用户列表

**GET** `/api/users`

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "username": "elder001",
      "name": "张三",
      "phone": "13800138000",
      "userType": "elder",
      "userTypeDesc": "老人",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

---

### 3. 创建用户

**POST** `/api/users`

**请求体：**
```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "phone": "string",
  "role": "elder|family|doctor|admin",
  "elderId": 1,        // 家属角色时可选
  "elderIds": [1, 2],  // 家属角色时可选，支持多选
  "age": 65,           // 老人角色时可选
  "gender": "男",      // 老人角色时可选
  "bloodType": "A"     // 老人角色时可选
}
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "userId": 1
  }
}
```

---

### 4. 获取单个用户

**GET** `/api/users/{id}`

---

### 5. 更新用户

**PUT** `/api/users/{id}`

**请求体：**
```json
{
  "username": "string",
  "password": "string",
  "name": "string",
  "phone": "string",
  "role": "elder|family|doctor|admin",
  "elderIds": [1, 2]
}
```

---

### 6. 删除用户

**DELETE** `/api/users/{id}`

---

## 老人管理模块

### 1. 获取老人列表

**GET** `/api/elders`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| user_id | Integer | 否 | 用户ID，筛选指定用户的老人信息 |

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "name": "张三",
      "username": "elder001",
      "phone": "13800138000",
      "age": 65,
      "gender": "男",
      "bloodType": "A",
      "height": 170.0,
      "weight": 65.0
    }
  ]
}
```

---

### 2. 获取医生签约的老人列表

**GET** `/api/elders/doctor/{doctorId}`

---

### 3. 创建老人

**POST** `/api/elders`

**请求体：**
```json
{
  "userId": 1,
  "age": 65,
  "gender": "男",
  "bloodType": "A",
  "height": 170.0,
  "weight": 65.0
}
```

---

### 4. 更新老人

**PUT** `/api/elders/{id}`

---

### 5. 删除老人

**DELETE** `/api/elders/{id}`

---

## 医生管理模块

### 1. 获取所有医生

**GET** `/api/doctors`

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "name": "李医生",
      "phone": "13900139000",
      "department": "内科"
    }
  ]
}
```

---

### 2. 获取单个医生

**GET** `/api/doctors/{id}`

---

### 3. 按科室获取医生

**GET** `/api/doctors/department/{department}`

---

### 4. 搜索医生

**GET** `/api/doctors/search?name={name}`

---

### 5. 创建医生

**POST** `/api/doctors`

**请求体：**
```json
{
  "userId": 1,
  "name": "李医生",
  "phone": "13900139000",
  "department": "内科"
}
```

---

### 6. 更新医生

**PUT** `/api/doctors/{id}`

---

### 7. 删除医生

**DELETE** `/api/doctors/{id}`

---

## 家属管理模块

### 1. 获取老人的家属列表

**GET** `/api/family-members/elder/{elderId}`

---

### 2. 获取单个家属

**GET** `/api/family-members/{id}`

---

### 3. 获取所有家属

**GET** `/api/family-members`

---

### 4. 创建家属

**POST** `/api/family-members`

**请求体：**
```json
{
  "userId": 1,
  "elderId": 1,
  "name": "张家属",
  "phone": "13800138001",
  "relationship": "子女"
}
```

---

### 5. 更新家属

**PUT** `/api/family-members/{id}`

---

### 6. 删除家属

**DELETE** `/api/family-members/{id}`

---

## 家属绑定模块

### 1. 获取已绑定的老人列表

**GET** `/api/family/list`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| familyId | Integer | 否 | 家属用户ID，默认为1 |

---

### 2. 绑定老人

**POST** `/api/family/bind`

**请求体：**
```json
{
  "familyId": 1,
  "elderId": 1,
  "relationship": "子女",
  "name": "张家属",
  "phone": "13800138001"
}
```

---

### 3. 解绑老人

**POST** `/api/family/unbind`

**请求体：**
```json
{
  "familyId": 1,
  "elderId": 1
}
```

---

### 4. 搜索老人

**GET** `/api/family/search?keyword={keyword}`

---

## 健康数据模块

### 1. 获取健康数据列表

**GET** `/api/health-data`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| user_id | Integer | 否 | 用户ID，默认为1 |

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "heartRate": 75,
      "systolicPressure": 120,
      "diastolicPressure": 80,
      "steps": 5000,
      "recordedAt": "2024-01-01T08:00:00"
    }
  ]
}
```

---

### 2. 添加健康数据

**POST** `/api/health-data`

> **说明：** 添加健康数据时，系统会自动触发 HealthMonitorAgent 进行实时分析，若检测到异常会自动触发 EmergencyResponseAgent 应急响应。

**请求体：**
```json
{
  "userId": 1,
  "heartRate": 75,
  "systolicPressure": 120,
  "diastolicPressure": 80,
  "steps": 5000
}
```

**响应：**
```json
{
  "code": 200,
  "message": "Health data added successfully",
  "data": null
}
```

---

## 药品管理模块

### 1. 获取药品列表

**GET** `/api/medications`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| user_id | Integer | 否 | 用户ID，默认为1 |

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "name": "阿司匹林",
      "description": "每日一次",
      "dosage": "100mg",
      "frequency": "每日一次",
      "time": "08:00"
    }
  ]
}
```

---

### 2. 添加药品

**POST** `/api/medications`

**请求体：**
```json
{
  "userId": 1,
  "name": "阿司匹林",
  "description": "每日一次",
  "dosage": "100mg",
  "frequency": "每日一次",
  "time": "08:00"
}
```

---

### 3. 更新药品

**PUT** `/api/medications/{id}`

---

### 4. 删除药品

**DELETE** `/api/medications/{id}`

---

### 5. 记录用药

**POST** `/api/medications/{medId}/record`

---

### 6. 获取后端配置

**GET** `/api/config`

**响应：**
```json
{
  "code": 200,
  "data": {
    "status": "connected",
    "serverTime": "2024-01-01T08:00:00"
  }
}
```

---

## SOS求助模块

### 1. 发送SOS求助

**POST** `/api/sos`

**请求体：**
```json
{
  "userId": 1,
  "location": "上海市浦东新区xxx路xxx号"
}
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "message": "SOS sent successfully",
    "sos_id": 1
  }
}
```

---

### 2. 取消SOS求助

**POST** `/api/sos/{sosId}/cancel`

---

## 咨询模块

### 1. 获取咨询记录

**GET** `/api/consultations`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| user_id | Integer | 否 | 用户ID，默认为1 |

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "message": "最近感觉头晕",
      "response": "建议测量血压",
      "type": "文本",
      "createdAt": "2024-01-01T08:00:00"
    }
  ]
}
```

---

### 2. 添加咨询

**POST** `/api/consultations`

**请求体：**
```json
{
  "userId": 1,
  "message": "最近感觉头晕",
  "type": "文本"
}
```

---

## 通知模块

### 1. 获取通知列表

**GET** `/api/notifications`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Integer | 是 | 老人用户ID |
| type | String | 否 | 通知类型：emergency/alert/info/doctor_notification |

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "type": "emergency",
      "status": "sent",
      "message": "【紧急通知】老人出现异常状况",
      "targetName": "张家属",
      "createdAt": "2024-01-01T08:00:00"
    }
  ]
}
```

---

### 2. 确认单条通知

**PUT** `/api/notifications/{id}/acknowledge`

**响应：**
```json
{
  "code": 200,
  "message": "通知已确认",
  "data": {
    "recordId": 1,
    "status": "acknowledged"
  }
}
```

---

### 3. 批量确认通知

**PUT** `/api/notifications/acknowledge-all`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Integer | 是 | 老人用户ID |
| type | String | 否 | 通知类型，为空则确认所有 |

**响应：**
```json
{
  "code": 200,
  "message": "已确认 5 条通知",
  "data": {
    "userId": 1,
    "acknowledgedCount": 5
  }
}
```

---

## 应急响应模块

### 1. 风险评估

**POST** `/api/emergency/assess`

**请求体：**
```json
{
  "userId": 1,
  "heartRate": 120,
  "systolicPressure": 180,
  "diastolicPressure": 110,
  "accelerationX": 0.5,
  "accelerationY": 0.3,
  "accelerationZ": 9.8,
  "behaviorNote": "跌倒"
}
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "riskLevel": "危重",
    "riskScore": 85,
    "needEmergency": true,
    "analysis": "心率过速，血压偏高，疑似跌倒",
    "recommendation": "立即调度救援"
  }
}
```

---

### 2. 路线规划

**POST** `/api/emergency/route`

**请求体：**
```json
{
  "originLat": 31.2304,
  "originLng": 121.4737,
  "destLat": 31.2404,
  "destLng": 121.4837,
  "transportMode": "driving"
}
```

---

### 3. 应急调度

**POST** `/api/emergency/dispatch`

**请求体：**
```json
{
  "userId": 1,
  "emergencyType": "medical",
  "emergencyLevel": "危重",
  "userLat": 31.2304,
  "userLng": 121.4737
}
```

**响应：**
```json
{
  "code": 200,
  "data": {
    "taskId": 1,
    "status": "dispatched",
    "eta": 15,
    "message": "已调度最近救援资源"
  }
}
```

---

### 4. 更新任务状态

**PUT** `/api/emergency/task/{taskId}/status?status={status}`

---

### 5. 计算两点间距离

**GET** `/api/emergency/distance?lat1={lat1}&lng1={lng1}&lat2={lat2}&lng2={lng2}`

---

### 6. 估算行程时间

**GET** `/api/emergency/eta?distanceKm={distance}&transportMode={mode}`

---

## AI分析模块

### 1. 分析心率数据

**GET** `/api/health/analyze/heartrate?heartRate={heartRate}`

**响应：**
```json
{
  "code": 200,
  "data": {
    "analysis": "心率正常"
  }
}
```

---

### 2. 分析血压数据

**GET** `/api/health/analyze/bloodpressure?systolic={systolic}&diastolic={diastolic}`

**响应：**
```json
{
  "code": 200,
  "data": {
    "analysis": "血压正常"
  }
}
```

---

### 3. 分析加速度数据

**GET** `/api/health/analyze/acceleration?x={x}&y={y}&z={z}`

---

## 首页数据模块

### 1. 获取首页数据

**GET** `/api/home-data`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| user_id | Integer | 否 | 用户ID，默认为1 |

**响应：**
```json
{
  "code": 200,
  "data": {
    "userName": "张三",
    "heartRate": 75,
    "bloodPressure": "120/80",
    "steps": 5000,
    "medicationReminders": [...],
    "recentAlerts": [...]
  }
}
```

---

## 管理员模块

### 1. 获取聚合用户数据

**GET** `/api/admin/users`

**响应：**
```json
{
  "code": 200,
  "data": {
    "elders": [
      {
        "elderId": 1,
        "name": "张三",
        "familyMembers": [...],
        "doctorRelations": [...]
      }
    ],
    "families": [...],
    "doctors": [...]
  }
}
```

---

### 2. 获取操作日志

**GET** `/api/admin/logs`

---

### 3. 创建操作日志

**POST** `/api/admin/logs`

**请求体：**
```json
{
  "operator": "admin",
  "operation": "删除用户"
}
```

---

### 4. 清空操作日志

**DELETE** `/api/admin/logs`

---

## 老人-医生关联模块

### 1. 获取老人的医生关联列表

**GET** `/api/elder-doctor?elderId={elderId}`

---

### 2. 创建老人-医生关联

**POST** `/api/elder-doctor`

**请求体：**
```json
{
  "elderId": 1,
  "doctorId": 1
}
```

---

### 3. 删除老人-医生关联

**DELETE** `/api/elder-doctor?elderId={elderId}&doctorId={doctorId}`

---

## 附录

### 用户角色类型

| 类型 | 说明 |
|------|------|
| elder | 老人 |
| family | 家属 |
| doctor | 医生 |
| admin | 管理员 |

### 通知类型

| 类型 | 说明 |
|------|------|
| emergency | 紧急通知 |
| alert | 预警通知 |
| info | 一般信息 |
| doctor_notification | 医生通知 |

### 风险等级

| 等级 | 说明 |
|------|------|
| 正常 | 无风险 |
| 轻度 | 轻度异常，需关注 |
| 中度 | 中度异常，需干预 |
| 危重 | 严重异常，需紧急救援 |

### 智能体工作流

```
健康数据上报
    ↓
HealthMonitorAgent（实时分析）
    ↓
检测到异常？
    ├─ 否 → 仅记录
    └─ 是 → EmergencyResponseAgent
                ↓
         风险评估 + 应急调度
                ↓
         FamilyNotificationAgent
                ↓
         通知家属和社区医生
```
