# 老人健康监护系统 — API 接口文档

> 版本：v1.0  
> 后端框架：Java Spring Boot  
> 基础地址：`http://localhost:8080`  
> Android 模拟器访问主机：`http://10.0.2.2:8080`  
> 统一前缀：`/api`

---

## 目录

1. [通用说明](#1-通用说明)
2. [用户模块](#2-用户模块)
3. [首页模块](#3-首页模块)
4. [健康数据模块](#4-健康数据模块)
5. [AI 分析模块](#5-ai-分析模块)
6. [家属绑定模块](#6-家属绑定模块)
7. [家庭成员模块](#7-家庭成员模块)
8. [SOS 紧急求助模块](#8-sos-紧急求助模块)
9. [医生咨询模块](#9-医生咨询模块)
10. [用药管理模块](#10-用药管理模块)
11. [鉴权说明](#11-鉴权说明)
12. [错误码说明](#12-错误码说明)

---

## 1. 通用说明

### 1.1 统一响应格式

所有接口 HTTP 状态码均为 `200`，业务成败通过响应体中的 `code` 字段判断。

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 业务状态码，200 表示成功 |
| message | String | 提示信息 |
| data | Object / Array / null | 业务数据 |

### 1.2 请求头

```http
Content-Type: application/json
```

### 1.3 用户类型（userType）

| 值 | 说明 |
|----|------|
| elderly | 老人 |
| family | 家属 |
| doctor | 医生 |

---

## 2. 用户模块

### 2.1 用户登录

**POST** `/api/login`

**鉴权：** 公开

**描述：** 校验用户名和密码，返回用户信息。

**请求体：**

```json
{
  "username": "elderly01",
  "password": "123456"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "user": {
      "id": 1,
      "name": "张大爷",
      "userType": "elderly",
      "age": 72,
      "gender": "男",
      "bloodType": "A",
      "height": 170.0,
      "weight": 65.0
    }
  }
}
```

**失败响应：**

```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

---

### 2.2 验证登录状态

**GET** `/api/verify`

**鉴权：** 公开

**描述：** 验证当前用户登录状态（开发阶段返回固定测试用户）。

**请求参数：** 无

**成功响应：** 同登录接口，返回 `data.user`

---

### 2.3 获取用户信息

**GET** `/api/user/{userId}`

**鉴权：** 需登录

**描述：** 根据用户 ID 获取用户详细信息。

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| userId | Integer | 用户 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "张大爷",
    "userType": "elderly",
    "age": 72,
    "gender": "男",
    "bloodType": "A",
    "height": 170.0,
    "weight": 65.0
  }
}
```

**失败响应：**

```json
{
  "code": 404,
  "message": "用户不存在",
  "data": null
}
```

---

## 3. 首页模块

### 3.1 获取首页数据

**GET** `/api/home-data`

**鉴权：** 公开

**描述：** 获取老人端首页聚合数据，包含用户信息、最新健康摘要和当前日期。

**Query 参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| user_id | Integer | 否 | 1 | 用户 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "user": {
      "name": "张大爷",
      "age": 72
    },
    "health": {
      "steps": 5200,
      "heartRate": 72
    },
    "date": "2026年05月31日"
  }
}
```

---

## 4. 健康数据模块

### 4.1 获取健康数据列表

**GET** `/api/health-data`

**鉴权：** 公开

**描述：** 获取指定用户最近 10 条健康记录，按记录时间倒序。

**Query 参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| user_id | Integer | 否 | 1 | 用户 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 10,
      "heartRate": 72,
      "systolicPressure": 125,
      "diastolicPressure": 84,
      "steps": 5200,
      "recordedAt": "2026-05-31 14:30:00"
    }
  ]
}
```

---

### 4.2 上传健康数据

**POST** `/api/health-data`

**鉴权：** 公开

**描述：** 新增一条健康监测数据。

**请求体：**

```json
{
  "userId": 1,
  "heartRate": 72,
  "systolicPressure": 125,
  "diastolicPressure": 84,
  "steps": 5200
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Integer | 是 | 用户 ID |
| heartRate | Integer | 否 | 心率（bpm） |
| systolicPressure | Integer | 否 | 收缩压（mmHg） |
| diastolicPressure | Integer | 否 | 舒张压（mmHg） |
| steps | Integer | 否 | 步数 |

**成功响应：**

```json
{
  "code": 200,
  "message": "Health data added successfully",
  "data": null
}
```

---

## 5. AI 分析模块

> 后端内部调用 DeepSeek API（`deepseek-v4-flash`）进行大模型分析。

### 5.1 心率 AI 分析

**GET** `/api/health/analyze/heartrate`

**鉴权：** 公开

**描述：** 调用大模型分析心率数据，返回简洁健康建议（老人端展示）。

**Query 参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| heartRate | Integer | 是 | 心率值（bpm） |

**请求示例：**

```
GET /api/health/analyze/heartrate?heartRate=72
```

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "analysis": "心率正常，继续保持。"
  }
}
```

---

### 5.2 血压 AI 分析

**GET** `/api/health/analyze/bloodpressure`

**鉴权：** 公开

**描述：** 调用大模型分析血压数据，返回简洁健康建议（老人端展示）。

**Query 参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| systolic | Integer | 是 | 收缩压（mmHg） |
| diastolic | Integer | 是 | 舒张压（mmHg） |

**请求示例：**

```
GET /api/health/analyze/bloodpressure?systolic=125&diastolic=84
```

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "analysis": "血压偏高，请注意低盐饮食，每天走路半小时。"
  }
}
```

---

### 5.3 加速度 AI 分析

**GET** `/api/health/analyze/acceleration`

**鉴权：** 公开

**描述：** 调用大模型分析三轴加速度，判断跌倒风险或异常活动。**分析结果仅在后端控制台输出，不返回给前端。**

**Query 参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| x | Double | 是 | X 轴加速度（m/s²） |
| y | Double | 是 | Y 轴加速度（m/s²） |
| z | Double | 是 | Z 轴加速度（m/s²） |

**请求示例：**

```
GET /api/health/analyze/acceleration?x=0.12&y=-0.45&z=9.76
```

**成功响应：**

```json
{
  "code": 200,
  "message": "received",
  "data": null
}
```

**后端日志示例：**

```
【加速度AI分析成功】x=0.12, y=-0.45, z=9.76, 分析结果: 活动正常，未检测到异常。
```

---

## 6. 家属绑定模块

> 基础路径：`/api/family`

### 6.1 获取已绑定老人列表

**GET** `/api/family/list`

**鉴权：** 公开

**描述：** 获取家属账号已绑定的老人列表。

**Query 参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| familyId | Integer | 否 | 1 | 家属用户 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "elderlyId": 2,
      "name": "张大爷",
      "relationship": "儿子",
      "phone": "13800138000"
    }
  ]
}
```

---

### 6.2 绑定老人

**POST** `/api/family/bind`

**鉴权：** 公开

**描述：** 家属账号绑定一位老人。

**请求体：**

```json
{
  "familyId": 1,
  "elderlyId": 2,
  "relationship": "儿子"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| familyId | Integer | 是 | 家属用户 ID |
| elderlyId | Integer | 是 | 老人用户 ID |
| relationship | String | 是 | 与老人的关系 |

**成功响应：**

```json
{
  "code": 200,
  "message": "绑定成功",
  "data": null
}
```

**失败响应：**

```json
{
  "code": 400,
  "message": "该老人已绑定",
  "data": null
}
```

---

### 6.3 解绑老人

**POST** `/api/family/unbind`

**鉴权：** 公开

**描述：** 解除家属与老人的绑定关系。

**请求体：**

```json
{
  "familyId": 1,
  "elderlyId": 2
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| familyId | Integer | 是 | 家属用户 ID |
| elderlyId | Integer | 是 | 老人用户 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "解绑成功",
  "data": null
}
```

---

### 6.4 搜索老人

**GET** `/api/family/search`

**鉴权：** 公开

**描述：** 通过手机号或用户名搜索可绑定的老人。

**Query 参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 是 | 搜索关键词（手机号或用户名） |

**请求示例：**

```
GET /api/family/search?keyword=13800138000
```

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "name": "张大爷",
      "phone": "13800138000",
      "age": 72
    }
  ]
}
```

---

## 7. 家庭成员模块

### 7.1 获取家属列表

**GET** `/api/family-members`

**鉴权：** 公开

**描述：** 获取某位老人名下的家属联系人列表。

**Query 参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| user_id | Integer | 否 | 1 | 老人用户 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "张小明",
      "relationship": "儿子",
      "phone": "13900139000"
    }
  ]
}
```

---

### 7.2 添加家属

**POST** `/api/family-members`

**鉴权：** 公开

**描述：** 为老人添加一位家属联系人。

**请求体：**

```json
{
  "userId": 1,
  "name": "张小明",
  "relationship": "儿子",
  "phone": "13900139000"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Integer | 是 | 老人用户 ID |
| name | String | 是 | 家属姓名 |
| relationship | String | 是 | 关系 |
| phone | String | 是 | 联系电话 |

**成功响应：**

```json
{
  "code": 200,
  "message": "Family member added successfully",
  "data": null
}
```

---

### 7.3 获取联系记录

**GET** `/api/contact-records`

**鉴权：** 公开

**描述：** 获取老人与家属的联系记录。

**Query 参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| user_id | Integer | 否 | 1 | 老人用户 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "familyMemberName": "张小明",
      "type": "call",
      "status": "completed",
      "createdAt": "2026-05-31 10:00:00"
    }
  ]
}
```

---

## 8. SOS 紧急求助模块

### 8.1 发送 SOS 求助

**POST** `/api/sos`

**鉴权：** 需登录

**描述：** 老人发起紧急 SOS 求助，系统记录位置并通知家属。

**请求体：**

```json
{
  "userId": 1,
  "location": "北京市朝阳区XX路XX号"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Integer | 是 | 老人用户 ID |
| location | String | 是 | 当前位置描述 |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "message": "SOS sent successfully",
    "sos_id": 5
  }
}
```

---

### 8.2 取消 SOS 求助

**POST** `/api/sos/{sosId}/cancel`

**鉴权：** 需登录

**描述：** 取消已发起的 SOS 求助。

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| sosId | Integer | SOS 记录 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "SOS cancelled successfully",
  "data": null
}
```

**失败响应：**

```json
{
  "code": 404,
  "message": "SOS记录不存在",
  "data": null
}
```

---

## 9. 医生咨询模块

### 9.1 获取咨询记录

**GET** `/api/consultations`

**鉴权：** 需登录

**描述：** 获取用户的历史咨询记录列表。

**Query 参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| user_id | Integer | 否 | 1 | 用户 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "message": "最近经常头晕怎么办？",
      "response": "建议先测量血压，保持充足休息...",
      "type": "text",
      "createdAt": "2026-05-31 09:00:00"
    }
  ]
}
```

---

### 9.2 提交咨询

**POST** `/api/consultations`

**鉴权：** 需登录

**描述：** 提交一条新的健康咨询，系统返回咨询记录（含 AI/医生回复）。

**请求体：**

```json
{
  "userId": 1,
  "message": "最近经常头晕怎么办？",
  "type": "text"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Integer | 是 | 用户 ID |
| message | String | 是 | 咨询内容 |
| type | String | 否 | 咨询类型，默认 `text` |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2,
    "message": "最近经常头晕怎么办？",
    "response": "建议先测量血压...",
    "type": "text",
    "createdAt": "2026-05-31 14:00:00"
  }
}
```

---

## 10. 用药管理模块

### 10.1 获取用药列表

**GET** `/api/medications`

**鉴权：** 需登录

**描述：** 获取用户的用药计划列表。

**Query 参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| user_id | Integer | 否 | 1 | 用户 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "阿司匹林",
      "description": "抗血小板",
      "dosage": "1片",
      "frequency": "每日1次",
      "time": "08:00"
    }
  ]
}
```

