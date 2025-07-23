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
|1.0.2|更新包名|
|1.1.0|去掉invoke操作。使用多态模式实现逻辑|
|1.1.1|更新util包，使用标准util|
|1.1.2|更新util包，使用标准util|


## 仓库地址
|平台|地址|
|---|---|
|github|https://github.com/yilijishu/yili-mybatis-plus|
|gitee|https://gitee.com/yilijishu/yili-mybatis-plus|

## 名词说明

|名词|说明|
|---|---|
|id|主键id，唯一，由数据库自增生成|
|virtualid虚拟ID|虚拟id，唯一，使用@VirtualId标注，由系统生成管理。在修改、删除中 均可作为条件操作|
|逻辑删除|逻辑删除，是使用@DelTag标记的字段，字段只有是否两种选项，是标识为删除，否标识为未删除|

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
### 17、 @DelTag
``
注解@DelTag 设定删除表示，主要用于逻辑删除。
``

```java
//DEMO 设定删除标记
@DelTag("true")
private Boolean delTag;
```


## 五、 统一接口标准
``
以下是统一标准的使用说明。专为标准化输出使用
``
### 1、 标准接口描述
``
如果使用标准接口 请继承StandardWeb类。基础路径为/{packageName}/{entityName}/{operate}
子类设定好基础路径。/{sub-class-project} 最终路径为：/{sub-class-project}/{packageName}/{entityName}/{operate}
接口根据operate不同，传递的参数也不相同。
``
### 2、 接口URL
```
POST URL: /{sub-class-project}/{packageName}/{entityName}/{operate}
```
``
以下路径说明 按照实体类com.yilijishu.UserMember举例说明
``

|路径参数|说明|
|---|---|
|sub-class-project|继承StandardWeb的子类指定的路径|
|packageName|操作哪个entity 请把包路径用-替换，packageName传递com-yilijishu|
|entityName|实体类名称，驼峰结构使用-转换 ， entityName传递user-member|
|operate|操作，主要有以下几种： save：保存， d-save：批量插入，unn：指定修改 ，u：全修改 ，d：删除，dd-id：根据ID批量删除，dd-vid：根据虚拟ID批量删除， ddv-id：根据ID批量逻辑删除，ddv-vid：根据虚拟ID批量逻辑删除， q：查询 ， g：获取单条数据 ， qp：分页查询 |

### 3、 参数说明

``
以下为常态传递的参数
``

|operate值|传递方式|类型|说明|参数说明|
|---|---|---|---|---|
|packageName|path|string|解析路径所得|格式为Entity的包名，转换成格式所示：com.yilijishu --> com-yilijishu|
|entityName|path|string|解析路径所得|格为Entity的类名，转换成格式所示：UserMember --> user-member|
|operate|path|string|操作：增删改查|参数说明在下方详细说明|
|start|param|int|页数|分页，开始页数 例如第一页传递 1， 只有operate为分页查询时有效|
|size|param|int|每页数|分页，每页数量， 例如每页显示20条，传递 20，只有operate为分页查询时有效|
|body|request body|string|消息体|根据operate不同传递不同。在下方详细说明|

``
不同的operate，body参数传递的也不相同，以下是body说明
``

|operate值|说明|body消息体传递|
|---|---|---|
|save|插入数据|json.toJsonString(entity), 例如: "{\"age\":10,\"name\":\"上帝\"}"|
|d-save|批量插入|json.toJsonString(list<entity>), 例如: "[{\"age\":10,\"name\":\"上帝\"}]"|
|unn|指定修改,会根据是否为空进行修改，不为空则修改|json.toJsonString(entity), 例如: "{\"id\":1, \"age\":10,\"name\":\"上帝\"}"|
|u|全修改，无论是否为空，均修改|json.toJsonString(entity),例如: "{\"id\":1, \"age\":10,\"name\":\"上帝\"}"|
|d|删除，可以传递条件或者id、虚拟ID|json.toJsonString(entity),例如: "{\"id\":1}"|
|dd-id|根据ID批量删除|json.toJsonString(list), 例如："[1,2,3]"|
|dd-vid|根据虚拟ID批量删除|json.toJsonString(list), 例如："[1,2,3]"|
|ddv-id|根据ID批量逻辑删除|json.toJsonString(list), 例如："[1,2,3]"|
|ddv-vid|根据虚拟ID批量逻辑删除|json.toJsonString(list), 例如："[1,2,3]"|
|q|查询，给定条件查询，不传递则查询全部|json.toJsonString(entity), 例如: "{\"age\":10,\"name\":\"上帝\"}"|
|g|获取单条数据，给定条件获取,当出现多条数据时，返回第一条|json.toJsonString(entity), 例如: "{\"age\":10,\"name\":\"上帝\"}"|
|qp|分页查询，给定条件查询|json.toJsonString(entity), 例如: "{\"age\":10,\"name\":\"上帝\"}",同时传递start，和size 参数|