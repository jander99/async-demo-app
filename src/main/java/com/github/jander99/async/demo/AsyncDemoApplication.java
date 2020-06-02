package com.github.jander99.async.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

@SpringBootApplication(proxyBeanMethods = false)
@EnableAsync
public class AsyncDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncDemoApplication.class, args);
    }

    @Bean // The default Task Scheduler for @Async ops
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean("FastServiceThreadPool")
    public Executor fastServiceThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(500);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("FastServiceThreadPool-");
        executor.initialize();
        return executor;
    }

    @Bean("slowTemplate")
    public RestTemplate slowRestTemplate() {
        return generateRestTemplate(500,500);
    }

    @Bean("fastServiceTemplate")
    public RestTemplate fastRestTemplate() {
        return generateRestTemplate(500, 500);
    }

    @Bean("asyncFastServiceTemplate")
    public RestTemplate asyncFastRestTemplate() {
        return generateRestTemplate(500, 500);
    }

    private RestTemplate generateRestTemplate(int readTimeout, int connectTimeout) {
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
        factory.setReadTimeout(readTimeout);
        factory.setConnectTimeout(connectTimeout);
        return new RestTemplate(factory);
    }

}
