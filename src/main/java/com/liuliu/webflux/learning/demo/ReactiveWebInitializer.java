package com.liuliu.webflux.learning.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.server.adapter.AbstractReactiveWebInitializer;

public class ReactiveWebInitializer extends AbstractReactiveWebInitializer {

	@Override
	protected Class<?>[] getConfigClasses() {
		return new Class[] { DemoLearningApplication.class };
	}

	@Override
	protected ApplicationContext createApplicationContext() {
		SpringApplication springApplication = new SpringApplication(getConfigClasses());
		return springApplication.run();
	}
}
