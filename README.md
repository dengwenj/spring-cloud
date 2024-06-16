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
