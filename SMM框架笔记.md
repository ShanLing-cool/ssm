# Spring



## 1 Spring 配置文件



### 1.1 Bean标签范围配置



> ### 对象创建区分



当scope创建为 **singleton** 时，对象只会被创建 **一次** ，并且是在第一步 **读取配置文件** 时就已经创建了。

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161734077.png" alt="image-20210816173424995" style="zoom:80%;" />

打印对象地址：--》相同

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161742981.png" alt="image-20210816174238953" style="zoom:80%;" />



======================================================================



换成scope="**prototype**"时，第一步读取配置文件时，并没有创建，而是在 **getBean** 时创建，且每次getBean就创建一个对象。



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161735144.png" alt="image-20210816173530069" style="zoom:50%;" />



得到第一个bean时：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161736161.png" alt="image-20210816173611089" style="zoom:50%;" />



得到第二个bean时:

![image-20210816173637877](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161736958.png)



打印对象地址：--》**不同**

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161741290.png" alt="image-20210816174118264" style="zoom: 80%;" />



> ### 总结

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161748497.png" alt="image-20210816174847419" style="zoom: 67%;" />





### 1.2 Bean生命周期配置



> ### Bean生命周期配置



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161927390.png" alt="image-20210816192746348" style="zoom: 67%;" />



在UserDaoImpl类中编写`init()`和`destory()`方法：

```java
public class UserDaoImpl implements UserDao {

    public UserDaoImpl(){
        System.out.println("创建...");
    }

    public void init(){
        System.out.println("初始化...");
    }

    public void destory(){
        System.out.println("销毁...");
    }

    public void save() {
        System.out.println("save running...");
    }
}
```



在xml文件中配置方法：

```xml
<bean id="userDao" class="com.zovz.dao.impl.UserDaoImpl" scope="singleton"
      init-method="init" destroy-method="destory"></bean>
```



打印输出：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161933138.png" alt="image-20210816193341080" style="zoom:67%;" />



因为``ApplicationContext`接口没有`close`方法，程序会自动关闭，`destory`方法也就不会执行

想要执行``destory`方法，则需要`ClassPathXmlApplicationContext`类定义app即可，即

```java
ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("applicationContext.xml");
```



打印输出:

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161941558.png" alt="image-20210816194115498" style="zoom: 67%;" />





### 1.3 Bean实例化三种方式



> ### 无参**构造**方法实例化



```java
public class UserDaoDemo {
    public static void main(String[] args) {
        ApplicationContext app = new   ClassPathXmlApplicationContext("applicationContext.xml");
        UserDao userDao = (UserDao) app.getBean("userDao");
        userDao.save();
    }
}
```



xml配置如下：

```xml
<bean id="userDao" class="com.zovz.dao.impl.UserDaoImpl"></bean>
```



打印输出：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108162017122.png" alt="image-20210816201723077" style="zoom: 67%;" />





> ### 工厂**静态**方法实例化



新建一个类，代码如下：

```java
public class StaticFactory {
    public static UserDao getUserDao() {
        return new UserDaoImpl();
    }
}
```



因为是静态类，所以可以直接使用类名来进行访问，xml文件如下：

```xml
<bean id="userDao" class="com.zovz.factory.StaticFactory" factory-method="getUserDao"></bean>
```



返回：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108161958245.png" alt="image-20210816195824210" style="zoom:67%;" />





> ### 工厂**实例**方法实例化



new一个DynamicFactory类，返回对象

```java
public class DynamicFactory {
    public UserDao getUserDao() {
        return new UserDaoImpl();
    }
}
```



因为DynamicFactory不再是static，需要有该类的对象才能访问`getUserDao`方法

第一行先是创建了一个DynamicFactory对象，第二行再是调用第一行的对象，在调用``getUserDao`方法

```xml
<bean id="factory" class="com.zovz.factory.DynamicFactory"></bean>
<bean id="userDao" factory-bean="factory" factory-method="getUserDao"></bean>
```





返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108162006209.png" alt="image-20210816200628176" style="zoom: 67%;" />





### 1.4 Bean的依赖注入分析



> ### 概念



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108162106710.png" alt="image-20210816210648629" style="zoom:67%;" />





> ### 方式



怎么将UserDao怎样注入到UserService内部呢？

答：

1. **构造方法**
2. **set方法**





> ### set方法



首先在UserServiceImpl类中定义UserDao的set构造方法，将容器中UserDao的对象传入UserService的对象中，

实现依赖注入

```java
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void save() {
        userDao.save();
    }
}
```



xml配置文件如下：

```xml
<bean id="userDao" class="com.zovz.dao.impl.UserDaoImpl"></bean>
<bean id="userService" class="com.zovz.service.impl.UserServiceImpl">
    <property name="userDao" ref="userDao"></property>
</bean>
```



其中name是指UserServiceImpl类中的UserDao的set方法，需要去掉set并且将首字母换成小写。

对象的传递需要用到ref，连接到userDao。



注意：



使用如下方法new对象时，调用方法会抛出空指针异常，因为new出来的对象是放在内存中的，而不是在spring容器中，所以并没有在UserServiceImpl类中通过set方法赋值给`save()`方法



![image-20210816212719666](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108162127747.png)





还可以使用p命名空间注入：



```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userDao" class="com.zovz.dao.impl.UserDaoImpl"></bean>
    <bean id="userService" class="com.zovz.service.impl.UserServiceImpl" p:userDao-ref="userDao"/>
</beans>
```





> ### **构造方法**



在UserServiceImpl类中定义构造方法

```java
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    public UserServiceImpl() {
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public void save() {
        userDao.save();
    }
}
```



xml配置如下：

```xml
<bean id="userDao" class="com.zovz.dao.impl.UserDaoImpl"></bean>
<bean id="userService" class="com.zovz.service.impl.UserServiceImpl">
    <constructor-arg name="userDao" ref="userDao"></constructor-arg>
</bean>
```



注意：name中的参数是指`public UserServiceImpl(UserDao userDao)`中的userDao





### 1.5 Bean的依赖注入的数据类型



![image-20210816220750664](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108162207722.png)



> ### 引用数据类型：



xml配置文件如下：

```xml
<bean id="userDao" class="com.zovz.dao.impl.UserDaoImpl">
    <property name="age" value="18"/>
    <property name="name" value="zovz"/>
</bean>
<bean id="userService" class="com.zovz.service.impl.UserServiceImpl">
        <constructor-arg name="userDao" ref="userDao"></constructor-arg>
</bean>
```



在UserDaoImpl中定义name，age属性和get，set方法

```java
public class UserDaoImpl implements UserDao {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public UserDaoImpl() {
        System.out.println("创建...");
    }

    public void save() {
        System.out.println(age + "====" + name);
        System.out.println("running...");
    }
}
```



打印结果：

![image-20210817201531660](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108172015709.png)





> ### 集合数据类型



首先创建三种类型的集合：

```java
//创建集合
private List<String> stringList;
private Map<String, User> userMap;
private Properties properties;
```



xml配置如下：

```xml
<bean id="userDao" class="com.zovz.dao.impl.UserDaoImpl">
    
    <property name="stringList">
        <list>
            <value>aaa</value>
            <value>bbb</value>
            <value>ccc</value>
        </list>
    </property>
    
    <!--因为userMap是<String, User>类型的，所以需要传入User类型的对象，需要引用-->
    <property name="userMap">
        <map>
            <!--key值自定义-->
            <!--需要引用时写ref-->
            <entry key="u1" value-ref="user1"></entry>
            <entry key="u2" value-ref="user2"></entry>
        </map>
    </property>
    
    <property name="properties">
        <props>
            <!--key值自定义-->
            <!--里面写值 如：pp1-->
            <prop key="p1">pp1</prop>
            <prop key="p2">pp2</prop>
            <prop key="p3">pp3</prop>
        </props>
    </property>
</bean>

<bean id="user1" class="com.zovz.domain.User">
        <property name="name" value="tom"></property>
        <property name="addr" value="beijing"></property>
</bean>
<bean id="user2" class="com.zovz.domain.User">
        <property name="name" value="jack"></property>
        <property name="addr" value="shanghai"></property>
</bean>
```



User类：

```java
package com.zovz.domain;

/**
 * Created by Intellij IDEA.
 * Date:  2021/8/16
 */
public class User {
    private String name;
    private String addr;

    public void setName(String name) {
        this.name = name;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                '}';
    }
}
```



打印结果：

![image-20210817201909451](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108172019512.png)





### 1.6 引入其他配置文件（分模块开发）



![image-20210817203103855](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108172031921.png)



在主配置文件里的`<bean></bean>`标签下导入`<import resource="applicationContextxxxxxx.xml"></import>`，在主配置文件加载时，其他引入的配置文件也会被加载





### 1.7 知识要点



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108172038074.png" alt="image-20210817203845983" style="zoom:67%;" />



注意：如果要使用**构造方法**进行有参构造注入的话，`<constructor-arg>`与`<property>`标签的属性和字标签一致，即



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108172043538.png" alt="image-20210817204305453" style="zoom: 67%;" />





## 2. Spring相关API



### 2.1 ApplicationContext的继承体系



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108172048588.png" alt="image-20210817204835497" style="zoom:80%;" />



紫色的代表接口，浅绿代表抽象类，绿色的是实现类，我们用的是`ClassPathXmlApplicationContext`





### 2.2 ApplicationContext的实现类

![image-20210817205624517](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108172056600.png)

```java
//第一个类
ApplicationContext app = new ClassPathXmlApplicationContext("applicationContext.xml");

//第二个类（绝对路径）
 ApplicationContext app = new FileSystemXmlApplicationContext("C:\\Users\\pc\\Desktop\\成神之路\\SSM\\Spring\\Demo01\\src\\main\\resources\\applicationContext.xml");

//第三个类
//使用注解开发，以后会讲
```





### 2.3 getBean()方法使用



两种方式：一种是通过id来获取，一种是通过字节码对象类型来获取

![image-20210817210415763](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108172104845.png)

第一种：（可以使用**多个**相同类型的对象，只要id不同即，但是**需要**类型转换：）



```java
UserService userService = (UserService) app.getBean("userService");
```



第二种：（**不可以**使用多个相同类型的对象，只能有一个，但是**不需要**类型转换）

```java
UserService userService =  app.getBean(UserServiceImpl.class);
```



 如果某一个类型的对象有多个的时候，用id；有一个的时候，可以使用.class





## 3. Spring配置数据源



### 3.1 数据源（连接池）的作用



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108172115567.png" alt="image-20210817211518502" style="zoom:67%;" />





### 3.2 数据源开发的步骤



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108172118392.png" alt="image-20210817211826334" style="zoom: 80%;" />





### 3.3 数据源的手动创建



首先在maven的pom.xml文件中添加配置

```xml
<dependencies>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.47</version>
    </dependency>
    <dependency>
        <groupId>c3p0</groupId>
        <artifactId>c3p0</artifactId>
        <version>0.9.1.2</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>1.1.10</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.11</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```



连接代码：

```java
package com.zovz.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by Intellij IDEA.
 * Date:  2021/8/17
 */
public class DataSourceTest {
    @Test
    //测试手动创建 c3p0 数据源
    public void test1() throws PropertyVetoException, SQLException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();

        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306?school");
        dataSource.setUser("root");
        dataSource.setPassword("123456");

        //获取资源
        Connection connection = dataSource.getConnection();

        //返回com.mchange.v2.c3p0.impl.NewProxyConnection@6debcae2
        System.out.println(connection);
        //表面上是关闭的，实际上归还资源到数据源
        connection.close();
    }

    @Test
    //测试手动创建 druid 数据源
    public void test2() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306?school");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

        DruidPooledConnection connection = dataSource.getConnection();
        //返回com.mysql.jdbc.JDBC4Connection@5fe5c6f
        System.out.println(connection);
        connection.close();
    }

    @Test
    //测试手动创建 c3p0 数据源(加载配置文件形式)
    public void test03() throws PropertyVetoException, SQLException {
        //读取配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String driver = bundle.getString("jdbc.driver");
        String url = bundle.getString("jdbc.url");
        String username = bundle.getString("jdbc.username");
        String password = bundle.getString("jdbc.password");

        //创建数据源对象，设置连接参数
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driver);
        dataSource.setJdbcUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        Connection connection = dataSource.getConnection();
        //com.mchange.v2.c3p0.impl.NewProxyConnection@5ba23b66
        System.out.println(connection);
        connection.close();
    }
}
```



jdbc.properties 配置如下

```properties
jdbc.driver=com.mysql.jdbc.Driver;
jdbc.url=jdbc:mysql://localhost:3306?school
jdbc.username=root
jdbc.password=123456
```





### 3.4 Spring配置数据源



新建一个`applicationContext.xml`文件，配置如下：

```xml
<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
    <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
    <property name="jdbcUrl" value="jdbc:mysql://localhost:3306?school"></property>
    <property name="user" value="root"></property>
    <property name="password" value="123456"></property>
