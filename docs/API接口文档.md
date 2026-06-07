# 老人健康监护系统 — API 接口文档

> 版本：v2.0  
> 后端框架：Java Spring Boot  
> 基础地址：`http://localhost:8080`  
> Android 模拟器访问主机：`http://10.0.2.2:8080`  
> 统一前缀：`/api`

---

## 目录

1. [通用说明](#1-通用说明)
2. [用户模块](#2-用户模块)
3. [老人管理模块](#3-老人管理模块)
4. [医生管理模块](#4-医生管理模块)
5. [首页模块](#5-首页模块)
6. [健康数据模块](#6-健康数据模块)
7. [AI 分析模块](#7-ai-分析模块)
8. [家属管理模块](#8-家属管理模块)
9. [通知模块](#9-通知模块)
10. [SOS 紧急求助模块](#10-sos-紧急求助模块)
11. [用药管理模块](#11-用药管理模块)
12. [既往病史模块](#12-既往病史模块)
13. [老人-医生关联模块](#13-老人-医生关联模块)
14. [应急响应模块](#14-应急响应模块)
15. [管理员模块](#15-管理员模块)
16. [鉴权说明](#16-鉴权说明)
17. [错误码说明](#17-错误码说明)

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
| admin | 管理员 |

---

## 2. 用户模块

### 2.1 用户登录

**POST** `/api/login`

**鉴权：** 公开

**描述：** 校验用户名和密码，返回用户信息及 Token。

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
    },
    "token": "eyJhbGciOiJIUzI1NiJ9..."
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

**描述：** 验证当前用户登录状态（有 Token 则解析 Token，否则返回固定测试用户）。

**请求参数：** 无

**成功响应：** 同登录接口，返回 `data.user` + `data.token`

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

### 2.4 获取所有用户列表

**GET** `/api/users`

**鉴权：** 需登录

**描述：** 获取系统中所有用户的列表（管理端使用）。

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "username": "elderly01",
      "name": "张大爷",
      "phone": "13800138000",
      "userType": "elder",
      "userTypeDesc": "老人",
      "createdAt": "2026-01-01T00:00:00"
    }
  ]
}
```

---

### 2.5 创建用户

**POST** `/api/users`

**鉴权：** 需登录

**描述：** 创建新用户，同时自动创建角色对应的关联记录（如创建老人时自动创建 elder 表记录）。

**请求体：**

```json
{
  "role": "elder",
  "username": "elderly02",
  "password": "123456",
  "name": "李奶奶",
  "phone": "13800138001"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| role | String | 否 | 角色：elder / family / doctor / admin，默认 elder |
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| name | String | 否 | 姓名 |
| phone | String | 否 | 手机号 |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": { "userId": 10 }
}
```

---

### 2.6 获取单个用户

**GET** `/api/users/{id}`

**鉴权：** 需登录

**描述：** 根据用户 ID 获取用户基本信息。

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| id | Integer | 用户 ID |

---

### 2.7 更新用户

**PUT** `/api/users/{id}`

**鉴权：** 需登录

**描述：** 更新用户信息，家属角色支持同步更新 `elderId` 绑定关系。

**请求体：**

```json
{
  "name": "张大爷",
  "phone": "13900139000",
  "role": "family",
  "elderIds": [1, 2]
}
```

---

### 2.8 删除用户

**DELETE** `/api/users/{id}`

**鉴权：** 需登录

**描述：** 删除指定用户。

---

## 3. 老人管理模块

> 基础路径：`/api/elders`

### 3.1 获取老人列表

**GET** `/api/elders`

**鉴权：** 公开

**描述：** 获取所有老人列表。支持 `user_id` 参数筛选单个用户的老人信息。

**Query 参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| user_id | Integer | 否 | 用户 ID，传入则只返回该用户关联的老人 |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "name": "张大爷",
      "username": "elderly01",
      "phone": "13800138000",
      "age": 72,
      "gender": "男",
      "bloodType": "A",
      "height": 170.0,
      "weight": 65.0
    }
  ]
}
```

---

### 3.2 获取单个老人信息

**GET** `/api/elders/{id}`

**鉴权：** 公开

---

### 3.3 根据医生获取签约老人列表

**GET** `/api/elders/doctor/{doctorUserId}`

**鉴权：** 公开

**描述：** 根据医生的用户 ID 获取该医生签约的所有老人列表。

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| doctorUserId | Integer | 医生的 user 表 ID |

---

### 3.4 创建老人

**POST** `/api/elders`

**鉴权：** 公开

---

### 3.5 更新老人

**PUT** `/api/elders/{id}`

**鉴权：** 公开

---

### 3.6 删除老人

**DELETE** `/api/elders/{id}`

**鉴权：** 公开

---

## 4. 医生管理模块

> 基础路径：`/api/doctors`

### 4.1 获取所有医生

**GET** `/api/doctors`

**鉴权：** 公开

---

### 4.2 获取单个医生

**GET** `/api/doctors/{id}`

**鉴权：** 公开

| 路径参数 | 类型 | 说明 |
|----------|------|------|
| id | Integer | 医生 ID |

---

### 4.3 按科室查询医生

**GET** `/api/doctors/department/{department}`

**鉴权：** 公开

| 路径参数 | 类型 | 说明 |
|----------|------|------|
| department | String | 科室名称 |

---

### 4.4 按姓名搜索医生

**GET** `/api/doctors/search?name={name}`

**鉴权：** 公开

| Query 参数 | 类型 | 必填 | 说明 |
|------------|------|------|------|
| name | String | 是 | 医生姓名（模糊匹配） |

---

### 4.5 创建医生

**POST** `/api/doctors`

**鉴权：** 公开

---

### 4.6 更新医生

**PUT** `/api/doctors/{id}`

**鉴权：** 公开

---

### 4.7 删除医生

**DELETE** `/api/doctors/{id}`

**鉴权：** 公开

---

## 5. 首页模块

### 5.1 获取首页数据

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

## 6. 健康数据模块

### 6.1 获取健康数据列表

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

### 6.2 上传健康数据

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

## 7. AI 分析模块

> 后端内部调用 DeepSeek API（`deepseek-v4-flash`）进行大模型分析。

### 7.1 心率 AI 分析

**GET** `/api/health/analyze/heartrate`

**鉴权：** 公开

**Query 参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| heartRate | Integer | 是 | 心率值（bpm） |

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

### 7.2 血压 AI 分析

**GET** `/api/health/analyze/bloodpressure`

**鉴权：** 公开

**Query 参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| systolic | Integer | 是 | 收缩压（mmHg） |
| diastolic | Integer | 是 | 舒张压（mmHg） |

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

### 7.3 加速度 AI 分析

**GET** `/api/health/analyze/acceleration`

**鉴权：** 公开

**描述：** 调用大模型分析三轴加速度，判断跌倒风险或异常活动。**分析结果仅在后端控制台输出，不返回给前端。**

**Query 参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| x | Double | 是 | X 轴加速度（m/s²） |
| y | Double | 是 | Y 轴加速度（m/s²） |
| z | Double | 是 | Z 轴加速度（m/s²） |

**成功响应：**

```json
{
  "code": 200,
  "message": "received",
  "data": null
}
```

---

## 8. 家属管理模块

### 8.1 获取老人名下的家属列表

**GET** `/api/family-members/elder/{elderId}`

**鉴权：** 公开

**描述：** 获取某位老人名下的所有家属联系人。

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| elderId | Integer | 老人 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 5,
      "elderId": 1,
      "name": "张小明",
      "relationship": "儿子",
      "phone": "13900139000"
    }
  ]
}
```

---

### 8.2 获取单个家属成员

**GET** `/api/family-members/{id}`

**鉴权：** 公开

---

### 8.3 获取所有家属成员

**GET** `/api/family-members`

**鉴权：** 公开

---

### 8.4 添加家属成员

**POST** `/api/family-members`

**鉴权：** 公开

**请求体：**

```json
{
  "userId": 5,
  "elderId": 1,
  "name": "张小明",
  "relationship": "儿子",
  "phone": "13900139000"
}
```

---

### 8.5 更新家属成员

**PUT** `/api/family-members/{id}`

**鉴权：** 公开

---

### 8.6 删除家属成员

**DELETE** `/api/family-members/{id}`

**鉴权：** 公开

---

## 9. 通知模块

> 基础路径：`/api/notifications`  
> 为移动端家属端 APP 提供通知查询、确认等接口。

### 9.1 获取通知列表

**GET** `/api/notifications?userId={userId}&type={type}`

**鉴权：** 公开

**Query 参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Integer | 是 | 老人用户 ID |
| type | String | 否 | 通知类型过滤：emergency / alert / info / doctor_notification |

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "type": "alert",
      "title": "心率异常提醒",
      "message": "老人心率偏高，请注意观察",
      "status": "sent",
      "createdAt": "2026-06-07T10:30:00"
    }
  ]
}
```

---

### 9.2 确认单条通知

**PUT** `/api/notifications/{id}/acknowledge`

**鉴权：** 公开

| 路径参数 | 类型 | 说明 |
|----------|------|------|
| id | Integer | 通知记录 ID |

---

### 9.3 批量确认通知

**PUT** `/api/notifications/acknowledge-all?userId={userId}&type={type}`

**鉴权：** 公开

| Query 参数 | 类型 | 必填 | 说明 |
|------------|------|------|------|
| userId | Integer | 是 | 老人用户 ID |
| type | String | 否 | 通知类型过滤 |

---

## 10. SOS 紧急求助模块

### 10.1 发送 SOS 求助

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

### 10.2 取消 SOS 求助

**POST** `/api/sos/{sosId}/cancel`

**鉴权：** 需登录

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

## 11. 用药管理模块

### 11.1 获取用药列表

**GET** `/api/medications`

**鉴权：** 需登录

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

### 11.2 添加用药计划

**POST** `/api/medications`

**鉴权：** 需登录

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

---

### 11.3 更新用药计划

**PUT** `/api/medications/{id}`

**鉴权：** 需登录

---

### 11.4 删除用药计划

**DELETE** `/api/medications/{id}`

**鉴权：** 需登录

---

### 11.5 记录服药（打卡）

**POST** `/api/medications/{medId}/record`

**鉴权：** 需登录

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

### 11.6 后端连通性检查

**GET** `/api/config`

**鉴权：** 公开

**描述：** 后端服务连通性检查，返回服务状态和当前时间。

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "connected",
    "serverTime": "2026-06-07T10:00:00"
  }
}
```

---

## 12. 既往病史模块

> 基础路径：`/api/medical-history`

### 12.1 获取老人既往病史

**GET** `/api/medical-history/elder/{elderId}`

**鉴权：** 公开

| 路径参数 | 类型 | 说明 |
|----------|------|------|
| elderId | Integer | 老人 ID |

---

### 12.2 添加既往病史

**POST** `/api/medical-history`

**鉴权：** 公开

---

### 12.3 更新既往病史

**PUT** `/api/medical-history/{id}`

**鉴权：** 公开

---

### 12.4 删除既往病史

**DELETE** `/api/medical-history/{id}`

**鉴权：** 公开

---

### 12.5 批量保存既往病史

**POST** `/api/medical-history/batch/{elderId}`

**鉴权：** 公开

**描述：** 批量保存老人的既往病史记录。

| 路径参数 | 类型 | 说明 |
|----------|------|------|
| elderId | Integer | 老人 ID |

---

## 13. 老人-医生关联模块

> 基础路径：`/api/elder-doctor`

### 13.1 查询关联列表

**GET** `/api/elder-doctor?elderId={elderId}`

**鉴权：** 公开

| Query 参数 | 类型 | 必填 | 说明 |
|------------|------|------|------|
| elderId | Integer | 是 | 老人 ID |

---

### 13.2 创建关联

**POST** `/api/elder-doctor`

**鉴权：** 公开

**请求体：**

```json
{
  "elderId": 1,
  "doctorId": 2
}
```

---

### 13.3 删除关联

**DELETE** `/api/elder-doctor?elderId={elderId}&doctorId={doctorId}`

**鉴权：** 公开

| Query 参数 | 类型 | 必填 | 说明 |
|------------|------|------|------|
| elderId | Integer | 是 | 老人 ID |
| doctorId | Integer | 是 | 医生 ID |

---

## 14. 应急响应模块

> 基础路径：`/api/emergency`  
> 多智能体应急响应子系统，包含风险评估、路线规划、应急调度、人工决策等。

### 14.1 风险评估

**POST** `/api/emergency/assess`

**鉴权：** 公开

**描述：** 基于健康数据进行风险评估，返回风险等级和建议。

---

### 14.2 路线规划

**POST** `/api/emergency/route`

**鉴权：** 公开

**描述：** 规划救援路线，返回最优路径信息。

---

### 14.3 应急调度

**POST** `/api/emergency/dispatch`

**鉴权：** 公开

**描述：** 执行应急调度，派遣救援资源。

---

### 14.4 更新任务状态

**PUT** `/api/emergency/task/{taskId}/status?status={status}`

**鉴权：** 公开

| 路径参数 | 类型 | 说明 |
|----------|------|------|
| taskId | Integer | 任务 ID |

| Query 参数 | 类型 | 必填 | 说明 |
|------------|------|------|------|
| status | String | 是 | 任务状态 |

---

### 14.5 计算距离

**GET** `/api/emergency/distance?lat1={lat1}&lng1={lng1}&lat2={lat2}&lng2={lng2}`

**鉴权：** 公开

**描述：** 计算两点间直线距离（单位：km）。

---

### 14.6 估算行程时间

**GET** `/api/emergency/eta?distanceKm={distanceKm}&transportMode={mode}`

**鉴权：** 公开

**描述：** 根据距离和交通方式估算行程时间（单位：分钟）。

| Query 参数 | 类型 | 必填 | 默认值 | 说明 |
|------------|------|------|--------|------|
| distanceKm | Double | 是 | - | 距离（千米） |
| transportMode | String | 否 | driving | 交通方式：driving / walking |

---

### 14.7 获取待人工决策事件

**GET** `/api/emergency/pending-events`

**鉴权：** 公开

**描述：** 获取当前需要人工决策的告警事件列表。

---

### 14.8 人工决策处置

**POST** `/api/emergency/human-decision?eventId={eventId}&decision={decision}&notes={notes}`

**鉴权：** 公开

| Query 参数 | 类型 | 必填 | 说明 |
|------------|------|------|------|
| eventId | String | 是 | 事件 ID |
| decision | String | 是 | 决策：DISPATCH_AMBULANCE / HOME_VISIT / MONITOR / DISMISS |
| notes | String | 否 | 决策备注 |

---

### 14.9 人工修正风险等级

**PUT** `/api/emergency/correct-risk-level?eventId={eventId}&correctedLevel={level}&reason={reason}`

**鉴权：** 公开

| Query 参数 | 类型 | 必填 | 说明 |
|------------|------|------|------|
| eventId | String | 是 | 事件 ID |
| correctedLevel | String | 是 | 修正后等级：MINOR / MODERATE / CRITICAL |
| reason | String | 是 | 修正原因 |

---

### 14.10 获取告警队列状态

**GET** `/api/emergency/queue`

**鉴权：** 公开

**描述：** 获取当前告警队列的快照。

---

### 14.11 获取待处理事件数量

**GET** `/api/emergency/staff/count`

**鉴权：** 公开

**描述：** 获取当前待人工决策的事件数量。

---

## 15. 管理员模块

### 15.1 管理员聚合查询

**GET** `/api/admin/users`

**鉴权：** 需登录

**描述：** 获取所有老人、家属、医生的聚合数据（管理端使用）。

**成功响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "elders": [
      {
        "elderId": 1,
        "name": "张大爷",
        "familyMembers": [],
        "doctorRelations": []
      }
    ],
    "families": [],
    "doctors": []
  }
}
```

---

### 15.2 获取操作日志

**GET** `/api/admin/logs`

**鉴权：** 需登录

---

### 15.3 创建操作日志

**POST** `/api/admin/logs`

**鉴权：** 需登录

---

### 15.4 清空操作日志

**DELETE** `/api/admin/logs`

**鉴权：** 需登录

---

## 16. 鉴权说明

| 接口 | 鉴权要求 |
|------|---------|
| POST `/api/login` | 公开 |
| GET `/api/verify` | 公开 |
| GET `/api/user/{userId}` | **需登录** |
| GET `/api/users`, POST `/api/users`, GET/PUT/DELETE `/api/users/{id}` | **需登录** |
| GET/POST/PUT/DELETE `/api/elders/**` | 公开 |
| GET/POST/PUT/DELETE `/api/doctors/**` | 公开 |
| GET `/api/home-data` | 公开 |
| GET/POST `/api/health-data` | 公开 |
| GET `/api/health/analyze/**` | 公开 |
| GET/POST/PUT/DELETE `/api/family-members/**` | 公开 |
| GET/PUT `/api/notifications/**` | 公开 |
| POST `/api/sos` | **需登录** |
| POST `/api/sos/{sosId}/cancel` | **需登录** |
| GET/POST/PUT/DELETE `/api/medications/**` | **需登录** |
| GET `/api/config` | 公开 |
| GET/POST/PUT/DELETE `/api/medical-history/**` | 公开 |
| GET/POST/DELETE `/api/elder-doctor` | 公开 |
| POST/PUT/GET `/api/emergency/**` | 公开 |
| GET `/api/admin/users` | **需登录** |
| GET/POST/DELETE `/api/admin/logs` | **需登录** |

---

## 17. 错误码说明

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
| 4 | GET | `/api/users` | 用户管理 |
| 5 | POST | `/api/users` | 用户管理 |
| 6 | GET | `/api/users/{id}` | 用户管理 |
| 7 | PUT | `/api/users/{id}` | 用户管理 |
| 8 | DELETE | `/api/users/{id}` | 用户管理 |
| 9 | GET | `/api/elders` | 老人管理 |
| 10 | GET | `/api/elders/{id}` | 老人管理 |
| 11 | GET | `/api/elders/doctor/{doctorUserId}` | 老人管理 |
| 12 | POST | `/api/elders` | 老人管理 |
| 13 | PUT | `/api/elders/{id}` | 老人管理 |
| 14 | DELETE | `/api/elders/{id}` | 老人管理 |
| 15 | GET | `/api/doctors` | 医生管理 |
| 16 | GET | `/api/doctors/{id}` | 医生管理 |
| 17 | GET | `/api/doctors/department/{department}` | 医生管理 |
| 18 | GET | `/api/doctors/search` | 医生管理 |
| 19 | POST | `/api/doctors` | 医生管理 |
| 20 | PUT | `/api/doctors/{id}` | 医生管理 |
| 21 | DELETE | `/api/doctors/{id}` | 医生管理 |
| 22 | GET | `/api/home-data` | 首页 |
| 23 | GET | `/api/health-data` | 健康数据 |
| 24 | POST | `/api/health-data` | 健康数据 |
| 25 | GET | `/api/health/analyze/heartrate` | AI 分析 |
| 26 | GET | `/api/health/analyze/bloodpressure` | AI 分析 |
| 27 | GET | `/api/health/analyze/acceleration` | AI 分析 |
| 28 | GET | `/api/family-members` | 家属管理 |
| 29 | GET | `/api/family-members/{id}` | 家属管理 |
| 30 | GET | `/api/family-members/elder/{elderId}` | 家属管理 |
| 31 | POST | `/api/family-members` | 家属管理 |
| 32 | PUT | `/api/family-members/{id}` | 家属管理 |
| 33 | DELETE | `/api/family-members/{id}` | 家属管理 |
| 34 | GET | `/api/notifications` | 通知 |
| 35 | PUT | `/api/notifications/{id}/acknowledge` | 通知 |
| 36 | PUT | `/api/notifications/acknowledge-all` | 通知 |
| 37 | POST | `/api/sos` | SOS |
| 38 | POST | `/api/sos/{sosId}/cancel` | SOS |
| 39 | GET | `/api/medications` | 用药 |
| 40 | POST | `/api/medications` | 用药 |
| 41 | PUT | `/api/medications/{id}` | 用药 |
| 42 | DELETE | `/api/medications/{id}` | 用药 |
| 43 | POST | `/api/medications/{medId}/record` | 用药 |
| 44 | GET | `/api/config` | 系统 |
| 45 | GET | `/api/medical-history/elder/{elderId}` | 既往病史 |
| 46 | POST | `/api/medical-history` | 既往病史 |
| 47 | PUT | `/api/medical-history/{id}` | 既往病史 |
| 48 | DELETE | `/api/medical-history/{id}` | 既往病史 |
| 49 | POST | `/api/medical-history/batch/{elderId}` | 既往病史 |
| 50 | GET | `/api/elder-doctor` | 老人-医生关联 |
| 51 | POST | `/api/elder-doctor` | 老人-医生关联 |
| 52 | DELETE | `/api/elder-doctor` | 老人-医生关联 |
| 53 | POST | `/api/emergency/assess` | 应急响应 |
| 54 | POST | `/api/emergency/route` | 应急响应 |
| 55 | POST | `/api/emergency/dispatch` | 应急响应 |
| 56 | PUT | `/api/emergency/task/{taskId}/status` | 应急响应 |
| 57 | GET | `/api/emergency/distance` | 应急响应 |
| 58 | GET | `/api/emergency/eta` | 应急响应 |
| 59 | GET | `/api/emergency/pending-events` | 应急响应 |
| 60 | POST | `/api/emergency/human-decision` | 应急响应 |
| 61 | PUT | `/api/emergency/correct-risk-level` | 应急响应 |
| 62 | GET | `/api/emergency/queue` | 应急响应 |
| 63 | GET | `/api/emergency/staff/count` | 应急响应 |
| 64 | GET | `/api/admin/users` | 管理员 |
| 65 | GET | `/api/admin/logs` | 管理员 |
| 66 | POST | `/api/admin/logs` | 管理员 |
| 67 | DELETE | `/api/admin/logs` | 管理员 |

**共计 67 个 REST 接口。**

---

## 附录 B：快速测试命令

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

# 后端连通性检查
curl "http://localhost:8080/api/config"

# 上传健康数据
curl -X POST http://localhost:8080/api/health-data \
  -H "Content-Type: application/json" \
  -d "{\"userId\":1,\"heartRate\":72,\"systolicPressure\":125,\"diastolicPressure\":84,\"steps\":5200}"

# 获取通知列表
curl "http://localhost:8080/api/notifications?userId=1"

# 发送 SOS
curl -X POST http://localhost:8080/api/sos \
  -H "Content-Type: application/json" \
  -d "{\"userId\":1,\"location\":\"北京市朝阳区XX路XX号\"}"

# 获取用药列表
curl "http://localhost:8080/api/medications?user_id=1"
```
