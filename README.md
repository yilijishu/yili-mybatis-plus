# 框架使用说明书
``
框架主要使用JDK.compiler包生成代码。
支持三类数据库：MYSQL、ORACLE、POSTGRESQL
字段创建表结构和字段注意：ORACLE不支持友好重复创建。会报错。
所有的bean property name转义column name 默认均使用驼峰结构。
``
## 版本发布

|版本号|说明|
|----|----|
|1.0.0|初版|
|1.0.1|更新scm、git、url信息，添加说明信息|
|||


## 一、 MAVEN引用
```xml
// maven引用方式
            <dependency>
                <artifactId>yili-mybatis-plus</artifactId>
                <groupId>cn.yili.mybatis</groupId>
                <version>1.0.0</version>
            </dependency>
```

## 二、 使用
### 1、 Mapper 建议使用方式
``
Mapper使用方式，继承BaseMapper 指定Entity泛型类。
``
```java
@Table
public class ActivityAward {
    
}

@Mapper
public interface ActivityAwardMapper extends BaseMapper<ActivityAward> {
    
}
```
### 2、 Manager建议使用方式 
``
建议事物存在Manager层，当前层不使用除了数据库以外的其他tcp操作。
``
```java
@Repository
@Transactional
public class ActivityAwardManager extends BaseManager<ActivityAward, ActivityAwardMapper> {
    
    public ActivityAward get(ActivityAward p) {
        return mapper.get(p);
    }

}

```


## 三、 表结构生成

### 使用说明 class AutoCreateAction
````java
//可以在主项目中使用执行如下语句，自动生成表结构。每次启动项目时会执行一次，建议执行一次后。清理掉当前代码。如果未清理，仅支持对于MYSQL、
// POSTGRESQL的表结构校验。对于ORACLE则会出现创建失败报错情况发生。注：下个版本会进行修复处理
//传递ApplicationContext参数给AutoCreateAction
//重复一次：建议执行一次后。清理掉当前代码。仅支持MYSQL。POSTGRESQL的重复执行。
AutoCreateAction aca = new AutoCreateAction(applicationContext);
aca.autoCreate();
````

## 四、 注解
``
注解说明中，
``
### 1、 @AddSelectCondition
``
注解 @AddSelectCondition 给 BaseMapper 的通用查询select(T p, Page page) 添加自定义的查询条件，
``
````java
        // DEMO：
        @AddSelectCondition
        public String customSql() {
            StringBuffer stringBuffer = new StringBuffer();
            String CM_SELECT = " <if test=\"p.ids != null and p.ids.size > 0\">\n" +
            " and id in \n" +
            "<foreach collection=\"p.ids\" item=\"a\" open=\"(\" separator=\",\" close=\")\">\n" +
            "#{a}\n" +
            "</foreach>\n" +
            "</if>  ";
    
            if (ids != null && ids.size() > 0) {
            stringBuffer.append(" and id in ");
            stringBuffer.append("(");
            for (int i = 0; i < ids.size(); i++) {
            stringBuffer.append(ids.get(i));
            if (i + 1 < ids.size()) {
            stringBuffer.append(",");
            }
            }
            stringBuffer.append(")");
            }
            return stringBuffer.toString();
        }
````

### 2、 @AutoCreateTime
``
注解@AutoCreateTime 自动插入创建时间戳。 例如：@AutoCreateTime
``

````java
//DEMO
@AutoCreateTime
private LocalDateTime createdTime;
````

### 3、 @AutoModifyTime
``
注解@AutoModifyTime 自动插入修改时间戳 例如：@AutoModifyTime
``

````java
//DEMO
@AutoModifyTime
private LocalDateTime modifyTime;
````

### 4、 @Column
``
注解@Column 重命名表列名 @Column("type_b") 例如： 重命名字段为 type_b
``

````java
//DEMO
@Column("column_example")
private String example;
````

### 5、 @ColumnNotNull
``
注解@ColumnNotNull 表明字段不可以为空。 例如：@ColumnNotNull 设定 字段 not null
``

````java
//DEMO
@ColumnNotNull
private String example;
````

### 6、 @ColumnType
``
注解@ColumnType , 指定字段的数据类型 例如：@ColumnType("BIGINT")
``

````java
//DEMO
@ColumnType("BIGINT")
private Long example;
````

### 7、 @DefWhere
``
注解@DefWhere ,默认的where条件，对于select、query 均有效。 
``

````java
@DefWhere("0")
private Boolean delTag;
// == del_tag=0
````

### 8、@IfFieldCondition
``
注解@IfFieldCondition 添加字段添加and判断条件。所设置的字段名必须要返回boolean 对于select有效。
``

````java
@IfFieldCondition("getAll") 
private String platformNumber;

@IgnoreColumn
private Boolean all = false;

public Boolean getAll() {
    return !(all != null && all);
}

//解析为： 满足getAll方法的条件。platformNumber 才会生效。

````

### 9、 @IgnoreColumn
``
注解@IgnoreColumn 忽略字段，指定当前字段非表字段
``

````java
//忽略all字段
@IgnoreColumn
private Boolean all = false;
````

### 10、 @IgnoreInsertColumn
``
注解@IgnoreInsertColumn 插入时忽略当前字段，当前字段为表字段
``
````java
//插入的时候忽略当前字段
@IgnoreInsertColumn
private Boolean delTag;


// 等于：example表 有id和del_tag字段。 使用当前注解@IgnoreInsertColumn后。插入语法insert into example (id) value(null);
````

### 11、 @OrderBy
``
注解@OrderBy 两个参数 一个参数value = "ASC｜｜DESC" order表明顺序。降序处理。
``

````java
//DEMO
@OrderBy(value = "DESC", order = 999)
private Long id;

//order by id desc 
````

### 12、 @OverrideOrderBy
``
注解@OverrideOrderBy 自定义order by  
``

````java
    //DEMO：
    @OverrideOrderBy
    public String customOrderBy() {
        StringBuffer sbf = new StringBuffer();
        if(this.getOrderBy() != null) {
            if(this.getOrderBy() == NewsOrderBy.RECOMMED) {
                sbf.append(" order by `recommed`,`recommed_time` desc");
            } else if(this.getOrderBy() == NewsOrderBy.TIME_ASC){
                sbf.append(" order by `id` asc");
            } else {
                sbf.append(" order by `id` desc");
            }
        } else {
            sbf.append(" order by `id` desc");
        }
        return sbf.toString();
    }
````

### 13、 @SetDataBase
``
注解@SetDataBase 设定数据库，目前仅支持MYSQL、ORACLE、POSTGRESQL。参数value 代表的是数据库名SetDataBase.DataBaseEnum枚举类
``

````java
//设定为
@SetDataBase(SetDataBase.DataBaseEnum.POSTGRESQL)
public class Application {
    
}
````

### 14、 @Table
``
注解@Table 表定义，共有两个字段value：表名定义，可以不设置，不设置默认为className的,参数2supClass 父类名（全路径）。
``

````java
//DEMO
@Table(supClass = "cn.yili.commons.entity.base.BaseEntity")
public class Address extends BaseEntity {

}
````


### 15、 @TableId
``
注解@TableId 设定主键ID，value为定义列名
``

````java
//设定表名
@TableId
private Long id;
````

### 16、 @VirtualTableId
``
注解@VirtualTableId 虚拟表ID，通过代码生成的ID。设定虚拟ID可以使用虚拟ID操作表数据。等同于ID使用。
``

````java
//DEMO 设定userId为虚拟ID
@VirtualTableId
private Long userId;
````


