# 仓储服务 项目说明

前端界面要使用Edge浏览器，(不要使用Chrome,会出现未知错误)

(redis密码: 123456)

### 服务器
- 服务器1号 192.168.200.130 (FinalShell名: CentOS7 , 密码: root) 
  - jdk  : 运行java项目
  - git  : 版本控制工具
  - maven: 项目构建工具
  - jar  : SpringBoot项目打成jar包基于内置的tomcat运行
  - MySql: 主从复制结构中的从库slave(密码: root)
  - Redis: 缓存中间件 (redis密码: 123456)
  
- 服务器2号 192.168.200.128 (FinalShell名: newCentOS, 密码: root) 
  - Nginx: 部署前端项目、配置反向代理
  - MySql: 主从复制结构中的主库master(密码: root)
  