</bean>
```



测试类如下：

```java
@Test
//测试Spring容器产生数据源对象
public void test04() throws SQLException {
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

    ComboPooledDataSource dataSource = (ComboPooledDataSource) context.getBean("dataSource");
    //DataSource dataSource1 = context.getBean(DataSource.class);

    Connection connection = dataSource.getConnection();

    //返回com.mchange.v2.c3p0.impl.NewProxyConnection@69b794e2
    System.out.println(connection);
    connection.close();
}
```



现在在xml文件配置好了，并且能成功打印输出connection对象，但是如果想进一步解耦，在xml中引入properties的话，则需要下面这种xml配置方式。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!--加载外部properties-->
    <context:property-placeholder location="classpath:jdbc.properties"/>

    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.driver}"></property>
        <property name="jdbcUrl" value="${jdbc.url}"></property>
        <property name="user" value="${jdbc.username}"></property>
        <property name="password" value="${jdbc.password}"></property>
    </bean>

</beans>
```



我的文件结构：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108181609231.png" alt="image-20210818160905057" style="zoom:67%;" />



这样就可以在xml中引入外部properties了，并且成功打印输出connection对象





## 4. Spring注解开发



### 4.1 Spring原始注解



Spring是轻代码而重配置的框架,配置比较繁重,影响开发效率,所以注解开发是—种趋势,注解代替κm巸置
文件可以简化配置,提高开发效率。



Spring原始注解主要是替代`<Bean>`的配置

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108181615475.png" alt="image-20210818161507330" style="zoom:67%;" />





> ### 实操



UserDaoImpl类：

```java
//<bean id="userDao" class="com.zovz.dao.impl.UserDaoImpl"></bean>
@Component("userDao")
public class UserDaoImpl implements UserDao {
    public void save() {
        System.out.println("save running...");
    }
}
```



UserServiceImpl类：

```java
//<bean id="userService" class="com.zovz.service.Impl.UserServiceImpl"></bean>
@Component("userService")
public class UserServiceImpl implements UserService {

    //<property name="userDao" ref="userDao"></property>
    @Autowired//按照数据类型从spring容器中进行匹配的
    //此处也可以省略 @Qualifier 注解
    //@Qualifier是按照id值来配对的，即如果按照数据类型类型写一个注解，按照名称写两个注解
    @Qualifier("userDao")

    @Resource(name="userDao")//相当于@Autowired加上@Qualifier
    private UserDao userDao;
    
    //此处如果使用注解开发，可省略set方法
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void save() {
        userDao.save();
    }
}
```



xml配置文件如下：

```xml
<!--配置组件扫描-->
<context:component-scan base-package="com.zovz"/>
```



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108181716617.png" alt="image-20210818171620532" style="zoom: 67%;" />

**意思就是Spring会扫描`base-package`所设置的包下的所有类，字段，方法和子包**





注意：在xml文件配置时，要写set方法；而使用注解开发时可以省略，因为注解可以通过**反射**来获取对象并赋值





还可以使用@Value注解直接赋值，可以在里面使用spl表达式进行properties文件的取值

@Scope("singleton")-->创建一个对象

 @PostConstruct-->初始化方法

@PreDestroy-->销毁方法

```java
//<bean id="userService" class="com.zovz.service.Impl.UserServiceImpl"></bean>
@Service("userService")
@Scope("singleton")
public class UserServiceImpl implements UserService {

    @Value("${jdbc.driver}")
    private String driver;

    //<property name="userDao" ref="userDao"></property>
    @Autowired
    @Qualifier("userDao")

    @Resource(name = "userDao")
    private UserDao userDao;

    @PostConstruct
    //初始化方法
    public void init(){
        System.out.println("初始化...");
    }

    @PreDestroy
    //销毁方法
    public void destory(){
        System.out.println("销毁...");
    }

    public void save() {
        System.out.println(driver);
        userDao.save();
    }
}
```





### 4.2 Spring新注解



> ### 原因



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108181910304.png" alt="image-20210818191010217" style="zoom:67%;" />



> ### 简单说明



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108181916448.png" alt="image-20210818191606341" style="zoom: 60%;" />





> ### 实操



新建一个资源类`DataSourceConfiguration`，

```java
//<context:property-placeholder location="classpath:jdbc.properties"/>
@PropertySource("classpath:jdbc.properties")
public class DataSourceConfiguration {
    @Value("${jdbc.driver}")
    private String driver;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.username}")
    private String username;

    //Spring会将当前方法的返回值以指定名称即dataSource存储到spring容器中
    @Bean("dataSource")
    public DataSource getDataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setDriverClass(driver);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
```



引入资源类，这时候xml文件就不需要了

```java
//标志该类是Spring的核心配置类
@Configuration

//<context:component-scan base-package="com.zovz"/>
@ComponentScan("com.zovz")
//<import resource="applicationContext.xml"></import>
@Import({DataSourceConfiguration.class})
public class SpringConfiguration {

}
```



改一下测试类

```java
public class UserController {
    public static void main(String[] args) {
        //使用注解获得应用上下文对象
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        UserService userService = context.getBean(UserService.class);
        userService.save();
    }
}
```





### 4.3 原始Juint测试Spring的问题



> ### 问题



![image-20210818200816390](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108182008474.png)



上述问题解决思路：

1. 让 SpringJunit负责创建spring容器,但是需要将配置文件的名称告诉它
2. 将需要进行测试Bean直接在测试类中进行注入





> ### 步骤



1. 导入 spring集成 Junit的坐标
2. 使用 @Runwith注解替换原来的运行期
3. 使用 @Contextconfiguration指定配置文件或配置类
4. 使用 @Autowired注入需要测试的对象
5. 创建测试方法进行测试



```java
//用Spring的内核来帮我们调用Junit进行测试
@RunWith(SpringJUnit4ClassRunner.class)

//使用xml文件开发
//@ContextConfiguration("classpath:applicationContext.xml")

//使用全注解开发
@ContextConfiguration(classes = {SpringConfiguration.class})

public class SpringJunitTest {

    @Autowired
    private UserService userService;

    @Autowired
    private DataSource dataSource;

    @Test
    public void test() throws SQLException {
        userService.save();
        System.out.println(dataSource.getConnection());
    }
}
```





## 5. Spring集成web环境



### 5.1 ApplicationContext应用上下文获取方式



![image-20210818212245857](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108182122970.png)





> ### 实操



web.xml配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         id="WebApp_ID" version="3.0">


    <!--全局初始化参数-->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>applicationContext.xml</param-value>
    </context-param>

    <!--配置监听器-->
    <listener>
        <listener-class>com.zovz.listener.ContextLoaderListener</listener-class>
    </listener>


    <servlet>
        <servlet-name>UserServlet</servlet-name>
        <servlet-class>com.zovz.web.UserServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>UserServlet</servlet-name>
        <url-pattern>/userServlet</url-pattern>
    </servlet-mapping>
</web-app>
```



UserServlet类：

```java
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = req.getServletContext();
        // ApplicationContext app = (ApplicationContext) servletContext.getAttribute("app");
        ApplicationContext app = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        UserService userService = app.getBean(UserService.class);
        userService.save();

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
```



ContextLoaderListener服务器加载监听器：

```java
public class ContextLoaderListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {

        ServletContext servletContext = sce.getServletContext();

        //读取web.xml的全局参数
        String contextConfigLocation = servletContext.getInitParameter("contextConfigLocation");

        ApplicationContext app = new ClassPathXmlApplicationContext(contextConfigLocation);

        //将spring的应用上下文对象存储到ServletContext域中
        servletContext.setAttribute("app", app);
        System.out.println("Spring容器创建完毕");
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }
}
```



WebApplicationContextUtils工具类：

```java
public class WebApplicationContextUtils {

    public static ApplicationContext getWebApplicationContext(ServletContext servletContext) {
        return (ApplicationContext) servletContext.getAttribute("app");
    }
}
```



applicationContext.xml配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userDao" class="com.zovz.dao.impl.UserDaoImpl"></bean>

    <bean id="userService" class="com.zovz.service.impl.UserServiceImpl">
        <property name="userDao" ref="userDao"></property>
    </bean>
</beans>
```





### 5.2 Spring提供获取应用上下文的工具



![image-20210818221346546](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108182213658.png)





Spring集成web环境步骤：



1. 配置ContextLoaderListener监听器
2. 使用WebApplicationContextUtils获得应用上下文





<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108182224460.png" alt="image-20210818222405356" style="zoom: 60%;" />



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108182224942.png" alt="image-20210818222426662" style="zoom:60%;" />



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108182224418.png" alt="image-20210818222453306" style="zoom:60%;" />





# SpringMVC



## 1. SpringMVC简介



### 1.1 SpringMVC概述



![image-20210820155420436](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108201554536.png)





### 1.2 SpringMVC的开发步骤



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108201554915.png" alt="image-20210820155441781" style="zoom: 80%;" />





### 1.3 SpringMVC快速入门



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108201555529.png" alt="image-20210820155546446" style="zoom:67%;" />



1.导入SpringMVC相关坐标

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.0.5.RELEASE</version>
</dependency>
```



2.配置SpringMVC核心控制器DispathcerServlet

```xml
<!--配置springmvc的前端控制器-->
<servlet>
    <servlet-name>DispatcherServlet</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:spring-mvc.xml</param-value>
    </init-param>
<!--服务器启动时就加载这个servlet，不配的话就是默认第一次访问时创建对象-->
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>DispatcherServlet</servlet-name>
<!--缺省标识符，表示任何路径都会走这个servlet-->  
    <url-pattern>/</url-pattern>
</servlet-mapping>
```



3.创建controller类和视图页面并且使用注解配置Controller中业务方法的映射地址



controller类：

```java
@Controller
public class UserController {

    @RequestMapping("/quick")
    public String save() {
        System.out.println("controllersave running");
        return "success.jsp";
    }
}
```



视图：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>SUCCESS!</h1>
</body>
</html>
```



4.配置Spring-mvc.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context  http://www.springframework.org/schema/context/spring-context.xsd">

<!--controller组件扫描-->
    <context:component-scan base-package="com.zovz.controller"/>
</beans>
```



SpringMVC的流程图示：

![image-20210820163120171](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108201631313.png)







## 2 SpringMVC组件解析



### 2.1 SpringMVC执行流程

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108201657751.png" alt="image-20210820165706605" style="zoom: 60%;" />



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108201702110.png" alt="image-20210820170257980" style="zoom:67%;" />

 



### 2.2 SpringMVC注解解析



![image-20210820171731808](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108201717941.png)



示例代码：

```java
@Controller
@RequestMapping("/user")
public class UserController {

    //method报错：Request method 'GET' not supported
    //params报错：Parameter conditions "username" not met for actual request parameters:
    //参数报错解决方法：http://localhost:8080/test02_war_exploded/user/quick?username=1
    @RequestMapping(value = "/quick", method = RequestMethod.GET, params = {"username"})
    public String save() {
        System.out.println("controllersave running");
        return "/success.jsp";
    }
}
```





### 2.3 SpringMVC组件扫描补充



![image-20210820172455933](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108201724088.png)



这是第一种引入方式：

```xml
<!--controller组件扫描-->
<context:component-scan base-package="com.zovz.controller"/>
```



另外一种引入方式：

包括Controller注解的类，使用`include`

```xml
<!--controller组件扫描-->
<context:component-scan base-package="com.zovz.controller">
    <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
</context:component-scan>
```

不包括Controller注解的类，使用`exclude`

```xml
<context:component-scan base-package="com.zovz.controller">
    <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
</context:component-scan>
```





### 2.4 SpringMVC的XML配置解析



> ### 修改转发和重定向方式



打开`ViewResolver`视图解析器，找到`InternalResourceViewResolver`类，发现其继承`UrlBasedViewResolver`类，有如下常量

```java
public static final String REDIRECT_URL_PREFIX = "redirect:";
public static final String FORWARD_URL_PREFIX = "forward:";
```

第一个代表是重定向，第二个代表是转发，即我们写的`"/success.jsp"`其实都是` forward:/success.jsp`

```java
public String save() {
    System.out.println("controllersave running");
    return "/success.jsp";
}
```



测试：

默认` "/success.jsp"`时，页面地址没有发生变化，所以是视图解析器默认是转发

改为`"redirect:/success.jsp"时`，页面地址发生变化，所以方式可以改变

```xml
http://localhost:8080/test02_war_exploded/success.jsp
```





> ### 修改地址前缀和后缀



spring-mvc.xml配置如下：

```xml
<!--配置内部资源视图解析器-->
<bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <!--set方法，前缀-->
<!--就等于/jsp/success.jsp-->
    <property name="prefix" value="/jsp/"></property>
    <!--后缀-->
    <property name="suffix" value=".jsp"></property>
