# chiral-carbon-captcha

chiral-carbon-captcha api.
手性碳原子验证码API，启动后可以通过http请求获取验证码，具体接口文档可以在swagger页面查看。

![手性碳原子验证码示例图片](https://user-images.githubusercontent.com/53334104/207500151-c183e106-31f5-4afc-9276-1ca271477b73.png)

# Installation and Getting Started

> environment requires: JDK11 libstdc++.so.6: version `CXXABI_1.3.8'

skija相关依赖需要JDK11，使用JDK8运行会导致skija相关类出现编译版本高于运行版本的错误。

jetbrains skija依赖的动态库在ubuntu一般都有，推荐使用ubuntu。centos7的gcc版本过低会报错。

Windows环境如果获取不到静态资源，可以尝试修改获取资源方法。

运行项目

```
以ubuntu为例
1. 从releases页面获取jar包
2. sudo apt-get install openjdk-11-jdk
3. java -vsersion查看是否安装成功
4. nohup java -Dspring.profiles.active=prod -jar chiral-carbon-captcha-0.0.1.jar &
```

手动构建并运行

```
git clone https://github.com/woxigousade/chiral-carbon-captcha.git
mvn clean -DskipTests=true package
java -Dspring.profiles.active=prod -jar chiral-carbon-captcha-0.0.1.jar
```

后台运行

```
nohup java -Dspring.profiles.active=prod -jar chiral-carbon-captcha-0.0.1.jar &
```

接口文档

```
http://localhost:9999/swagger-ui/index.html#/chiral-carbon-captcha-controller/getChiralCarbonCaptchaUsingPOST
```

# Extra
生成图片用到的分子文件来源:
https://ftp.ncbi.nlm.nih.gov/pubchem/Compound/CURRENT-Full/SDF/

默认添加了1W+个.mol文件，如果还想自行添加，可以在上述地址下载sdf文件，
并使用com.gousade.captcha.SdfUtilsTests#splitSDFFile工具进行文件拆分，
com.gousade.captcha.filterChiralFiles获取包含手性碳原子的文件，
放入src/main/resources/static/captcha/carbon/mol目录即可。

# Reference
https://github.com/cinit/NeoAuthBotPlugin
