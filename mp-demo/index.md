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