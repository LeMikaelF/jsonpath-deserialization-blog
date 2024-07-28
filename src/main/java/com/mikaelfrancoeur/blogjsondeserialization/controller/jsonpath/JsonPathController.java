package com.mikaelfrancoeur.blogjsondeserialization.controller.jsonpath;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.mikaelfrancoeur.blogjsondeserialization.CloudTrailLogsService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class JsonPathController {

    private final CloudTrailLogsService service;

    @PostMapping(value = "cloudtrail/logs/jsonpath")
    void consumeLog(InputStream json) {
        service.consumeLog(new CloudTrailLogDTO(json));
    }

    @Getter
    private static class CloudTrailLogDTO {

        private static final com.jayway.jsonpath.JsonPath USER_ARN_JSON_PATH =
                com.jayway.jsonpath.JsonPath.compile("$.Records[0].userIdentity.arn");
        private static final com.jayway.jsonpath.JsonPath EVENT_TIME_JSON_PATH =
                com.jayway.jsonpath.JsonPath.compile("$.Records[0].eventTime");
        private static final com.jayway.jsonpath.JsonPath INSTANCE_IDS_JSON_PATH =
                com.jayway.jsonpath.JsonPath.compile("$.Records[0].requestParameters.instancesSet.items[*].instanceId");

        private final String userArn;
        private final OffsetDateTime eventTime;
        private final List<String> instanceIds;

        public CloudTrailLogDTO(InputStream json) {
            DocumentContext documentContext = JsonPath.parse(json);
            this.userArn = documentContext.read(USER_ARN_JSON_PATH);
            this.eventTime = OffsetDateTime.parse(documentContext.read(EVENT_TIME_JSON_PATH));
            this.instanceIds = documentContext.read(INSTANCE_IDS_JSON_PATH);
        }
    }
}
