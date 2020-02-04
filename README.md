## 描述
本starter的宗旨是让web应用通过简单的注解配置，就可以实现角色认证功能。
***

### 注解介绍

###### `@JAuth`
这个注解中有两个属性`roles`（角色）和`roleGroup`（角色组）。
该注解可作用与class、method上，表示当前交易需要用户登陆后，且拥有相应角色时，才能访问。
eg:  
```
public class Demo1Controller {
    
    @RequestMapping("/getUser")
    @JAuth("admin2")
    public User getUser(@RequestParam int id){
        //TODO ......
    }
}
```
上面代码就表示，'/getUser' 这个交易需要用户登陆且拥有admin2角色，才能访问。


###### `@JAuthRequestMapping` 
这个注解是`@JAuth`和`@RequestMapping`两个注解的合体，更加简洁。eg：
```
public class Demo2Controller {
    
    @JAuthRequestMapping(value="/getUser",roles="admin2")
    public User getUser(@RequestParam int id){
        //TODO ......
    }
}
```
***

### 使用说明
本starter中定义了如下接口：用户`JUser`、角色组`JRoleGroup`(可继承抽象类`AbstractJRoleGroup`)、用户获取服务`UserDetailService`.  
开发者需要自己去实现上述接口，并将`JRoleGroup`和`UserDetailService`注册到Spring中。eg:  
```
@Configuration
public class AutoConfig {

    @Bean("RG_admin")
    public JRoleGroup admin() {
        MyRoleGroup myRoleGroup = new MyRoleGroup();
        myRoleGroup.setGroupName("RG_admin");
        myRoleGroup.add("admin");
        return myRoleGroup;
    }

    @Bean
    public MyUserDetailService createUserDetailService() {
        return new MyUserDetailService();
    }

}
```
#### 扩展
当用户鉴权失败后，本starter默认返回json数据。  
未登陆：`{"code":"1001","msg":"please login first"}`  
没权限：`{"code": "2001","msg": "permission defined to the [requestURI]."}`  
如需要个性化处理，请实现接口`JAuthFailProcessor`，并注册至Spring中。
 

