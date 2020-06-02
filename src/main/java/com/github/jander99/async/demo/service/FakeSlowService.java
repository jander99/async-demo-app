package com.github.jander99.async.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
@Slf4j
public class FakeSlowService {

    private final static Long SLEEP_CEILING = 300L;

    private final RestTemplate restTemplate;

    private final String goserverLocation;

    public FakeSlowService(@Qualifier("slowTemplate") RestTemplate restTemplate,
                           @Value("${goserver.path}") String goserverLocation) {
        this.restTemplate = restTemplate;
        this.goserverLocation = goserverLocation;
    }


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
        StopWatch watch = new StopWatch("RestTemplate");
        watch.start();
        try {
            String url = String.format(goserverLocation + "/?t=%s",millis);
            restTemplate.getForObject(url,String.class);
        } catch (RestClientException rce) {
            log.error("Slow Service derped: {}", rce.getMessage());
        }
        watch.stop();
    }
}
