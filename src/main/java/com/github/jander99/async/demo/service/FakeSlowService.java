package com.github.jander99.async.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
@Slf4j
public class FakeSlowService {

    private final static Long SLEEP_CEILING = 300L;

    private final RestTemplate restTemplate;

    public FakeSlowService(@Qualifier("slowTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private final int port = 8000;

    public long callService(int primaryMinLatency, int primaryMaxLatency, int failoverMinLatency, int failoverMaxLatency) {

        long randomPrimaryLatency = new Random().nextInt(primaryMaxLatency - primaryMinLatency) + primaryMinLatency;
        long randomFailoverLatency = new Random().nextInt(failoverMaxLatency - failoverMinLatency) + failoverMinLatency;

        if (randomPrimaryLatency >= SLEEP_CEILING) {
            externalLocalCall(SLEEP_CEILING);
            externalLocalCall(randomFailoverLatency);
            return SLEEP_CEILING + randomFailoverLatency;
        } else {
            externalLocalCall(randomPrimaryLatency);
            return randomPrimaryLatency;
        }
    }

    private void externalLocalCall(long millis) {
        try {
            restTemplate.getForObject(String.format("http://localhost:%s/?t=%s",port,millis),String.class);
        } catch (RestClientException rce) {
            log.error("Slow Service derped: {}", rce.getMessage());
        }
    }
}
