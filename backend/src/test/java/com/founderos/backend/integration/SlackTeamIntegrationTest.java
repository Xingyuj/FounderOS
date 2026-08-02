package com.founderos.backend.integration;

import com.fasterxml.jackson.databind.*;
import com.founderos.backend.client.OrchestratorClient;
import com.founderos.backend.client.OrchestratorClient.*;
import com.founderos.backend.domain.*;
import com.founderos.backend.repository.*;
import com.founderos.backend.slack.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "slack.enabled=true", "slack.signing-secret=test-signing-secret", "slack.team-id=T-FOUNDER", "slack.founder-user-id=U-FOUNDER",
    "slack.bot-token=xoxb-test", "slack.admin-token=test-admin-token", "slack.worker-delay-ms=3600000"
})
@AutoConfigureMockMvc @Testcontainers
class SlackTeamIntegrationTest {
    @Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r) { r.add("spring.datasource.url", postgres::getJdbcUrl); r.add("spring.datasource.username", postgres::getUsername); r.add("spring.datasource.password", postgres::getPassword); }
    @Autowired MockMvc mvc; @Autowired ObjectMapper mapper; @Autowired SlackWorker worker;
    @Autowired JdbcTemplate jdbc;
    @Autowired WorkTaskRepository tasks; @Autowired SlackInboxEventRepository inbox; @Autowired SlackOutboxMessageRepository outbox; @Autowired SlackDecisionActionRepository actions;
    @MockitoBean OrchestratorClient orchestrator; @MockitoBean SlackWebApiClient slackClient;

    @BeforeEach void stub() {
        jdbc.execute("TRUNCATE slack_inbox_event, slack_outbox_message, slack_decision_action, work_task, slack_conversation, slack_channel_binding, project CASCADE");
        when(orchestrator.start(any(), anyString(), anyString(), anyString())).thenAnswer(i -> new StartResponse(i.getArgument(1), "WAITING_FOR_FOUNDER", "wait_for_founder", new DecisionPayload("Who should the first version serve?", List.of("Founders", "Operators"), "Founders", "Choose the initial audience")));
        when(orchestrator.resume(anyString(), anyString(), nullable(String.class))).thenAnswer(i -> new ResumeResponse(i.getArgument(0), "COMPLETED", "end", new ArtifactPayload("PRODUCT_BRIEF", "Product Brief", "# Product Brief")));
        when(slackClient.post(any())).thenReturn("1800000000.000001");
    }

    @Test void routesChannelAndDmMessagesIdempotentlyAndDeliversFromOutbox() throws Exception {
        mvc.perform(put("/api/slack/channels/C-PRODUCT").header("X-FounderOS-Admin-Token", "test-admin-token").contentType("application/json").content("{\"kind\":\"FUNCTIONAL_CHANNEL\",\"primaryAgentRole\":\"PRODUCT_LEAD\",\"label\":\"product\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.primaryAgentRole").value("PRODUCT_LEAD"));

        String channelEvent = event("E-PRODUCT-1", "U-FOUNDER", "C-PRODUCT", "channel", "Define the onboarding scope", "1800000000.1");
        postEvent(channelEvent, now()).andExpect(status().isOk()).andExpect(jsonPath("$.accepted").value(true));
        postEvent(channelEvent, now()).andExpect(status().isOk()).andExpect(jsonPath("$.accepted").value(false));
        assertThat(worker.processInboxOnce()).isTrue();
        assertThat(tasks.findAll()).anySatisfy(task -> { assertThat(task.getAccountableAgentRole()).isEqualTo(AgentRole.PRODUCT_LEAD); assertThat(task.getDescription()).isEqualTo("Define the onboarding scope"); });
        assertThat(worker.deliverOutboxOnce()).isTrue();
        verify(slackClient).post(argThat(message -> message.getAgentRole() == AgentRole.PRODUCT_LEAD && message.getText().contains("Assigned")));

        String dmEvent = event("E-DM-1", "U-FOUNDER", "D-FOUNDER", "im", "Engineering Lead: Review the architecture", "1800000000.2");
        postEvent(dmEvent, now()).andExpect(status().isOk());
        assertThat(worker.processInboxOnce()).isTrue();
        assertThat(tasks.findAll()).anySatisfy(task -> { assertThat(task.getAccountableAgentRole()).isEqualTo(AgentRole.ENGINEERING_LEAD); assertThat(task.getDescription()).isEqualTo("Review the architecture"); });
    }

    @Test void rejectsForgedStaleAndUnauthorizedEventsAndIgnoresBots() throws Exception {
        mvc.perform(get("/api/slack/agents").header("X-FounderOS-Admin-Token", "wrong")).andExpect(status().isUnauthorized());
        String valid = event("E-SECURITY-1", "U-FOUNDER", "C-ONE", "channel", "hello", "1800000001.1");
        mvc.perform(post("/integrations/slack/events").header("X-Slack-Request-Timestamp", now()).header("X-Slack-Signature", "v0=bad").contentType("application/json").content(valid)).andExpect(status().isUnauthorized());
        postEvent(valid, Long.toString(Instant.now().minusSeconds(301).getEpochSecond())).andExpect(status().isUnauthorized());
        postEvent(event("E-SECURITY-2", "U-OTHER", "C-ONE", "channel", "hello", "1800000001.2"), now()).andExpect(status().isUnauthorized());
        String bot = "{\"type\":\"event_callback\",\"team_id\":\"T-FOUNDER\",\"event_id\":\"E-BOT\",\"event\":{\"type\":\"message\",\"bot_id\":\"B1\",\"channel\":\"C-ONE\",\"ts\":\"1800000001.3\",\"text\":\"loop\"}}";
        postEvent(bot, now()).andExpect(status().isOk()).andExpect(jsonPath("$.reason").value("bot_event"));
        assertThat(inbox.findByExternalId("E-BOT")).isEmpty();
    }

    @Test void publishesAndResolvesFounderDecisionThroughOpaqueSlackAction() throws Exception {
        String created = mvc.perform(post("/api/projects").contentType("application/json").content("{\"name\":\"Slack project\",\"idea\":\"A coordinated team\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String decisionId = mapper.readTree(created).at("/decision/id").asText();
        String published = mvc.perform(post("/api/slack/decisions/" + decisionId + "/publish").header("X-FounderOS-Admin-Token", "test-admin-token").contentType("application/json").content("{\"channelId\":\"C-FOUNDER\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.actionTokens.length()").value(2)).andReturn().getResponse().getContentAsString();
        String token = mapper.readTree(published).at("/actionTokens/0").asText();
        assertThat(outbox.findAll()).anySatisfy(message -> assertThat(message.getBlocksJson()).contains("resolve_decision").contains(token));

        String rawInteraction = interaction(token, "111.100");
        postInteraction(rawInteraction, now()).andExpect(status().isOk()).andExpect(jsonPath("$.accepted").value(true));
        assertThat(worker.processInboxOnce()).isTrue();
        mvc.perform(get("/api/projects/" + mapper.readTree(created).at("/project/id").asText())).andExpect(status().isOk()).andExpect(jsonPath("$.project.status").value("COMPLETED")).andExpect(jsonPath("$.artifacts.length()").value(1));
        assertThat(actions.findById(UUID.fromString(token)).orElseThrow().getConsumedAt()).isNotNull();
        assertThat(outbox.findAll()).anySatisfy(message -> { assertThat(message.getOperation()).isEqualTo(SlackDeliveryOperation.UPDATE); assertThat(message.getTargetMessageTs()).isEqualTo("1800000002.1"); });

        String secondClick = interaction(token, "111.200");
        postInteraction(secondClick, now()).andExpect(status().isOk()).andExpect(jsonPath("$.accepted").value(true));
        assertThat(worker.processInboxOnce()).isTrue();
        verify(orchestrator, times(1)).resume(anyString(), anyString(), nullable(String.class));
    }

    private org.springframework.test.web.servlet.ResultActions postEvent(String body, String timestamp) throws Exception {
        return mvc.perform(post("/integrations/slack/events").header("X-Slack-Request-Timestamp", timestamp).header("X-Slack-Signature", sign(timestamp, body)).contentType("application/json").content(body));
    }
    private org.springframework.test.web.servlet.ResultActions postInteraction(String body, String timestamp) throws Exception {
        return mvc.perform(post("/integrations/slack/interactions").header("X-Slack-Request-Timestamp", timestamp).header("X-Slack-Signature", sign(timestamp, body)).contentType("application/x-www-form-urlencoded").content(body));
    }
    private String event(String eventId, String user, String channel, String channelType, String text, String ts) throws Exception {
        return mapper.writeValueAsString(Map.of("type", "event_callback", "team_id", "T-FOUNDER", "event_id", eventId, "event", Map.of("type", "message", "user", user, "channel", channel, "channel_type", channelType, "ts", ts, "text", text)));
    }
    private String interaction(String token, String actionTs) throws Exception {
        String payload = mapper.writeValueAsString(Map.of("type", "block_actions", "team", Map.of("id", "T-FOUNDER"), "user", Map.of("id", "U-FOUNDER"), "channel", Map.of("id", "C-FOUNDER"), "message", Map.of("ts", "1800000002.1"), "container", Map.of("thread_ts", "1800000002.1"), "actions", List.of(Map.of("action_id", "resolve_decision", "value", token, "action_ts", actionTs))));
        return "payload=" + URLEncoder.encode(payload, StandardCharsets.UTF_8);
    }
    private String now() { return Long.toString(Instant.now().getEpochSecond()); }
    private String sign(String timestamp, String body) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec("test-signing-secret".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return "v0=" + HexFormat.of().formatHex(mac.doFinal(("v0:" + timestamp + ":" + body).getBytes(StandardCharsets.UTF_8)));
    }
}