</bean>
```



只需要写success即可

```java
public String save() {
    System.out.println("controllersave running");
    return "success";
}
```



### 2.5 知识要点



> ### SpringMVC的相关组件

- 前端控制器：DispatcherServlet
- 处理器映射器：HandlerMapping
- 处理器适配器：HandlerAdapter
- 处理器：Handler
- 视图解析器：View Resolver
- 视图：View



> ### SpringMVC的注释和配置

- 请求映射注解：@RequestMapping
- 视图解析器配置：

*REDIRECT_URL_PREFIX = "redirect:";*

*FORWARD_URL_PREFIX = "forward:";*

*prefix="";*

*suffix="";*





## 3.SpringMVC数据响应



### 3.1 数据响应方式



1. **页面跳转**

- 直接返回字符串
- 通过ModelAndView对象返回



2. **回写数据**

- 直接返回字符串
- 返回对象或集合





### 3.2 页面跳转-返回字符串形式



直接返回字符串：此种方式会将返回的字符串与视图解析器的前后缀拼接后跳转

![image-20210828204422697](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108282044298.png)





### 3.3 页面跳转-返回ModelAndView形式



模型与视图可以分开单独设置，这里给出例子：

```java
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "/quick2")
    public ModelAndView save2() {
        /*
        * Model:模型 作用封装数据
        * View:视图 作用展示数据
        * */
        ModelAndView modelAndView = new ModelAndView();
        //设置模型数据-->相当于放进域中，如同request.setAttribute(K,V)
        modelAndView.addObject("username", "itcast");
        ModelAndView modelAndView = new ModelAndView();
        //设置视图名称
        modelAndView.setViewName("success");
        return modelAndView;
    }
```

 `ModelAndView`在域中添加键值，然后在JSP前端页面中获取，`<h1>SUCCESS!${username}</h1>`,成功返回

`ModelAndView`方法返回success，然后在`spring-mvc.xml`文件中配置前缀后缀，拼接成地址，成功返回



这里还有另一种注入方式：

```java
@RequestMapping(value = "/quick3")
public ModelAndView save2(ModelAndView modelAndView) {
    //设置模型数据-->相当于放进域中，如同request.setAttribute(K,V)
    modelAndView.addObject("username", "zovz");
    //设置视图名称
    modelAndView.setViewName("success");
    return modelAndView;
}
```

当SpringMVC执行这个方法时，发现参数`ModelAndView`没有，需要MVC提供，SpringMVC框架就会提供这个参数，可以直接用。



还有第三种方式：

```java
@RequestMapping(value = "/quick4")
public String save4(Model model) {
    //这里传入model，设置模型数据
    model.addAttribute("username", "zissz");
    //这里直接返回视图
    return "success";
}
```



以上都是SpringMVC帮我们封装好的对象，如果我们想用原始的方式设置参数呢，即使用request

看如下代码：

```java
@RequestMapping(value = "/quick5")
public String save5(HttpServletRequest request) {
    request.setAttribute("username", "zhang");
    return "success";
}
```

这样页面也能获取到设置的参数，作用与model差不多，只是不常用。





### 3.4 回写数据-返回字符串形式



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108282137315.png" alt="image-20210828213731159"  />



第一种方式：通过传入response参数打印输出

```java
@RequestMapping(value = "/quick6")
public void save6(HttpServletResponse response) throws IOException {
    response.getWriter().println("hello zovz");
}
```



第二种方式：通过设置注解，直接返回打印输出

```java
@ResponseBody //告知SpringMVC框架 不进行视图跳转 直接进行数据响应
@RequestMapping(value = "/quick7")
public String save7() throws IOException {
    return "hello zovz";
}
```





### 3.5 回写数据-返回JSON格式字符串



直接手写返回json字符串：

```java
@ResponseBody //告知SpringMVC框架 不进行视图跳转 直接进行数据响应
@RequestMapping(value = "/quick8")
public String save8() throws IOException {
    return "{\"username\":\"zovz\",\"age\":18}";
}
```

这种方式过于麻烦，第二种方式如下，使用json的转换工具将对象转换成json格式字符串再返回



首先导入maven依赖：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.9.0</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.0</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.9.0</version>
</dependency>
```



将对象转化为json格式字符串返回：

```java
@ResponseBody //告知SpringMVC框架 不进行视图跳转 直接进行数据响应
@RequestMapping(value = "/quick9")
public String save9() throws IOException {
    User user = new User();
    user.setAge(18);
    user.setName("zovz");
    //使用json的转换工具将对象转换成json格式字符串在返回
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(user);
    return json;
}
```





### 3.6 回写数据-返回对象或集合



现在已经可以成功将对象转化为json字符串了，但是每进行一次转化，就要重复编写上面的两行代码，很麻烦，我想返回一个对象，让SpringMVC自动帮我转化为json，而下面的操作就可以实现：



在`DispatcherServlet.properties`中找到相应的适配器：

```properties
org.springframework.web.servlet.HandlerAdapter=org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter,\
   org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter,\
   org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
```



进入`RequestMappingHandlerAdapter`类，找到`setMessageConverters`方法，即消息转换器：

```java
public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
    this.messageConverters = messageConverters;
}
```



找到set方法后，我们只需要在配置文件中写入相应的配置即可：

```xml
<!--配置处理器映射器-->
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
    <property name="messageConverters">
        <list>
            <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter"/>
        </list>
    </property>
</bean>
```



测试代码：

```java
@ResponseBody //告知SpringMVC框架 不进行视图跳转 直接进行数据响应
@RequestMapping(value = "/quick10")
//期望SpringMVC将返回的user自动转化为json格式字符串
public User save10() throws IOException {
    User user = new User();
    user.setAge(18);
    user.setName("zovz");
    return user;
}
```



最后成功返回json字符串，总结如下：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108291608918.png" alt="image-20210829160822526" style="zoom:80%;" />



但是上面的xml方式配置太过繁琐，下面介绍注解方法进行转换：

![image-20210829162649656](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108291626804.png)



首先导入MVC的命名空间：

```xml
<beans xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">
```



在下面编写注解：

```xml
<!--MVC的注解驱动-->
<mvc:annotation-driven/>
```



这样就完成了，页面也成功显示json字符串，当然别忘了在测试方法上加上注解`@ResponseBody`





## 4. SpringMVC的请求



客户端请求参数的格式是：==name=value&name=value......==

服务器端要获得请求的参数，有时候还需要进行数据的封装，SpringMVC可以接受如下类型的参数：

- 基本类型参数
- POJO类型参数
- 数据类型参数
- 集合类型参数





### 4.1 获得基本类型参数



![image-20210829163609143](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108291636262.png)



在浏览器中输入url：`http://localhost:8080/test02_war_exploded/user/quick11?username=zhangsan&age=10`

页面跳转后，控制台打印出结果：

![image-20210829164000254](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108291640328.png)



测试代码如下：

```java
@ResponseBody
@RequestMapping(value = "/quick11")
public void save11(String username, int age) throws IOException {
    System.out.println(username);
    System.out.println(age);
}
```

因为现在不需要向前端传数据，所以方法返回类型设置为void。





### 4.2 获得POJO类型参数



![image-20210829164510412](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108291645548.png)



在浏览器中输入url：`http://localhost:8080/test02_war_exploded/user/quick12?name=zhangsan&age=10`

页面跳转后，控制台打印出结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108291647741.png" alt="image-20210829164738659" style="zoom: 80%;" />



测试代码如下：

```java
    @ResponseBody
    @RequestMapping(value = "/quick12")
    public void save12(User user) throws IOException {
        System.out.println(user);
    }
```





### 4.3 获得数组类型参数



![image-20210829164901325](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108291649477.png)



在浏览器中输入url：`http://localhost:8080/test02_war_exploded/user/quick13?str=aaa&str=bbb&str=ccc`

页面跳转后，控制台打印出结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108291651941.png" alt="image-20210829165103153" style="zoom:80%;" />



测试代码如下：

```java
    @ResponseBody
    @RequestMapping(value = "/quick13")
    public void save13(String[] str) throws IOException {
        for (int i = 0; i < str.length; i++) {
            System.out.println(str[i]);
        }
        //也可以使用Arrays工具类打印
        //System.out.println(Arrays.asList(str));
    }
```





### 4.4 获得集合类型参数



我们在方法上是可以直接封装一个集合的，但是他需要特殊的场景，一般来说表单提交的集合，我们都把它封装到一个VO对象中。



JSP前端页面：

```jsp
<form action="${pageContext.request.contextPath}/user/quick14" method="post">
    <%--表明是第几个User对象的name和age--%>
    <input type="text" name="userList[0].name">
    <input type="text" name="userList[0].age">
    <input type="text" name="userList[1].name">
    <input type="text" name="userList[1].age">
    <input type="submit">
</form>
```



测试代码：

```java
@ResponseBody
@RequestMapping(value = "/quick14")
public void save14(VO vo) throws IOException {
    System.out.println(vo);
}
```



VO类（封装集合）：

```java
public class VO {
    private List<User> userList;

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public String toString() {
        return "VO{" +
                "userList=" + userList +
                '}';
    }
}
```



返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108291708267.png" alt="image-20210829170819184" style="zoom:80%;" />





下面的场景我们就可以把集合写在方法形参的位置：



当使用ajax提交时，可以指定`contentType`为json形式，那么在方法参数位置使用`@RequestBody`可以直接接收集合数据而无需使用POJO进行包装。



JSP前端页面：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script src="${pageContext.request.contextPath}/js/jquery-3.5.1.js"></script>
    <script>
        var userList = new Array();
        userList.push({username: "zhangsan", age: 18});
        userList.push({username: "lisi", age: 28});

        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/user/quick15",
            data: JSON.stringify(userList),
            contentType: "application/json;charset=utf-8"
        })
    </script>
</head>
<body>

</body>
</html>
```



测试类：

```java
@ResponseBody
@RequestMapping(value = "/quick15")
public void save15(@RequestBody List<User> userList) throws IOException {
    System.out.println(userList);
}
```



注意：SpringMVC会拦截静态资源请求，所以我们要在`spring-mvc.xml`进行配置：

```xml
<!--开放资源的访问-->
<mvc:resources mapping="/js/**" location="/js/"/>
```

别忘了jquery导入之后，要**重新刷新maven**





### 4.5 静态资源访问的开启



我们在访问服务器的静态资源时，SpringMVC会找不到静态资源请求所映射的地址，也就找不到静态资源所在的目录。



需要在`spring-mvc.xml`中进行如下配置：

```xml
<!--开放资源的访问-->
<mvc:resources mapping="/js/**" location="/js/"/>
```



前面是你在访问服务端找资源的地址，后面代表具体资源所在的目录

地址是你浏览器端访问时带的，目录是你请求发过来后服务器端去指定的位置执行对应的访问

地址就是用来映射的，而目录才是访问的路径



还可以采用另一种方式，交给Tomcat服务器自己去寻找静态资源所在的目录，而在JavaWeb中，是可以找到的：

```xml
<!--SpringMVC找不到服务器请求地址所在的映射时，SpringMVC就会交给Tomcat自己去执行-->
<mvc:default-servlet-handler/>
```





### 4.6 配置全局乱码过滤器



我使用的Tomcat服务器是9.0.48版本的，所以get请求不会出现乱码问题，但是post请求却会出现乱码问题



get请求：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108292113998.png" alt="image-20210829211348827" style="zoom:67%;" />



post请求：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108292112972.png" alt="image-20210829211202862" style="zoom: 67%;" />



在JavaWeb中，我们使用`request.setCharacterEncoding()`可以解决中文乱码问题



下面介绍如何使用过滤器来解决乱码：

![image-20210829211611668](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108292116820.png)



我们在`web.xml`中进行配置**全局**过滤器：

```xml
<!--配置全局过滤的filter-->
<filter>
    <filter-name>CharacterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>CharacterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```





### 4.7 参数绑定注解@RequestParam



![image-20210829212646501](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108292126647.png)



比如我们表单中写了属性值为name，但是我们服务端想在地址中写入username时也能映射到name，那么我们可以加上注释`@RequestParam`

```java
@ResponseBody
@RequestMapping(value = "/quick16")
public void save16(@RequestParam(value = "username") String name) throws IOException {
    System.out.println(name);
}
```



这时候我们在浏览器地址中访问：`http://localhost:8080/test02_war_exploded/user/quick16?username=11`，服务端则成功返回了`11`。