---

### 10.2 添加用药计划

**POST** `/api/medications`

**鉴权：** 需登录

**描述：** 为用户添加一条用药提醒计划。

**请求体：**

```json
{
  "userId": 1,
  "name": "阿司匹林",
  "description": "抗血小板",
  "dosage": "1片",
  "frequency": "每日1次",
  "time": "08:00"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Integer | 是 | 用户 ID |
| name | String | 是 | 药品名称 |
| description | String | 否 | 药品说明 |
| dosage | String | 是 | 服用剂量 |
| frequency | String | 是 | 服用频次 |
| time | String | 是 | 提醒时间（如 `08:00`） |

**成功响应：**

```json
{
  "code": 200,
  "message": "Medication added successfully",
  "data": null
}
```

---

### 10.3 记录服药（打卡）

**POST** `/api/medications/{medId}/record`

**鉴权：** 需登录

**描述：** 记录一次服药行为（用药打卡）。

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| medId | Integer | 用药计划 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "Medication recorded successfully",
  "data": null
}
```

---

## 11. 鉴权说明

| 接口 | 鉴权要求 |
|------|---------|
| POST `/api/login` | 公开 |
| GET `/api/verify` | 公开 |
| GET `/api/home-data` | 公开 |
| GET/POST `/api/health-data` | 公开 |
| GET `/api/health/analyze/**` | 公开 |
| GET/POST `/api/family/**` | 公开 |
| GET/POST `/api/family-members` | 公开 |
| GET `/api/contact-records` | 公开 |
| GET `/api/user/{userId}` | **需登录** |
| POST `/api/sos` | **需登录** |
| POST `/api/sos/{sosId}/cancel` | **需登录** |
| GET/POST `/api/consultations` | **需登录** |
| GET/POST `/api/medications` | **需登录** |
| POST `/api/medications/{medId}/record` | **需登录** |

