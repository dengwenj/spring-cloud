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