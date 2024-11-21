## 启动项目
###  WebfluxTestApplication类的main方法

## 接口访问
###  controller访问
http://127.0.0.1:8080/webflux-test/mono

http://127.0.0.1:8080/webflux-test/flux

###  router访问
http://127.0.0.1:8080/webflux-test/router/mono

http://127.0.0.1:8080/webflux-test/router/flux


###  router匹配访问
curl --location --request POST 'http://127.0.0.1:8080/webflux-test/orders/' \
--header 'accept: application/json' \
--header 'Content-Type: application/json' \
--data-raw '{"id":6}'

curl --location --request GET 'http://127.0.0.1:8080/webflux-test/orders/6'

###  router匹配访问  异常拦截
curl --location --request GET 'http://127.0.0.1:8080/webflux-test/orders/101'

curl --location --request GET 'http://127.0.0.1:8080/webflux-test/orders/5'


For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.3.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.3.1/maven-plugin/reference/html/#build-image)



