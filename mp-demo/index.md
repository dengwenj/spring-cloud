### 使用 MybatisPlus 的基本步骤
* 1、引入 MybatisPlus 依赖，替代 MyBatis 依赖
* 2、定义 Mapper 接口并继承 BaseMapper
* MybatisPlus 通过扫描实体类，并基于反射获取实体类信息作为数据库表信息
* 类名驼峰转下划线作为表名
* 名为 id 的字段作为主键
* 变量名驼峰转下划线作为表的字段名

### 常用注解
* @TableName：用来指定表名
* @TableId：用来指定表中的主键字段信息
* @TableField：用来指定表中的普通字段信息

### IdType 枚举
* AUTO：数据库自增长
* INPUT：通过 set 方法自行输入
* ASSIGN_ID：分配 ID，接口 IdentifierGenerator 的方法 nextId 来生成 id，默认实现类为 DefaultIdentifierGenerator 雪花算法

### 使用 @TableField 的常见场景
* 成员变量名与数据库字段名不一致
* 成员变量名以 is 开头，且是布尔值
* 成员变量名与数据库关键字冲突
* 成员变量不是数据库字段
```java
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("tb_user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String name;
    
    @TableField("is_married")
    private Boolean isMarried;
    
    @TableField("`order`")
    private Integer order;
    
    @TableField(exist = false)
    private String address;
}
```

## MybatisPlus 使用的基本流程是什么？
* 1、引入起步依赖
* 2、自定义 Mapper 基础 BaseMapper
* 3、在实体类上添加注解声明表信息
* 4、在 application.yml 中根据需要添加配置

## 条件构造器
* MybatisPlus 支持各种复杂的 where 条件
* Wrapper、AbstractWrapper、UpdateWrapper、QueryWrapper、AbstractLambdaWrapper、LambdaUpdateWrapper、LambdaQueryWrapper
* QueryWrapper 和 LambdaQueryWrapper 通常用来构建 select、delete、update 的 where 条件部分
* UpdateWrapper 和 LambdaUpdateWrapper 通常只有在 set 语句比较特殊才使用
* 尽量使用 LambdaQueryWrapper 和 LambdaUpdateWrapper，避免硬编码
```java
@Test
void testSelectByQueryWrapper() {
    // 条件构建器
    Wrapper<User> wrapper = new QueryWrapper<User>()
        .select("id", "username", "info", "balance")
        .like("username", "o")
        .ge("balance", 2000);
    List<User> users = userMapper.selectList(wrapper);
    System.out.println("users" + users);
}

@Test
void testSelectByLambdaQueryWrapper() {
    // 条件构建器
    Wrapper<User> wrapper = new LambdaQueryWrapper<User>()
        .select(User::getId, User::getUsername, User::getInfo, User::getBalance)
        .like(User::getUsername, "o")
        .ge(User::getBalance, 2000);
    List<User> users = userMapper.selectList(wrapper);
    System.out.println("users" + users);
}

@Test
void testUpdateByQueryWrapper() {
    User user = new User();
    user.setBalance(2000);
    Wrapper<User> wrapper = new QueryWrapper<User>().eq("username", "jack");
    userMapper.update(user, wrapper);
}

@Test
void testUpdateByUpdateWrapper() {
    Wrapper<User> wrapper = new UpdateWrapper<User>()
        .setSql("balance = balance - 200")
        .in("id", List.of(1L, 2L, 3L, 4L));
    userMapper.update(null, wrapper);
}
```

### 自定义 SQL
* 可以利用 MybatisPlus 的 Wrapper 来构建复杂的 Where 条件，然后自己定义 SQL 语句中剩下的部分
```java
@Test
void testUpdateBalanceByIds() {
    Wrapper<User> wrapper = new QueryWrapper<User>().in("id", List.of(1L, 2L, 3L, 4L));
    userMapper.updateBalanceByIds(wrapper, 200);
}

public interface UserMapper extends BaseMapper<User> {
    void updateBalanceByIds(@Param(Constants.WRAPPER) Wrapper<User> wrapper, Integer balance);
}

//<update id="updateBalanceByIds">
//  update user set balance = balance - #{balance} ${ew.customSqlSegment}
//</update>
```
### Service 接口
* MP 的 Service 接口使用流程是怎样的？
* 1、自定义 Service 接口继承 IService 接口：public interface IUserService extends IService<User> {}
* 2、自定义 Service 实现类，实现自定义接口并继承 ServiceImpl 类：
* public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {}
* 基础的增删改查可以直接掉 Service 中实现的方法，有业务逻辑的自定义方法，Mapper 也是一样，复杂的可以自定义 sql

