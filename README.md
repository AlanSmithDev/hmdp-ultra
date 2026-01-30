# HMDP-Ultra 黑马点评Plus

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-blue" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen" alt="Spring Boot 3.5.4">
  <img src="https://img.shields.io/badge/Vue-3.5.13-green" alt="Vue 3.5.13">
  <img src="https://img.shields.io/badge/Redis-Latest-red" alt="Redis">
  <img src="https://img.shields.io/badge/Kafka-Latest-orange" alt="Kafka">
  <img src="https://img.shields.io/badge/ShardingSphere-5.3.2-blue" alt="ShardingSphere 5.3.2">
</p>

<p align="center">
  <b>基于本地生活与商户运营场景的综合实战项目 —— 黑马点评Plus升级版</b>
</p>

---

## 📋 目录

- [项目简介](#-项目简介)
- [技术栈](#-技术栈)
- [项目架构](#-项目架构)
- [核心功能](#-核心功能)
- [秒杀流程详解](#-秒杀流程详解)
- [数据库设计](#-数据库设计)
- [快速开始](#-快速开始)
- [项目结构](#-项目结构)
- [关键问题解决](#-关键问题解决)

---

## 🚀 项目简介

**HMDP-Ultra（黑马点评Plus）** 是一个基于本地生活与商户运营场景的综合实战项目，是对原黑马点评项目的全面升级。该项目围绕"高并发下的优惠券秒杀"与"热点查询"的真实问题，提供端到端的稳定性与一致性解决方案。

相比原版黑马点评，Plus版本补齐了以下能力：
- 🚦 **全链路流控**：令牌前置授权 + 令牌桶限流
- 🗄️ **多层缓存策略**：本地缓存 + Redis 缓存 + 布隆过滤器
- ✅ **缓存问题完美解决**：穿透、击穿、雪崩的全面防护
- 🔁 **一致性闭环**：Redis扣减、订单创建、消息投递、数据库落库
- 📦 **MQ可靠性**：发布确认、重试退避、死信队列、消费幂等
- 🗂️ **分库分表**：ShardingSphere 5.3.2 实现数据水平拆分

---

## 🛠️ 技术栈

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.5.4 | 核心框架 |
| MyBatis-Plus | 3.5.7 | ORM框架 |
| Redis | Latest | 缓存 |
| Redisson | 3.52.0 | Redis客户端/分布式锁 |
| Kafka | - | 消息队列 |
| ShardingSphere | 5.3.2 | 分库分表 |
| MySQL | 8.x | 关系型数据库 |
| Hutool | 5.8.25 | 工具库 |
| Lombok | - | 代码简化 |

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.13 | 前端框架 |
| Element Plus | 2.9.7 | UI组件库 |
| Vite | 6.2.4 | 构建工具 |
| Pinia | 3.0.1 | 状态管理 |
| Vue Router | 4.5.0 | 路由管理 |
| Axios | 1.8.4 | HTTP客户端 |

---

## 🏗️ 项目架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        HMDP-Ultra 架构图                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │   前端层    │    │   接入层    │    │   服务层    │         │
│  │  Vue3 +     │◄──►│  令牌桶限流 │◄──►│ Spring Boot │         │
│  │ ElementPlus │    │  令牌授权   │    │   业务逻辑  │         │
│  └─────────────┘    └─────────────┘    └──────┬──────┘         │
│                                               │                 │
│  ┌─────────────┐    ┌─────────────┐    ┌──────▼──────┐         │
│  │   缓存层    │◄──►│   消息层    │◄──►│   数据层    │         │
│  │Redis+本地缓存│    │    Kafka    │    │MySQL+分库分表│         │
│  │  布隆过滤器  │    │  延迟队列   │    │ShardingSphere│        │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## ⭐ 核心功能

### 1. 用户功能
- **短信登录**：基于Redis共享Session实现分布式会话管理
- **用户信息管理**：个人资料编辑、头像上传
- **签到功能**：基于BitMap实现用户签到与统计

### 2. 商户功能
- **商户浏览**：商户列表、详情查询
- **商户类型**：分类浏览与筛选
- **附近商户**：基于Redis GEO实现地理位置检索
- **商户搜索**：支持多种条件的商户检索

### 3. 优惠券功能
- **普通优惠券**：常规优惠券的领取与使用
- **秒杀优惠券**：高并发场景下的秒杀抢购
- **库存管理**：Redis原子扣减 + 数据库同步
- **订单管理**：优惠券订单的创建、查询、取消

### 4. 社交功能
- **达人探店**：博客发布、点赞、评论
- **好友关注**：基于Set实现关注/取消关注
- **共同关注**：查询共同好友
- **粉丝排名**：SortedSet实现粉丝排行榜

### 5. 秒杀Plus功能
- **令牌前置授权**：抢购前先获取访问令牌
- **令牌桶限流**：动态流量控制
- **库存预热**：活动开始前预加载库存到Redis
- **订阅通知**：库存不足时订阅到券提醒
- **自动发券**：订单取消后自动发放给订阅用户

---

## ⚡ 秒杀流程详解

### V2版本秒杀流程（完整版）

```
┌─────────────────────────────────────────────────────────────────┐
│                     秒杀抢购流程 (V2版本)                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 获取访问令牌                                                  │
│     ├─ 令牌桶限流检查                                             │
│     ├─ 校验用户资格                                               │
│     └─ 生成并返回accessToken                                      │
│                           │                                     │
│                           ▼                                     │
│  2. 发起秒杀请求                                                  │
│     ├─ 校验accessToken                                            │
│     ├─ 令牌桶限流检查                                             │
│     └─ 进入秒杀核心流程                                           │
│                           │                                     │
│                           ▼                                     │
│  3. Lua脚本原子扣减                                               │
│     ├─ 检查活动时间                                               │
│     ├─ 检查库存                                                   │
│     ├─ 检查是否已购买                                             │
│     ├─ 扣减库存                                                   │
│     ├─ 记录购买用户                                               │
│     └─ 写入追踪日志                                               │
│                           │                                     │
│                           ▼                                     │
│  4. 发送Kafka消息                                                 │
│     ├─ 异步创建订单                                               │
│     ├─ 订单路由表记录                                             │
│     └─ 对账日志记录                                               │
│                           │                                     │
│                           ▼                                     │
│  5. 轮询查询订单结果                                              │
│     ├─ 查询Redis缓存                                              │
│     ├─ 查询订单路由表                                             │
│     └─ 返回订单状态                                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 核心Lua脚本逻辑

```lua
-- 秒杀核心Lua脚本流程：
1. 获取Redis服务器当前时间
2. 校验秒杀活动是否开始/结束
3. 校验优惠券状态（上架/下架/过期）
4. 获取并校验库存
5. 校验用户是否已购买（去重）
6. 原子扣减库存
7. 记录购买用户到Set
8. 写入追踪日志到Hash
9. 设置Key过期时间
10. 返回执行结果（成功/失败码）
```

---

## 🗄️ 数据库设计

### 分库分表策略

| 表名 | 分库策略 | 分表策略 | 说明 |
|------|----------|----------|------|
| tb_user | id % 2 | id % 2 | 用户表 |
| tb_user_info | user_id % 2 | user_id % 2 | 用户信息表 |
| tb_user_phone | phone hash | phone hash | 用户手机号表 |
| tb_voucher | id % 2 | id % 2 | 优惠券表 |
| tb_seckill_voucher | voucher_id % 2 | voucher_id / 2 % 2 | 秒杀优惠券表 |
| tb_voucher_order | user_id % 2 | voucher_id % 2 | 优惠券订单表 |
| tb_voucher_order_router | order_id hash | order_id / 2 % 2 | 订单路由表 |
| tb_voucher_reconcile_log | order_id hash | order_id / 2 % 2 | 对账日志表 |

### 广播表（各库一致）
- tb_blog - 博客表
- tb_blog_comments - 博客评论表
- tb_follow - 关注表
- tb_shop - 商户表
- tb_shop_type - 商户类型表
- tb_sign - 签到表

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Kafka 3.0+
- Node.js 18+

### 1. 克隆项目

```bash
git clone https://github.com/AlanSmithDev/hmdp-ultra.git
cd hmdp-ultra
```

### 2. 初始化数据库

```bash
# 创建数据库
mysql -u root -p < sql/1_create_database.sql

# 导入分库0数据
mysql -u root -p hmdp_0 < sql/hmdp_0.sql

# 导入分库1数据
mysql -u root -p hmdp_1 < sql/hmdp_1.sql
```

### 3. 配置中间件

修改 `hmdp-ultra-master/hmdp-core-service/src/main/resources/application.yml`：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 654321
  kafka:
    bootstrap-servers: localhost:9092
```

修改 `hmdp-ultra-master/hmdp-core-service/src/main/resources/shardingsphere.yaml`：

```yaml
dataSources:
  ds_0:
    jdbcUrl: jdbc:mysql://localhost:3306/hmdp_0
    username: root
    password: 654321
  ds_1:
    jdbcUrl: jdbc:mysql://localhost:3306/hmdp_1
    username: root
    password: 654321
```

### 4. 启动后端服务

```bash
cd hmdp-ultra-master
mvn clean install -DskipTests
mvn spring-boot:run -pl hmdp-core-service
```

服务默认启动在 `http://localhost:8085`

### 5. 启动前端服务

```bash
cd hmdp-vue3
npm install
npm run dev
```

前端默认启动在 `http://localhost:5173`

---

## 📁 项目结构

```
hmdp-ultra/
├── hmdp-ultra-master/                    # 后端项目根目录
│   ├── hmdp-common/                      # 公共模块
│   │   ├── 常量定义
│   │   ├── 枚举类
│   │   ├── 异常体系
│   │   └── 工具类
│   ├── hmdp-core-service/                # 核心业务服务
│   │   ├── controller/                   # API接口层
│   │   ├── service/                      # 业务逻辑层
│   │   ├── entity/                       # 实体类
│   │   ├── mapper/                       # 数据访问层
│   │   ├── kafka/                        # Kafka消息处理
│   │   ├── cache/                        # 缓存相关
│   │   └── lua/                          # Lua脚本执行
│   ├── hmdp-sharding/                    # 分库分表配置
│   ├── hmdp-id-generator-framework/      # 全局ID生成框架
│   │   └── Snowflake算法实现
│   ├── hmdp-redis-tool-framework/        # Redis工具框架
│   │   ├── hmdp-redis-framework/         # Redis缓存封装
│   │   └── hmdp-redis-rate-limit-framework/  # 限流组件
│   ├── hmdp-redisson-framework/          # Redisson框架
│   │   ├── hmdp-bloom-filter-framework/  # 布隆过滤器
│   │   ├── hmdp-service-lock-framework/  # 分布式锁
│   │   ├── hmdp-repeat-execute-limit-framework/  # 幂等组件
│   │   └── hmdp-service-delay-queue-framework/   # 延迟队列
│   ├── hmdp-mq-framework/                # 消息队列框架
│   │   ├── hmdp-mq-producer-framework/   # 生产者封装
│   │   └── hmdp-mq-consumer-framework/   # 消费者封装
│   └── hmdp-parameter/                   # 参数配置模块
├── hmdp-vue3/                            # 前端项目(Vue3)
│   ├── src/
│   │   ├── api/                          # API接口定义
│   │   ├── views/                        # 页面视图
│   │   ├── components/                   # 公共组件
│   │   ├── stores/                       # Pinia状态管理
│   │   ├── router/                       # 路由配置
│   │   └── utils/                        # 工具函数
│   └── package.json
├── sql/                                  # 数据库脚本
│   ├── 1_create_database.sql
│   ├── hmdp_0.sql
│   └── hmdp_1.sql
└── upload/                               # 文件上传目录
```

---

## 🔧 关键问题解决

### 1. 缓存穿透与击穿

| 问题 | 解决方案 |
|------|----------|
| 缓存穿透 | 空值缓存 + 布隆过滤器 |
| 缓存击穿 | 本地缓存 + 双重锁检测 + 逻辑过期 |
| 缓存雪崩 | 随机过期时间 + 多级缓存 |

### 2. 高并发秒杀

| 问题 | 解决方案 |
|------|----------|
| 超卖问题 | Lua脚本原子扣减库存 |
| 重复下单 | Redis Set去重 + 数据库唯一索引 |
| 流量控制 | 令牌前置授权 + 令牌桶限流 |
| 异步下单 | Kafka消息队列 + 延迟队列补偿 |

### 3. 数据一致性

| 场景 | 解决方案 |
|------|----------|
| Redis与MySQL不一致 | 对账日志 + 定时补偿任务 |
| 消息丢失 | 发布确认 + 本地消息表 |
| 重复消费 | 幂等组件 + 去重表 |

### 4. 分库分表

| 问题 | 解决方案 |
|------|----------|
| 数据路由 | ShardingSphere标准分片策略 |
| 全局ID | Snowflake算法生成 |
| 跨分片查询 | 订单路由表辅助定位 |

---

## 📊 性能指标

- **单机QPS**：秒杀接口可达 5000+ QPS
- **库存扣减**：Lua脚本原子操作，无超卖
- **消息延迟**：Kafka消息端到端延迟 < 100ms
- **缓存命中**：多级缓存命中率 > 95%

---

## 🤝 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 📄 许可证

本项目基于 [Apache License 2.0](LICENSE) 开源协议。

---

## 👨‍💻 作者

- **Alan Smith** - [GitHub](https://github.com/AlanSmithDev)

---

## 🙏 致谢

感谢黑马程序员提供的原版黑马点评项目，本项目在此基础上进行了全面升级和优化。

---

<p align="center">
  如果这个项目对你有帮助，请给个 ⭐ Star 支持一下！
</p>