注解还可以设置如下几个参数：

![image-20210829213718581](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108292137723.png)



测试代码：

```java
@ResponseBody
@RequestMapping(value = "/quick16")
public void save16(@RequestParam(value = "username",required = false,defaultValue = "hello") String name) throws IOException {
    System.out.println(name);
}
```





###  4.8 获得Restful风格的参数



![image-20210829214542871](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108292145035.png)



![image-20210829215415963](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108292154128.png)

其中`@PathVariable`中的参数value必须要和`@RequestMapping`参数要**一致**



测试类：

```java
//http://localhost:8080/test02_war_exploded/user/quick17/zhangsan
@ResponseBody
@RequestMapping(value = "/quick17/{username}")
public void save17(@PathVariable(value = "username") String name) throws IOException {
    System.out.println(name);
}
```



只需要在url地址中输入：`http://localhost:8080/test02_war_exploded/user/quick17/zhangsan`

服务端就能接收到name的值为`zhangsan`



### 4.9 自定义类型转换器



当我们使用`http://localhost:8080/test02_war_exploded/user/quick19?date=2021/1/20`

服务端可以正常返回，但是如果我们想用`date=2021-1-20`，这时服务端就会报错，所以我们可以自定义转换器



![image-20210829220611960](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108292206093.png)



第一步：定义转换器实现Converter接口

```java
public class DateConverter implements Converter<String, Date> {
    public Date convert(String dateStr) {
        //将日期字符串转换为日期对象
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
```



第二步：在配置文件中声明转换器

```xml
<!--声明转换器-->
<bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
    <property name="converters">
        <list>
            <bean class="com.zovz.converter.DateConverter"/>
        </list>
    </property>
</bean>
```



第三步：在`annotation-driven`中引入转换器

```xml
<!--MVC的注解驱动-->
<mvc:annotation-driven conversion-service="conversionService"/>
```



测试：在地址栏中输入：`http://localhost:8080/test02_war_exploded/user/quick18?date=2021-1-20`

服务端返回`Wed Jan 20 00:00:00 CST 2021`





### 4.10 获得servlet相关API



![image-20210830094411394](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108300944902.png)



测试类：

```java
@ResponseBody
@RequestMapping(value = "/quick19")
public void save19(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
    System.out.println(request);
    System.out.println(response);
    System.out.println(session);
}
```



打印输出：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108300949945.png" alt="image-20210830094927859" style="zoom:67%;" />





### 4.11 获得请求头信息



**1. @RequestHeader**

![image-20210830095222426](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108300952557.png)



测试类：

```java
@ResponseBody
@RequestMapping(value = "/quick20")
public void save20(@RequestHeader(value = "User-Agent", required = false) String user_agent) throws IOException {
    System.out.println(user_agent);
}
```



控制台打印：

Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36



与浏览器后端一致：

![image-20210830095633387](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108300956564.png)





**2. @CookieValue**



![image-20210830100013013](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108301000148.png)



测试类：

```java
@ResponseBody
@RequestMapping(value = "/quick21")
public void save21(@CookieValue(value = "JSESSIONID") String jsessionId) throws IOException {
    System.out.println(jsessionId);
}
```



控制台打印：

AE64DF559D1F1428B4A616DCFBE5A02F



与浏览器后端一致：

![image-20210830100343316](C:\Users\pc\AppData\Roaming\Typora\typora-user-images\image-20210830100343316.png)





### 4.12 文件上传



1. **文件上传客户端三要素**

- 表单项type=“file”
- 表单的提交方式为post
- 表单的enctype属性是多部分表单形式，及enctype="multipart/form-data"



因为表单上传默认是`enctype="application/x-www-form-urlencoded`，它的意思是键值对方式上传，而我们要文件上传，所以必须要使用`enctype="multipart/form-data"`和`post`。



2. **文件上传原理**



- 当form表单修改为多部分表单时，`request.getParameter()`将失效
- enctype="application/x-www-form-urlencoded"时，form表单的正文内容格式是：

key=value&key=value&key=value

- 当form表单的enctype取值为`Multipart/form-data`时，请求正文内容就变成多部分形式：

![image-20210830102540311](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108301025485.png)



我们之前使用的是Apache的fileupload包来进行文件上传的，而SpringMVC也是封装了fileupload，更加简便。





### 4.13 单文件上传步骤



1. 导入fileupload和io坐标

![image-20210830105227584](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108301052738.png)

2. 配置文件上传解析器

![image-20210830105211432](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108301052603.png)

3. 编写文件上传代码

第一步：在`pom.xml`和`spring-mvc.xml`文件中配置依赖

```xml
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.4</version>
</dependency>
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.8.0</version>
</dependency>
```



```xml
<!--配置文件上传解析器-->
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
    <property name="defaultEncoding" value="utf-8"/>
    <property name="maxUploadSize" value="500000"/>
</bean>
```



第二步：JSP前端页面

```jsp
<form action="${pageContext.request.contextPath}/user/quick22" method="post" enctype="multipart/form-data">
    名称：<input type="text" name="username"><br>
    文件：<input type="file" name="uploadFile">
    <input type="submit">
</form>
```



第三步：测试类

```java
@ResponseBody
@RequestMapping(value = "/quick22")
public void save22(String username, MultipartFile uploadFile) throws IOException {
    System.out.println(username);
    System.out.println(uploadFile);
    System.out.println(uploadFile.getName());
    System.out.println(uploadFile.getOriginalFilename());
    //获得文件名称
    String originalFilename = uploadFile.getOriginalFilename();
    //保存文件
    uploadFile.transferTo(new File("C:\\Users\\pc\\Desktop\\" + originalFilename));
}
```



控制台打印输出：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108301048441.png" alt="image-20210830104831323" style="zoom:67%;" />



并且桌面上成功接收到了服务器上传的文件。

注意：`public void save22(String username, MultipartFile uploadFile)`中的`username`和`uploadFile`参数要与JSP前端页面中的`name`属性要**一致**





### 4.14 多文件上传



![image-20210830111228537](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108301112754.png)

![image-20210830111253234](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108301112392.png)



前端JSP：

```jsp
<form action="${pageContext.request.contextPath}/user/quick23" method="post" enctype="multipart/form-data">
    名称：<input type="text" name="username"><br>
    文件：<input type="file" name="uploadFile">
    文件：<input type="file" name="uploadFile">
    <input type="submit">
</form>
```



测试类：

```java
@ResponseBody
@RequestMapping(value = "/quick23")
public void save23(String username, MultipartFile[] uploadFile) throws IOException {
    System.out.println(username);
    for (MultipartFile multipartFile : uploadFile) {
        //获得文件名称
        String originalFilename = multipartFile.getOriginalFilename();
        //保存文件
        multipartFile.transferTo(new File("C:\\Users\\pc\\Desktop\\" + originalFilename));
    }
}
```





## 5. Spring JdbcTemplate的基本使用



### 5.1 概述



它是 spring框架中提供的一个对象,是对原始繁琐的 Jdbc API对象的简单封装。 

spring框架为我们提供了很多的操作模板类。例如:

操作关系型数据的 JdbcTemplate和 HibernateTemplate,

操作nosq数据库的 RedisTemplate,

操作消息队列的 Jms Template等等。





### 5.2 开发步骤



1. 导入spring-jdbc和spring-tx坐标
2. 创建数据库表和实体
3. 创建JdbcTemplate对象
4. 执行数据库操作





### 5.3 快速入门代码实现



数据库表结构：

![image-20210830120859507](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108301208720.png)





测试类：

```java
public class JdbcTemplateTest {

    @Test
    //测试JdbcTemplate开发步骤
    public void test() throws PropertyVetoException {
        //创建数据源对象
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/school??useUnicode=true&characterEncoding=utf-8&useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("123456");

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //设置数据源对象 知道数据库在哪里
        jdbcTemplate.setDataSource(dataSource);
        //执行操作
        int row = jdbcTemplate.update("insert into student values (?,?,?)", 210, "tom", 50);
        System.out.println(row);
    }
}
```



打印输出：1



注意:根据MySQL 5.5.45+、5.6.26+和5.7.6+的要求，如果不设置显式选项，则必须建立默认的SSL连接。

即`jdbc:mysql://localhost:3306/school?useUnicode=true&characterEncoding=utf-8&useSSL=false`





### 5.4 Spring产生JdbcTemplate对象



![image-20210830121513106](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108301215331.png)



测试类：

```java
@Test
//测试Spring产生jdbcTemplate对象
public void test2() throws PropertyVetoException {
    ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("applicationContext.xml");
    JdbcTemplate jdbcTemplate = app.getBean(JdbcTemplate.class);
    int row = jdbcTemplate.update("insert into student values (?,?,?)", 300, "tom", 50);
    System.out.println(row);
}
```



`applicationContext.xml`：

```xml
<!--配置数据源对象 -->
<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
    <property name="driverClass" value="com.mysql.jdbc.Driver"/>
    <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/school"/>
    <property name="user" value="root"/>
    <property name="password" value="123456"/>
</bean>

<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
    <property name="dataSource" ref="dataSource"/>
</bean>
```





当然，我们还可以做进一步的抽取，新建`jdbc.properties`：

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/school
jdbc.username=root
jdbc.password=123456
```



`applicationContext.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:content="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context  http://www.springframework.org/schema/context/spring-context.xsd">

    <!--加载jdbc.properties-->
    <content:property-placeholder location="classpath:jdbc.properties"/>

    <!--配置数据源对象 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.driver}"/>
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>
</beans>
```



这样就完成了进一步的解耦。





### 5.5 增删改查操作



使用Spring集成Junit来进行测试。



测试类：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class JdbcTemplateCRUDTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    //查询一个简单的数据类型--》返回 106
    @Test
    public void testUpdate5() {
        Long count = jdbcTemplate.queryForObject("select count(*) from student", Long.class);
        System.out.println(count);
    }
    
    //查询一个对象--》返回 Student{id=1, name='张鹏辉', score=1}
    @Test
    public void testUpdate4() {
        Student student = jdbcTemplate.queryForObject("select * from student where id = ?", new BeanPropertyRowMapper<Student>(Student.class),1);
        System.out.println(student);
    }
    
    //查询多个对象--》返回一个[Student{id=1, name='张鹏辉', score=1}, Student{id=2, name='韩曼向', score=2}, Student{id=3, name='吴向梅', score=14}, ...]的列表集合
    @Test
    public void testUpdate() {
        List<Student> list = jdbcTemplate.query("select * from student", new BeanPropertyRowMapper<Student>(Student.class));
        System.out.println(list);
    }
    
    //更新
    @Test
    public void testUpdate1() {
        jdbcTemplate.update("update student set score = ? where id = ?", 1, 1);
    }
    
    //删除
    @Test
    public void testUpdate2() {
        jdbcTemplate.update("delete from student where id = ?", 200);
    }
    
    //增加
    @Test
    public void testUpdate3() {
        jdbcTemplate.update("insert into student values (?,?,?)", 200, "tom", 200);
    }
}
```



总结：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108301450832.png" alt="image-20210830145018667" style="zoom: 67%;" />





## 6. SpringMVC拦截器



### 6.1 拦截器（interceptor）的作用



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108311901073.png" alt="image-20210831190108967" style="zoom:67%;" />





### 6.2 拦截器和过滤器区别



![image-20210831190206260](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108311902394.png)





### 6.3 拦截器快速入门



自定义拦截器很简单，只有如下三步：

1. 创建拦截器类实现HandlerInterceptor接口
2. 配置拦截器
3. 测试拦截器的拦截效果





测试类：

```java
@Controller
public class TargetController {

    @RequestMapping(value = "/target")
    public ModelAndView show() {
        System.out.println("目标资源执行...");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "itcast");
        modelAndView.setViewName("index");
        return modelAndView;
    }
}
```



拦截器类：

```java
public class MyInterceptor1 implements HandlerInterceptor {
    //在目标方法执行之前
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        System.out.println("pre1");
        String param = request.getParameter("param");
        if (param == null || !param.equals("yes")) {
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return false;
        } else {
            return true;
        }
    }

    //在目标方法执行之后，视图对象返回之前
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        modelAndView.addObject("name", "zovz");
        System.out.println("post1");
    }

    //在整个流程都执行完毕后执行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println("after1");
    }
}
```



拦截器的配置文件：

