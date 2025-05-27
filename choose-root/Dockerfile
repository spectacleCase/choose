# 使用官方OpenJDK镜像作为基础镜像
FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /choose

# 将本地的JAR包添加到镜像中
ADD target/choose-app.jar /app/choose-app.jar

# 暴露应用程序运行所需的端口
EXPOSE 12150

# 定义启动命令
ENTRYPOINT ["java", "-jar", "/app/choose-app.jar"]