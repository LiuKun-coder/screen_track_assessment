# MyViolation 违规记录管理接口文档

## 概述
本文档定义了与 `MyViolations.vue` 组件相关的所有后端接口，用于管理用户的违规记录查询、筛选和详情查看功能。

## 基础信息
- **基础URL**: `/api/violations`
- **认证方式**: Bearer Token
- **数据格式**: JSON
- **字符编码**: UTF-8

---

## 1. 获取用户违规记录列表

### 接口信息
- **接口路径**: `GET /api/violations/my`
- **功能描述**: 获取当前用户的违规记录列表，支持分页和筛选
- **权限要求**: 需要用户登录

### 请求参数

#### Query Parameters
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码，从1开始 |
| pageSize | Integer | 否 | 4 | 每页记录数 |
| type | String | 否 | null | 违规类型筛选（超速、违停） |
| place | String | 否 | null | 违规地点筛选（东门、西门、南门、北门） |
| startDate | String | 否 | null | 开始日期（YYYY-MM-DD格式） |
| endDate | String | 否 | null | 结束日期（YYYY-MM-DD格式） |

### 请求示例
```http
GET /api/violations/my?page=1&pageSize=4&type=超速&place=东门&startDate=2024-06-01&endDate=2024-06-30
Authorization: Bearer <token>
```

### 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "time": "2024-06-01 10:00:00",
        "place": "东门",
        "type": "超速",
        "detail": "东门门口红绿灯处",
        "status": "已处理",
        "fine": 200,
        "points": 3,
        "createdAt": "2024-06-01 10:05:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 4,
      "total": 8,
      "totalPages": 2
    }
  }
}
```

### 响应字段说明
| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应状态码 |
| message | String | 响应消息 |
| data.list | Array | 违规记录列表 |
| data.list[].id | Integer | 记录ID |
| data.list[].time | String | 违规时间 |
| data.list[].place | String | 违规地点 |
| data.list[].type | String | 违规类型 |
| data.list[].detail | String | 具体位置描述 |
| data.list[].status | String | 处理状态 |
| data.list[].fine | Integer | 罚款金额 |
| data.list[].points | Integer | 扣分 |
| data.list[].createdAt | String | 记录创建时间 |
| data.pagination | Object | 分页信息 |

---

## 2. 获取违规记录详情

### 接口信息
- **接口路径**: `GET /api/violations/{id}/detail`
- **功能描述**: 获取指定违规记录的详细信息
- **权限要求**: 需要用户登录，只能查看自己的记录

### 路径参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Integer | 是 | 违规记录ID |

### 请求示例
```http
GET /api/violations/1/detail
Authorization: Bearer <token>
```

### 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "time": "2024-06-01 10:00:00",
    "place": "东门",
    "type": "超速",
    "detail": "东门门口红绿灯处",
    "status": "已处理",
    "fine": 200,
    "points": 3,
    "description": "在限速30km/h路段超速行驶",
    "evidence": {
      "images": ["image1.jpg", "image2.jpg"],
      "video": "video1.mp4"
    },
    "location": {
      "latitude": 39.9042,
      "longitude": 116.4074,
      "address": "北京市东城区东门街道"
    },
    "createdAt": "2024-06-01 10:05:00",
    "updatedAt": "2024-06-01 10:05:00"
  }
}
```

---

## 3. 获取违规类型选项

### 接口信息
- **接口路径**: `GET /api/violations/types`
- **功能描述**: 获取所有可用的违规类型选项
- **权限要求**: 需要用户登录

### 请求示例
```http
GET /api/violations/types
Authorization: Bearer <token>
```

### 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "value": "超速",
      "label": "超速",
      "description": "超过规定速度行驶"
    },
    {
      "value": "违停",
      "label": "违停",
      "description": "违规停车"
    },
    {
      "value": "闯红灯",
      "label": "闯红灯",
      "description": "违反交通信号灯"
    },
    {
      "value": "逆行",
      "label": "逆行",
      "description": "逆向行驶"
    }
  ]
}
```

---

## 4. 获取违规地点选项

### 接口信息
- **接口路径**: `GET /api/violations/places`
- **功能描述**: 获取所有可用的违规地点选项
- **权限要求**: 需要用户登录

### 请求示例
```http
GET /api/violations/places
Authorization: Bearer <token>
```

### 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "value": "东门",
      "label": "东门",
      "description": "东门区域"
    },
    {
      "value": "西门",
      "label": "西门",
      "description": "西门区域"
    },
    {
      "value": "南门",
      "label": "南门",
      "description": "南门区域"
    },
    {
      "value": "北门",
      "label": "北门",
      "description": "北门区域"
    }
  ]
}
```

---

## 5. 获取违规记录统计信息

### 接口信息
- **接口路径**: `GET /api/violations/statistics`
- **功能描述**: 获取用户违规记录的统计信息
- **权限要求**: 需要用户登录

### 请求参数
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| period | String | 否 | "month" | 统计周期（week/month/year） |

### 请求示例
```http
GET /api/violations/statistics?period=month
Authorization: Bearer <token>
```