---

## 12. 错误码说明

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误或业务校验失败 |
| 401 | 登录失败 / 未授权 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 附录 A：接口总览

| # | 方法 | 路径 | 模块 |
|---|------|------|------|
| 1 | POST | `/api/login` | 用户 |
| 2 | GET | `/api/verify` | 用户 |
| 3 | GET | `/api/user/{userId}` | 用户 |
| 4 | GET | `/api/home-data` | 首页 |
| 5 | GET | `/api/health-data` | 健康数据 |
| 6 | POST | `/api/health-data` | 健康数据 |
| 7 | GET | `/api/health/analyze/heartrate` | AI 分析 |
| 8 | GET | `/api/health/analyze/bloodpressure` | AI 分析 |
| 9 | GET | `/api/health/analyze/acceleration` | AI 分析 |
| 10 | GET | `/api/family/list` | 家属绑定 |
| 11 | POST | `/api/family/bind` | 家属绑定 |
| 12 | POST | `/api/family/unbind` | 家属绑定 |
| 13 | GET | `/api/family/search` | 家属绑定 |
| 14 | GET | `/api/family-members` | 家庭成员 |
| 15 | POST | `/api/family-members` | 家庭成员 |
| 16 | GET | `/api/contact-records` | 家庭成员 |
| 17 | POST | `/api/sos` | SOS |
| 18 | POST | `/api/sos/{sosId}/cancel` | SOS |
| 19 | GET | `/api/consultations` | 咨询 |
| 20 | POST | `/api/consultations` | 咨询 |
| 21 | GET | `/api/medications` | 用药 |
| 22 | POST | `/api/medications` | 用药 |
| 23 | POST | `/api/medications/{medId}/record` | 用药 |

