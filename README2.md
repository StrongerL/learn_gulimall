## 三级分类

针对微服务gulimall-product。

### 查询分类数据

查询所有分类信息并以树形结构返回。

1. 改造CategoryEntity，添加属性和注解

   ```java
   /**
    * 子类
    */
   @TableField(exist=false)
   private List<CategoryEntity> children;
   ```

2. 在CategoryService接口中添加方法

   ```java
   List<CategoryEntity> listWithTree();
   ```

3. 在CategoryServiceImpl实现添加的方法

   ```java
   @Override
   public List<CategoryEntity> listWithTree() {
       //1、查出所有分类
       List<CategoryEntity> entities = baseMapper.selectList(null);
   
       //2、组装成父子的树形结构
   
       //2.1）、找到所有的一级分类
       List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
               categoryEntity.getParentCid() == 0
       ).map((menu)->{
           menu.setChildren(getChildren(menu,entities));
           return menu;
       }).sorted((menu1,menu2)->{
           return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
       }).collect(Collectors.toList());
   
   
       return level1Menus;
   }
   
   //递归查找所有菜单的子菜单
   private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){
   
       List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
           return categoryEntity.getParentCid() == root.getCatId();
       }).map(categoryEntity -> {
           //1、找到子菜单
           categoryEntity.setChildren(getChildren(categoryEntity,all));
           return categoryEntity;
       }).sorted((menu1,menu2)->{
           //2、菜单的排序
           return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
       }).collect(Collectors.toList());
   
       return children;
   }
   ```

4. 在CategoryController中修改方法

   ```java
   /**
    *  查询所有列表，并按照级别组装为树形结构返回
    */
   @RequestMapping("/list/tree")
   public R list(){
       List<CategoryEntity> entities = categoryService.listWithTree();
       return R.ok().put("data", entities);
   }
   ```

### 配置网关

// todo

运行renren-fast-vue和renren-fast，登录，账户名密码均为admin。

登陆后创建相关目录和菜单

为分类系统编写界面组件，static->config->index.js中更改baseUrl

为renren-fast注册服务

在gateway中添加路由规则



### 跨域问题

// todo

在gateway中定义配置跨域

在renren-fast中注释掉自带的跨域配置

// 明明做好了上述工作，但是依然显示跨域。。。重启了idea、chrome，自己好了。。。



### 分类信息的显示

// todo

为product配置注册中心和配置中心

gateway中设置路由规则，注意精确的路由放在前边，否则可能会被更模糊的先拦截

修改renren-fast-vue，显示分类信息



### 前端显示删除等按钮

为el-tree加入按钮

```vue
<span class="custom-tree-node" slot-scope="{ node, data }">
    <span>{{ node.label }}</span>
    <span>
      <el-button type="text" size="mini" @click="() => append(data)">Append</el-button>
      <el-button type="text" size="mini" @click="() => remove(node, data)">Delete</el-button>
    </span>
</span>
```

加入方法

```vue
append(data) {},
remove(node, data) {}
```

为el-tree添加属性

```vue
<el-tree
    :data="menus"
    :props="defaultProps"
    expand-on-click-node="false"
    show-checkbox
    node-key="catId"
>
```

此处我有一个疑问？这里是如何找到catId的，代码中似乎并没有体现他是在node中还是data中，也没有指明路径。

添加判断条件，增加删除只在需要出现的时候出现



### 实现删除功能（50 - 51集）

@RequestBody需要请求体，因此需要post请求

后端

编写删除代码

使用逻辑删除

1. 配置逻辑删除规则（可省略）
2. 实体类添加@TableLogic注解

调整日志级别为debug观察sql语句

前端

remove函数

弹框确认

确认消息

展开

### 实现新增功能（52集）

对话框，以及对应的显示标志

添加表单，以及对应的表单数据

创建表单提交的函数

append函数设置一些默认值

表单提交的函数具体实现

一个bug的解决

具体的表现为：新增一个二级分类，可以正常显示，为已有的二级分类添加一个三级分类，也可以正常显示；但是为新增的一个二级分类添加一个三级分类，却不能正常显示，原因出在com/atguigu/gulimall/product/service/impl/CategoryServiceImpl.java中的getChildren(CategoryEntity root,List\<CategoryEntity\> all)方法上，getParentCid()和getCatId()方法返回的都是Long对象而非long基本数据类型，所以比较是否相等应该使用equals()方法。

```java
private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){

    List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
        // 这里的比较是有问题的，应该修改为
        // return categoryEntity.getParentCid().equals(root.getCatId());
        return categoryEntity.getParentCid() == root.getCatId();
    }).map(categoryEntity -> {
        categoryEntity.setChildren(getChildren(categoryEntity,all));
        return categoryEntity;
    }).sorted((menu1,menu2)->{
        return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
    }).collect(Collectors.toList());

    return children;
}
```

那么为什么已有的二级分类新增三级分类可以正常显示呢？在[-128, 127]范围内的类对象，如果值相同，那么他们的地址也是相同的，都指向常量池中的同一个对象，超出了这个范围地址就不一样。已有的二级分类的catI范围均在[-128, 127]内，而新增的二级分类的catId超出了这个范围，所以使用==进行比较，值相同也会返回false。

### 实现修改功能（53集）

添加修改按钮

修改对话框和新增分类的方法

添加edit方法

添加提交方法

### 实现拖拽功能（54 - 56集）

为el-tree添加拖拽属性和拖拽判断方法

添加监听事件，拖拽成功后，更新父id、排序、层级

后台接口

### 拖拽完善（57集）

添加拖拽按钮

批量保存

### 批量删除（58集）

删除按钮

对应的删除方法

## 品牌管理

### 新增品牌管理（59集）

创建菜单

复制生成的组件

权限

### 优化页面（60集）

显示状态文本

自定义格式

修改界面

对应的事件函数

### 文件上传（61 - 62集）

阿里云oss开通与使用

文件上传代码

子账户

直接使用阿里云oss starter而非自己创建，这是[网址](https://github.com/alibaba/aliyun-spring-boot/tree/master/aliyun-spring-boot-samples/aliyun-oss-spring-boot-sample)

- 引入starter依赖
- 配置
- 自动注入

### 第三方服务（63集）

创建一个module

引入oss

配置中心（首先改一下pom中的版本）

注册中心

排除mybatis

端口

签名后直传

网关

### 前端上传（64集）

upload文件夹

上传组件action更改

导入组件

oss跨域配置

### 前端新增品牌完善（65集）

表格宽度

显示状态绑定

显示图片

// 导入Image等组件

自定义校验首字母和sort

默认值

### 后端校验

JSR303

字段添加校验注解，自定义错误提示

接收时添加开启校验的注解

紧跟BingingResult获取错误信息

自定义注解，可以使用正则表达式







