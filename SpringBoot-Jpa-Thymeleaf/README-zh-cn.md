# 通过一个简单的与Springboot集成Demo认识Thymeleaf模板
### 什么是Thymeleaf
Thymeleaf是一种Java XML / XHTML / HTML5模板引擎，可以在Web和非Web环境中使用。它更适合在基于MVC的Web应用程序的视图层提供XHTML / HTML5，但即使在脱机环境中，它也可以处理任何XML文件。它提供了完整的Spring Framework集成。

### Thymeleaf 的基础使用  
Thymeleaf的使用是由两部分组成的：标签 + 表达式，标签是Thymeleaf的语法结构，而表达式就是语法里的内容实现。
通过标签 + 表达式，让数据和模板结合，最终转换成html代码，返回给用户。  

下面通过一个Springboot结合jpa加Thymeleaf显现一个简单的curd。  
controller:  
```java
@Controller
public class UserController {

    /**
     * The User service.
     */
    @Autowired
    UserService userService;

    /**
     * Index string.
     *
     * @return the string
     */
    @RequestMapping("/")
    public String index() {
        return "redirect:/list";
    }

    /**
     * List string.
     *
     * @param model the model
     * @return the string
     */
    @RequestMapping("/list")
    public String list(Model model) {
        List<User> users = userService.getUserList();
        model.addAttribute("users", users);
        return "user/list";
    }

    /**
     * To add string.
     *
     * @return the string
     */
    @RequestMapping("/toAdd")
    public String toAdd() {
        return "user/userAdd";
    }

    /**
     * Add string.
     *
     * @param user the user
     * @return the string
     */
    @RequestMapping("/add")
    public String add(User user) {
        userService.save(user);
        return "redirect:/list";
    }

    /**
     * To edit string.
     *
     * @param model the model
     * @param id    the id
     * @return the string
     */
    @RequestMapping("/toEdit")
    public String toEdit(Model model, Long id) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "user/userEdit";
    }

    /**
     * Edit string.
     *
     * @param user the user
     * @return the string
     */
    @RequestMapping("/edit")
    public String edit(User user) {
        userService.edit(user);
        return "redirect:/list";
    }

    /**
     * Delete string.
     *
     * @param id the id
     * @return the string
     */
    @RequestMapping("/delete")
    public String delete(Long id) {
        userService.delete(id);
        return "redirect:/list";
    }
}
```
显示全部消息页面：  
```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8"/>
    <title>userList</title>
    <link rel="stylesheet" th:href="@{/css/bootstrap.css}"/>
</head>
<body class="container">
<br/>
<h1>用户列表</h1>
<br/><br/>
<div class="with:80%">
    <table class="table table-hover">
        <thead>
        <tr>
            <th>#</th>
            <th>User Name</th>
            <th>Password</th>
            <th>Age</th>
            <th>Edit</th>
            <th>Delete</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="user : ${users}">
            <th scope="row" th:text="${user.id}">1</th>
            <td th:text="${user.userName}">neo</td>
            <td th:text="${user.password}">Otto</td>
            <td th:text="${user.age}">6</td>
            <td><a th:href="@{/toEdit(id=${user.id})}">edit</a></td>
            <td><a th:href="@{/delete(id=${user.id})}">delete</a></td>
        </tr>
        </tbody>
    </table>
</div>
<div class="form-group">
    <div class="col-sm-2 control-label">
        <a  th:href="@{/toAdd}" class="btn btn-info">add</a>
    </div>
</div>

</body>
</html>
```
此例子贴出一些较关键代码，具体源码请到github上查看。  

另外,我们通常使用Thymeleaf来做前后端分离，它的语法能够自动被浏览识别，也就意味着即使在没有动态数据时，它仍可以正常编程出页面。  
我经常使用的前后端分离思想有以绝对路径引用和用web映射引用：  
1. 以绝对路径应用模板，即不需要将页面放置在项目中，可将它发至任意可以被项目应用到的路径。  
通过配置下面的选项，来指定视图跳转的前缀和静态资源的位置：  
```
#spring.thymeleaf.prefix=file:///E:/thymeleaf/templates
#spring.resources.static-locations=file:///E:/thymeleaf/static/
```
注意：上述引用绝对路径windows下使用file:///，而linux下使用file:/home/thymeleaf/tempaltes,区别在于file:的斜杠数
2. 通过web服务器，将页面映射出来，常用的做法有使用node和nginx，这里以nginx为例，配置nginx 路由，指向模板页面和静态资源，同样，配置以下配置选项，指定构建视图url的前缀和静态资源的位置：
```
#spring.thymeleaf.prefix=http://localhost:8080/templates/
#spring.resources.static-locations=http://localhost:8080/static/
```

项目源码：    
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Thymeleaf  
