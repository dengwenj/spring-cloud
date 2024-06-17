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

## nacos 注册中心

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
      server-addr: localhost:8848
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