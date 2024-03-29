## 三级分类（45 - 58集）

针对微服务gulimall-product。

### 查询分类数据（45集）

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

### 配置网关（46集）

运行renren-fast-vue和renren-fast，登录，账户名密码均为admin。

登陆后创建相关目录和菜单

为分类系统编写界面组件，static->config->index.js中更改baseUrl

为renren-fast注册服务

在gateway中添加路由规则



### 跨域问题（47集）

跨域原因：当出现跨域请求时，浏览器会向服务器询问是否可以跨域，默认是不允许的。

解决办法：我们只需要在每一个响应头中加入允许跨域访问的属性即可。

在gateway中定义配置跨域

在renren-fast中注释掉自带的跨域配置



### 分类信息的显示（48集）

为product配置注册中心和配置中心

gateway中设置路由规则，注意精确的路由放在前边，否则可能会被更模糊的先拦截

修改renren-fast-vue，显示分类信息



### 前端显示删除等按钮（49集）

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

## 品牌管理（59 - 69集）

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

### 后端校验（66集）

JSR303

字段添加校验注解，自定义错误提示

接收时添加开启校验的注解

紧跟BingingResult获取错误信息

自定义注解，可以使用正则表达式

### 异常统一处理（67集）

@ControllerAdvice

状态码

### 分组校验（68集）

groups属性

在common中添加valid包

@validated注解

### 自定义校验（69集）

引入依赖

自定义校验注解

配置错误信息

自定义校验器

关联校验注解和校验器，可以指定多个校验器

分割修改和修改状态

## 商品属性（70 - 101集）

### SPU和SKU（70）

spu/sku

基本属性spu

销售属性sku

数据库表的解释

数据库缺少了value_type字段，可以使用如下方法修复（本项目中的sql文件没有这个问题）。

修改数据库表pms_attr

```sql
drop table if exists pms_attr;

/*==============================================================*/
/* Table: pms_attr                                              */
/*==============================================================*/
create table pms_attr
(
   attr_id              bigint not null auto_increment comment '属性id',
   attr_name            char(30) comment '属性名',
   search_type          tinyint comment '是否需要检索[0-不需要，1-需要]',
   value_type           tinyint comment '值的类型',
   icon                 varchar(255) comment '属性图标',
   value_select         char(255) comment '可选值列表[用逗号分隔]',
   attr_type            tinyint comment '属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]',
   enable               bigint comment '启用状态[0 - 禁用，1 - 启用]',
   catelog_id           bigint comment '所属分类',
   show_desc            tinyint comment '快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整',
   primary key (attr_id)
);

alter table pms_attr comment '商品属性';
```

重新生成相关文件并替换

AttrEntity.java和AttrDao.xml

### 属性分组前端（71）

admin执行sql

接口文档

抽出商品分类组件

创建属性分组组件

父子组件传递数据

### 属性分组后端与前端查询（72）

后端controller、service、serviceimpl

前端查询时加入id

### 73、商品服务-API-属性分组-分组新增&级联选择器

新增时所属分类修改为级联选择框

修改后端代码，当子分类为空时不发送该属性

前端提交时提交数组的最后一个元素

### 74、商品服务-API-属性分组-分组修改&级联选择器回显

前端修改时路径回显

后端查询路径

前端修改后清空状态

新增时添加搜索功能

### 75、商品服务-API-品牌管理-品牌分类关联与级联更新

分类原始数据

mybatis plus分页插件

品牌的模糊查询

复制前端代码

获取品牌的关联分类

保存关联关系

更新品牌名和分类名时同步更新关联表的信息

### 76、商品服务-API-平台属性-规格参数新增与VO

属性分组 查询全部 加入参数

新增规格参数的后端代码

### 77、商品服务-API-平台属性-规格参数列表

新建AttrRespVo类

编写后端查询代码

### 78、商品服务-API-平台属性-规格修改

修改AttrRespVo类

修改时回显分类和分组

更新修改逻辑

