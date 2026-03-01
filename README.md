# HMDP-Ultra 黑马点评Ultra项目完整代码

> 🔥🔥 **热门推荐** 🔥🔥 黑马点评Plus升级版实战项目，基于本地生活与商户运营场景的全面重构

[![Stars](https://img.shields.io/github/stars/AlanSmithDev/hmdp-ultra?style=flat-square&logo=github)](https://github.com/AlanSmithDev/hmdp-ultra)
[![Forks](https://img.shields.io/github/forks/AlanSmithDev/hmdp-ultra?style=flat-square&logo=github)](https://github.com/AlanSmithDev/hmdp-ultra)
[![License](https://img.shields.io/github/license/AlanSmithDev/hmdp-ultra?style=flat-square)](https://github.com/AlanSmithDev/hmdp-ultra/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-blue?style=flat-square&logo=openjdk)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-green?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)

---

**目录导航**

- [📖 项目简介](#-项目简介)
- [🎯 核心特性](#-核心特性)
- [🏗️ 技术架构](#️-技术架构)
- [📦 项目结构](#-项目结构)
- [🚀 快速开始](#-快速开始)
- [💡 核心模块详解](#-核心模块详解)
- [🛠️ 技术栈清单](#️-技术栈清单)
- [📊 高并发方案](#-高并发方案)
- [🔧 配置说明](#-配置说明)
- [📝 接口文档](#-接口文档)
- [🤝 贡献指南](#-贡献指南)
- [📄 开源协议](#-开源协议)

---

## 📖 项目简介

HMDP-Ultra（黑马点评Ultra）是对原**黑马点评项目**的**全面重构升级版本**。本项目围绕**"高并发下的优惠券秒杀"**与**"热点查询"**的真实业务场景，提供端到端的稳定性与一致性解决方案。

### 🎯 业务场景

| 场景 | 描述 | 技术挑战 |
|------|------|----------|
| **优惠券秒杀** | 限时抢购、限量发放 | 高并发库存扣减、令牌桶限流、数据一致性 |
| **热点查询** | 热门商铺、爆款商品查询 | 多级缓存、缓存击穿防护、热点数据探测 |
| **用户订阅** | 优惠券开售提醒 | 延迟队列、用户分层、精准触达 |
| **订单管理** | 秒杀订单创建与取消 | 消息可靠性、对账机制、事务一致性 |

### ✨ 为什么选择 HMDP-Ultra？

- ✅ **真正企业级实战**：全套高并发解决方案，工程化落地
- ✅ **框架模块化设计**：分布式锁、Redis工具、MQ封装、ID生成器高度复用
- ✅ **完整数据一致性**：Redis预减库存 + 数据库乐观锁 + 对账闭环
- ✅ **开箱即用**：配置简洁，文档详尽，上手即用

---

## 🎯 核心特性

### 1️⃣ 全链路流控体系

```
┌─────────────────────────────────────────────────────────────────┐
│                        请求入口                                    │
│  ┌──────────┐   ┌──────────────┐   ┌─────────────┐              │
│  │ 令牌前置  │ → │  令牌桶限流  │ → │  滑动窗口   │              │
│  │ 授权机制  │   │  (IP+User)   │   │  双重校验   │              │
│  └──────────┘   └──────────────┘   └─────────────┘              │
└─────────────────────────────────────────────────────────────────┘
```

- **令牌前置授权**：秒杀前先申请令牌，防止无效请求冲击系统
- **令牌桶限流**：基于IP和用户ID的二维限流
- **滑动窗口**：精确控制请求频率，支持白名单配置

### 2️⃣ 多级缓存架构

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────┐
│   本地缓存   │ ←  │  Redis缓存   │ ←  │ 布隆过滤器  │ ←  │ 数据库  │
│  (热点数据)  │    │  (热点+冷数据)│    │  (拦截穿透) │    │         │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────┘
```

### 3️⃣ 缓存问题全面防护

| 问题 | 解决方案 | 实现方式 |
|------|----------|----------|
| **缓存穿透** | 布隆过滤器 + 空值标记 | `BloomFilterHandler` + `CACHE_NULL_TTL` |
| **缓存击穿** | 逻辑过期 + 互斥锁 | `queryWithLogicalExpire` + 分布式锁 |
| **缓存雪崩** | 随机TTL + 逻辑过期 | 过期时间随机化 + 异步重建 |

### 4️⃣ 消息队列可靠性

```
                    ┌──────────────────┐
                    │   Kafka Topic    │
                    └────────┬─────────┘
                             │
              ┌──────────────┼──────────────┐
              ↓              ↓              ↓
       ┌──────────┐   ┌──────────┐   ┌──────────────┐
       │  发布确认  │   │  重试退避  │   │   死信队列    │
       │ Callback  │   │  指数回退  │   │  (DLQ)       │
       └──────────┘   └──────────┘   └──────────────┘
```

### 5️⃣ 数据一致性闭环

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Redis预减库存 │ → │  MQ异步下单  │ → │  延迟检测    │ →  │   对账补偿   │
│             │    │             │    │  (10秒阈值)   │    │             │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

---

## 🏗️ 技术架构

### 系统架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              前端 (Vue 3 + Vite + Element Plus)              │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │ HTTP
┌─────────────────────────────────────▼─────────────────────────────────────┐
│                           网关层 (Gateway)                                   │
│                    ┌─────────────────┬─────────────────┐                   │
│                    │   登录拦截器      │   限流拦截器     │                   │
│                    └─────────────────┴─────────────────┘                   │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼─────────────────────────────────────┐
│                           核心服务 (hmdp-core-service)                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │ User     │  │  Shop    │  │ Voucher  │  │  Blog    │  │  Follow  │      │
│  │ 用户服务  │  │ 商铺服务  │  │ 优惠券   │  │ 博客服务  │  │ 关注服务  │      │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘      │
│                                                                             │
│  ┌───────────────────────────────────────────────────────────────────┐      │
│  │                     Seckill Voucher 秒杀核心                        │      │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │      │
│  │  │ 令牌服务    │  │  限流服务   │  │  订单服务   │  │  对账服务   │ │      │
│  │  │ TokenService│  │ RateLimit  │  │ OrderService│  │ Reconcile  │ │      │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘ │      │
│  └───────────────────────────────────────────────────────────────────┘      │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │
          ┌───────────────────────────┼───────────────────────────┐
          ↓                           ↓                           ↓
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────────────┐
│   Redis Cluster   │    │   Kafka Cluster  │    │    MySQL + ShardingSphere │
│  ┌──────────────┐ │    │  ┌────────────┐  │    │  ┌────────────────────┐  │
│  │ 缓存/锁/Lua  │ │    │  │ 消息队列   │  │    │  │ hmdp_0 / hmdp_1    │  │
│  │ 限流/计数器  │ │    │  │ 秒杀订单   │  │    │  │ 分库分表           │  │
│  └──────────────┘ │    │  └────────────┘  │    │  └────────────────────┘  │
└──────────────────┘    └──────────────────┘    └─────────────────────────────┘
```

### 框架依赖关系

```
hmdp-plus (父项目)
│
├── hmdp-common                    # 公共模块（枚举、异常、工具）
├── hmdp-sharding                  # 分库分表配置
├── hmdp-parameter                 # 参数配置
│
├── hmdp-core-service              # 核心业务服务
│   ├── hmdp-common
│   ├── hmdp-redis-tool-framework  # Redis工具框架
│   ├── hmdp-redisson-framework    # 分布式锁框架
│   ├── hmdp-mq-framework          # 消息队列框架
│   └── hmdp-id-generator-framework # ID生成器框架
│
├── hmdp-redisson-framework        # 分布式锁框架（可独立使用）
│   ├── hmdp-redisson-common-framework
│   ├── hmdp-service-lock-framework  # @ServiceLock 注解锁
│   ├── hmdp-repeat-execute-limit-framework  # 防重复执行
│   ├── hmdp-bloom-filter-framework # 布隆过滤器
│   └── hmdp-service-delay-queue-framework    # 延迟队列
│
├── hmdp-redis-tool-framework      # Redis工具框架（可独立使用）
│   ├── hmdp-redis-framework       # Redis基础操作（String/Hash/List/Set/ZSet）
│   └── hmdp-redis-rate-limit-framework       # 限流（令牌桶+滑动窗口）
│
├── hmdp-mq-framework              # 消息队列框架（可独立使用）
│   ├── hmdp-mq-producer-framework # 消息生产者
│   └── hmdp-mq-consumer-framework # 消息消费者
│
└── hmdp-id-generator-framework    # ID生成器框架（可独立使用）
    └── 雪花算法实现（支持MAC地址自动注册）
```

---

## 📦 项目结构

### 后端结构 (`hmdp-ultra-master`)

```
hmdp-ultra-master/
├── hmdp-common/                        # 公共基础模块
│   └── src/main/java/org/javaup/
│       ├── constant/                    # 常量定义
│       │   ├── Constant.java
│       │   ├── DistributedLockConstants.java
│       │   └── RepeatExecuteLimitConstants.java
│       ├── enums/                       # 枚举类
│       │   ├── BaseCode.java            # 业务错误码（0-10013）
│       │   ├── BusinessType.java
│       │   ├── LogType.java
│       │   ├── OrderStatus.java
│       │   ├── ReconciliationStatus.java
│       │   ├── SeckillVoucherOrderOperate.java
│       │   ├── StockUpdateType.java
│       │   ├── SubscribeStatus.java
│       │   └── VoucherStatus.java
│       ├── exception/                    # 异常定义
│       │   ├── ArgumentError.java
│       │   ├── ArgumentException.java
│       │   ├── BaseException.java
│       │   └── HmdpFrameException.java
│       ├── utils/
│       │   └── DateUtils.java           # 日期工具（29KB+）
│       ├── core/
│       │   └── SpringUtil.java           # Spring上下文
│       ├── environment/
│       │   └── SpringEnvironment.java
│       └── threadlocal/
│           └── BaseParameterHolder.java  # ThreadLocal封装
│
├── hmdp-core-service/                   # 核心业务服务 ⭐
│   └── src/main/java/org/javaup/
│       ├── controller/                   # 控制器层（10个）
│       │   ├── ShopController.java       # 商铺API
│       │   ├── ShopTypeController.java    # 商铺类型API
│       │   ├── UserController.java        # 用户API
│       │   ├── VoucherController.java      # 优惠券API
│       │   ├── VoucherOrderController.java # 优惠券订单API ⭐
│       │   ├── BlogController.java         # 博客API
│       │   ├── BlogCommentsController.java # 博客评论API
│       │   ├── FollowController.java       # 关注API
│       │   ├── UploadController.java       # 文件上传API
│       │   └── TestController.java         # 测试API
│       │
│       ├── service/                       # 服务层
│       │   ├── IVoucherOrderService.java  # 订单核心接口
│       │   ├── ISeckillVoucherService.java # 秒杀券接口
│       │   ├── IReconciliationTaskService.java # 对账任务
│       │   ├── IRollbackFailureLogService.java # 回滚日志
│       │   └── impl/                       # 实现类（17个）
│       │       ├── VoucherOrderServiceImpl.java      # 36KB+ 核心
│       │       ├── VoucherServiceImpl.java           # 24KB+
│       │       ├── ReconciliationTaskServiceImpl.java # 10KB+
│       │       └── ...
│       │
│       ├── mapper/                        # 数据访问层（15个）
│       │   ├── VoucherOrderMapper.java
│       │   ├── SeckillVoucherMapper.java
│       │   ├── VoucherReconcileLogMapper.java
│       │   └── ...
│       │
│       ├── entity/                        # 实体类（14个）
│       │   ├── Voucher.java
│       │   ├── VoucherOrder.java
│       │   ├── SeckillVoucher.java
│       │   ├── VoucherReconcileLog.java   # 对账日志
│       │   └── ...
│       │
│       ├── kafka/                         # Kafka消息处理 ⭐
│       │   ├── consumer/
│       │   │   ├── SeckillVoucherConsumer.java          # 秒杀券消费者
│       │   │   ├── SeckillVoucherInvalidationConsumer.java
│       │   │   └── SeckillVoucherInvalidationDlqConsumer.java
│       │   ├── producer/
│       │   │   ├── SeckillVoucherProducer.java
│       │   │   └── SeckillVoucherInvalidationProducer.java
│       │   ├── message/
│       │   │   ├── SeckillVoucherMessage.java
│       │   │   └── SeckillVoucherInvalidationMessage.java
│       │   └── redis/
│       │       └── RedisVoucherData.java   # Redis库存操作
│       │
│       ├── delay/                          # 延迟队列 ⭐
│       │   ├── consumer/
│       │   │   └── ConsumerDelayedVoucherReminder.java  # 15KB+
│       │   └── message/
│       │       └── DelayedVoucherReminderMessage.java
│       │
│       ├── cache/                          # 缓存处理
│       │   ├── CacheClient.java            # 缓存工具（穿透/击穿/雪崩）
│       │   ├── SeckillVoucherLocalCache.java
│       │   └── SeckillVoucherCacheInvalidationPublisher.java
│       │
│       ├── utils/                           # 工具类
│       │   ├── RedisIdWorker.java           # Redis ID生成器
│       │   ├── SimpleRedisLock.java        # 简单分布式锁
│       │   ├── LoginInterceptor.java
│       │   ├── RefreshTokenInterceptor.java
│       │   └── UserHolder.java             # 用户上下文
│       │
│       ├── lua/                            # Lua脚本
│       │   ├── SeckillVoucherDomain.java    # 秒杀域
│       │   ├── SeckillVoucherOperate.java  # 库存操作
│       │   └── SeckillVoucherRollBackOperate.java  # 回滚操作
│       │
│       └── init/                            # 初始化
│           ├── BloomFilterDataInit.java     # 布隆过滤器初始化
│           ├── SeckillVoucherDataRenewalInit.java # 秒杀券数据续期
│           └── DelCacheDataInit.java
│
├── hmdp-redisson-framework/               # 分布式锁框架
│   ├── hmdp-redisson-common-framework/
│   │   ├── RedissonCommonAutoConfiguration.java
│   │   ├── RedissonBaseProperties.java
│   │   ├── lockinfo/                       # 锁信息处理
│   │   └── locallock/
│   │       └── LocalLockCache.java
│   │
│   ├── hmdp-service-lock-framework/       # @ServiceLock ⭐
│   │   ├── servicelock/
│   │   │   ├── ServiceLocker.java          # 锁接口
│   │   │   ├── annotion/
│   │   │   │   └── ServiceLock.java        # @ServiceLock注解
│   │   │   ├── aspect/
│   │   │   │   └── ServiceLockAspect.java  # AOP切面
│   │   │   ├── impl/
│   │   │   │   ├── RedissonReentrantLocker.java  # 可重入锁
│   │   │   │   ├── RedissonFairLocker.java      # 公平锁
│   │   │   │   ├── RedissonReadLocker.java      # 读锁
│   │   │   │   └── RedissonWriteLocker.java     # 写锁
│   │   │   └── factory/
│   │   │       └── ServiceLockFactory.java
│   │   ├── servicelock/info/
│   │   │   ├── LockTimeOutStrategy.java    # 超时策略
│   │   │   └── LockTimeOutHandler.java
│   │   └── config/
│   │       └── ServiceLockAutoConfiguration.java
│   │
│   ├── hmdp-repeat-execute-limit-framework/ # 防重复执行
│   │
│   ├── hmdp-bloom-filter-framework/        # 布隆过滤器
│   │   └── handler/
│   │       ├── BloomFilterHandler.java    # 核心实现
│   │       └── BloomFilterHandlerFactory.java
│   │
│   └── hmdp-service-delay-queue-framework/ # 延迟队列
│       └── core/
│           ├── DelayConsumerQueue.java    # 消费者
│           ├── DelayProduceQueue.java      # 生产者
│           └── DelayBaseQueue.java
│
├── hmdp-redis-tool-framework/             # Redis工具框架
│   ├── hmdp-redis-framework/              # Redis基础操作 ⭐
│   │   ├── redis/
│   │   │   ├── RedisCache.java             # 接口（1300+行）
│   │   │   ├── RedisCacheImpl.java         # 实现（52KB+）
│   │   │   ├── CacheUtil.java
│   │   │   └── RedisKeyBuild.java          # Key构建器
│   │   └── core/
│   │       └── RedisKeyManage.java         # Key枚举管理
│   │
│   └── hmdp-redis-rate-limit-framework/    # 限流框架 ⭐
│       ├── execute/
│       │   ├── RateLimitHandler.java       # 限流接口
│       │   └── RedisRateLimitHandler.java  # 实现（12KB+）
│       ├── lua/
│       │   ├── SlidingRateLimitOperate.java    # 滑动窗口
│       │   └── TokenBucketRateLimitOperate.java # 令牌桶
│       └── ratelimit/extension/
│           ├── RateLimitContext.java
│           ├── RateLimitPenaltyPolicy.java
│           └── ThresholdPenaltyPolicy.java  # 阈值惩罚策略
│
├── hmdp-mq-framework/                      # 消息队列框架
│   ├── hmdp-mq-producer-framework/
│   │   └── AbstractProducerHandler.java    # 生产者基类
│   ├── hmdp-mq-consumer-framework/
│   │   └── AbstractConsumerHandler.java    # 消费者基类
│   └── hmdp-mq-common-framework/
│       └── MessageExtend.java              # 消息扩展
│
└── hmdp-id-generator-framework/             # ID生成器框架
    └── toolkit/
        ├── SnowflakeIdGenerator.java       # 雪花算法 ⭐
        ├── WorkAndDataCenterIdHandler.java
        └── WorkDataCenterId.java
```

### 前端结构 (`hmdp-vue3`)

```
hmdp-vue3/
├── src/
│   ├── api/                    # API接口
│   ├── assets/                 # 静态资源
│   ├── components/             # 公共组件
│   ├── router/                 # 路由配置
│   ├── store/                  # Pinia状态管理
│   ├── utils/                  # 工具函数
│   └── views/                  # 页面视图
│       ├── shop/               # 商铺相关
│       ├── voucher/            # 优惠券相关
│       ├── user/                # 用户相关
│       └── ...
├── package.json
└── vite.config.js
```

---

## 🚀 快速开始

### 环境要求

| 环境 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 强烈推荐JDK 17 |
| MySQL | 8.0+ | 支持分库分表 |
| Redis | 6.0+ | 推荐Redis 7.x |
| Kafka | 2.8+ | 消息队列 |
| Node.js | 16+ | 前端开发 |
| Maven | 3.8+ | 项目构建 |

### 📥 克隆项目

```bash
git clone https://github.com/AlanSmithDev/hmdp-ultra.git
cd hmdp-ultra
```

### 🗄️ 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行数据库初始化脚本
source sql/1_create_database.sql

# 分别导入两个分库
source sql/hmdp_0.sql
source sql/hmdp_1.sql
```

### 🖥️ 后端启动

```bash
cd hmdp-ultra-master

# Maven构建（跳过测试）
mvn clean install -DskipTests

# 启动核心服务
mvn spring-boot:run -pl hmdp-core-service
```

> ⚠️ **启动前请确保**：MySQL、Redis、Kafka 服务已正常运行

### 🌐 前端启动

```bash
cd hmdp-vue3

# 安装依赖
npm install
# 或使用pnpm
pnpm install

# 开发模式启动
npm run dev

# 生产环境打包
npm run build
```

### ✅ 验证运行

启动成功后，访问以下地址：

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端 | http://localhost:5173 | Vue 3 开发服务器 |
| 后端API | http://localhost:8080 | Spring Boot API |
| Knife4j文档 | http://localhost:8080/doc.html | API在线文档 |

---

## 💡 核心模块详解

### 🎫 优惠券秒杀流程

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│  用户点击 │ →  │  申请令牌 │ →  │  令牌校验 │ →  │  创建订单 │ →  │ MQ异步处理│
│  立即抢购  │     │ (Token)   │     │ (限流)    │     │          │     │          │
└──────────┘     └──────────┘     └──────────┘     └──────────┘     └──────────┘
                                                                        │
                                          ┌──────────────────────────────┘
                                          ↓
                               ┌──────────────────┐    ┌──────────────────┐
                               │   延迟检测(10秒)   │ →  │    对账补偿      │
                               │   消息超时丢弃     │    │   数据一致性闭环  │
                               └──────────────────┘    └──────────────────┘
```

### 📊 数据库设计

#### 分库分表策略

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

#### 广播表（各库一致）

| 表名 | 说明 |
|------|------|
| tb_blog | 博客表 |
| tb_blog_comments | 博客评论表 |
| tb_follow | 关注表 |
| tb_shop | 商户表 |
| tb_shop_type | 商户类型表 |
| tb_sign | 签到表 |

### ⚡ 秒杀V2流程详解

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

#### 核心Lua脚本逻辑

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

### 🔐 核心代码示例

#### 1. 令牌发放服务

```java
// ISeckillAccessTokenService.java
public interface ISeckillAccessTokenService {
    /**
     * 发放秒杀令牌
     * @param voucherId 优惠券ID
     * @param userId 用户ID
     * @return 令牌字符串
     */
    String issueAccessToken(Long voucherId, Long userId);

    /**
     * 校验并消费令牌
     * @param voucherId 优惠券ID
     * @param userId 用户ID
     * @param token 令牌
     * @return 是否有效
     */
    boolean validateAndConsume(Long voucherId, Long userId, String token);
}
```

#### 2. 限流处理器

```java
// RedisRateLimitHandler.java 核心方法
@Override
public void execute(Long voucherId, Long userId, RateLimitScene scene) {
    String clientIp = resolveClientIp();

    // 白名单检查
    if (isWhitelisted(userId, clientIp)) {
        return;
    }

    // 封禁检查
    checkBans(voucherId, userId, clientIp);

    // 构建限流Key
    List<String> keys = buildRateLimitKeys(voucherId, userId, clientIp, useSliding);

    // 执行Lua脚本限流
    Integer result = executeLua(useSliding, keys, args);

    // 处理结果
    handleResult(ctx);
}
```

#### 3. 分布式锁注解

```java
// 使用示例
@ServiceLock(name = "voucher:order:", lockType = LockType.REENTRANT_LOCK,
            waitTime = 3, leaseTime = 10)
public Result<Long> seckillVoucher(Long voucherId) {
    // 业务逻辑，自动加锁解锁
    return voucherOrderService.seckillVoucher(voucherId);
}
```

### 📊 核心配置项

```yaml
# application.yml 核心配置
spring:
  data:
    redis:
      host: localhost
      port: 6379
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: hmdp-consumer-group

hmdp:
  seckill:
    rate-limit:
      ip-window-millis: 1000      # IP窗口时间(ms)
      ip-max-attempts: 5          # IP最大请求数
      user-window-millis: 1000    # 用户窗口时间(ms)
      user-max-attempts: 2        # 用户最大请求数
      enable-sliding-window: true  # 启用滑动窗口
```

---

## 🛠️ 技术栈清单

### 后端技术栈

| 分类 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **基础框架** | Spring Boot | 3.5.4 | 核心框架 |
| | Java | 17 | 编程语言 |
| **数据访问** | MyBatis-Plus | 3.5.7 | ORM框架 |
| | ShardingSphere | 5.3.2 | 分库分表 |
| **缓存** | Redis | 6.0+ | 缓存+分布式锁 |
| | Redisson | 3.52.0 | Redis客户端 |
| **消息队列** | Kafka | 2.8+ | 异步消息 |
| **安全** | Sa-Token | 1.43.0 | 认证授权 |
| **工具** | Hutool | 5.8.25 | 工具集 |
| | Fastjson | 2.0.9 | JSON处理 |
| **文档** | Knife4j | 4.3.0 | API文档 |
| **并发** | QPS-Helper | 1.1.3 | 限流辅助 |

### 前端技术栈

| 分类 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **框架** | Vue | 3.5.13 | 核心框架 |
| | Vite | 5.x | 构建工具 |
| **UI** | Element Plus | 2.x | 组件库 |
| **状态** | Pinia | 2.x | 状态管理 |
| **网络** | Axios | 1.x | HTTP客户端 |

---

## 📊 高并发方案

### 方案总览

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           HMDP-Ultra 高并发架构                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【入口层】                                                                 │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                     │
│   │  令牌桶限流  │ →  │  黑白名单   │ →  │  IP/用户限流 │                    │
│   │  令牌前置授权 │    │  灵活配置   │    │  双重校验    │                     │
│   └─────────────┘    └─────────────┘    └─────────────┘                     │
│                                                                             │
│   【缓存层】                                                                 │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                     │
│   │  布隆过滤器  │ →  │  多级缓存   │ →  │  热点探测   │                     │
│   │  拦截穿透   │    │  本地+Redis │    │  自动升级   │                     │
│   └─────────────┘    └─────────────┘    └─────────────┘                     │
│                                                                             │
│   【数据层】                                                                 │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                     │
│   │ Redis预减库存│ →  │  MQ异步化   │ →  │  对账补偿   │                     │
│   │  原子操作    │    │ 削峰填谷    │    │  最终一致   │                     │
│   └─────────────┘    └─────────────┘    └─────────────┘                     │
│                                                                             │
│   【存储层】                                                                 │
│   ┌─────────────┐    ┌─────────────┐                                        │
│   │ 分库分表    │ →  │  乐观锁    │                                        │
│   │ ShardingSphere│   │  版本控制  │                                        │
│   └─────────────┘    └─────────────┘                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 核心要点速记

| 序号 | 问题 | 解决方案 | 关键类/方法 |
|------|------|----------|-------------|
| 1 | 缓存穿透 | 布隆过滤器 + 空值标记 | `BloomFilterHandler` |
| 2 | 缓存击穿 | 逻辑过期 + 互斥锁 | `queryWithLogicalExpire` |
| 3 | 缓存雪崩 | 随机TTL + 异步重建 | `setWithLogicalExpire` |
| 4 | 超卖问题 | Redis原子扣减 + Lua脚本 | `SeckillVoucherOperate` |
| 5 | 消息丢失 | 发布确认 + 延迟检测 | `AbstractProducerHandler` |
| 6 | 消费重复 | 消费者幂等 + 对账日志 | `SeckillVoucherConsumer` |
| 7 | 数据一致 | Redis预减 → MQ → DB → 对账 | `ReconciliationTaskService` |
| 8 | 热点数据 | 本地缓存 + 热点探测 | `SeckillVoucherLocalCache` |

### 🔧 关键问题解决

#### 1. 缓存穿透与击穿

| 问题 | 解决方案 |
|------|----------|
| 缓存穿透 | 空值缓存 + 布隆过滤器 |
| 缓存击穿 | 本地缓存 + 双重锁检测 + 逻辑过期 |
| 缓存雪崩 | 随机过期时间 + 多级缓存 |

#### 2. 高并发秒杀

| 问题 | 解决方案 |
|------|----------|
| 超卖问题 | Lua脚本原子扣减库存 |
| 重复下单 | Redis Set去重 + 数据库唯一索引 |
| 流量控制 | 令牌前置授权 + 令牌桶限流 |
| 异步下单 | Kafka消息队列 + 延迟队列补偿 |

#### 3. 数据一致性

| 场景 | 解决方案 |
|------|----------|
| Redis与MySQL不一致 | 对账日志 + 定时补偿任务 |
| 消息丢失 | 发布确认 + 本地消息表 |
| 重复消费 | 幂等组件 + 去重表 |

#### 4. 分库分表

| 问题 | 解决方案 |
|------|----------|
| 数据路由 | ShardingSphere标准分片策略 |
| 全局ID | Snowflake算法生成 |
| 跨分片查询 | 订单路由表辅助定位 |

### 📊 性能指标

| 指标 | 数值 | 说明 |
|------|------|------|
| **单机QPS** | 5000+ QPS | 秒杀接口可达 |
| **库存扣减** | Lua原子操作 | 无超卖 |
| **消息延迟** | < 100ms | Kafka端到端 |
| **缓存命中** | > 95% | 多级缓存 |

---

## 🔧 配置说明

### application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: hmdp-ultra
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/hmdp_0?useUnicode=true&characterEncoding=utf8
    username: root
    password: root
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: hmdp-consumer-group
      auto-offset-reset: earliest

# HMDP核心配置
hmdp:
  token:
    cache-ttl-minutes: 10          # 令牌缓存时间
    enable: true                    # 是否启用令牌
  rate-limit:
    ip-window-millis: 1000
    ip-max-attempts: 5
    user-window-millis: 1000
    user-max-attempts: 2
    enable-sliding-window: true
  reconciliation:
    schedule-cron: 0 */5 * * * ?   # 对账任务cron
```

---

## 📝 接口文档

启动项目后，访问 **Knife4j** 在线文档：

```
http://localhost:8080/doc.html
```

### 核心API列表

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| **优惠券** | `/voucher-order/seckill/token/{id}` | GET | 申请秒杀令牌 |
| | `/voucher-order/seckill/{id}` | POST | 提交秒杀订单 |
| | `/voucher-order/cancel` | POST | 取消订单 |
| | `/voucher/reconciliation/task/all` | POST | 触发全量对账 |
| **商铺** | `/shop/{id}` | GET | 查询商铺 |
| | `/shop/of/type` | GET | 按类型查询商铺 |
| **用户** | `/user/login` | POST | 用户登录 |
| | `/user/code` | GET | 获取验证码 |

---

## 🤝 贡献指南

欢迎提交 **Issue** 和 **Pull Request**！

### 提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式（不影响功能）
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

### 开发流程

1. **Fork** 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 **Pull Request**

---

## 📄 开源协议

本项目基于 **Apache License 2.0** 开源协议。

详细内容请查阅 [LICENSE](LICENSE) 文件。

---

## 👤 作者信息

**Alan Smith** - [GitHub](https://github.com/AlanSmithDev)

> 💡 如果该项目对您有帮助，请 **Star** 支持一下！

---

## 🙏 致谢

- 感谢 [Redisson](https://github.com/redisson/redisson) 提供的优秀分布式锁实现
- 感谢 [ShardingSphere](https://github.com/shardingsphere/shardingsphere) 提供的分库分表方案
- 感谢 [MyBatis-Plus](https://github.com/baomidou/mybatis-plus) 提供的高效ORM支持

---

<p align="center">
  <strong>Made with ❤️ by Alan Smith | Licensed under Apache-2.0</strong>
</p>