```xml
<!--配置拦截器-->
<mvc:interceptors>
    <mvc:interceptor>
        <!--对那些资源执行拦截操作-->
        <mvc:mapping path="/**"/>
        <bean class="com.zovz.interceptor.MyInterceptor1"/>
    </mvc:interceptor>
    <mvc:interceptor>
        <!--对那些资源执行拦截操作-->
        <mvc:mapping path="/**"/>
        <bean class="com.zovz.interceptor.MyInterceptor2"/>
    </mvc:interceptor>
</mvc:interceptors>
```



最后执行的结果是：pre1，pre2，目标资源执行...，post2，post1，after2，after1

即在**配置文件**中，拦截器的定义谁在最前面，就最先执行，最后结束。





### 6.4 拦截器方法说明



![image-20210831190831859](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108311908011.png)







## 7. SpringMVC异常处理



使用原生异常的弊端：

1. 将原生的try，catch代码块与业务代码放在一起，增加耦合
2. 对于同一种类型的异常，需要编写多次捕获异常的代码，代码重复率高



### 7.1 异常处理的思路



![image-20210831191022804](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108311910941.png)





### 7.2 异常处理的两种方式



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108311920122.png" alt="image-20210831192008997" style="zoom:67%;" />





### 7.3 简单异常处理器SimpleMappingExceptionResolver



![image-20210831192339034](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108311923184.png)





**异常处理器实现**



`spring-mvc.xml`：

```xml
<!--配置异常处理器-->
<bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
    <!--<property name="defaultErrorView" value="error"/>------默认页面-->
    <property name="exceptionMappings">
        <map>
            <entry key="java.lang.ClassCastException" value="error1"/>
            <entry key="com.zovz.exceprion.MyException" value="error2"/>
        </map>
    </property>
</bean>
```



异常类：

```java
public class DemoServiceImpl implements DemoService {
    
    public void show1() {
        System.out.println("抛出类型转换异常...");
        Object str = "zhangsan";
        Integer num = (Integer) str;
    }

    public void show2() {
        System.out.println("抛出除零异常...");
        int i = 1 / 0;
    }

    public void show3() throws FileNotFoundException {
        System.out.println("文件找不到异常...");
        FileInputStream fileInputStream = new FileInputStream("C:/xxx/xxx/xxx");
    }

    public void show4() {
        System.out.println("空指针异常...");
        String str = null;
        System.out.println(str.length());
    }

    public void show5() throws MyException {
        System.out.println("自定义异常...");
        throw new MyException();
    }


}
```



测试类（调用自定义异常方法）：

```java
@Controller
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping(value = "/show")
    public String show(@RequestParam(value = "name", required = true) String name) throws FileNotFoundException, MyException {
        System.out.println("show running...");
       //demoService.show1();
       //demoService.show1();
       //demoService.show1();
       //demoService.show1();
        
        //自定义异常方法
        demoService.show5();
        return "index";
    }
}
```



返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108311949476.png" alt="image-20210831194906329" style="zoom: 50%;" />





### 7.4 自定义异常处理步骤



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108312044142.png" alt="image-20210831204425002" style="zoom:67%;" />





JSP前端页面：



```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>这是一个通用的错误提示页面</h1>
<h1>${info}</h1>
</body>
</html>
```





需要新建一个异常处理类：

```java
public class MyExceptionResolver implements HandlerExceptionResolver {
    /**
     * 参数Exception：异常对象
     * 返回值ModelAndView：跳转到错误视图信息
     */
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        ModelAndView modelAndView = new ModelAndView();

        if (e instanceof MyException) {
            modelAndView.addObject("info", "自定义异常");
        } else if (e instanceof ClassCastException) {
            modelAndView.addObject("info", "类转换异常");
        }
        modelAndView.setViewName("error");
        return modelAndView;
    }
}
```



还需要在`springmvc.xml`中配置：

```xml
<!--编写自定义的处理器-->
<bean class="com.zovz.resolver.MyExceptionResolver"/>
```



返回结果：



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108312058837.png" alt="image-20210831205838692" style="zoom:67%;" />





知识要点：



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108312056990.png" alt="image-20210831205633822" style="zoom: 67%;" />





# AOP



## 1. AOP的简介



### 1.1 什么是AOP

![image-20210831210810304](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108312108447.png)





### 1.2 AOP的作用及其优势



- 作用：在程序运行期间，在不修改源码的情况下对方法进行功能增强
- 优势：减少重复代码，提高开发效率，并且便于维护





### 1.3 AOP的底层实现



![image-20210831213822739](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108312138883.png)





### 1.4 AOP的动态代理技术



![image-20210831213609042](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108312136243.png)



AOP的底层实现技术是动态代理，有两种技术，一种是基于JDK的，一个是基于cglib；一个是要有接口，一个是不需要接口。





### 1.5 JDK的动态代理



`Target.java`：

```java
public class Target implements TargetInterface {
    @Override
    public void save() {
        System.out.println("save running...");
    }
}
```



`Advice.java`：

```java
public class Advice {

    public void before(){
        System.out.println("前置增强");
    }

    public void afterReturning(){
        System.out.println("后置增强");
    }

}
```



`ProxyTest.java`：

```java
public class ProxyTest {
    public static void main(final String[] args) {

        final Target target = new Target();
        final Advice advice = new Advice();

        //返回值，就是动态代理生成的对象
        TargetInterface proxy = (TargetInterface) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),  //目标对象类加载器
                target.getClass().getInterfaces(),  //目标对象相同的接口字节码对象数组
                new InvocationHandler() {
                    //调用代理对象的任何方法 实质执行的都是invoke方法
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        //前置增强
                        advice.before();
                        //执行目标方法
                        Object invoke = method.invoke(target, args);
                        //后置增强
                        advice.afterReturning();

                        return invoke;
                    }
                }
        );

        //调用代理对象的方法
        proxy.save();
    }
}
```



返回结果：

![image-20210831221506351](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108312215474.png)





### 1.6 cglib的动态代理



由于Spring已经是5版本，所以cglib已经集成到了core核心中，可以直接用。



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202108312221362.png" alt="image-20210831222124227" style="zoom: 80%;" />





cglib的动态代理不需要`Target`类继承接口



`Target`：

```java
public class Target {
    public void save() {
        System.out.println("save running...");
    }
}
```



`ProxyTest`：

```java
public class ProxyTest {
    public static void main(final String[] args) {

        final Target target = new Target();
        final Advice advice = new Advice();

        //返回值，就是动态代理生成的对象 基于cglib

        //1.创建增强器-->cglib提供
        Enhancer enhancer = new Enhancer();

        //2.设置父类（目标）
        enhancer.setSuperclass(Target.class);

        //3.设置回调
        enhancer.setCallback(new MethodInterceptor() {
            //相当于jdk的invoke方法
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                //执行前置
                advice.before();

                //执行目标
                Object invoke = method.invoke(target, args);

                //执行后置
                advice.afterReturning();

                return invoke;
            }
        });

        //4.创建代理对象
        Target o = (Target) enhancer.create();

        o.save();
    }
}
```





### 1.7 AOP相关概念



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011109962.png" alt="image-20210901110903564" style="zoom:67%;" />





### 1.8 AOP开发明确的事项



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011130858.png" alt="image-20210901113024736" style="zoom:67%;" />





### 1.9 知识要点



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011131286.png" alt="image-20210901113138193" style="zoom:67%;" />





## 2. 基于XML的AOP开发



### 2.1 快速入门



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011133495.png" alt="image-20210901113333410" style="zoom:67%;" />





`pom.xml`：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>5.0.5.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.8.4</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-test</artifactId>
        <version>5.0.5.RELEASE</version>
    </dependency>
</dependencies>
```



目标接口：

```java
public interface TargetInterface {

    public void save();

}
```



目标类：

```java
public class Target implements TargetInterface {
    @Override
    public void save() {
        System.out.println("save running...");
    }
}
```



`applicationContext.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--配置目标对象-->
    <bean id="target" class="com.zovz.aop.impl.Target"></bean>

    <!--配置切面-->
    <bean id="myAspect" class="com.zovz.aop.MyAspect"></bean>

    <!--配置织入，告诉spring框架，哪些方法（切点）需要进行哪些增强（前置，后置...）-->
    <aop:config>
        <!--声明切面-->
        <aop:aspect ref="myAspect">
            <!--切面：切点+通知-->
            <aop:before method="before" pointcut="execution(public void com.zovz.aop.impl.Target.save())"/>
        </aop:aspect>
    </aop:config>
    
</beans>
```





将配置织入注释掉，返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011337437.png" alt="image-20210901133744345" style="zoom: 80%;" />



将配置织入加入，返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011338462.png" alt="image-20210901133806362" style="zoom: 80%;" />





### 2.2 切面表达式的写法



![image-20210901141226475](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011412270.png)



将上面切点配置修改为：

```java
<!--配置织入，告诉spring框架，哪些方法（切点）需要进行哪些增强（前置，后置...）-->
<aop:config>
    <!--声明切面-->
    <aop:aspect ref="myAspect">
        <!--切面：切点+通知-->
        <aop:before method="before" pointcut="execution(* com.zovz.aop.*.*(..))"/>
    </aop:aspect>
</aop:config>
```



成功返回：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011338462.png" alt="image-20210901133806362" style="zoom: 80%;" />





### 2.3 通知的类型



通知的配置语法：

![image-20210901141603936](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011416088.png)





**后置通知：**



`MyAspect`类：

```java
    public void before(){
        System.out.println("前置增强...");
    }

    public void afterggg(){
        System.out.println("后置增强...");
    }
```



`applicationContext.xml`：

```xml
<!--配置织入，告诉spring框架，哪些方法（切点）需要进行哪些增强（前置，后置...）-->
<aop:config>
    <!--声明切面-->
    <aop:aspect ref="myAspect">
        <!--切面：切点+通知-->
        <aop:before method="before" pointcut="execution(* com.zovz.aop.*.*(..))"/>
        <aop:after-returning method="afterggg" pointcut="execution(* com.zovz.aop.*.*(..))"/>
    </aop:aspect>
</aop:config>
```



返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011422174.png" alt="image-20210901142254100" style="zoom: 80%;" />



**环绕后通知：**



`MyAspect`类：

```java
//Proceeding JoinPoint:正在执行的连接点--》切点
public Object around(ProceedingJoinPoint pjp) throws Throwable {
    System.out.println("环绕前增强...");
    //切点方法
    Object proceed = pjp.proceed();
    System.out.println("环绕后增强...");
    return proceed;
}
```



`applicationContext.xml`：

```xml
    <!--配置织入，告诉spring框架，哪些方法（切点）需要进行哪些增强（前置，后置...）-->
    <aop:config>
        <!--声明切面-->
        <aop:aspect ref="myAspect">
            <!--切面：切点+通知-->
            <aop:around method="around" pointcut="execution(* com.zovz.aop.*.*(..))"/>
        </aop:aspect>
    </aop:config>
```



返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011429881.png" alt="image-20210901142910802" style="zoom:80%;" />



**异常抛出通知：**



`MyAspect`类：

```java
public void afterThrowing(){
    System.out.println("异常抛出增强...");
}
```



`applicationContext.xml`：

```xml
<!--配置织入，告诉spring框架，哪些方法（切点）需要进行哪些增强（前置，后置...）-->
<aop:config>
    <!--声明切面-->
    <aop:aspect ref="myAspect">
        <!--切面：切点+通知-->
        <!--<aop:before method="before" pointcut="execution(* com.zovz.aop.*.*(..))"/>-->
        <!--<aop:after-returning method="afterggg" pointcut="execution(* com.zovz.aop.*.*(..))"/>-->
        <aop:around method="around" pointcut="execution(* com.zovz.aop.*.*(..))"/>
        <aop:after-throwing method="afterThrowing" pointcut="execution(* com.zovz.aop.*.*(..))"/>
    </aop:aspect>
</aop:config>
```



返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011454559.png" alt="image-20210901145410433" style="zoom:80%;" />



 **最终增强：**



无论抛不抛异常，最终增强方法都会执行



`MyAspect`类：

```java
public void after(){
    System.out.println("最终增强...");
}
```



`applicationContext.xml`：

```xml
<!--配置织入，告诉spring框架，哪些方法（切点）需要进行哪些增强（前置，后置...）-->
<aop:config>
    <!--声明切面-->
    <aop:aspect ref="myAspect">
        <!--切面：切点+通知-->
        <aop:around method="around" pointcut="execution(* com.zovz.aop.*.*(..))"/>
        <aop:after-throwing method="afterThrowing" pointcut="execution(* com.zovz.aop.*.*(..))"/>
        <aop:after method="after" pointcut="execution(* com.zovz.aop.*.*(..))"/>
    </aop:aspect>