### 79、商品服务-API-平台属性-销售属性维护

属性分组的查询、修改、新增

新建一个产品常量类

### 80、商品服务-API-平台属性-查询分组关联属性&删除关联

根据分组id查询相关的关联

批量删除关联

### 81、商品服务-API-平台属性-查询分组未关联的属性

@GetMapping("/{attrgroupId}/noattr/relation")

AttrGroupController -> service -> impl

### 82、商品服务-API-平台属性-新增分组与属性关联

@PostMapping("/attr/relation")

AttrGroupController -> AttrAttrgroupRelationService -> AttrAttrgroupRelationServiceImpl

### 83、商品服务-API-新增商品-调试会员等级相关接口

member网关配置

复制前端代码

添加几个会员等级

### 84、商品服务-API-新增商品-获取分类关联的品牌

解决前端错误

- npm install --save pubsub-js
- 在src下的main.js中引用：
  import PubSub from 'pubsub-js'
  Vue.prototype.PubSub = PubSub

CategoryBrandRelationController -> @GetMapping("/brands/list")

### 85、商品服务-API-新增商品-获取分类下所有分组以及属性

AttrGroupWithAttrsVo

/product/attrgroup/{catelogId}/withattr

### 86、商品服务-API-新增商品-商品新增vo抽取

新增商品信息

准备vo

### 87、商品服务-API-新增商品-商品新增业务流程分析

流程分析

### 88、商品服务-API-新增商品-保存SPU基本信息

/product/spuinfo/save

spuInfoService.saveSpuInfo(vo);前四步

### 89、商品服务-API-新增商品-保存SKU基本信息

5.1、5.2、5.3

### 90、商品服务-API-新增商品-调用远程服务保存优惠等信息

feign包

开启feign

to包

远程服务

修改R类

@PostMapping("/coupon/spubounds/save")

@PostMapping("/coupon/skufullreduction/saveinfo")

### 91、商品服务-API-新增商品-商品保存debug完成

限制服务内存

com.atguigu.gulimall.product.entity.SpuInfoDescEntity中添加注解@TableId(type = IdType.INPUT)

### 92、商品服务-API-新增商品-商品保存其他问题处理

由于直接复制粘贴的，所以这里不需要再改了。

### 93、商品服务-API-商品管理-SPU检索

/product/spuinfo/list

前端修改新建等对应的编码（无需修改）

yml调整日期格式

### 94、商品服务-API-商品管理-SKU检索

/product/skuinfo/list

spu纠错（无需修改）

### 95、仓储服务-API-仓库管理-整合ware服务&获取仓库列表

ware加入注册中心

网关配置

mybatis plus配置

日志级别

/ware/wareinfo/list

### 96、仓储服务-API-仓库管理-查询库存&创建采购需求

/ware/waresku/list

/ware/purchasedetail/list

### 97、仓储服务-API-仓库管理-合并采购需求

/ware/purchase/unreceive/list

/ware/purchase/merge

时间格式

### 98、仓储服务-API-仓库管理-领取采购单

/ware/purchase/received

### 99、仓储服务-API-仓库管理-完成采购

/ware/purchase/done

配置分页

### 100、商品服务-API-商品管理-SPU规格维护

/product/attr/base/listforspu/{spuId}

前端2个问题

- 页面404

  ```js
  // 在/src/router/index.js 在mainRoutes->children添加
  { path: '/product-attrupdate', component: _import('modules/product/attrupdate'), name: 'attr-update', meta: { title: '规格维护', isTab: true } }
  ```

- 回显问题

  ```js
  // attrupdate.vue的showBaseAttrs中
  if (v.length == 1) {
        v = v【0】 + ''
   }
  // 修改为
  if (v.length == 1 && attr.valueType == 0) {
       v = v【0】 + ''
   }
  ```

/product/attr/update/{spuId}

### 101、分布式基础篇总结

![分布式基础篇总结](readme_pics\分布式基础篇总结.png)









