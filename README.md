# chiral-carbon-captcha
chiral-carbon-captcha api.
手性碳原子验证码API，启动后可以通过http请求获取验证码，具体接口文档可以在swagger页面查看。

![手性碳原子验证码示例图片](https://user-images.githubusercontent.com/53334104/207500151-c183e106-31f5-4afc-9276-1ca271477b73.png)

# Installation and Getting Started

>environment requires: JDK8 libstdc++.so.6: version `CXXABI_1.3.8'

默认用JDK11编译，但语法只用到JDK8。jetbrains skija依赖的动态库在ubuntu一般都有，centos7的gcc版本过低会报错。

克隆、编译、运行项目
```
git clone https://github.com/woxigousade/chiral-carbon-captcha.git
mvn clean -DskipTests=true package
java -Dspring.profiles.active=prod -jar chiral-carbon-captcha-0.0.1.jar
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
