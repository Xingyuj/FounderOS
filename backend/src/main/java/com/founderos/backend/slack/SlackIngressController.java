package com.founderos.backend.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.founderos.backend.domain.SlackInboxEvent;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@RestController
@RequestMapping("/integrations/slack")
public class SlackIngressController {
    private final SlackRequestVerifier verifier;
    private final SlackProperties properties;
    private final SlackIngressService ingress;
    private final ObjectMapper mapper;
    public SlackIngressController(SlackRequestVerifier verifier, SlackProperties properties, SlackIngressService ingress, ObjectMapper mapper) {
        this.verifier = verifier; this.properties = properties; this.ingress = ingress; this.mapper = mapper;
    }

    @PostMapping(value = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> events(@RequestHeader("X-Slack-Request-Timestamp") String timestamp,
                                    @RequestHeader("X-Slack-Signature") String signature,
                                    @RequestBody String rawBody) {
        verifier.verify(timestamp, signature, rawBody);
        JsonNode body = parse(rawBody);
        requireTeam(body.path("team_id").asText());
        if ("url_verification".equals(body.path("type").asText())) return ResponseEntity.ok(Map.of("challenge", body.path("challenge").asText()));
        if (!"event_callback".equals(body.path("type").asText())) return ResponseEntity.ok(Map.of("accepted", false, "reason", "unsupported_envelope"));
        JsonNode event = body.path("event");
        if (event.hasNonNull("bot_id") || "bot_message".equals(event.path("subtype").asText())) return ResponseEntity.ok(Map.of("accepted", false, "reason", "bot_event"));
        String eventType = event.path("type").asText();
        if (!Set.of("message", "app_mention").contains(eventType)) return ResponseEntity.ok(Map.of("accepted", false, "reason", "unsupported_event"));
        requireFounder(event.path("user").asText());
        String channel = required(event, "channel");
        String messageTs = required(event, "ts");
        String threadTs = event.path("thread_ts").asText(messageTs);
        String externalId = required(body, "event_id");
        boolean accepted = ingress.accept(SlackInboxEvent.message(externalId, properties.getTeamId(), properties.getFounderUserId(), channel, messageTs, threadTs, event.path("channel_type").asText(), event.path("text").asText()));
        return ResponseEntity.ok(Map.of("accepted", accepted));
    }

    @PostMapping(value = "/interactions", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> interactions(@RequestHeader("X-Slack-Request-Timestamp") String timestamp,
                                          @RequestHeader("X-Slack-Signature") String signature,
                                          @RequestBody String rawBody) {
        verifier.verify(timestamp, signature, rawBody);
        String payloadText = formValue(rawBody, "payload").orElseThrow(() -> new IllegalArgumentException("Missing Slack interaction payload"));
        JsonNode payload = parse(payloadText);
        requireTeam(payload.path("team").path("id").asText());
        requireFounder(payload.path("user").path("id").asText());
        JsonNode action = payload.path("actions").path(0);
        if (!"resolve_decision".equals(action.path("action_id").asText())) return ResponseEntity.ok(Map.of("accepted", false, "reason", "unsupported_action"));
        UUID token;
        try { token = UUID.fromString(action.path("value").asText()); } catch (RuntimeException e) { throw new IllegalArgumentException("Invalid decision action token"); }
        String channel = required(payload.path("channel"), "id");
        String messageTs = payload.path("message").path("ts").asText();
        String threadTs = payload.path("container").path("thread_ts").asText(messageTs);
        String externalId = "interaction:" + sha256(rawBody);
        boolean accepted = ingress.accept(SlackInboxEvent.decision(externalId, properties.getTeamId(), properties.getFounderUserId(), channel, messageTs, threadTs, token, findComment(payload.path("state"))));
        return ResponseEntity.ok(Map.of("accepted", accepted));
    }

    private void requireTeam(String team) { if (!properties.getTeamId().equals(team)) throw new SlackSecurityException("Slack workspace is not authorized"); }
    private void requireFounder(String user) { if (!properties.getFounderUserId().equals(user)) throw new SlackSecurityException("Slack user is not authorized"); }
    private String required(JsonNode node, String field) { String value = node.path(field).asText(); if (value.isBlank()) throw new IllegalArgumentException("Missing Slack field: " + field); return value; }
    private Optional<String> formValue(String body, String key) {
        return Arrays.stream(body.split("&")).map(part -> part.split("=", 2)).filter(pair -> URLDecoder.decode(pair[0], StandardCharsets.UTF_8).equals(key)).map(pair -> pair.length == 2 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "").findFirst();
    }
    private String findComment(JsonNode node) {
        if (node == null || node.isMissingNode()) return null;
        if (node.isObject() && "plain_text_input".equals(node.path("type").asText()) && node.has("value")) return node.path("value").asText();
        for (JsonNode child : node) { String result = findComment(child); if (result != null && !result.isBlank()) return result; }
        return null;
    }
    private String sha256(String value) {
        try { return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new IllegalStateException("Unable to hash Slack interaction", e); }
    }
    private JsonNode parse(String value) { try { return mapper.readTree(value); } catch (JsonProcessingException e) { throw new IllegalArgumentException("Malformed Slack payload"); } }
}
