# 用户模块 API 文档

## 功能概述
用户模块提供用户注册、登录、信息查询等基本功能，使用UUID作为临时token，不依赖外部认证服务。

## API 接口

### 1. 用户注册
- **接口地址**: `POST /api/user/register`
- **请求参数**:
  ```json
  {
    "username": "用户名(3-20位字母数字下划线)",
    "password": "密码(至少包含一个字母和数字，6-20位)",
    "email": "邮箱(可选)",
    "phone": "手机号(可选)"
  }
  ```
- **响应示例**:
  ```json
  {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "nickname": "testuser",
    "createTime": "2025-10-02T17:00:00"
  }
  ```

### 2. 用户登录
- **接口地址**: `POST /api/user/login`
- **请求参数**:
  ```json
  {
    "username": "用户名",
    "password": "密码"
  }
  ```
- **响应示例**:
  ```json
  {
    "token": "550e8400-e29b-41d4-a716-446655440000",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "nickname": "testuser",
      "createTime": "2025-10-02T17:00:00"
    }
  }
  ```

### 3. 获取用户信息
- **接口地址**: `GET /api/user/profile?token=xxx`
- **请求参数**:
  - `token`: 用户登录后返回的token
- **响应示例**:
  ```json
  {
    "id": 1,
    "username": "testuser",
    "nickname": "测试用户",
    "email": "test@example.com",
    "createTime": "2025-10-02T17:00:00"
  }
  ```

### 4. 用户登出
- **接口地址**: `POST /api/user/logout?token=xxx`
- **请求参数**:
  - `token`: 用户登录后返回的token
- **响应示例**:
  ```json
  "登出成功"
  ```

## 安全特性
1. 密码使用BCrypt算法加密存储
2. 用户名、邮箱唯一性校验
3. 账户状态验证（启用/禁用）
4. 邀请码自动生成

## 数据库表结构
- `user` 表存储用户基本信息
- 所有敏感信息都进行了适当处理
- 密码字段只写不读，不会在响应中返回

## 使用说明
1. 用户注册后可直接登录
2. 登录成功后返回UUID作为临时token
3. 后续请求可通过token获取用户信息
4. 支持邮箱和手机号注册