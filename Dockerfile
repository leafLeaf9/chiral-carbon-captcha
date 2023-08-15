FROM ubuntu:latest
MAINTAINER woxigousade

ENV LANG=en_US.UTF-8 LANGUAGE=en_US.UTF-8
ENV LC_ALL=en_US.UTF-8
ENV TZ=Asia/Shanghai

RUN apt update
#添加中文字体
#RUN apt-get install -y fontconfig fonts-wqy-zenhei fonts-wqy-microhei && fc-cache -fv

#安装jdk11
RUN apt-get -y install openjdk-11-jdk

COPY target/chiral-carbon-captcha-0.0.1.jar /app.jar

EXPOSE 9999

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=prod","-Dfile.encoding=UTF-8","-jar","/app.jar"]

#FROM maven:3.8.1-jdk-11 AS builder
#
## 将项目源码复制到容器中的/app目录
#COPY . /app
#
## 在/app目录下构建项目
#WORKDIR /app
#RUN mvn clean -B package
#
## 第二阶段：基于ubuntu运行jar包
#FROM ubuntu:20.04
#
#MAINTAINER woxigousade
#
#ENV LANG=en_US.UTF-8 LANGUAGE=en_US.UTF-8
#ENV LC_ALL=en_US.UTF-8
#ENV TZ=Asia/Shanghai
#
#
## 设置工作目录为/app
#WORKDIR /app
#
## 从第一阶段中复制jar文件到/app目录下
#COPY --from=builder /app/target/*.jar app.jar
#
## 安装openjdk-11-jre软件包
#RUN apt-get update && apt-get install -y openjdk-11-jre
#
#EXPOSE 9999
#
## 使用java -jar命令来运行jar文件
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=prod","-Dfile.encoding=UTF-8","-jar","/app.jar"]