</aop:config>
```



返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011501209.png" alt="image-20210901150107128" style="zoom: 80%;" />





### 2.4 切点表达式的抽取



![image-20210901150525219](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011505358.png)



`applicationContext.xml`：

```xml
<!--配置织入，告诉spring框架，哪些方法（切点）需要进行哪些增强（前置，后置...）-->
<aop:config>
    <!--声明切面-->
    <aop:aspect ref="myAspect">
        <aop:pointcut id="myPointCut" expression="execution(* com.zovz.aop.*.*(..))"/>
        <!--切面：切点+通知-->
        <aop:around method="around" pointcut-ref="myPointCut"/>
        <aop:after-throwing method="afterThrowing" pointcut-ref="myPointCut"/>
        <aop:after method="after" pointcut-ref="myPointCut"/>
    </aop:aspect>
</aop:config>
```





### 2.5 知识要点



![image-20210901151825270](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011518490.png)





## 3. 基于注解的AOP开发



### 3.1 快速入门



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011520541.png" alt="image-20210901152022438" style="zoom: 67%;" />



`MyAspect`类：

```java
@Component("MyAspect")
@Aspect //标注当前MyAspect是一个切面类
public class MyAspect {

    //配置前置通知
    @Before(value = "execution(* com.zovz.anno.*.*(..))")
    public void before() {
        System.out.println("前置增强...");
    }

    @AfterReturning(value = "execution(* com.zovz.anno.*.*(..))")
    public void afterggg() {
        System.out.println("后置增强...");
    }

    //Proceeding JoinPoint:正在执行的连接点--》切点
    @Around(value = "execution(* com.zovz.anno.*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("环绕前增强...");
        //切点方法
        Object proceed = pjp.proceed();
        System.out.println("环绕后增强...");
        return proceed;
    }

    @AfterThrowing(value = "execution(* com.zovz.anno.*.*(..))")
    public void afterThrowing() {
        System.out.println("异常抛出增强...");
    }

    @After(value = "execution(* com.zovz.anno.*.*(..))")
    public void after() {
        System.out.println("最终增强...");
    }
}
```



`applicationContext-anno.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--开启组件扫描-->
    <context:component-scan base-package="com.zovz.anno"/>

    <!--aop自动代理-->
    <aop:aspectj-autoproxy/>

</beans>
```



返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011619360.png" alt="image-20210901161929250" style="zoom:80%;" />



### 3.2 切点表达式的抽取



![image-20210901154239091](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011542222.png)



有两种方式可以引入：

```java
@Component("MyAspect")
@Aspect //标注当前MyAspect是一个切面类
public class MyAspect {

    //Proceeding JoinPoint:正在执行的连接点--》切点
    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("环绕前增强...");
        //切点方法
        Object proceed = pjp.proceed();
        System.out.println("环绕后增强...");
        return proceed;
    }

    @After(value = "MyAspect.pointcut()")
    public void after() {
        System.out.println("最终增强...");
    }
}
```





### 3.3 知识要点



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011548042.png" alt="image-20210901154807953" style="zoom: 67%;" />





![image-20210901153850597](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011538752.png)





# Spring的事务控制



## 1. 编程式事务控制相关对象



### 1.1 PlatformTransactionManager



是根据不用的DAO层的实现而定义的，不需要手动编程，但是在配置时需要告诉Spring框架。



![image-20210901163132353](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011631497.png)





### 1.2 TransactionDefinition



封装一些事务的参数，不需要手动编程，但是在配置时需要告诉Spring框架。



![image-20210901163411005](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011634128.png)



#### 1.事务隔离级别



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011637600.png" alt="image-20210901163740499" style="zoom: 67%;" />



#### 2.事务传播行为



![image-20210901163809240](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011638392.png)





### 1.3 TransactionStatus



随着程序的进行，内部信息会随着项目改变，不要通过配置告诉Spring框架。



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011649330.png" alt="image-20210901164913186"  />



**PlatformTransactionManager+TransactionDefinition=TransactionStatus**





## 2. 基于XML的声明式事务控制



### 2.1 什么是声明式事务控制



![image-20210901170014968](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011700123.png)



`Controller`层：

```java
public class AccountController {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        AccountService accountService = context.getBean(AccountService.class);
        accountService.transfer("tom", "lucy", 500);
    }
}
```



`service`层：

```java
public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void transfer(String outMan, String inMan, double money) {
        accountDao.out(outMan, money);
        accountDao.in(inMan, money);
    }
}
```



`Dao`层：

```java
public class AccountDaoImpl implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void out(String outMan, double money) {
        jdbcTemplate.update("update account set money = money - ? where name = ?", money, outMan);
    }

    public void in(String inMan, double money) {
        jdbcTemplate.update("update account set money = money + ? where name = ?", money, inMan);
    }
}
```



`applicationContext.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:property-placeholder location="classpath:jdbc.properties"/>

    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.class}"/>
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <bean id="accountDao" class="com.zovz.dao.impl.AccountDaoImpl">
        <property name="jdbcTemplate" ref="jdbcTemplate"/>
    </bean>

    <bean id="accountService" class="com.zovz.service.impl.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"/>
    </bean>

</beans>
```



`jdbc.properties`：

```properties
jdbc.class=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/test?useSSL=false
jdbc.username=root
jdbc.password=123456
```



数据库返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011955949.png" alt="image-20210901195511851"  />



当service层加入异常时：

```java
public void transfer(String outMan, String inMan, double money) {
    accountDao.out(outMan, money);
    int i = 1 / 0;
    accountDao.in(inMan, money);
}
```



数据库返回：

![image-20210901195656258](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109011956357.png)



钱只转出了，但没有转进。





### 2.2 声明式事务控制的实现



声明式事务控制明确事项：

- 谁是切点？
- 谁是通知？
- 配置切面？





保留上方异常错误，`applicationContext.xml`如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd
       http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:property-placeholder location="classpath:jdbc.properties"/>

    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.class}"/>
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <bean id="accountDao" class="com.zovz.dao.impl.AccountDaoImpl">
        <property name="jdbcTemplate" ref="jdbcTemplate"/>
    </bean>

    <!--目标对象 内部方法就是切点-->
    <bean id="accountService" class="com.zovz.service.impl.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"/>
    </bean>

    <!--配置平台事务管理器-->
    <!--只有当jdbc的时候使用datasource，使用不同的dao层技术实现时，需要改变-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!--通知 事务的增强-->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <!--设置事务的属性信息-->
        <tx:attributes>
            <tx:method name="*"/>
        </tx:attributes>
    </tx:advice>

    <!--配置事务的aop织入-->
    <!--事务的增强用advisor，普通的增强用aspect-->
    <aop:config>
        <aop:advisor advice-ref="txAdvice" pointcut="execution(* com.zovz.service.impl.*.*(..))"/>
    </aop:config>

</beans>
```



再次测试，返回结果：

![image-20210901201543705](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012015829.png)



但是数据并没有减少或者增加，说明事物回滚成功：

![image-20210901201700245](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012017342.png)





### 2.3 切点方法的事务参数的配置



![image-20210901202624960](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012026125.png)



`applicationContext.xml`：

```xml
<!--通知 事务的增强-->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
    <!--设置事务的属性信息-->
    <tx:attributes>
        <tx:method name="transfer" isolation="REPEATABLE_READ" propagation="REQUIRED" read-only="false"/>
        <tx:method name="save" isolation="REPEATABLE_READ" propagation="REQUIRED" read-only="false"/>
        <!--如果是查询方法，可以设置为只读-->
        <tx:method name="findAll" isolation="REPEATABLE_READ" propagation="REQUIRED" read-only="true"/>
        <!--以update开头的方法-->
        <tx:method name="update*" isolation="REPEATABLE_READ" propagation="REQUIRED" read-only="true"/>
        <tx:method name="*"/>
    </tx:attributes>
</tx:advice>
```





### 2.4 知识要点



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012030782.png" alt="image-20210901203040677" style="zoom: 67%;" />





## 3. 基于注解的声明式事务控制



### 3.1 快速入门



`applicationContext.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd
       http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!--组件扫描-->
    <context:component-scan base-package="com.zovz"/>

    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.class}"/>
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!--配置平台事务管理器-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!--事务的注解驱动-->
    <tx:annotation-driven transaction-manager="transactionManager"/>

</beans>
```





Dao层：

```java
@Repository("accountDao")
public class AccountDaoImpl implements AccountDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void out(String outMan, double money) {
        jdbcTemplate.update("update account set money = money - ? where name = ?", money, outMan);

    }

    public void in(String inMan, double money) {
        jdbcTemplate.update("update account set money = money + ? where name = ?", money, inMan);
    }
}
```



Service层：

```java
@Service("accountService")
//在类上注释，代表这个类下的所有方法都使用这个事务控制参数
@Transactional(isolation = Isolation.DEFAULT)
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;

    //就近原则，优先使用方法上的
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void transfer(String outMan, String inMan, double money) {
        accountDao.out(outMan, money);
         int i = 1 / 0;
        accountDao.in(inMan, money);
    }

    @Transactional(isolation = Isolation.DEFAULT)
    public void xxx(){}
}
```





### 3.2 注解配置声明式事务控制解析



![image-20210901204501181](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012045351.png)





### 3.3 知识要点



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012045998.png" alt="image-20210901204543887" style="zoom:67%;" />





# MyBatis



## 1. MyBatis简介



### 1.1 原始jdbc操作



**查询数据：**

![image-20210901210128060](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012101235.png)



**插入数据：**

![image-20210901210318213](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012103420.png)



发现问题：

1. 代码中有很多重复
2. 每次进行操作时，注册驱动，获得链接和关闭链接都要再写一遍，特别消耗程序性能
3. sql语句和java代码耦合，不方便修改





### 1.2 原始jdbc操作的分析



![image-20210901211117020](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012111172.png)





### 1.3 什么是Mybatis



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012112091.png" alt="image-20210901211207952" style="zoom:67%;" />





## 2. MyBatis的快速入门



### 2.1 MyBatis开发步骤



![image-20210901211514063](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012115236.png)





### 2.2 环境搭建



1. 导入MyBatis的坐标和其他相关坐标

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012158770.png" alt="image-20210901215817623" style="zoom:80%;" />



2. 创建user数据表



![image-20210901215854294](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012158417.png)



3. 编写User实体

![image-20210901220232649](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012202767.png)



4. 编写UserMapper映射文件

![image-20210901215933880](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012159027.png)



5. 编写MyBatis核心文件

![image-20210901220103967](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012201135.png)





### 2.3 编写测试代码



![image-20210901213501226](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012135385.png)



`pom.xml`：

```xml
<dependencies>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.47</version>
    </dependency>
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.4.6</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.17</version>
    </dependency>
</dependencies>
```



数据库表：

![image-20210901220432956](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012204057.png)



User实体类：

```java
public class User {
    private int id;
    private String name;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
```



`UserMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="userMapper">
    <select id="findAll" resultType="com.zovz.domain.User">
        select * from user
    </select>
</mapper>
```



`sqlMapConfig.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!--数据源的环境-->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/test?useSSL=false"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>

    <!--加载映射文件-->
    <mappers>
        <mapper resource="com/zovz/mapper/UserMapper.xml"/>
    </mappers>

</configuration>
```



返回结果：

![image-20210901220413688](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012204788.png)





### 2.4 知识小结



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012156956.png" alt="image-20210901215601837" style="zoom: 67%;" />





## 3. MyBatis的映射文件概述



![image-20210901220838966](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109012208173.png)





## 4. MyBatis的增删改查操作



### 4.1 插入操作



`MyBatisTest`测试类：

```java
public class MyBatisTest {

    @Test
    public void test2() throws IOException {

        //模拟User对象
        User user = new User();

        user.setId(6);
        user.setUsername("tom");
        user.setPassword("123456");

        //1.获得核心配置文件
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        //2.获得session工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        //3.获得session会话对象
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //4.执行操作  参数：namespace+id
        sqlSession.insert("userMapper.save", user);

        //5.mybatis执行更新操作 提交事务
        sqlSession.commit();

        //5.释放资源
        sqlSession.close();
    }
}
```



`UserMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="userMapper">

    <!--插入操作，括号中放的是属性名-->
    <insert id="save" parameterType="com.zovz.domain.User">
        insert into user values (#{id},#{username},#{password})
    </insert>
</mapper>
```



返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021025840.png" alt="image-20210902102543587" style="zoom:80%;" />





插入操作注意问题：



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021028367.png" alt="image-20210902102811249" style="zoom: 67%;" />





### 4.2 MyBatis的修改数据操作



#### 1. 编写UserMapper映射文件



![image-20210902103615038](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021036175.png)



#### 2. 编写修改实体User的代码



![image-20210902103734230](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021037351.png)



