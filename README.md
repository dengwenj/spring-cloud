## Spring-cloud

## 单体架构
* 将业务的所有功能集中在一个项目中开发，打成一个包部署
* 优点：架构简单、部署成本低
* 缺点：团队协作成本高、系统发布效率低、系统可用性差
* 单体架构适合开发功能相对简单，规模较小的项目

## 微服务
* 微服务架构，是服务化思想指导下的一套最佳实践架构方案。服务化，就是把单体架构中的功能模块拆分为多个独立项目
* 粒度小，团队自治，服务自治

## SpringCloud
* SpringCloud 是目前国内使用最广泛的微服务框架
* SpringCloud 是标准的制定者，定义了微服务组件的标准(接口和规范)，所有的微服务组件都来实现接口和规范
* SpringCloud 集成了各种微服务功能组件，并基于 SpringBoot 实现了这些组件的自动装配，从而提供了良好的开箱即用体验

## 怎么拆分?
* 高内聚：每个微服务的职责要尽量单一，包含的业务相互关联度高、完成度高
* 低耦合：每个微服务的功能要相对独立，尽量减少对其他微服务的依赖
* 纵向拆分：按照业务模块来拆分
* 横向拆分：抽取公共服务，提供复用性
* 工程结构有两种：1、独立Project，2、Maven聚合

## 远程调用
* Spring 给我们提供了一个 RestTemplate 工具，可以方便的实现Http请求的发送。
* 1、注入 RestTemplate 到 Spring 容器
* 2、发起远程调用
```java
ResponseEntity<List<ItemDTO>> response = restTemplate.exchange(
    "http://localhost:8081/items?ids={ids}",
    HttpMethod.GET,
    null,
    new ParameterizedTypeReference<List<ItemDTO>>() {
    },
    Map.of("ids", itemIds.stream().map((item) -> item + "").collect(Collectors.joining(",")))
);
```

## 拆分后碰到的第一个问题是什么，如何解决？
* 拆分后，某些数据在不同的服务，无法直接调用本地方法查询数据
* 利用 RestTemplate 发送 http 请求，实现远程调用

## 服务治理中的三个角色分别是什么？
* 服务提供者：暴露服务接口，供其它服务调用
* 服务消费者：调用其它服务提供的接口
* 注册中心：记录并监控微服务各实例状态，推送服务变更信息

## 消费者如何知道提供者的地址？
* 服务提供者会在启动时注册自己信息到注册中心，消费者可以从注册中心订阅和拉取服务信息

## 消费者如何得知服务状态变更？
* 服务提供者通过心跳机制向注册中心报告自己的健康状态，当心跳异常注册中心会将异常服务剔除，并通知订阅了该服务的消费者

## 当提供者有多个实例时，消费者该选择哪一个？
* 消费者可以通过负载均衡算法，从多个实例中选择一个

## nacos 注册中心(是个服务，也需要下载安装运行)

## 服务注册
* 服务注册步骤如下：
* 1、引入 nacos discovery 依赖
```xml
<!--        nacos 服务注册发现-->
<dependencies>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
</dependencies>
```
* 2、配置 nacos 地址
```yaml
spring:
  application:
    name: item-service # 微服务名称
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        ip: 127.0.0.1
```

## 服务发现
* 消费者需要连接 nacos 以拉取和订阅服务，因此服务发现的前两步与服务注册时一样，后面再加上服务调用即可：
* 1、引入 nacos discovery 依赖
* 2、配置 nacos 地址
* 3、服务发现
```java
// 根据服务名称获取服务的实例列表
List<ServiceInstance> instances = discoveryClient.getInstances("item-service");
// 手写负载均衡，从实例列表中挑选一个实例
ServiceInstance instance = instances.get(new Random().nextInt(instances.size()));
// 获取实例的 ip 和 端口
URI uri = instance.getUri();
```

## OpenFeign
* OpenFeign 是一个声明式的 http 客户端，是 SpringCloud 在 Eureka 公司开源的 Feign 基础上改造而来
* 其作用就是基于 SpringMVC 的常见注解，帮我们优雅的实现 http 请求的发送
* OpenFeign 已经被 SpringCloud 自动装配，实现起来非常简单
* 1、引入依赖，包括 OpenFeign 和负载均衡组件 SpringCloudLoadBalancer
* 2、通过@EnableFeignClients注解，启用 OpenFeign 功能
* 3、编写 FeignClient
* 4、使用 FeignClient，实现远程调用
```xml
<!--       OpenFeign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
<!--        负载均衡-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>
```
```java
@EnableFeignClients
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}
```
```java
@FeignClient(value = "item-service")
public interface ItemClient {
    @GetMapping("/items")
    List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);
}
```
```java
List<ItemDTO> items = itemClient.queryItemByIds(itemIds);
```

