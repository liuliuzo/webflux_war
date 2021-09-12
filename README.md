这篇文章很早就写了，才发现尽然显示不出来。

经过我的调研，结论是不建议使用war包部署，好在是有Workaround    ：（

github springboot issues/12455)

#Workaround

根据spring 官网描述

> **Servlet 3.1+ Container**

To deploy as a WAR to any Servlet 3.1+ container, you can extend and include [`AbstractReactiveWebInitializer`](https://docs.spring.io/spring-framework/docs/5.3.4/javadoc-api/org/springframework/web/server/adapter/AbstractReactiveWebInitializer.html) in the WAR. That class wraps an `HttpHandler` with `ServletHttpHandlerAdapter` and registers that as a `Servlet`.

所以尝试了下： [demo project](https://github.com/liuliuzo/webflux_war)

#加载springboot上下文

AbstractReactiveWebInitializer 的`onStartup(ServletContext servletContext)`是和容器的钩子，我们这里需要重写createApplicationContext来加载springboot的上下文。

```

public class ReactiveWebInitializer extends AbstractReactiveWebInitializer {

    @Override
    protected Class<?>[] getConfigClasses() {
        return new Class[] { StartBootApplication.class };
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        SpringApplication springApplication = new SpringApplication(getConfigClasses());
        return springApplication.run();
    }
}

```

## POM文件

```

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                        https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>com.liuliu.webflux.war</groupId>
    <artifactId>webflux-war</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>war</packaging>
    <name>webflux-war</name>
    <description>Demo project for Spring Boot webflux war</description>
    <properties>
    <java.version>1.8</java.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.1.1.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
            </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    <build>
        <finalName>webflux-war</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>
            <plugin>
                <artifactId>maven-war-plugin</artifactId>
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#测试结果

```
2021-03-14 21:09:11.565 [reactor-http-nio-2] DEBUG o.s.web.server.adapter.HttpWebHandlerAdapter - [a2c7e670] HTTP GET "/mono"
2021-03-14 21:09:11.567 [reactor-http-nio-2] DEBUG o.s.w.r.r.m.a.RequestMappingHandlerMapping - [a2c7e670] Mapped to public reactor.core.publisher.Mono<java.lang.String> com.liuliu.webflux.learning.demo.HelloController.monoHandle()
2021-03-14 21:09:11.568 [reactor-http-nio-2] INFO  com.liuliu.webflux.learning.demo.HelloController - mono-start:75c66ab5-3908-4e75-852b-2d7ae72def1e
2021-03-14 21:09:11.569 [reactor-http-nio-2] INFO  com.liuliu.webflux.learning.demo.HelloController - mono-end:75c66ab5-3908-4e75-852b-2d7ae72def1e
2021-03-14 21:09:11.569 [reactor-http-nio-2] DEBUG o.s.w.r.r.m.annotation.ResponseBodyResultHandler - Using 'text/html' given [text/html, application/xhtml+xml, image/avif, image/webp, image/apng, application/xml;q=0.9, application/signed-exchange;v=b3;q=0.9, */*;q=0.8] and supported [text/plain;charset=UTF-8, text/event-stream, text/plain;charset=UTF-8, */*]
2021-03-14 21:09:11.569 [reactor-http-nio-2] DEBUG o.s.w.r.r.m.annotation.ResponseBodyResultHandler - [a2c7e670] 0..1 [java.lang.String]
2021-03-14 21:09:21.572 [reactor-http-nio-2] DEBUG org.springframework.core.codec.CharSequenceEncoder - [a2c7e670] Writing "mono handle:75c66ab5-3908-4e75-852b-2d7ae72def1e"
2021-03-14 21:09:21.573 [reactor-http-nio-2] DEBUG reactor.netty.channel.ChannelOperationsHandler - [id: 0xa2c7e670, L:/[0:0:0:0:0:0:0:1]:8081 - R:/[0:0:0:0:0:0:0:1]:63993] Writing object DefaultHttpResponse(decodeResult: success, version: HTTP/1.1)
HTTP/1.1 200 OK
Content-Type: text/html;charset=UTF-8
Content-Length: 48
2021-03-14 21:09:21.581 [reactor-http-nio-2] DEBUG reactor.netty.channel.ChannelOperationsHandler - [id: 0xa2c7e670, L:/[0:0:0:0:0:0:0:1]:8081 - R:/[0:0:0:0:0:0:0:1]:63993] Writing object
2021-03-14 21:09:21.583 [reactor-http-nio-2] DEBUG o.s.web.server.adapter.HttpWebHandlerAdapter - [a2c7e670] Completed 200 OK
2021-03-14 21:09:21.585 [reactor-http-nio-2] DEBUG reactor.netty.http.server.HttpServerOperations - [id: 0xa2c7e670, L:/[0:0:0:0:0:0:0:1]:8081 - R:/[0:0:0:0:0:0:0:1]:63993] Last HTTP response frame
2021-03-14 21:09:21.586 [reactor-http-nio-2] DEBUG reactor.netty.channel.ChannelOperationsHandler - [id: 0xa2c7e670, L:/[0:0:0:0:0:0:0:1]:8081 - R:/[0:0:0:0:0:0:0:1]:63993] Writing object EmptyLastHttpContent
2021-03-14 21:09:21.588 [reactor-http-nio-2] DEBUG reactor.netty.http.server.HttpServerOperations - [id: 0xa2c7e670, L:/[0:0:0:0:0:0:0:1]:8081 - R:/[0:0:0:0:0:0:0:1]:63993] Decreasing pending responses, now 0
2021-03-14 21:09:21.588 [reactor-http-nio-2] DEBUG reactor.netty.channel.ChannelOperationsHandler - [id: 0xa2c7e670, L:/[0:0:0:0:0:0:0:1]:8081 - R:/[0:0:0:0:0:0:0:1]:63993] No ChannelOperation attached. Dropping: EmptyLastHttpContent
```

测试可用
