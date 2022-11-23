# 仓储服务 项目说明

前端界面要使用Edge浏览器，(不要使用Chrome,会出现未知错误)

### 服务器
- 服务器①号 192.168.200.130 (FinalShell名: CentOS7)
  - Nginx: 部署前端项目、配置反向代理
  - MySql: 主从复制结构中的主库
  
- 服务器②号 192.168.200.132 (FinalShell名: newCentOS)
  - jdk  : 运行java项目
  - git  : 版本控制工具
  - maven: 项目构建工具
  - jar  : SpringBoot项目打成jar包基于内置的tomcat运行
  - MySql: 主从复制结构中的从库
  
- 服务器③号 
  - Redis: 缓存中间件