# HMDP-Ultra  Heimo Review Ultra - Complete Project Code

> 🔥🔥 **Hot Recommendation** 🔥🔥 Heimo Review Plus upgraded version, comprehensive reconstruction based on local life and merchant operation scenarios

[![Stars](https://img.shields.io/github/stars/AlanSmithDev/hmdp-ultra?style=flat-square&logo=github)](https://github.com/AlanSmithDev/hmdp-ultra)
[![Forks](https://img.shields.io/github/forks/AlanSmithDev/hmdp-ultra?style=flat-square&logo=github)](https://github.com/AlanSmithDev/hmdp-ultra)
[![License](https://img.shields.io/github/license/AlanSmithDev/hmdp-ultra?style=flat-square)](https://github.com/AlanSmithDev/hmdp-ultra/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-blue?style=flat-square&logo=openjdk)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-green?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)

---

**Table of Contents**

- [📖 Project Introduction](#-project-introduction)
- [🎯 Core Features](#-core-features)
- [🏗️ Technical Architecture](#️-technical-architecture)
- [📦 Project Structure](#-project-structure)
- [🚀 Quick Start](#-quick-start)
- [💡 Core Module Details](#-core-module-details)
- [🛠️ Technology Stack](#️-technology-stack)
- [📊 High Concurrency Solutions](#-high-concurrency-solutions)
- [🔧 Configuration Guide](#-configuration-guide)
- [📝 API Documentation](#-api-documentation)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)

---

## 📖 Project Introduction

HMDP-Ultra (Heimo Review Ultra) is a **comprehensive reconstruction and upgrade** of the original Heimo Review project. This project focuses on **"coupon flash sales under high concurrency"** and **"hot query"** real business scenarios, providing end-to-end stability and consistency solutions.

### 🎯 Business Scenarios

| Scenario | Description | Technical Challenges |
|----------|-------------|----------------------|
| **Coupon Flash Sale** | Limited-time purchase, limited quantity | High-concurrency inventory deduction, token bucket rate limiting, data consistency |
| **Hot Query** | Popular shops, bestseller queries | Multi-level cache, cache breakdown protection, hot data detection |
| **User Subscription** | Coupon sale notifications | Delayed queue, user stratification, precise outreach |
| **Order Management** | Flash sale order creation and cancellation | Message reliability, reconciliation mechanism, transaction consistency |

### ✨ Why Choose HMDP-Ultra?

- ✅ **Truly Enterprise-Level Practice**: Full set of high-concurrency solutions, engineering implementation
- ✅ **Modular Framework Design**: Distributed lock, Redis tools, MQ encapsulation, ID generator highly reusable
- ✅ **Complete Data Consistency**: Redis pre-deduction inventory + database optimistic lock + reconciliation closed-loop
- ✅ **Ready to Use**: Simple configuration, detailed documentation, quick start

---

## 🎯 Core Features

### 1️⃣ Full-Chain Traffic Control System

```
┌─────────────────────────────────────────────────────────────────┐
│                        Request Entry                             │
│  ┌──────────┐   ┌──────────────┐   ┌─────────────┐              │
│  │  Token   │ → │ Token Bucket │ → │   Sliding   │              │
│  │  Pre-Auth│   │  Rate Limit  │   │   Window    │              │
│  │          │   │  (IP+User)   │   │ Dual Check  │              │
│  └──────────┘   └──────────────┘   └─────────────┘              │
└─────────────────────────────────────────────────────────────────┘
```

- **Token Pre-Authorization**: Apply for token before flash sale to prevent invalid requests
- **Token Bucket Rate Limiting**: 2D rate limiting based on IP and User ID
- **Sliding Window**: Precise request frequency control, whitelist configuration supported

### 2️⃣ Multi-Level Cache Architecture

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────┐
│ Local Cache │ ←  │  Redis Cache│ ←  │Bloom Filter│ ←  │Database │
│ (Hot Data)  │    │(Hot+Cold)   │    │(Block Pierce│    │         │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────┘
```

### 3️⃣ Comprehensive Cache Problem Protection

| Problem | Solution | Implementation |
|---------|----------|----------------|
| **Cache Penetration** | Bloom Filter + Null Mark | `BloomFilterHandler` + `CACHE_NULL_TTL` |
| **Cache Breakdown** | Logical Expire + Mutex Lock | `queryWithLogicalExpire` + Distributed Lock |
| **Cache Avalanche** | Random TTL + Logical Expire | Random expiration time + Async rebuild |

### 4️⃣ Message Queue Reliability

```
                    ┌──────────────────┐
                    │   Kafka Topic    │
                    └────────┬─────────┘
                             │
              ┌──────────────┼──────────────┐
              ↓              ↓              ↓
       ┌──────────┐   ┌──────────┐   ┌──────────────┐
       │ Publish  │   │ Retry    │   │    DLQ       │
       │ Callback │   │ Backoff  │   │(Dead Letter) │
       └──────────┘   └──────────┘   └──────────────┘
```

### 5️⃣ Data Consistency Closed-Loop

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│Redis Pre-Dec│ →  │ MQ Async    │ →  │  Delay      │ →  │Recon-      │
│Inventory    │    │ Order      │    │  Detection  │    │ciliation   │
│             │    │            │    │  (10s)      │    │Compensation│
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

---

## 🏗️ Technical Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Frontend (Vue 3 + Vite + Element Plus)         │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │ HTTP
┌─────────────────────────────────────▼─────────────────────────────────────┐
│                           Gateway Layer (Gateway)                           │
│                    ┌─────────────────┬─────────────────┐                   │
│                    │   Login Filter  │   Rate Limit    │                   │
│                    │                 │   Filter        │                   │
│                    └─────────────────┴─────────────────┘                   │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼─────────────────────────────────────┐
│                           Core Service (hmdp-core-service)                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │ User     │  │  Shop    │  │ Voucher  │  │  Blog    │  │  Follow  │      │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │  │ Service  │      │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  └──────────┘      │
│                                                                             │
│  ┌───────────────────────────────────────────────────────────────────┐      │
│  │                     Seckill Voucher Core                           │      │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │      │
│  │  │  Token     │  │  Rate Limit│  │  Order     │  │ Reconcile │ │      │
│  │  │  Service   │  │  Service   │  │  Service   │  │  Service  │ │      │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘ │      │
│  └───────────────────────────────────────────────────────────────────┘      │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │
          ┌───────────────────────────┼───────────────────────────┐
          ↓                           ↓                           ↓
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────────────┐
│   Redis Cluster  │    │   Kafka Cluster │    │    MySQL + ShardingSphere│
│  ┌──────────────┐ │    │  ┌────────────┐  │    │  ┌────────────────────┐  │
│  │Cache/Lock/Lua│ │    │  │ Message    │  │    │  │ hmdp_0 / hmdp_1    │  │
│  │RateLimit/Count│ │    │  │ Queue      │  │    │  │ Sharding           │  │
│  └──────────────┘ │    │  └────────────┘  │    │  └────────────────────┘  │
└──────────────────┘    └──────────────────┘    └─────────────────────────────┘
```

### Framework Dependency

```
hmdp-plus (Parent Project)
│
├── hmdp-common                    # Common Module (Enums, Exceptions, Utils)
├── hmdp-sharding                  # Sharding Configuration
├── hmdp-parameter                 # Parameter Configuration
│
├── hmdp-core-service              # Core Business Service
│   ├── hmdp-common
│   ├── hmdp-redis-tool-framework  # Redis Tool Framework
│   ├── hmdp-redisson-framework    # Distributed Lock Framework
│   ├── hmdp-mq-framework          # Message Queue Framework
│   └── hmdp-id-generator-framework # ID Generator Framework
│
├── hmdp-redisson-framework        # Distributed Lock Framework (Standalone)
│   ├── hmdp-redisson-common-framework
│   ├── hmdp-service-lock-framework  # @ServiceLock Annotation Lock
│   ├── hmdp-repeat-execute-limit-framework  # Prevent Duplicate Execution
│   ├── hmdp-bloom-filter-framework # Bloom Filter
│   └── hmdp-service-delay-queue-framework    # Delay Queue
│
├── hmdp-redis-tool-framework      # Redis Tool Framework (Standalone)
│   ├── hmdp-redis-framework       # Redis Basic Operations
│   └── hmdp-redis-rate-limit-framework       # Rate Limiting (Token Bucket + Sliding Window)
│
├── hmdp-mq-framework              # Message Queue Framework (Standalone)
│   ├── hmdp-mq-producer-framework # Message Producer
│   └── hmdp-mq-consumer-framework # Message Consumer
│
└── hmdp-id-generator-framework    # ID Generator Framework (Standalone)
    └── Snowflake Algorithm (Auto MAC address registration)
```

---

## 📦 Project Structure

### Backend Structure (`hmdp-ultra-master`)

```
hmdp-ultra-master/
├── hmdp-common/                        # Common Base Module
│   └── src/main/java/org/javaup/
│       ├── constant/                    # Constants
│       │   ├── Constant.java
│       │   ├── DistributedLockConstants.java
│       │   └── RepeatExecuteLimitConstants.java
│       ├── enums/                       # Enums
│       │   ├── BaseCode.java            # Business Codes (0-10013)
│       │   ├── BusinessType.java
│       │   ├── LogType.java
│       │   ├── OrderStatus.java
│       │   ├── ReconciliationStatus.java
│       │   ├── SeckillVoucherOrderOperate.java
│       │   ├── StockUpdateType.java
│       │   ├── SubscribeStatus.java
│       │   └── VoucherStatus.java
│       ├── exception/                    # Exceptions
│       │   ├── ArgumentError.java
│       │   ├── ArgumentException.java
│       │   ├── BaseException.java
│       │   └── HmdpFrameException.java
│       ├── utils/
│       │   └── DateUtils.java           # Date Utils (29KB+)
│       ├── core/
│       │   └── SpringUtil.java           # Spring Context
│       ├── environment/
│       │   └── SpringEnvironment.java
│       └── threadlocal/
│           └── BaseParameterHolder.java  # ThreadLocal Wrapper
│
├── hmdp-core-service/                   # Core Business Service ⭐
│   └── src/main/java/org/javaup/
│       ├── controller/                   # Controllers (10)
│       │   ├── ShopController.java       # Shop API
│       │   ├── ShopTypeController.java    # Shop Type API
│       │   ├── UserController.java        # User API
│       │   ├── VoucherController.java      # Voucher API
│       │   ├── VoucherOrderController.java # Voucher Order API ⭐
│       │   ├── BlogController.java         # Blog API
│       │   ├── BlogCommentsController.java # Blog Comments API
│       │   ├── FollowController.java       # Follow API
│       │   ├── UploadController.java       # File Upload API
│       │   └── TestController.java         # Test API
│       │
│       ├── service/                       # Service Layer
│       │   ├── IVoucherOrderService.java  # Order Core Interface
│       │   ├── ISeckillVoucherService.java # Seckill Voucher Interface
│       │   ├── IReconciliationTaskService.java # Reconciliation Task
│       │   ├── IRollbackFailureLogService.java # Rollback Log
│       │   └── impl/                       # Implementations (17)
│       │       ├── VoucherOrderServiceImpl.java      # 36KB+ Core
│       │       ├── VoucherServiceImpl.java           # 24KB+
│       │       ├── ReconciliationTaskServiceImpl.java # 10KB+
│       │       └── ...
│       │
│       ├── mapper/                        # Data Access Layer (15)
│       │   ├── VoucherOrderMapper.java
│       │   ├── SeckillVoucherMapper.java
│       │   ├── VoucherReconcileLogMapper.java
│       │   └── ...
│       │
│       ├── entity/                        # Entities (14)
│       │   ├── Voucher.java
│       │   ├── VoucherOrder.java
│       │   ├── SeckillVoucher.java
│       │   ├── VoucherReconcileLog.java   # Reconciliation Log
│       │   └── ...
│       │
│       ├── kafka/                         # Kafka Message Processing ⭐
│       │   ├── consumer/
│       │   │   ├── SeckillVoucherConsumer.java          # Seckill Voucher Consumer
│       │   │   ├── SeckillVoucherInvalidationConsumer.java
│       │   │   └── SeckillVoucherInvalidationDlqConsumer.java
│       │   ├── producer/
│       │   │   ├── SeckillVoucherProducer.java
│       │   │   └── SeckillVoucherInvalidationProducer.java
│       │   ├── message/
│       │   │   ├── SeckillVoucherMessage.java
│       │   │   └── SeckillVoucherInvalidationMessage.java
│       │   └── redis/
│       │       └── RedisVoucherData.java   # Redis Inventory Operations
│       │
│       ├── delay/                          # Delay Queue ⭐
│       │   ├── consumer/
│       │   │   └── ConsumerDelayedVoucherReminder.java  # 15KB+
│       │   └── message/
│       │       └── DelayedVoucherReminderMessage.java
│       │
│       ├── cache/                          # Cache Processing
│       │   ├── CacheClient.java            # Cache Utils (Penetration/Breakdown/Avalanche)
│       │   ├── SeckillVoucherLocalCache.java
│       │   └── SeckillVoucherCacheInvalidationPublisher.java
│       │
│       ├── utils/                           # Utils
│       │   ├── RedisIdWorker.java           # Redis ID Generator
│       │   ├── SimpleRedisLock.java        # Simple Distributed Lock
│       │   ├── LoginInterceptor.java
│       │   ├── RefreshTokenInterceptor.java
│       │   └── UserHolder.java             # User Context
│       │
│       ├── lua/                            # Lua Scripts
│       │   ├── SeckillVoucherDomain.java    # Seckill Domain
│       │   ├── SeckillVoucherOperate.java  # Inventory Operations
│       │   └── SeckillVoucherRollBackOperate.java  # Rollback Operations
│       │
│       └── init/                            # Initialization
│           ├── BloomFilterDataInit.java     # Bloom Filter Init
│           ├── SeckillVoucherDataRenewalInit.java # Seckill Voucher Data Renewal
│           └── DelCacheDataInit.java
│
├── hmdp-redisson-framework/               # Distributed Lock Framework
│   ├── hmdp-redisson-common-framework/
│   │   ├── RedissonCommonAutoConfiguration.java
│   │   ├── RedissonBaseProperties.java
│   │   ├── lockinfo/                       # Lock Info Processing
│   │   └── locallock/
│   │       └── LocalLockCache.java
│   │
│   ├── hmdp-service-lock-framework/       # @ServiceLock ⭐
│   │   ├── servicelock/
│   │   │   ├── ServiceLocker.java          # Lock Interface
│   │   │   ├── annotion/
│   │   │   │   └── ServiceLock.java        # @ServiceLock Annotation
│   │   │   ├── aspect/
│   │   │   │   └── ServiceLockAspect.java  # AOP Aspect
│   │   │   ├── impl/
│   │   │   │   ├── RedissonReentrantLocker.java  # Reentrant Lock
│   │   │   │   ├── RedissonFairLocker.java      # Fair Lock
│   │   │   │   ├── RedissonReadLocker.java      # Read Lock
│   │   │   │   └── RedissonWriteLocker.java     # Write Lock
│   │   │   └── factory/
│   │   │       └── ServiceLockFactory.java
│   │   ├── servicelock/info/
│   │   │   ├── LockTimeOutStrategy.java    # Timeout Strategy
│   │   │   └── LockTimeOutHandler.java
│   │   └── config/
│   │       └── ServiceLockAutoConfiguration.java
│   │
│   ├── hmdp-repeat-execute-limit-framework/ # Prevent Duplicate Execution
│   │
│   ├── hmdp-bloom-filter-framework/        # Bloom Filter
│   │   └── handler/
│   │       ├── BloomFilterHandler.java    # Core Implementation
│   │       └── BloomFilterHandlerFactory.java
│   │
│   └── hmdp-service-delay-queue-framework/ # Delay Queue
│       └── core/
│           ├── DelayConsumerQueue.java    # Consumer
│           ├── DelayProduceQueue.java      # Producer
│           └── DelayBaseQueue.java
│
├── hmdp-redis-tool-framework/             # Redis Tool Framework
│   ├── hmdp-redis-framework/              # Redis Basic Operations ⭐
│   │   ├── redis/
│   │   │   ├── RedisCache.java             # Interface (1300+ lines)
│   │   │   ├── RedisCacheImpl.java         # Implementation (52KB+)
│   │   │   ├── CacheUtil.java
│   │   │   └── RedisKeyBuild.java          # Key Builder
│   │   └── core/
│   │       └── RedisKeyManage.java         # Key Enum Management
│   │
│   └── hmdp-redis-rate-limit-framework/    # Rate Limit Framework ⭐
│       ├── execute/
│       │   ├── RateLimitHandler.java       # Rate Limit Interface
│       │   └── RedisRateLimitHandler.java  # Implementation (12KB+)
│       ├── lua/
│       │   ├── SlidingRateLimitOperate.java    # Sliding Window
│       │   └── TokenBucketRateLimitOperate.java # Token Bucket
│       └── ratelimit/extension/
│           ├── RateLimitContext.java
│           ├── RateLimitPenaltyPolicy.java
│           └── ThresholdPenaltyPolicy.java  # Threshold Penalty Strategy
│
├── hmdp-mq-framework/                      # Message Queue Framework
│   ├── hmdp-mq-producer-framework/
│   │   └── AbstractProducerHandler.java    # Producer Base Class
│   ├── hmdp-mq-consumer-framework/
│   │   └── AbstractConsumerHandler.java    # Consumer Base Class
│   └── hmdp-mq-common-framework/
│       └── MessageExtend.java              # Message Extension
│
└── hmdp-id-generator-framework/             # ID Generator Framework
    └── toolkit/
        ├── SnowflakeIdGenerator.java       # Snowflake Algorithm ⭐
        ├── WorkAndDataCenterIdHandler.java
        └── WorkDataCenterId.java
```

### Frontend Structure (`hmdp-vue3`)

```
hmdp-vue3/
├── src/
│   ├── api/                    # API Interfaces
│   ├── assets/                 # Static Resources
│   ├── components/             # Common Components
│   ├── router/                 # Router Configuration
│   ├── store/                  # Pinia State Management
│   ├── utils/                  # Utility Functions
│   └── views/                  # Page Views
│       ├── shop/               # Shop Related
│       ├── voucher/            # Voucher Related
│       ├── user/                # User Related
│       └── ...
├── package.json
└── vite.config.js
```

---

## 🚀 Quick Start

### Environment Requirements

| Environment | Version | Description |
|-------------|---------|-------------|
| JDK | 17+ | JDK 17 recommended |
| MySQL | 8.0+ | Sharding supported |
| Redis | 6.0+ | Redis 7.x recommended |
| Kafka | 2.8+ | Message Queue |
| Node.js | 16+ | Frontend Development |
| Maven | 3.8+ | Project Build |

### 📥 Clone Project

```bash
git clone https://github.com/AlanSmithDev/hmdp-ultra.git
cd hmdp-ultra
```

### 🗄️ Database Initialization

```bash
# Login to MySQL
mysql -u root -p

# Execute database initialization script
source sql/1_create_database.sql

# Import two sharding databases separately
source sql/hmdp_0.sql
source sql/hmdp_1.sql
```

### 🖥️ Backend Startup

```bash
cd hmdp-ultra-master

# Maven build (skip tests)
mvn clean install -DskipTests

# Start core service
mvn spring-boot:run -pl hmdp-core-service
```

> ⚠️ **Before startup**: Ensure MySQL, Redis, Kafka services are running

### 🌐 Frontend Startup

```bash
cd hmdp-vue3

# Install dependencies
npm install
# or use pnpm
pnpm install

# Start development mode
npm run dev

# Production build
npm run build
```

### ✅ Verify Running

After successful startup, access:

| Service | Address | Description |
|---------|---------|-------------|
| Frontend | http://localhost:5173 | Vue 3 Dev Server |
| Backend API | http://localhost:8080 | Spring Boot API |
| Knife4j Docs | http://localhost:8080/doc.html | API Online Documentation |

---

## 💡 Core Module Details

### 🎫 Coupon Flash Sale Process

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│  User    │ →  │  Apply    │ →  │  Token   │ →  │  Create  │ →  │ MQ Async │
│  Click   │     │  Token   │     │  Verify  │     │  Order   │     │  Process │
│  Buy Now │     │          │     │ (Rate Limit)│    │          │     │          │
└──────────┘     └──────────┘     └──────────┘     └──────────┘     └──────────┘
                                                                        │
                                          ┌──────────────────────────────┘
                                          ↓
                               ┌──────────────────┐    ┌──────────────────┐
                               │   Delay Detect   │ →  │   Reconciliation │
                               │   (10 seconds)   │    │   Compensation   │
                               └──────────────────┘    └──────────────────┘
```

### 📊 Database Design

#### Sharding Strategy

| Table | Sharding DB | Sharding Table | Description |
|-------|-------------|-----------------|-------------|
| tb_user | id % 2 | id % 2 | User table |
| tb_user_info | user_id % 2 | user_id % 2 | User info table |
| tb_user_phone | phone hash | phone hash | Phone number table |
| tb_voucher | id % 2 | id % 2 | Voucher table |
| tb_seckill_voucher | voucher_id % 2 | voucher_id / 2 % 2 | Flash sale voucher |
| tb_voucher_order | user_id % 2 | voucher_id % 2 | Voucher order table |
| tb_voucher_order_router | order_id hash | order_id / 2 % 2 | Order router table |
| tb_voucher_reconcile_log | order_id hash | order_id / 2 % 2 | Reconciliation log |

#### Broadcast Tables (Consistent across all DBs)

| Table | Description |
|-------|-------------|
| tb_blog | Blog table |
| tb_blog_comments | Blog comments table |
| tb_follow | Follow table |
| tb_shop | Shop table |
| tb_shop_type | Shop type table |
| tb_sign | Sign-in table |

### ⚡ V2 Flash Sale Process

```
┌─────────────────────────────────────────────────────────────────┐
│                     Flash Sale Flow (V2)                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. Get Access Token                                              │
│     ├─ Token bucket rate limit check                             │
│     ├─ Validate user qualification                               │
│     └─ Generate and return accessToken                           │
│                           │                                      │
│                           ▼                                      │
│  2. Submit Flash Sale Request                                    │
│     ├─ Validate accessToken                                      │
│     ├─ Token bucket rate limit check                             │
│     └─ Enter flash sale core process                             │
│                           │                                      │
│                           ▼                                      │
│  3. Lua Script Atomic Deduction                                   │
│     ├─ Check activity time                                       │
│     ├─ Check stock                                                │
│     ├─ Check if already purchased                                 │
│     ├─ Deduct stock                                               │
│     ├─ Record purchased user                                      │
│     └─ Write tracking log                                         │
│                           │                                      │
│                           ▼                                      │
│  4. Send Kafka Message                                           │
│     ├─ Async create order                                        │
│     ├─ Order router table record                                 │
│     └─ Reconciliation log record                                  │
│                           │                                      │
│                           ▼                                      │
│  5. Poll Query Order Result                                      │
│     ├─ Query Redis cache                                         │
│     ├─ Query order router table                                  │
│     └─ Return order status                                       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Core Lua Script Logic

```lua
-- Flash Sale Core Lua Script Flow:
1. Get current Redis server time
2. Validate flash sale activity start/end time
3. Validate voucher status (listed/delisted/expired)
4. Get and validate stock
5. Validate if user has already purchased (deduplication)
6. Atomically deduct stock
7. Record purchased user to Set
8. Write tracking log to Hash
9. Set key expiration time
10. Return execution result (success/failure code)
```

### 🔐 Core Code Examples

#### 1. Token Issuance Service

```java
// ISeckillAccessTokenService.java
public interface ISeckillAccessTokenService {
    /**
     * Issue flash sale token
     * @param voucherId Voucher ID
     * @param userId User ID
     * @return Token string
     */
    String issueAccessToken(Long voucherId, Long userId);

    /**
     * Validate and consume token
     * @param voucherId Voucher ID
     * @param userId User ID
     * @param token Token
     * @return Is valid
     */
    boolean validateAndConsume(Long voucherId, Long userId, String token);
}
```

#### 2. Rate Limit Handler

```java
// RedisRateLimitHandler.java core method
@Override
public void execute(Long voucherId, Long userId, RateLimitScene scene) {
    String clientIp = resolveClientIp();

    // Whitelist check
    if (isWhitelisted(userId, clientIp)) {
        return;
    }

    // Ban check
    checkBans(voucherId, userId, clientIp);

    // Build rate limit keys
    List<String> keys = buildRateLimitKeys(voucherId, userId, clientIp, useSliding);

    // Execute Lua script for rate limiting
    Integer result = executeLua(useSliding, keys, args);

    // Handle result
    handleResult(ctx);
}
```

#### 3. Distributed Lock Annotation

```java
// Usage example
@ServiceLock(name = "voucher:order:", lockType = LockType.REENTRANT_LOCK,
            waitTime = 3, leaseTime = 10)
public Result<Long> seckillVoucher(Long voucherId) {
    // Business logic, automatic lock/unlock
    return voucherOrderService.seckillVoucher(voucherId);
}
```

### 📊 Core Configuration

```yaml
# application.yml core configuration
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
      ip-window-millis: 1000      # IP window time (ms)
      ip-max-attempts: 5          # IP max requests
      user-window-millis: 1000    # User window time (ms)
      user-max-attempts: 2        # User max requests
      enable-sliding-window: true  # Enable sliding window
```

---

## 🛠️ Technology Stack

### Backend Technology Stack

| Category | Technology | Version | Description |
|----------|------------|---------|-------------|
| **Framework** | Spring Boot | 3.5.4 | Core Framework |
| | Java | 17 | Programming Language |
| **Data Access** | MyBatis-Plus | 3.5.7 | ORM Framework |
| | ShardingSphere | 5.3.2 | Database Sharding |
| **Cache** | Redis | 6.0+ | Cache + Distributed Lock |
| | Redisson | 3.52.0 | Redis Client |
| **Message Queue** | Kafka | 2.8+ | Async Messaging |
| **Security** | Sa-Token | 1.43.0 | Authentication & Authorization |
| **Utils** | Hutool | 5.8.25 | Tool Suite |
| | Fastjson | 2.0.9 | JSON Processing |
| **Documentation** | Knife4j | 4.3.0 | API Documentation |
| **Concurrency** | QPS-Helper | 1.1.3 | Rate Limit Helper |

### Frontend Technology Stack

| Category | Technology | Version | Description |
|----------|------------|---------|-------------|
| **Framework** | Vue | 3.5.13 | Core Framework |
| | Vite | 5.x | Build Tool |
| **UI** | Element Plus | 2.x | Component Library |
| **State** | Pinia | 2.x | State Management |
| **Network** | Axios | 1.x | HTTP Client |

---

## 📊 High Concurrency Solutions

### Solution Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           HMDP-Ultra High Concurrency Architecture          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   【Entry Layer】                                                             │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                     │
│   │ Token Bucket │ →  │   Whitelist│ →  │IP/User Rate │                    │
│   │ Pre-Auth     │    │   Config   │    │  Dual Check  │                    │
│   └─────────────┘    └─────────────┘    └─────────────┘                     │
│                                                                             │
│   【Cache Layer】                                                             │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                     │
│   │Bloom Filter │ →  │Multi-Level  │ →  │Hot Data     │                     │
│   │Block Pierce  │    │  Cache      │    │Auto Upgrade │                     │
│   └─────────────┘    └─────────────┘    └─────────────┘                     │
│                                                                             │
│   【Data Layer】                                                             │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                     │
│   │Redis Pre-Dec│ →  │MQ Async     │ →  │Reconciliation│                    │
│   │Inventory    │    │Peak Shaving │    │Final Consistent│                   │
│   └─────────────┘    └─────────────┘    └─────────────┘                     │
│                                                                             │
│   【Storage Layer】                                                           │
│   ┌─────────────┐    ┌─────────────┐                                        │
│   │Sharding     │ →  │Optimistic   │                                        │
│   │Sphere       │    │Lock         │                                        │
│   └─────────────┘    └─────────────┘                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Key Points Quick Reference

| # | Problem | Solution | Key Class/Method |
|---|---------|----------|------------------|
| 1 | Cache Penetration | Bloom Filter + Null Mark | `BloomFilterHandler` |
| 2 | Cache Breakdown | Logical Expire + Mutex Lock | `queryWithLogicalExpire` |
| 3 | Cache Avalanche | Random TTL + Async Rebuild | `setWithLogicalExpire` |
| 4 | Overselling | Redis Atomic Deduction + Lua | `SeckillVoucherOperate` |
| 5 | Message Loss | Publish Callback + Delay Detection | `AbstractProducerHandler` |
| 6 | Duplicate Consumption | Consumer Idempotent + Reconciliation | `SeckillVoucherConsumer` |
| 7 | Data Consistency | Redis Pre-Dec → MQ → DB → Reconciliation | `ReconciliationTaskService` |
| 8 | Hot Data | Local Cache + Hot Detection | `SeckillVoucherLocalCache` |

### 🔧 Key Problem Solutions

#### 1. Cache Penetration & Breakdown

| Problem | Solution |
|---------|----------|
| Cache Penetration | Null Cache + Bloom Filter |
| Cache Breakdown | Local Cache + Double Lock + Logical Expire |
| Cache Avalanche | Random TTL + Multi-level Cache |

#### 2. High Concurrency Flash Sale

| Problem | Solution |
|---------|----------|
| Overselling | Lua Script Atomic Stock Deduction |
| Duplicate Order | Redis Set Deduplication + DB Unique Index |
| Traffic Control | Token Pre-auth + Token Bucket Rate Limit |
| Async Order | Kafka Message Queue + Delay Queue Compensation |

#### 3. Data Consistency

| Scenario | Solution |
|----------|----------|
| Redis/MySQL Inconsistency | Reconciliation Log + Scheduled Compensation |
| Message Loss | Publish Callback + Local Message Table |
| Duplicate Consumption | Idempotent Component + Deduplication Table |

#### 4. Sharding

| Problem | Solution |
|---------|----------|
| Data Routing | ShardingSphere Standard Sharding Strategy |
| Global ID | Snowflake Algorithm |
| Cross-shard Query | Order Router Table |

### 📊 Performance Metrics

| Metric | Value | Description |
|--------|-------|-------------|
| **Single Machine QPS** | 5000+ QPS | Flash sale endpoint |
| **Stock Deduction** | Lua Atomic Operation | No overselling |
| **Message Latency** | < 100ms | Kafka end-to-end |
| **Cache Hit Rate** | > 95% | Multi-level cache |

---

## 🔧 Configuration Guide

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

# HMDP Core Configuration
hmdp:
  token:
    cache-ttl-minutes: 10          # Token cache time
    enable: true                    # Enable token
  rate-limit:
    ip-window-millis: 1000
    ip-max-attempts: 5
    user-window-millis: 1000
    user-max-attempts: 2
    enable-sliding-window: true
  reconciliation:
    schedule-cron: 0 */5 * * * ?   # Reconciliation task cron
```

---

## 📝 API Documentation

After starting the project, access **Knife4j** online documentation:

```
http://localhost:8080/doc.html
```

### Core API List

| Module | Endpoint | Method | Description |
|--------|----------|--------|-------------|
| **Voucher** | `/voucher-order/seckill/token/{id}` | GET | Apply flash sale token |
| | `/voucher-order/seckill/{id}` | POST | Submit flash sale order |
| | `/voucher-order/cancel` | POST | Cancel order |
| | `/voucher/reconciliation/task/all` | POST | Trigger full reconciliation |
| **Shop** | `/shop/{id}` | GET | Query shop |
| | `/shop/of/type` | GET | Query shops by type |
| **User** | `/user/login` | POST | User login |
| | `/user/code` | GET | Get verification code |

---

## 🤝 Contributing

Issues and Pull Requests are welcome!

### Commit Convention

```
feat: New feature
fix: Fix bug
docs: Documentation update
style: Code formatting (no functional change)
refactor: Refactoring
test: Test related
chore: Build/tool related
```

### Development Flow

1. **Fork** this repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Create **Pull Request**

---

## 📄 License

This project is licensed under **Apache License 2.0**.

See [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Alan Smith** - [GitHub](https://github.com/AlanSmithDev)

> 💡 If this project is helpful to you, please give it a **Star**!

---

## 🙏 Acknowledgments

- Thanks to [Redisson](https://github.com/redisson/redisson) for excellent distributed lock implementation
- Thanks to [ShardingSphere](https://github.com/shardingsphere/shardingsphere) for database sharding solution
- Thanks to [MyBatis-Plus](https://github.com/baomidou/mybatis-plus) for efficient ORM support

---

<p align="center">
  <strong>Made with ❤️ by Alan Smith | Licensed under Apache-2.0</strong>
</p>
