package com.mikaelfrancoeur.blogjsondeserialization.controller.jsonpath;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.mikaelfrancoeur.blogjsondeserialization.CloudTrailLogsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class JsonPathDeclarativeController {

    private final CloudTrailLogsService service;

    @PostMapping(value = "cloudtrail/logs/jsonpath-declarative")
    void consumeLog(CloudTraiLogDTO cloudTrailLogDTO) {
        service.consumeLog(cloudTrailLogDTO);
    }

    private record CloudTraiLogDTO(
            @JsonPath("$.Records[0].userIdentity.arn")
            String userArn,
            @JsonPath("$.Records[0].eventTime")
            OffsetDateTime eventTime,
            @JsonPath("$.Records[0].requestParameters.instancesSet.items[*].instanceId")
            List<String> instanceIds
    ) {
    }

}