**共计 23 个 REST 接口。**

---

## 附录 B：移动端调用对照

| 移动端方法 | 文件 | 对应后端接口 | 状态 |
|-----------|------|-------------|------|
| `login()` | api_service.dart | POST `/api/login` | ✅ 已对接 |
| `getBoundElderlyList()` | api_service.dart | GET `/api/family/list` | ✅ |
| `bindElderly()` | api_service.dart | POST `/api/family/bind` | ✅ |
| `unbindElderly()` | api_service.dart | POST `/api/family/unbind` | ✅ |
| `searchElderly()` | api_service.dart | GET `/api/family/search` | ✅ |
| `uploadHealthData()` | api_service.dart | POST `/health/upload` | ⚠️ 路径不一致，后端应为 `/health-data` |
| `getLatestHealthData()` | api_service.dart | GET `/health/latest` | ⚠️ 后端未实现 |
| `getHealthHistory()` | api_service.dart | GET `/health/history` | ⚠️ 后端未实现 |
| `getHealthStatistics()` | api_service.dart | GET `/health/statistics` | ⚠️ 后端未实现 |
| `analyzeHeartRate()` | ai_service.dart | GET `/health/analyze/heartrate` | ✅ 已对接 |
| `analyzeBloodPressure()` | ai_service.dart | GET `/health/analyze/bloodpressure` | ✅ 已对接 |
| `reportAcceleration()` | ai_service.dart | GET `/health/analyze/acceleration` | ✅ 已对接 |

---

## 附录 C：快速测试命令

```bash
# 登录
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"elderly01\",\"password\":\"123456\"}"

# 心率 AI 分析
curl "http://localhost:8080/api/health/analyze/heartrate?heartRate=72"

# 血压 AI 分析
curl "http://localhost:8080/api/health/analyze/bloodpressure?systolic=125&diastolic=84"

# 加速度 AI 分析
curl "http://localhost:8080/api/health/analyze/acceleration?x=0.1&y=0.2&z=9.8"

# 获取首页数据
curl "http://localhost:8080/api/home-data?user_id=1"

# 上传健康数据
curl -X POST http://localhost:8080/api/health-data \
  -H "Content-Type: application/json" \
  -d "{\"userId\":1,\"heartRate\":72,\"systolicPressure\":125,\"diastolicPressure\":84,\"steps\":5200}"
```
