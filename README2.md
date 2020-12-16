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



运行renren-fast-vue和renren-fast，登录，账户名密码均为admin。

登录后













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