### 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 8,
    "totalFine": 1600,
    "totalPoints": 24,
    "typeDistribution": [
      {
        "type": "超速",
        "count": 4,
        "percentage": 50
      },
      {
        "type": "违停",
        "count": 4,
        "percentage": 50
      }
    ],
    "placeDistribution": [
      {
        "place": "东门",
        "count": 2,
        "percentage": 25
      },
      {
        "place": "西门",
        "count": 2,
        "percentage": 25
      },
      {
        "place": "南门",
        "count": 2,
        "percentage": 25
      },
      {
        "place": "北门",
        "count": 2,
        "percentage": 25
      }
    ],
    "monthlyTrend": [
      {
        "month": "2024-06",
        "count": 8
      }
    ]
  }
}
```

---

## 6. 导出违规记录

### 接口信息
- **接口路径**: `GET /api/violations/export`
- **功能描述**: 导出用户的违规记录为Excel文件
- **权限要求**: 需要用户登录

### 请求参数
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| format | String | 否 | "excel" | 导出格式（excel/pdf） |
| startDate | String | 否 | null | 开始日期 |
| endDate | String | 否 | null | 结束日期 |
| type | String | 否 | null | 违规类型筛选 |
| place | String | 否 | null | 违规地点筛选 |

### 请求示例
```http
GET /api/violations/export?format=excel&startDate=2024-06-01&endDate=2024-06-30
Authorization: Bearer <token>
```

### 响应格式
```
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="violations_20240601_20240630.xlsx"
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问，权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 错误响应格式
```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "errors": [
    {
      "field": "page",
      "message": "页码必须大于0"
    }
  ]
}
```

---

## 数据库表结构建议

### violations 表
```sql
CREATE TABLE violations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  time DATETIME NOT NULL,
  place VARCHAR(50) NOT NULL,
  type VARCHAR(50) NOT NULL,
  detail TEXT,
  status VARCHAR(20) DEFAULT 'pending',
  fine DECIMAL(10,2) DEFAULT 0,
  points INT DEFAULT 0,
  description TEXT,
  latitude DECIMAL(10,8),
  longitude DECIMAL(11,8),
  address VARCHAR(255),
  evidence JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user_id (user_id),
  INDEX idx_time (time),
  INDEX idx_type (type),
  INDEX idx_place (place),
  INDEX idx_status (status)
);
```

### violation_types 表
```sql
CREATE TABLE violation_types (
  id INT PRIMARY KEY AUTO_INCREMENT,
  value VARCHAR(50) UNIQUE NOT NULL,
  label VARCHAR(50) NOT NULL,
  description TEXT,
  fine_min DECIMAL(10,2) DEFAULT 0,
  fine_max DECIMAL(10,2) DEFAULT 0,
  points_min INT DEFAULT 0,
  points_max INT DEFAULT 0,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### violation_places 表
```sql
CREATE TABLE violation_places (
  id INT PRIMARY KEY AUTO_INCREMENT,
  value VARCHAR(50) UNIQUE NOT NULL,
  label VARCHAR(50) NOT NULL,
  description TEXT,
  latitude DECIMAL(10,8),
  longitude DECIMAL(11,8),
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 安全考虑

1. **认证授权**: 所有接口都需要用户登录认证
2. **数据隔离**: 用户只能查看自己的违规记录
3. **输入验证**: 对所有输入参数进行严格验证
4. **SQL注入防护**: 使用参数化查询
5. **XSS防护**: 对输出数据进行转义
6. **CSRF防护**: 使用CSRF Token
7. **限流**: 对敏感接口实施访问频率限制

---

## 性能优化建议

1. **数据库索引**: 在常用查询字段上建立索引
2. **分页查询**: 使用LIMIT和OFFSET进行分页
3. **缓存**: 对选项数据使用Redis缓存
4. **CDN**: 静态资源使用CDN加速
5. **压缩**: 启用Gzip压缩
6. **连接池**: 使用数据库连接池

---

## 测试用例

### 1. 获取违规记录列表
```bash
curl -X GET "http://localhost:8080/api/violations/my?page=1&pageSize=4" \
  -H "Authorization: Bearer <token>"
```

### 2. 获取违规记录详情
```bash
curl -X GET "http://localhost:8080/api/violations/1/detail" \
  -H "Authorization: Bearer <token>"
```

### 3. 获取违规类型选项
```bash
curl -X GET "http://localhost:8080/api/violations/types" \
  -H "Authorization: Bearer <token>"
```

### 4. 获取违规地点选项
```bash
curl -X GET "http://localhost:8080/api/violations/places" \
  -H "Authorization: Bearer <token>"
```

### 5. 获取统计信息
```bash
curl -X GET "http://localhost:8080/api/violations/statistics?period=month" \
  -H "Authorization: Bearer <token>"
```

---

## 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0.0 | 2024-06-01 | 初始版本，包含基础CRUD接口 |
| 1.1.0 | 2024-06-15 | 新增统计接口和导出功能 |
| 1.2.0 | 2024-06-30 | 优化分页和筛选功能 | 