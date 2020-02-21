FROM openjdk8-alpine-arthas:1.0
ENV TZ Asia/Shanghai
WORKDIR $PRO_PATH
ADD web-demo $PRO_PATH


CMD ["java", "-jar", "web-demo.jar"]