## 连接池
* OpenFeign 对 Http 请求做了优雅的伪装，不过其底层发起 http 请求，依赖于其他的框架，这些框架可以自己选择，包括以下三种：
* HttpURLConnection：默认实现，不支持连接池
* Apache HttpClient：支持连接池
* OKHttp：支持连接池
* 1、引入依赖
```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-okhttp</artifactId>
</dependency>
```
* 2、开启连接池功能
```yaml
feign:
  okhttp:
    enabled: true # 开启okhttp连接池支持
```

## 最佳实践
* 当定义的 FeignClient 不在 SpringBootApplication 的扫描包范围时，这些 FeignClient 无法使用，有两种方式解决
* 方式一：指定 FeignClient 所在包，@EnableFeignClients(basePackages = "com.hmall.api.client")
* 方式二：指定 FeignClient 字节码，@EnableFeignClients(clients = {UserClient.class})

## 日志
* 要自定义日志级别需要声明一个类型为 Logger.Level 的 Bean，在其中定义日志级别
```java
public class DefaultFeignConfig {
    @Bean 
    public Logger.Level feignLogLevel() {
        return Logger.Level.FULL;
    }
}
```
* 但此时这个 Bean 并未生效，想要配置某个 FeignClient 的日志，可以在 @FeignClient 注解中声明
* 如果想要全局配置，让所有 FeignClient 都按照这个日志配置，则需要再 @EnableFeignClients 注解中声明

## 总结
* 需要跨服务去做数据的查询，就需要用到远程调用的知识(RestTemplate)，
* RestTemplate 去做远程调用有很多问题，最大的问题就是把 ip 地址给写死了，当服务的提供者会发生变化时，代码中没有及时的察觉，
* 这就是需要去做服务治理，就引出了 nacos 注册中心，核心功能就是帮助我们去做服务注册发现，去管理所有的微服务，并监控微服务的状态等等，这样就解决了上述的问题，
* 只不过代码就会变得复杂，要自己获取实例列表手写负载均衡获取uri，这时就引出了 OpenFeign，它可以简化这种跨服务调用的代码，只需要定义一个接口就可以发起调用，然后就对 OpenFeign 做了进一步的优化和改造。

## 网关（也是个微服务，入口 ）
* 网关就是网络的关口，负责请求的路由、转发、身份校验
* SpringCloud 中网关的实现包括两种：
* 1、SpringCloudGateway：基于 webflux 响应式变成，无需调优即可获得优异性能
* 2、Netfilx Zuul：基于 Servlet 的阻塞式编程，需要调优才能获得与 SpringCloudGateway 类似的性能

## 网关配置路由规则
```yaml
server:
  port: 8080
spring:
  application:
    name: gateway # 服务名
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        ip: 127.0.0.1
    gateway:
      routes:
        - id: item-service # 路由规则id，自定义，唯一
          uri: lb://item-service # 路由目标微服务，lb代表负载均衡
          predicates: # 路由断言，判断请求是否符合规则，符合则路由到目标
            - Path=/items/** # 以请求路径做判断，以 /items 开头则符合
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/addresses/**,/users/**
```

## 路由属性
* 网关路由对应的 java 类型是 RouteDefinition，其中常见的属性有：
* 1、id：路由唯一标识
* 2、uri：路由目标地址
* 3、predicates：路由断言，判断请求是否符合当前路由
* 4、filters：路由过滤器，对请求或响应做特殊处理

## 路由断言
* Spring 提供了 12 种基本的 RoutePredicateFactory 实现：
* After：是某个时间点后的请求
* Before：是某个时间点之前的请求
* Between：是某两个时间点之间的请求
* Cookie：请求必须包含某些 cookie
* Header：请求必须包含某些 header
* Host：请求必须是访问某个 host（域名）
* Method：请求方式必须是指定方式
* Path：请求路径必须符合指定规则
* Query：请求参数必须包含指定参数
* RemoteAddr：请求者的 ip 必须是指定范围
* Weight：权重处理 
* XForwarded Remote Addr：基于请求的来源 ip 做判断

## 路由过滤器
* 网关中提供了33种路由过滤器，每种过滤器都有独特的作用
* AddRequestHeader：给当前请求添加一个请求头
* AddResponseHeader：给响应结果中添加一个响应头
* ...

## 网关登录校验
* 1、如何在网关转发之前做登录校验？
* 2、网关如何将用户信息传递给微服务？
* 3、如何在微服务之间传递用户信息？

## 自定义过滤器
* 网关过滤器有两种，分别是：
* GatewayFilter：路由过滤器，作用于任意指定的路由，默认不生效，要配置到路由后生效
* GlobalFilter：全局过滤器，作用范围是所有路由，声明后自动生效
* 第一个参数 exchange：请求上下文，包含整个过滤器链内共享数据，例如 request，response
* 第二个参数 chain：过滤器链，当前过滤器执行完后，要调用过滤器链中的下一个过滤器

## 自定义过滤器GlobalFilter
```java
@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        System.out.println(request.getHeaders());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 数字越小，优先级越高
        return 0;
    }
}
```