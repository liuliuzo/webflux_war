package com.liuliu.webflux.learning.demo;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class HelloController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HelloController.class);

    @RequestMapping("common")
    public String commonHandle() {
        String uuid = UUID.randomUUID().toString();
        log.info("common-start"+":" + uuid);
        String result = doThing("common handler"+":" + uuid);
        log.info("common-end"+":" + uuid);
        return result;
    }

    @RequestMapping("mono")
    public Mono<String> monoHandle() {
        String uuid = UUID.randomUUID().toString();
        log.info("mono-start"+":" + uuid);
        Mono<String> mono = Mono.fromSupplier(() -> doThing("mono handle"+":" + uuid));
        log.info("mono-end"+":" + uuid);
        return mono;
    }
    
    @RequestMapping("mono01")
    public Mono<String> mono01() {
        return Mono.just("hello webflux");
    }

    @RequestMapping("mono02")
    public Mono<Object> mono02() {
        return Mono.create(monoSink -> {
            log.info("create Mono {}", monoSink);
            monoSink.success("hello webflux");
        }).doOnSubscribe(subscription -> {
            log.info("doOnSubscribe {}", subscription);
        }).doOnNext(o -> {
            log.info("doOnNext {}", o);
        });
    }
     
    private String doThing(String msg) {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
