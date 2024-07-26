package com.mikaelfrancoeur.blogjsondeserialization.controller;

import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.mikaelfrancoeur.blogjsondeserialization.CloudTrailLogsService;
import com.mikaelfrancoeur.blogjsondeserialization.controller.jackson.PlainController;
import com.mikaelfrancoeur.blogjsondeserialization.controller.jsonpath.JsonPathController;

import lombok.SneakyThrows;

@WebMvcTest(controllers = {
        PlainController.class,
        JsonPathController.class
})
class ControllerTest implements WithAssertions {

    private MockMvc mockMvc;

    @MockBean
    private CloudTrailLogsService vanillaService;

    @BeforeEach
    void beforeEach(@Autowired WebApplicationContext wac) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build();
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = { "jackson", "jsonpath" })
    void deserialize(String path) {
        mockMvc.perform(post("/cloudtrail/logs/" + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cloudTrailLogsPayload()))
                .andExpect(status().isOk());

        verify(vanillaService).consumeLog(assertArg(
                cloudTrailLog -> assertThat(cloudTrailLog)
                        .extracting("userArn", "eventTime", "instanceIds")
                        .containsExactly(
                                "arn:aws:iam::123456789012:root",
                                OffsetDateTime.parse("2016-05-20T08:27:45Z"),
                                List.of("i-1a2b3c4d"))));
    }

    @SneakyThrows
    private String cloudTrailLogsPayload() {
        return new String(new ClassPathResource("testdata/cloudtrail-logs.json").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

}
