# 🍽️ WeChoice 🌟

## 简介

🍴 "WeChoice"通过用户偏好设置和浏览历史，提供**个性化**的餐饮推荐，同时融入**健康知识分享和社交互动**功能的小程序。

## 预览

![项目截图](https://www.helloimg.com/i/2024/11/26/6745d1f7d3b1a.png)
![项目截图](https://www.helloimg.com/i/2024/11/26/6745c91a44461.png)
![项目截图](https://www.helloimg.com/i/2024/11/26/6745c91a9b162.png)
![项目截图](https://www.helloimg.com/i/2024/11/26/6745c91b2d0bc.png)

## 功能

- 🔍 **美食搜索**：用户可以搜索各种美食和餐厅。
- 📍 **地理位置**：根据用户位置推荐附近的美食。
- 📖 **美食攻略**：提供详细的美食攻略和食谱。
- 🤖 **智能推荐**：基于用户喜好和行为，智能推荐美食。
- 🍴 **美食社区**：用户可以分享美食体验和照片，与其他用户互动。
- 💬 **IM 聊天**：内置即时通讯功能，用户可以实时交流美食心得。

## 技术选型

| 技术           | 说明            | 官网                                                                                                                       |
| -------------- | --------------- | -------------------------------------------------------------------------------------------------------------------------- |
| Spring Boot    | 后端框架        | [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)                                           |
| MyBatis-Plus   | 数据持久层框架  | [https://mybatis.plus/](https://mybatis.plus/)                                                                             |
| Redis          | 键值存储数据库  | [https://redis.io/](https://redis.io/)                                                                                     |
| MySQL          | 关系型数据库    | [https://www.mysql.com/](https://www.mysql.com/)                                                                           |
| JWT            | JSON Web Tokens | [https://jwt.io/](https://jwt.io/)                                                                                         |
| Vue 3          | 前端框架        | [https://v3.vuejs.org/](https://v3.vuejs.org/)                                                                             |
| Tailwind CSS   | 工具类样式库    | [https://tailwindcss.com/](https://tailwindcss.com/)                                                                       |
| vue-echarts    | Echarts 组件库  | [https://vue-echarts.github.io/](https://vue-echarts.github.io/)                                                           |
| pnpm           | 包管理工具      | [https://pnpm.io/](https://pnpm.io/)                                                                                       |
| vue-router     | Vue 路由管理器  | [https://router.vuejs.org/](https://router.vuejs.org/)                                                                     |
| 原生微信小程序 | 移动端应用开发  | [https://developers.weixin.qq.com/miniprogram/dev/framework/](https://developers.weixin.qq.com/miniprogram/dev/framework/) |

## 安装和部署

1. 🔧 **安装依赖**：
   ```bash
   # 后台管理前端使用pnpm安装依赖
   pnpm install
   pnpm dev
   ```
   ```bash
   # 微信小程序使用npm安装依赖
   npm install
   ```
2. 🏗️ **本地开发**：

   ```bash
   # 准备数据库
   - 执行项目根目录下的SQL文件以创建和初始化数据库。
   - 确保Redis服务已启动并运行。

   # 配置后端应用
   - 编辑`application.prod.yml`配置文件。
   - 填写必要的配置信息，包括对象存储服务的访问配置、高德API的Key、DeepSeek大模型的Key、微信开放平台的AppID和Secret。
   ```

   [腾讯对象存储](https://cloud.tencent.com/product/cos) |
   [高德地图](https://lbs.amap.com/) |
   [DeepSeek 大模型](https://platform.deepseek.com/usage) |
   [微信开发](https://developers.weixin.qq.com/miniprogram/dev/framework/)

## 仓库链接

- 🌐 **中台仓库**：[中台仓库链接](https://gitee.com/lzt_luo/choose-admin-web)
- 📱 **微信小程序仓库**：[微信小程序仓库链接](https://gitee.com/lzt_luo/choose-wexinapp)
- 🖥️ **后端仓库**：[后端仓库链接](https://gitee.com/lzt_luo/choose)

## 🌟

如果你喜欢这个项目，请给这个项目点个 Star 吧！
🎉 感谢你的关注和支持！

---
