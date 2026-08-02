package com.founderos.backend.slack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.founderos.backend.domain.SlackOutboxMessage;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import java.util.*;

@Component
public class SlackWebApiClient {
    private final RestClient client;
    private final SlackProperties properties;
    private final ObjectMapper mapper;
    public SlackWebApiClient(RestClient.Builder builder, SlackProperties properties, ObjectMapper mapper) {
        this.client = builder.baseUrl("https://slack.com/api").build(); this.properties = properties; this.mapper = mapper;
    }
    public String post(SlackOutboxMessage message) {
        if (properties.getBotToken() == null || properties.getBotToken().isBlank()) throw new SlackDeliveryException("Slack bot token is not configured", false);
        Map<String,Object> body = new LinkedHashMap<>(); body.put("channel", message.getSlackChannelId()); body.put("text", message.getText());
        if (message.getOperation() == com.founderos.backend.domain.SlackDeliveryOperation.UPDATE) body.put("ts", message.getTargetMessageTs());
        else if (message.getSlackThreadTs() != null) body.put("thread_ts", message.getSlackThreadTs());
        if (message.getBlocksJson() != null) try { body.put("blocks", mapper.readTree(message.getBlocksJson())); } catch (Exception e) { throw new SlackDeliveryException("Invalid persisted Slack blocks", false); }
        try {
            String method = message.getOperation() == com.founderos.backend.domain.SlackDeliveryOperation.UPDATE ? "/chat.update" : "/chat.postMessage";
            JsonNode result = client.post().uri(method).header("Authorization", "Bearer " + properties.getBotToken()).contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(JsonNode.class);
            if (result == null || !result.path("ok").asBoolean()) {
                String error = result == null ? "empty_response" : result.path("error").asText("unknown_error");
                boolean retryable = Set.of("ratelimited", "internal_error", "fatal_error", "request_timeout").contains(error);
                throw new SlackDeliveryException("Slack delivery failed: " + error, retryable);
            }
            return result.path("ts").asText();
        } catch (SlackDeliveryException e) { throw e; }
        catch (RestClientException e) { throw new SlackDeliveryException("Slack chat.postMessage transport failure", true); }
    }
    public static class SlackDeliveryException extends RuntimeException {
        private final boolean retryable;
        public SlackDeliveryException(String message, boolean retryable) { super(message); this.retryable = retryable; }
        public boolean isRetryable() { return retryable; }
    }
}
