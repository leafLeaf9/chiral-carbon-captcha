#FROM ubuntu-latest
#TODO 将skija依赖的动态库打包进镜像 /tmp/skija_0.93.1/libskija.so: /lib64/libstdc++.so.6: version `CXXABI_1.3.8'
FROM openjdk:11
MAINTAINER woxigousade
VOLUME /tmp
ADD target/chiral-carbon-captcha-0.0.1.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app.jar"]