#### 3. 修改操作注意问题



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021038540.png" alt="image-20210902103801449" style="zoom: 67%;" />





`MyBatisTest`测试类：

```java
public class MyBatisTest {

    @Test
    //修改操作
    public void test3() throws IOException {

        //模拟User对象
        User user = new User();

        user.setId(6);
        user.setUsername("lucy");
        user.setPassword("123");

        //1.获得核心配置文件
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        //2.获得session工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        //3.获得session会话对象
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //4.执行操作  参数：namespace+id
        sqlSession.update("userMapper.update", user);

        //5.mybatis执行更新操作 提交事务
        sqlSession.commit();

        //5.释放资源
        sqlSession.close();
    }
}
```



`UserMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="userMapper">

    <!--修改操作-->
    <update id="update" parameterType="com.zovz.domain.User">
        update user set username=#{username},password=#{password} where id = #{id}
    </update>
    
</mapper>
```



返回结果：



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021035202.png" alt="image-20210902103541699" style="zoom: 80%;" />





### 4.3 MyBatis的删除数据操作



#### 1.编写UserMapper映射文件



![image-20210902104836816](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021048918.png)



#### 2. 编写删除数据的代码



![image-20210902104913578](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021049696.png)



#### 3. 删除操作注意问题



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021046785.png" alt="image-20210902104610663" style="zoom:67%;" />



`MyBatisTest`：

```java
public class MyBatisTest {

    @Test
    //删除操作
    public void test4() throws IOException {

        //1.获得核心配置文件
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        //2.获得session工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        //3.获得session会话对象
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //4.执行操作  参数：namespace+id
        sqlSession.update("userMapper.delete", 6);

        //5.mybatis执行更新操作 提交事务
        sqlSession.commit();

        //5.释放资源
        sqlSession.close();
    }
}
```



`UserMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="userMapper">

    <!--修改操作-->
    <update id="delete" parameterType="java.lang.Integer">
        delete from user where id=#{id}
    </update>
    
</mapper>
```



返回结果：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021051907.png" alt="image-20210902105115815" style="zoom:80%;" />





### 4.4. 知识小结



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021053706.png" alt="image-20210902105359507" style="zoom: 67%;" />





## 5. MyBatis的核心配置文件概述



### 5.1 MyBatis核心配置文件层级关系



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021105948.png" alt="image-20210902110550823" style="zoom: 67%;" />



### 5.2 MyBatis常用配置解析



#### 1. environment标签

数据库环境的配置，支持多环境配置



![image-20210902111007094](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021110263.png)



![image-20210902111426613](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021114777.png)





#### 2. mapper标签

![image-20210902111743430](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021117618.png)





#### 3. Properties

![image-20210902112209476](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021122710.png)



`jdbc.properties`：

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/test?useSSL=false
jdbc.username=root
jdbc.password=123456
```



`sqlMapConfig.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!--通过properties标签加载外部properties文件-->
    <properties resource="jdbc.properties"/>

    <!--数据源的环境-->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"/>
                <property name="url" value="${jdbc.url}"/>
                <property name="username" value="${jdbc.username}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>

    <!--加载映射文件-->
    <mappers>
        <mapper resource="com/zovz/mapper/UserMapper.xml"/>
    </mappers>

</configuration>
```





#### 4. typeAliases标签

![image-20210902113151918](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021131106.png)



将下面的配置

```xml
<!--    修改操作-->
<update id="delete" parameterType="java.lang.Integer">
    delete from user where id=#{id}
</update>
```



改为：

```xml
<!--修改操作-->
<update id="delete" parameterType="int">
    delete from user where id=#{id}
</update>
```

程序也运行成功。



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021135037.png" alt="image-20210902113538901" style="zoom: 67%;" />





注意：

```xml
<!--修改操作-->
<update id="update" parameterType="com.zovz.domain.User">
    update user set username=#{username},password=#{password} where id = #{id}
</update>
```

不能改为

```xml
<!--修改操作-->
<update id="update" parameterType="user">
    update user set username=#{username},password=#{password} where id = #{id}
</update>
```



因为mybatis本身定义了Integer的别名，但是不知道User这些实体，没有为User定义别名，所以我们可以自定义别名。



`sqlMapConfig.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!--通过properties标签加载外部properties文件-->
    <properties resource="jdbc.properties"/>

    <!--自定义别名-->
    <typeAliases>
        <typeAlias type="com.zovz.domain.User" alias="user"/>
    </typeAliases>

    <!--数据源的环境-->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"/>
                <property name="url" value="${jdbc.url}"/>
                <property name="username" value="${jdbc.username}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>

    <!--加载映射文件-->
    <mappers>
        <mapper resource="com/zovz/mapper/UserMapper.xml"/>
    </mappers>

</configuration>
```



注意：`sqlMapConfig.xml`中的属性必须要按照顺序来配置，否则会报以下错误，即`typeAliases`必须要放在`properties`后面：



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021143102.png" alt="image-20210902114322988" style="zoom: 80%;" />



配好之后`parameterType`属性就可以全部使用user了



### 5.3 知识小结



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021150306.png" alt="image-20210902115018152" style="zoom:67%;" />

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021152373.png" alt="image-20210902115243192" style="zoom:67%;" />





## 6. MyBaits相应的API



### 6.1 SqlSession工厂构造器SqlSessionFactoryBuilder



![image-20210902115720697](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021157861.png)





### 6.2 SqlSession工厂对象SqlSessionFactory



SqlSessionFactory有多个方法创建SqlSession实例。常用的有如下两个：

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021159615.png" alt="image-20210902115926488" style="zoom:67%;" />



即（手动提交）：

```java
SqlSession sqlSession = sqlSessionFactory.openSession();
sqlSession.commit();
```



与（自动提交）

```java
SqlSession sqlSession = sqlSessionFactory.openSession(true);
```

作用相同，都可以进行更新操作。





### 6.3 SqlSession会话对象



![image-20210902120522317](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021205468.png)



查询一个对象：

```xml
<!--根据id查询-->
<select id="findById" resultType="user" parameterType="int">
    select * from user where id=#{id}
</select>
```



测试类：

```java
@Test
//查询一个对象操作
public void test() throws IOException {
    //1.获得核心配置文件
    InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

    //2.获得session工厂对象
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

    //3.获得session会话对象
    SqlSession sqlSession = sqlSessionFactory.openSession();

    //4.执行操作  参数：namespace+id
    User user = sqlSession.selectOne("userMapper.findById", 2);

    //5.打印数据
    System.out.println(user);

    //6.释放资源
    sqlSession.close();
}
```





## 7. MyBatis的Dao层实现



### 7.1 传统开发方式



Dao层：

```java
public class UserDaoImpl implements UserDao {

    public List<User> findAll() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();

        List<User> userList = sqlSession.selectList("userMapper.findAll");

        return userList;
    }
}
```



测试类：

```java
public class ServiceDemo {

    public static void main(String[] args) throws IOException {

        //创建dao对象,当前dao层的实现是手动创建
        UserDao userDao = new UserDaoImpl();

        List<User> userList = userDao.findAll();

        System.out.println(userList);
        
    }
}
```





### 7.2 代理开发方式



#### 1. 代理开发方式介绍



![image-20210902143706261](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021437417.png)





#### 2. 编写UserMapper接口

![image-20210902143804490](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021438647.png)

只要上面的情况都满足，就可以使用Mapper接口





#### 3. 测试代理方式



![image-20210902143930048](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021439199.png)





代理开发：



`UserMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zovz.dao.UserMapper">

    <!--查询操作-->
    <select id="findAll" resultType="user">
        select * from user
    </select>

    <select id="findById" parameterType="int" resultType="user">
        select * from user where id = #{id}
    </select>

</mapper>
```



`UserMapper`接口：

```java
public interface UserMapper {

    List<User> findAll() throws IOException;

    User findById(int id) throws IOException;

}
```



`ServiceDemo`类：

```java
public class ServiceDemo {

    public static void main(String[] args) throws IOException {

        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        List<User> userList = mapper.findAll();
        User user = mapper.findById(3);

        System.out.println(user);
        System.out.println(userList);

    }
}
```





### 7.3 知识小结



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021506063.png" alt="image-20210902150650863" style="zoom: 67%;" />





## 8. MyBatis映射文件深入



### 8.1 动态sql语句



#### 1. 动态sql语句概述



![image-20210902151214288](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021512597.png)



 

#### 2. 动态SQL之`<if>`

![image-20210902154515309](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021545524.png)



```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zovz.mapper.UserMapper">

    <!--查询操作-->
    <select id="findByCondition" parameterType="user" resultType="user">
        select * from user
        <where>
            <if test="id > 0">
                and id=#{id}
            </if>
            <if test="username != null">
                and username=#{username}
            </if>
            <if test="password != null">
                and password=#{password}
            </if>
        </where>
    </select>

</mapper>
```





#### 3. 动态SQL之`<foreach>`

![image-20210902154621946](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021546096.png)



```xml
<!--如果是数组就写array-->
<select id="findByIds" parameterType="list" resultType="user">
    select * from user
    <where>
        <foreach collection="list" open="id in (" close=")" item="id" separator=",">
            #{id}
        </foreach>
    </where>
</select>
```





### 8.2 SQL片段抽取



![image-20210902160005938](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021600135.png)



```xml
<!--    sql语句抽取-->
<sql id="selectUser">select * from user</sql>

<!--查询操作-->
<select id="findByCondition" parameterType="user" resultType="user">
    <include refid="selectUser"></include>
    <where>
        <if test="id > 0">
            and id=#{id}
        </if>
        <if test="username != null">
            and username=#{username}
        </if>
        <if test="password != null">
            and password=#{password}
        </if>
    </where>
</select>
```





### 8.3 知识小结



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021617188.png" alt="image-20210902161752043" style="zoom:67%;" />





## 9.Mybatis核心配置文件深入



### 9.1 typeHandlers标签



![image-20210902162043764](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021620955.png)



![image-20210902162244053](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021622234.png)



`MapperTest`：

```java
public class MapperTest {

    @Test
    public void test2() throws IOException {
        //1.获得核心配置文件
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        //2.获得session工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        //3.获得session会话对象
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        User user = mapper.findById(7);

        System.out.println(user);

        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void test1() throws IOException {
        //1.获得核心配置文件
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        //2.获得session工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        //3.获得session会话对象
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        //模拟user
        User user = new User();
        user.setUsername("ceshi");
        user.setPassword("abc");
        user.setBirthday(new Date());

        //执行保存操作
        mapper.save(user);

        sqlSession.commit();
        sqlSession.close();
    }
}
```



`UserMapper`：

```java
public interface UserMapper {

    void save(User user);

    User findById(int id);
}
```



`DateTypeHandler`：

```java
public class DateTypeHandler extends BaseTypeHandler<Date> {

    @Override
    //将java类型转换成数据库需要的类型
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Date date, JdbcType jdbcType) throws SQLException {
        long time = date.getTime();
        preparedStatement.setLong(i, time);
    }

    @Override
    //将数据库中类型转换成java类型
    //String参数就是数据库中要转换的字段的名称
    //resultset 查询出的结果集
    public Date getNullableResult(ResultSet resultSet, String s) throws SQLException {
        //获得结果集中的需要的数据（long）转换成date类型返回
        long aLong = resultSet.getLong(s);
        Date date = new Date();
        return date;
    }

    @Override
    //将数据库中类型转换成java类型
    //i是指字段的位置
    public Date getNullableResult(ResultSet resultSet, int i) throws SQLException {
        long aLong = resultSet.getLong(i);
        Date date = new Date();
        return date;
    }

