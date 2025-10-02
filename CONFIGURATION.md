# PaperTrail 项目配置说明

## 环境配置

本项目使用外部配置管理敏感信息，确保安全。

### 本地开发配置

1. 复制 `local.properties.example` 文件为 `local.properties`：
   ```bash
   cp local.properties.example local.properties
   ```

2. 编辑 `local.properties` 文件，填入你的实际配置值。

3. 在 `local.properties` 文件中配置以下内容：
   - 数据库连接信息
   - API 密钥
   - 支付宝相关配置等

### 环境变量配置（推荐）

除了使用本地配置文件外，你也可以通过环境变量配置敏感信息：

- `DB_URL`: 数据库连接 URL
- `DB_USERNAME`: 数据库用户名
- `DB_PASSWORD`: 数据库密码
- `NEW_API_KEY`: OpenAI API 密钥
- `NEW_API_URL`: OpenAI API URL
- `ALIPAY_APP_ID`: 支付宝应用 ID
- `ALIPAY_PRIVATE_KEY`: 支付宝私钥
- `ALIPAY_PUBLIC_KEY`: 支付宝公钥
- `PROXY_HOST`: 代理主机（可选）
- `PROXY_PORT`: 代理端口（可选）

### 配置优先级

配置的优先级顺序如下（从高到低）：
1. 环境变量
2. `local.properties` 文件
3. `application.properties` 文件中的默认值

### 注意事项

- `local.properties` 文件已被添加到 `.gitignore`，不会被提交到版本控制系统
- 永远不要将包含敏感信息的配置文件提交到代码仓库
- 部署时，请确保在目标环境中正确设置了相应的环境变量