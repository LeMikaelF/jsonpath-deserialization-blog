package com.mikaelfrancoeur.blogjsondeserialization.controller.jackson;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikaelfrancoeur.blogjsondeserialization.CloudTrailLogsService;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PlainController {

    private final CloudtrailLogMapper mapper = new CloudtrailLogMapper();
    private final CloudTrailLogsService service;

    @PostMapping("cloudtrail/logs/jackson")
    void consumeLog(@RequestBody CloudTrailLogDTO cloudTrailLogDTO) {
        service.consumeLog(mapper.map(cloudTrailLogDTO));
    }

    private record CloudTrailLogDTO(@JsonProperty("Records") List<CloudtrailRecord> records) { }

    private record CloudtrailRecord(OffsetDateTime eventTime, RequestParameters requestParameters, UserIdentity userIdentity) { }

    private record RequestParameters(InstanceSet instancesSet) { }

    private record UserIdentity(String arn) { }

    private record InstanceSet(List<Item> items) { }

    private record Item(String instanceId) { }

    private static class CloudtrailLogMapper {
        private CloudTraiLog map(CloudTrailLogDTO cloudtrailLogDTO) {
            CloudtrailRecord firstRecord = cloudtrailLogDTO.records().get(0);

            return CloudTraiLog.builder()
                    .userArn(firstRecord.userIdentity().arn())
                    .eventTime(firstRecord.eventTime())
                    .instanceIds(firstRecord.requestParameters()
                            .instancesSet()
                            .items()
                            .stream()
                            .map(Item::instanceId)
                            .toList())
                    .build();
        }
    }

    @Builder
    private record CloudTraiLog(
            String userArn,
            OffsetDateTime eventTime,
            List<String> instanceIds
    ) {
    }
}