### service 接口实现的 LambdaQuery 和 LambdaUpdate
* LambdaQuery 主要用来做复杂查询的，LambdaUpdate 主要用来复杂更新
```java
@Override
public void deductBalance(Long id, Integer money) {
    // 查询用户
    User user = this.getById(id);
    // 检验用户状态
    if (user == null && user.getStatus() == 2) {
        throw new RuntimeException("出错");
    }
    // 检验余额是否充足
    if (user.getBalance() < money) {
        throw new RuntimeException("用户余额不足");
    }
    // 扣减余额
    // this.baseMapper.deductBalance(id, money);
    int i = user.getBalance() - money;
    this.lambdaUpdate()
        .set(User::getBalance, i)
        .set(i == 0, User::getStatus, 2)
        .eq(User::getId, id)
        .eq(User::getBalance, user.getBalance()) // 乐观锁
        .update();
}

@Override
public List<User> getList(UserQuery userQuery) {
    String name = userQuery.getName();
    Integer status = userQuery.getStatus();
    Integer minBalance = userQuery.getMinBalance();
    Integer maxBalance = userQuery.getMaxBalance();

    return this.lambdaQuery()
        .like(name != null, User::getUsername, name)
        .eq(status != null, User::getStatus, status)
        .ge(minBalance != null, User::getBalance, minBalance)
        .le(maxBalance != null, User::getBalance, maxBalance)
        .list();
}
```

### IService 批量新增，用的 jdbc 的批处理
* 批量处理方案：
* 1、普通 for 循环逐条插入速度极差，不推荐
* 2、MP 的批量新增，基于预编译的批处理，性能一般
* 3、配置 jdbc 参数，开 rewriteBatchedStatements，性能最好

### MP 扩展功能
* service 出现相互调用，可以使用 Db 静态工具去调用
```java
@Override
public UserVO queryUserAndAddressById(Long id) {
    User user = this.getById(id);
    if (user == null) {
        throw new RuntimeException("报错了");
    }

    UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

    // Db 静态工具
    List<Address> addresses = Db.lambdaQuery(Address.class).eq(Address::getUserId, id).list();

    if (addresses != null && !addresses.isEmpty()) {
        List<AddressVO> addressVOS = BeanUtil.copyToList(addresses, AddressVO.class);
        userVO.setAddresses(addressVOS);
    }
    return userVO;
}
```

## 逻辑删除
* 逻辑删除就是基于代码逻辑模拟删除效果，但并不会真正删除数据，思路如下：
* 1、在表中添加一个字段标记数据是否被删除
* 2、当删除数据时把标记置为1
* 3、查询时只查询标记为 0 的数据
* MybatisPlus 提供了逻辑删除功能，无需改变方法调用的方式，而是在底层帮我们自动修改 CRUD 的语句，只需要在 application.yml 中配置
```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: 1 # 逻辑已删除值(默认为 1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
```

### 注意
* 逻辑删除本身也有自己的问题，比如：
* 1、会导致数据库表垃圾数据越来越多，影响查询效率
* 2、SQL 中全都需要对逻辑删除字段做判断，影响查询效率
* 因此，不太推荐采用逻辑删除功能，如果数据不能删除，可以采用把数据迁移到其它表的办法

### 枚举处理器
* 如何实现 PO 类中的枚举类型变量与数据库字段的转换？
* 1、给枚举中的与数据库对应 value 值添加 @EnumValue 注解
* 2、在配置文件中配置统一的枚举处理器，实现类型转换
```java
package com.itheima.mp.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserStatus {
    NORMAL(1, "正常"),
    FREEZE(2, "冻结");

    @EnumValue
    private final int value;
    @JsonValue // 返回给前端的值
    private final String desc;

    UserStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
```

## JSON 处理器
* 1、给 json 类型对应的 java 当中实体类字段定义注解 @TableField(typeHandler = JacksonTypeHandler.class)
* 2、给 @TableName 上加上 @TableName(value = "user", autoResultMap = true)
```java
package com.itheima.mp.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "user", autoResultMap = true)
public class  User {
    /**
     * 详细信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private UserInfo info;
}
```

### 分页插件
```java
@Configuration
public class MybatisConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 初始化核心插件，然后可以把其他插件添加进来
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(500L); // 设置分页上限

        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }
}

@Test
void testPage() {
    int pageNo = 1, pageSize = 2;
    Page<User> page = Page.of(pageNo, pageSize);
    // 排序。先排序了，然后再做返回数据的
    page.addOrder(new OrderItem("balance", true));
    page.addOrder(new OrderItem("id", true));

    Page<User> page1 = userService.page(page);

    System.out.println("page1.getTotal() = " + page1.getTotal());
    System.out.println("page1.getPages() = " + page1.getPages());
    page1.getRecords().forEach(System.out::println);
}
```