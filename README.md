## 说明

仅用于本人记录自己的学习过程，这里是[教程网址](https://www.bilibili.com/video/BV1np4y1C7Yf)。



## 环境配置

### virtualBox安装

安装前

- 在bios中打开cpu虚拟化。

安装后

- 记得更改虚拟机位置（不是安装位置），否则虚拟机会默认存储在c盘。

  管理 -> 全局设定 -> 常规 -> 默认虚拟电脑位置

  ![vBox安装1](readme_pics/vBox安装1.png)

  ![vBox安装2](readme_pics/vBox安装2.png)



### vagrant安装与使用

安装

- 按照向导一步步安装即可。

使用

快速配置虚拟机，[官网](https://app.vagrantup.com/boxes/search)有各种box的名称，以centos/7为例。

```shell
# 初始化配置文件，在当前目录生成Vagrantfile文件
vagrant init centos/7

# 修改配置文件Vagrantfile的内容
# 编码
Encoding.default_external = 'UTF-8'
# 禁用共享文件，否则默认会把Vagrantfile同级目录下的所有文件和目录导入虚拟机的/vagrant目录
config.vm.synced_folder "./vagrant_share", "/vagrant", disabled:true
# 磁盘大小，需要首先使用命令vagrant plugin install vagrant-disksize安装插件
# 只能增大磁盘大小，并没有挂载磁盘分区，如果需要起作用，还需要进一步设置，此处其实无用
config.disksize.size = "70GB"
# 配置网络
config.vm.network "private_network", ip: "192.168.56.10"

# ==================================完整的文件===============================================
# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure("2") do |config|
  # The most common configuration options are documented and commented below.
  # For a complete reference, please see the online documentation at
  # https://docs.vagrantup.com.

  # Every Vagrant development environment requires a box. You can search for
  # boxes at https://vagrantcloud.com/search.
  config.vm.box = "centos/7"

  # Disable automatic box update checking. If you disable this, then
  # boxes will only be checked for updates when the user runs
  # `vagrant box outdated`. This is not recommended.
  # config.vm.box_check_update = false

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  # NOTE: This will enable public access to the opened port
  # config.vm.network "forwarded_port", guest: 80, host: 8080

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine and only allow access
  # via 127.0.0.1 to disable public access
  # config.vm.network "forwarded_port", guest: 80, host: 8080, host_ip: "127.0.0.1"

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  config.vm.network "private_network", ip: "192.168.56.10"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network "public_network"

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  # config.vm.synced_folder "../data", "/vagrant_data"
  config.vm.synced_folder "./vagrant_share", "/vagrant", disabled:true

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  # Example for VirtualBox:
  #
  # config.vm.provider "virtualbox" do |vb|
  #   # Display the VirtualBox GUI when booting the machine
  #   vb.gui = true
  #
  #   # Customize the amount of memory on the VM:
  #   vb.memory = "1024"
  # end
  #
  # View the documentation for the provider you are using for more
  # information on available options.

  # Enable provisioning with a shell script. Additional provisioners such as
  # Puppet, Chef, Ansible, Salt, and Docker are also available. Please see the
  # documentation for more information about their specific syntax and use.
  # config.vm.provision "shell", inline: <<-SHELL
  #   apt-get update
  #   apt-get install -y apache2
  # SHELL
  Encoding.default_external = 'UTF-8'
  config.disksize.size = "70GB"
end
# ==========================================================================================

# ！不推荐！安装并启动（比较慢）
vagrant up

# ================================vagrant up 的替代方案=======================================
# 执行vagrant up命令时控制台会打印下载box的网址，自己去该网址下载
# 此处下载地址为 H:\virtualBox_boxes/CentOS-7-x86_64-Vagrant-2004_01.VirtualBox.box
# 加入box
vagrant box add centos/7 H:\virtualBox_boxes/CentOS-7-x86_64-Vagrant-2004_01.VirtualBox.box
# 开机
vagrant up
# ===========================================================================================

# ssh连接
vagrant ssh
```

vagrant常用命令

```shell
# 开机
vagrant up
# 关机
vagrant halt
# ssh连接
vagrant ssh
# 查看状态
vagrant status
# 删除虚拟机
vagrant destroy
# 一些信息
root用户密码为vagrant
```



### 安装docker

```shell
# ssh连接到虚拟机
# 安装
$ sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine

$ sudo yum install -y yum-utils \
  device-mapper-persistent-data \
  lvm2

$ sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo

# 最新版本
# $ sudo yum install docker-ce docker-ce-cli containerd.io
# 指定版本
$ sudo yum install docker-ce-19.03.2 docker-ce-cli-19.03.2 containerd.io

# 启动
$ sudo systemctl start docker

# 查看版本
$ docker -v

# 查看镜像
$ sudo docker images

# 开机自启动
$ sudo systemctl enable docker

# 镜像加速
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://f45ro9s9.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

docker常用命令

```shell
# 查看正在运行的容器
docker ps

# 停止容器
docker stop 容器id

# 删除容器
docker rm 容器id

# 删除镜像
docker rmi 镜像id
```



### 安装mysql

```shell
# 下载镜像
sudo docker pull mysql:5.7

# --name指定容器名字 -v目录挂载 -p指定端口映射 -e设置mysql参数
sudo docker run -p 3306:3306 --name mysql \
-v /mydata/mysql/log:/var/log/mysql \
-v /mydata/mysql/data:/var/lib/mysql \
-v /mydata/mysql/conf:/etc/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.7

# 进入/退出正在运行的容器
docker exec -it mysql bin/bash
exit

# 配置mysql
# 打开文件
vi /mydata/mysql/conf/my.conf

# 复制以下内容到文件
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8
[mysqld]
init_connect='SET collation_connection = utf8_unicode_ci'
init_connect='SET NAMES utf8'
character-set-server=utf8
collation-server=utf8_unicode_ci
skip-character-set-client-handshake
skip-name-resolve

# 重启mysql
docker restart mysql
```



### 安装redis

```shell
# 下载镜像
docker pull redis:5.0.10

# 首先创建配置文件，否则为设为目录
mkdir -p /mydata/redis/conf
touch /mydata/redis/conf/redis.conf

docker run -p 6379:6379 --name redis \
-v /mydata/redis/data:/data \
-v /mydata/redis/conf/redis.conf:/etc/redis/redis.conf \
-d redis:5.0.10 redis-server /etc/redis/redis.conf

# 进入redis容器
docker exec -it redis redis-cli

# 持久化
vim /mydata/redis/conf/redis.conf
# 插入下面内容
appendonly yes

# 重启
docker restart redis
```



### 安装maven

安装

- 下载并解压
- bin目录添加到path环境变量

配置

修改maven的conf目录下的settings.xml文件，修改如下。

```xml
<!-- 配置仓库地址 -->
<localRepository>H:\maven\apache-maven-3.6.1\my_repository_hub</localRepository>

<!-- 配置阿里云镜像 -->
<mirror>
    <id>nexus-aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Nexus aliyun</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public</url> 
</mirror>

<!-- 配置jdk1.8编译项目 -->
<profile>
    <id>jdk-1.8</id>
    <activation>
        <activeByDefault>true</activeByDefault>
        <jdk>1.8</jdk>
    </activation>
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
    </properties>
</profile>
```



### IDEA配置

#### 配置maven

config -> settings -> Build, Execution, Deployment -> Maven

修改maven home directory、User settings file、Local repository。

![idea配置maven](readme_pics/idea配置maven.png)

#### 安装插件

lombok、MyBatisx



### VS Code 配置

安装

略。

安装插件

- Auto Close Tag
- Auto Rename Tag
- Chinese (Simplified) Language Pack for Visual Studio Code
- ESLint
- HTML CSS Support
- HTML Snippets
- JavaScript (ES6) code snippets
- Live Server
- open in browser
- Vetur

### 配置git

略。

## 搭建项目框架

### 创建初始项目

- 使用github或者码云创建项目

- 使用idea从远程导入项目

  file -> new -> Project from Version Control -> git，输入仓库地址到URL，点击clone。

- 逐个创建微服务

  - 右键项目 -> new -> Module -> 左侧选择Spring Initializr -> next -> 填入Group、Artifact、Description，Java选择8 -> next -> 勾选web/spring web、spring cloud routing/openfeign -> next -> finish
  - 每个都要导入web、openfeign
  - 包名（Group）为com.atguigu.gulimall.xxx(product/order/ware/coupon/member)
  - 模块名（Artifact）为gulimall-xxx(product/order/ware/coupon/member)
  
- 用一个pom聚合所有微服务

  从任意一个微服务中复制一个pom到.idea同级目录，修改一下

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>com.atguigu.gulimall</groupId>
      <artifactId>gulimall</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <name>gulimall</name>
      <description>聚合服务</description>
      <packaging>pom</packaging>
  
      <modules>
          <module>gulimall-coupon</module>
          <module>gulimall-member</module>
          <module>gulimall-order</module>
          <module>gulimall-product</module>
          <module>gulimall-ware</module>
      </modules>
  
  </project>
  ```

- 修改gitignore文件

  ```.gitignore
  target/
  pom.xml.tag
  pom.xml.releaseBackup
  pom.xml.versionsBackup
  pom.xml.next
  release.properties
  dependency-reduced-pom.xml
  buildNumber.properties
  .mvn/timing.properties
  # https://github.com/takari/maven-wrapper#usage-without-binary-jar
  .mvn/wrapper/maven-wrapper.jar
  
  **/mvnw
  **/mvnw.cmd
  
  **/.mvn
  **/target/
  
  .idea
  
  **/.gitignore
  ```

  

### 配置数据库

```shell
# 进入虚拟机，设置redis和mysql自动启动
sudo docker update redis --restart=always
sudo docker update mysql --restart=always
```

- 使用MySQL Workbench 8.0 CE或者其他数据库连接软件连接数据库

  ![数据库配置maven](readme_pics\数据库配置maven.png)

- 创建数据库，编码方式使用utf8mb4，名字依次为

  - gulimall_oms（对应gulimall-order）
  - gulimall_pms（对应gulimall-product）
  - gulimall_sms（对应gulimall-coupon）
  - gulimall_ums（对应gulimall-member）
  - gulimall_wms（对应gulimall-ware）

- 使用sql文件导入表和数据

  复制sql文件，执行。

### 搭建后台

使用人人开源中的[renren-fast](https://gitee.com/renrenio/renren-fast)作为脚手架。

右下角提示时，允许自动配置。

// todo

### 搭建前端

使用人人开源中的[renren-fast-vue](https://gitee.com/renrenio/renren-fast-vue)作为脚手架

配置node

使用vscode打开项目

运行

```shell
# 下载相关依赖
npm install
# 出错解决方案
npm install报错：chromedriver@2.27.2 install: node install.js
解决：
如果执行过npm install，先删除 node_modules 文件夹，不然运行的时候可能会报错
执行下面的命令
npm install chromedriver --chromedriver_cdnurl=http://cdn.npm.taobao.org/dist/chromedriver
再执行 npm install 即可正常下载

# 运行
npm run dev
```



## 生成代码

使用人人开源的[renren-generator](https://gitee.com/renrenio/renren-generator)项目生成相关代码，以gulimall-product为例。

- 克隆项目

- 复制到工程中

- 添加到总的pom中的modules

  ```xml
  <modules>
      ...
      <module>renren-generator</module>
  </modules>
  ```

- 修改application.yml中的数据库信息

  ```yml
  #MySQL配置
  driverClassName: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://192.168.56.10:3306/gulimall_pms?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
  username: root
  password: root
  ```

- 修改generator.properties

  ```properties
  mainPath=com.atguigu
  #包名
  package=com.atguigu.gulimall
  moduleName=product
  #作者
  author=stronger
  #Email
  email=123456@gmail.com
  #表前缀(类名不会包含表前缀)
  tablePrefix=pms_
  ```

- 修改Controller生成规则，将RequiresPermissions相关的代码注释掉

  修改renren-generator\src\main\resources\template\Controller.java.vm文件。

  ```java
  package ${package}.${moduleName}.controller;
  
  import java.util.Arrays;
  import java.util.Map;
  
  // import org.apache.shiro.authz.annotation.RequiresPermissions;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.web.bind.annotation.PathVariable;
  import org.springframework.web.bind.annotation.RequestBody;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RequestParam;
  import org.springframework.web.bind.annotation.RestController;
  
  import ${package}.${moduleName}.entity.${className}Entity;
  import ${package}.${moduleName}.service.${className}Service;
  import ${mainPath}.common.utils.PageUtils;
  import ${mainPath}.common.utils.R;
  
  
  
  /**
   * ${comments}
   *
   * @author ${author}
   * @email ${email}
   * @date ${datetime}
   */
  @RestController
  @RequestMapping("${moduleName}/${pathName}")
  public class ${className}Controller {
      @Autowired
      private ${className}Service ${classname}Service;
  
      /**
       * 列表
       */
      @RequestMapping("/list")
      // @RequiresPermissions("${moduleName}:${pathName}:list")
      public R list(@RequestParam Map<String, Object> params){
          PageUtils page = ${classname}Service.queryPage(params);
  
          return R.ok().put("page", page);
      }
  
  
      /**
       * 信息
       */
      @RequestMapping("/info/{${pk.attrname}}")
      // @RequiresPermissions("${moduleName}:${pathName}:info")
      public R info(@PathVariable("${pk.attrname}") ${pk.attrType} ${pk.attrname}){
  		${className}Entity ${classname} = ${classname}Service.getById(${pk.attrname});
  
          return R.ok().put("${classname}", ${classname});
      }
  
      /**
       * 保存
       */
      @RequestMapping("/save")
      // @RequiresPermissions("${moduleName}:${pathName}:save")
      public R save(@RequestBody ${className}Entity ${classname}){
  		${classname}Service.save(${classname});
  
          return R.ok();
      }
  
      /**
       * 修改
       */
      @RequestMapping("/update")
      // @RequiresPermissions("${moduleName}:${pathName}:update")
      public R update(@RequestBody ${className}Entity ${classname}){
  		${classname}Service.updateById(${classname});
  
          return R.ok();
      }
  
      /**
       * 删除
       */
      @RequestMapping("/delete")
      // @RequiresPermissions("${moduleName}:${pathName}:delete")
      public R delete(@RequestBody ${pk.attrType}[] ${pk.attrname}s){
  		${classname}Service.removeByIds(Arrays.asList(${pk.attrname}s));
  
          return R.ok();
      }
  
  }
  ```

- 启动项目生成代码，将代码下载。

  启动后打开[网页](http://localhost:80/)，点击左侧renren-fast，将每页设为50条（大一些可以一次下载完），全选，生成代码。

  

## 整合代码

删除main/resource中的src

复制main到gulimall-product中的main

新建maven模块名字为gulimall-common（最后一步会把-去掉变为gulimallcommon，手动改一下即可），用来存放其他模块的共同依赖。

在总的pom中添加gulimall-common

```xml
<modules>
    ...
    <module>gulimall-common</module>
</modules>
```

在gulimall-common项目的pom中添加描述、依赖

```xml
<description>每一个微服务公共的依赖，如bean，工具类等。</description>

    <dependencies>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.8</version>
        </dependency>
        <!-- httpcomponents -->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpcore</artifactId>
            <version>4.4.12</version>
        </dependency>
        <!-- StringUtils -->
        <dependency>
            <groupId>commons-lang</groupId>
            <artifactId>commons-lang</artifactId>
            <version>2.6</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.5</version>
            <scope>provided</scope>
        </dependency>
        <!-- mabatis plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.2.0</version>
        </dependency>
    </dependencies>
```

将renren-fast中的一些类复制到gulimall-common中

- 将renren-fast模块java/io/common/exception中的RRException和java/io/common/utils中的Constant、PageUtil、Query、R复制到gulimall-common中的java/com/atguigu/common/utils中（需要自己创建包）
- 将renren-fast模块java/io/common/xss中的HTMLFilter和SQLFilter复制到gulimall-common中的java/com/atguigu/common/xss中

在product的pom中添加依赖

```xml
<dependency>
    <groupId>com.atguigu.gulimall</groupId>
    <artifactId>gulimall-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

修复gulimall-product中的错误。如果缺少共有的依赖，就上网搜依赖的maven，并添加到gulimall-common中；如果缺少文件，就到renren-fast项目中寻找，也添加到gulimall-common中。

## 整合mybatis plus

1. 在gulimall-common的pom中导入mabatis plus依赖（前边已经添加）

   ```xml
   <!-- mabatis plus -->
   <dependency>
       <groupId>com.baomidou</groupId>
       <artifactId>mybatis-plus-boot-starter</artifactId>
       <version>3.2.0</version>
   </dependency>
   ```

2. 配置
   1. 配置数据源

      1. 在gulimall-common的pom中导入数据库驱动依赖

         ```xml
         <!-- 导入mysql驱动 -->
         <dependency>
             <groupId>mysql</groupId>
             <artifactId>mysql-connector-java</artifactId>
             <version>8.0.17</version>
         </dependency>
         ```

      2. 在gulimall-product的application.yml（没有该文件则新建）中配置数据源相关信息

         ```yml
         spring:
           datasource:
             username: root
             password: root
             url: jdbc:mysql://192.168.56.10:3306/gulimall_pms?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
             driver-class-name: com.mysql.cj.jdbc.Driver
         ```

   2. 配置mybatis plus

      1. 在GulimallProductApplication.java中添加@mappperScan注解

         ```java
         @MapperScan("com.atguigu.gulimall.product.dao")
         @SpringBootApplication
         public class GulimallProductApplication {
         
             public static void main(String[] args) {
                 SpringApplication.run(GulimallProductApplication.class, args);
             }
         
         }
         ```

      2. 在gulimall-product的application.yml中配置sql映射文件位置等信息

         ```yml
         mybatis-plus:
           mapper-locations: classpath:/mapper/**/*.xml
           global-config:
             db-config:
               id-type: auto
         ```

         

## 其他模块

### 一般流程

以gulimall-coupon为例。

修改renren-generator模块的generator.properties，修改包名和表前缀

```properties
#包名
moduleName=coupon

#表前缀(类名不会包含表前缀)
tablePrefix=sms_
```

修改renren-generator模块的application.yml，修改数据库名称

```yml
url: jdbc:mysql://192.168.56.10:3306/gulimall_sms?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
```

启动renren-generator模块，生成代码

删除main/resource中的src

复制main到gulimall-coupon中的main

在gulimall-coupon的pom添加对gulimall-common的依赖

```xml
<dependency>
    <groupId>com.atguigu.gulimall</groupId>
    <artifactId>gulimall-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

配置gulimall-coupon的application.yml（没有该文件则新建），和product一样，唯一不同是数据库名称

```yml
spring:
  datasource:
    username: root
    password: root
    url: jdbc:mysql://192.168.56.10:3306/gulimall_sms?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  mapper-locations: classpath:/mapper/**/*.xml
  global-config:
    db-config:
      id-type: auto

```

// 添加@mappperScan注解（没有添加为什么也可以）

运行模块并测试，在浏览器中打开该网页调用请求（coupon/coupon/list为模块名/表名/请求名）

http://localhost:8080/coupon/coupon/list

返回结果为

```json
{"msg":"success","code":0,"page":{"totalCount":0,"pageSize":10,"totalPage":0,"currPage":1,"list":[]}}
```

这样就配置完成了。



### 配置端口

在5个模块的application.yml文件中配置服务器端口

```yml
server:
  port: 7000
```

端口号依次为：

- coupon设置为7000
- member设置为8000
- order设置为9000
- product设置为10000
- ware设置为11000











