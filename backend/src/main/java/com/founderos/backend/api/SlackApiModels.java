package com.founderos.backend.api;

import com.founderos.backend.domain.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;

public final class SlackApiModels {
    private SlackApiModels() {}
    public record AgentView(AgentRole role, String displayName, String responsibility) { public static AgentView of(AgentProfile a) { return new AgentView(a.getRole(), a.getDisplayName(), a.getResponsibility()); } }
    public record BindChannelRequest(@NotNull SlackChannelKind kind, @NotNull AgentRole primaryAgentRole, UUID projectId, @Size(max = 100) String label) {}
    public record ChannelBindingView(UUID id, String teamId, String channelId, SlackChannelKind kind, AgentRole primaryAgentRole, UUID projectId, String label) { public static ChannelBindingView of(SlackChannelBinding b) { return new ChannelBindingView(b.getId(), b.getSlackTeamId(), b.getSlackChannelId(), b.getChannelKind(), b.getPrimaryAgentRole(), b.getProjectId(), b.getLabel()); } }
    public record TaskView(UUID id, UUID projectId, String title, String description, TaskStatus status, AgentRole accountableAgent, TaskCreator createdBy, UUID sourceConversationId, String result, Instant createdAt) { public static TaskView of(WorkTask t) { return new TaskView(t.getId(), t.getProjectId(), t.getTitle(), t.getDescription(), t.getStatus(), t.getAccountableAgentRole(), t.getCreatedBy(), t.getSourceConversationId(), t.getResult(), t.getCreatedAt()); } }
    public record PublishDecisionRequest(@NotBlank String channelId, String threadTs) {}
    public record PublishDecisionResponse(UUID outboxMessageId, UUID decisionId, List<UUID> actionTokens) {}
}
