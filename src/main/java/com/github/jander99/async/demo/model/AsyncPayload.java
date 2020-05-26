package com.github.jander99.async.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsyncPayload {

    @JsonProperty("numIterations")
    private int numIterations;

    @JsonProperty("fastService1Type")
    private String fastService1Type;

    @JsonProperty("fastService2Type")
    private String fastService2Type;
}
