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

ENTRYPOINT ["java","-Dspring.profiles.active=prod","-Dfile.encoding=UTF-8","-jar","/app.jar"]