    @Override
    //将数据库中类型转换成java类型
    public Date getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        long aLong = callableStatement.getLong(i);
        Date date = new Date(aLong);
        return date;
    }
}
```



`UserMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zovz.mapper.UserMapper">

    <insert id="save" parameterType="user">
        insert into user values (#{id},#{username},#{password},#{birthday})
    </insert>

    <select id="findById" parameterType="int" resultType="user">
        select * from user where id=#{id}
    </select>

</mapper>
```





test1的返回结果：

![image-20210902171331382](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021713522.png)



test1的返回结果：

![image-20210902171009797](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021710926.png)





### 9.2 plugins标签



![image-20210902171444512](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021714655.png)



#### 1. 导入通用PageHelper坐标

![image-20210902171838717](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021718890.png)



#### 2. 在mybatis核心配置文件中配置PageHelper插件

![image-20210902172417999](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021724173.png)





#### 3. 代码实现



`pom.xml`：

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper</artifactId>
    <version>3.7.5</version>
</dependency>
<dependency>
    <groupId>com.github.jsqlparser</groupId>
    <artifactId>jsqlparser</artifactId>
    <version>0.9.1</version>
</dependency>
```



`MapperTest`:

```java
 @Test
    public void test3() throws IOException {
        //1.获得核心配置文件
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        //2.获得session工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        //3.获得session会话对象
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        //设置分页相关参数  当前页+每页显示的条数
        PageHelper.startPage(2, 3);

        List<User> userList = mapper.findAll();
        for (User user : userList) {
            System.out.println(user);
        }

        //获得与分页相关参数
        PageInfo<User> userPageInfo = new PageInfo<User>(userList);
        System.out.println("当前页" + userPage Info.getPageNum());
        System.out.println("每页显示条数" + userPageInfo.getPageSize());
        System.out.println("总条数" + userPageInfo.getTotal());
        System.out.println("总页数" + userPageInfo.getPages());
        System.out.println("上一页" + userPageInfo.getPrePage());
        System.out.println("下一页" + userPageInfo.getNextPage());
        System.out.println("是否是第一页" + userPageInfo.isIsFirstPage());
        System.out.println("是否是最后一页" + userPageInfo.isIsLastPage());


        sqlSession.close();
    }
```



`sqlMapConfig.xml`：

```xml
<!--配置分页助手插件-->
<plugins>
    <plugin interceptor="com.github.pagehelper.PageHelper">
        <property name="dialect" value="mysql"/>
    </plugin>
</plugins>
```



返回结果：

![image-20210902173833265](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021738466.png)





### 9.3 知识小结

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021738433.png" alt="image-20210902173849274" style="zoom:67%;" />





## 10. MyBatis的多表操作



### 10.1 一对一查询



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109021741985.png" alt="image-20210902174137813" style="zoom:67%;" />





`OrderMapper.xml`：

第一种封装方式：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zovz.mapper.OrderMapper">

    <resultMap id="orderMap" type="order">
        <!--手动指定字段与实体属性的映射关系-->
        <!--column 数据库的字段名称-->
        <!--property 实体的属性名称-->
        <id column="oid" property="id"></id>
        <result column="ordertime" property="ordertime"></result>
        <result column="total" property="total"></result>
        <result column="uid" property="user.id"></result>
        <result column="username" property="user.username"></result>
        <result column="password" property="user.password"></result>
        <result column="birthday" property="user.birthday"></result>
    </resultMap>

    <select id="findAll" resultMap="orderMap">
        select *,o.id oid from user u,orders o where u.id=o.uid
    </select>

</mapper>
```



第二种封装方式：

```xml
<resultMap id="orderMap" type="order">
    <!--手动指定字段与实体属性的映射关系-->
    <!--column 数据库的字段名称-->
    <!--property 实体的属性名称-->
    <id column="oid" property="id"></id>
    <result column="ordertime" property="ordertime"></result>
    <result column="total" property="total"></result>

    <!--property:当前实体（order）中的属性名称（private User user）-->
    <!--javaType:当前实体（order）中的属性的类型（User）-->
    <association property="user" javaType="user">
        <id column="uid" property="id"></id>
        <result column="username" property="username"></result>
        <result column="password" property="password"></result>
        <result column="birthday" property="birthday"></result>
    </association>

</resultMap>
```





返回结果：

![image-20210902201500131](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109022015289.png)





### 10.2 一对多查询



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109022024944.png" alt="image-20210902202425724" style="zoom:67%;" />





`UserMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zovz.mapper.UserMapper">

    <resultMap id="userMap" type="user">
        <id column="uid" property="id"></id>
        <result column="username" property="username"></result>
        <result column="password" property="password"></result>
        <result column="birthday" property="birthday"></result>
        <!--
             配置集合信息
           property:集合名称
        ofType:当前集合中的数据类型
        -->
        <collection property="orderList" ofType="order">
            <!--封装order的数据-->
            <id column="oid" property="id"></id>
            <result column="ordertime" property="ordertime"></result>
            <result column="total" property="total"></result>
        </collection>

    </resultMap>

    <select id="findAll" resultMap="userMap">
        select *,o.id oid from user u,orders o where u.id=o.uid
    </select>

</mapper>
```





返回结果：

![image-20210902205213656](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109022052786.png)





### 10.3 多对多查询



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109022054702.png" alt="image-20210902205438479" style="zoom:67%;" />





`UserMapper.xml`：

```xml
<resultMap id="userRoleMap" type="user">
    <!--user的信息-->
    <id column="userId" property="id"></id>
    <result column="username" property="username"></result>
    <result column="password" property="password"></result>
    <result column="birthday" property="birthday"></result>
    <!--user内部的roleList信息-->
    <collection property="roleList" ofType="role">
        <id property="id" column="roleId"></id>
        <result property="roleName" column="roleName"></result>
        <result property="roleDesc" column="roleDesc"></result>
    </collection>

</resultMap>

<select id="findUserAndRoleAll" resultMap="userRoleMap">
    select * from user u,sys_user_role ur,sys_role r where u.id=ur.userId and ur.roleId=r.id
</select>
```



返回结果：

![image-20210902211059930](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109022111072.png)



一对多和多对多基本相同，主要不同在于多了一张中间表，查询的SQL语句不同。





##  11. MyBatis的注解开发



### 11.1 MyBatis的常用注解

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109022114096.png" alt="image-20210902211449924" style="zoom:67%;" />





### 11.2 MyBatis的增删改查



我们完成简单的user表的增删改查的操作

![image-20210902215220304](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109022152487.png)



`MapperTest`测试类：

```java
public class MapperTest {

    private UserMapper mapper;

    @Before
    public void before() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = build.openSession(true);
        mapper = sqlSession.getMapper(UserMapper.class);
    }

    @Test
    public void test1() {
        User user = new User();
        user.setUsername("tom");
        user.setPassword("abc");
        mapper.save(user);
    }

    @Test
    public void test2(){
        User user = new User();
        user.setId(17);
        user.setUsername("lucy");
        user.setPassword("123");
        mapper.save(user);
    }

    @Test
    public void test3(){
        mapper.delete(17);
    }

    @Test
    public void test4(){
        User byId = mapper.findById(2);
        System.out.println(byId);
    }

    @Test
    public void test5(){
        List<User> all = mapper.findAll();
        for (User user : all) {
            System.out.println(user);
        }
    }
}
```



`UserMapper`接口：

```java
public interface UserMapper {

    void save(User user);

    void update(User user);

    void delete(int id);

    User findById(int id);

    List<User> findAll();

}
```



`UserMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zovz.mapper.UserMapper">

    <insert id="save" parameterType="user">
        insert into user values(#{id},#{username},#{password})
    </insert>

    <update id="update" parameterType="user">
        update user set username=#{username},password=#{password} where id=#{id}
    </update>

    <delete id="delete" parameterType="int">
        delete from user where id=#{id}
    </delete>

    <select id="findById" parameterType="int" resultType="user">
        select * from user where id=#{id}
    </select>

    <select id="findAll" resultType="user">
        select * from user
    </select>

</mapper>
```



`sqlMapConfig.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <!--通过properties标签加载外部properties文件-->
    <properties resource="jdbc.properties"/>

    <!--自定义别名-->
    <typeAliases>
        <typeAlias type="com.zovz.domain.User" alias="user"/>
    </typeAliases>


    <!--数据源的环境-->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"/>
                <property name="url" value="${jdbc.url}"/>
                <property name="username" value="${jdbc.username}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>

    <!--加载映射文件-->
    <mappers>
        <mapper resource="com/zovz/mapper/UserMapper.xml"/>
    </mappers>

</configuration>
```





### 11.3 使用注解完成基本的crud



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109022114096.png" alt="image-20210902211449924" style="zoom:67%;" />



`sqlMapConfig.xml`：

```xml
<!--加载映射关系-->
<mappers>
    <!--指定接口所在的包-->
    <package name="com.zovz.mapper"/>
</mappers>
```



`UserMapper`接口类：

```java
public interface UserMapper {

    @Insert("insert into user values(#{id},#{username},#{password})")
    void save(User user);

    @Update("update user set username=#{username},password=#{password} where id=#{id}")
    void update(User user);

    @Delete("delete from user where id=#{id}")
    void delete(int id);

    @Select("select * from user where id=#{id}")
    User findById(int id);

    @Select("select * from user")
    List<User> findAll();

}
```



别忘了删除``UserMapper.xml`文件，否则会报错。





### 11.4 MyBatis的注解实现复杂映射开发



![image-20210902222457062](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109022224423.png)



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109022226061.png" alt="image-20210902222600811" style="zoom:67%;" />





1. 一对一查询



`OrderMapper`接口：

第一种方法：

```java
public interface OrderMapper {

    @Select("select *,o.id oid from orders o,user u where o.uid=u.id")
    @Results({
            @Result(column = "oid", property = "id"),
            @Result(column = "ordertime", property = "ordertime"),
            @Result(column = "total", property = "total"),
            @Result(column = "uid", property = "user.id"),
            @Result(column = "username", property = "user.username"),
            @Result(column = "password", property = "user.password"),
    })
    List<Order> findAll();
    
}
```



第二种写法：

```java
public interface OrderMapper {

    @Select("select * from orders")
    @Results({
            @Result(column = "oid", property = "id"),
            @Result(column = "ordertime", property = "ordertime"),
            @Result(column = "total", property = "total"),
            @Result(
                    property = "user",//要封装的那个属性名称
                    column = "uid",//根据那个字段去查询user表的数据
                    javaType = User.class,//要封装的实体类型
                    //select属性 代表查询那个接口的方法获得数据
                    one = @One(select = "com.zovz.mapper.UserMapper.findById")
            )

    })
    List<Order> findAll();

}
```





2. 一对多查询



`UserMapper`接口：

```java
@Select("select * from user")
@Results({
                @Result(id = true, column = "id", property = "id"),
                @Result(column = "username", property = "username"),
                @Result(column = "password", property = "password"),
                @Result(
                        property = "orderList",
                        column = "id",
                        javaType = List.class,
                        many = @Many(select = "com.zovz.mapper.OrderMapper.findByUid")
                )
        })
public List<User> findUserAndOrderAll();
```



`OrderMapper`接口：

```java
@Select("select * from orders where uid =#{uid}")
public List<Order> findByUid(int uid);
```





3. 多对多查询



<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031759104.png" alt="image-20210903175945375" style="zoom:67%;" />



`UserMapper`接口：

```java
@Select("select * from user")
@Results({
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "username", property = "username"),
        @Result(column = "password", property = "password"),
        @Result(
                property = "roleList",
                column = "id",
                javaType = List.class,
                many = @Many(select = "com.zovz.mapper.RoleMapper.findByUid")
        )
})
public List<User> findUserAndRoleAll();
```



`RoleMapper`接口：

```java
@Select("select * from sys_user_role ur,sys_role r where ur.roleId=r.id and ur.userId=#{uid}")
public List<Role> findByUid(int id);
```





# SSM整合



## 1.1 准备工作



1. 原始方式整合

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031915807.png" alt="image-20210903191542472" style="zoom:67%;" />



2. 创建maven工程

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031916940.png" alt="image-20210903191605794" style="zoom:67%;" />



3. 导入maven坐标



```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.8.4</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>


        <dependency>
            <groupId>javax.servlet.jsp</groupId>
            <artifactId>javax.servlet.jsp-api</artifactId>
            <version>2.3.3</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version>
        </dependency>


        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.6</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>1.3.1</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <dependency>
            <groupId>c3p0</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.1.2</version>
        </dependency>


        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>jstl</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
        </dependency>
    </dependencies>
```



4. 编写实体类

![image-20210903191703641](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031917783.png)



5. 编写Mapper接口

![image-20210903191731073](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031917203.png)



6. 编写Service接口

![image-20210903191745728](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031917850.png)



7. 编写Service接口实现

![image-20210903191804750](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031918014.png)



8. 编写Controller

![image-20210903191838680](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031918921.png)



9. 编写添加页面

![image-20210903191909683](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031919882.png)



10. 编写列表页面

![image-20210903191944874](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031919024.png)



11. 编写相应配置文件

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031919239.png" alt="image-20210903191958071" style="zoom:67%;" />



12. 测试添加账户

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031920609.png" alt="image-20210903192024452" style="zoom:67%;" />



13. 测试账户列表

<img src="https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109031920546.png" alt="image-20210903192047255" style="zoom:67%;" />





## 1.2 Spring整合MyBatis



### 1. 整合思路



![image-20210903211748706](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109032117967.png)





### 2. 将SqlSessionFactory配置到Spring容器中



![image-20210903212042933](https://gitee.com/ZovZ/gitee-drawing-bed/raw/master/image/202109032120161.png)





剩下的文件看包里的ssm文件...













