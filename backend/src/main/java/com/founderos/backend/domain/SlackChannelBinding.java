package com.founderos.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "slack_channel_binding")
public class SlackChannelBinding {
    @Id private UUID id;
    @Column(name = "slack_team_id", nullable = false, length = 40) private String slackTeamId;
    @Column(name = "slack_channel_id", nullable = false, length = 40) private String slackChannelId;
    @Enumerated(EnumType.STRING) @Column(name = "channel_kind", nullable = false, length = 40) private SlackChannelKind channelKind;
    @Enumerated(EnumType.STRING) @Column(name = "primary_agent_role", nullable = false, length = 40) private AgentRole primaryAgentRole;
    @Column(name = "project_id") private UUID projectId;
    @Column(length = 100) private String label;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected SlackChannelBinding() {}
    public SlackChannelBinding(String teamId, String channelId, SlackChannelKind kind, AgentRole role, UUID projectId, String label) {
        if ((kind == SlackChannelKind.PROJECT_CHANNEL) != (projectId != null)) throw new IllegalArgumentException("Project channels require a project; functional channels must not have one");
        this.id = UUID.randomUUID(); this.slackTeamId = teamId; this.slackChannelId = channelId; this.channelKind = kind; this.primaryAgentRole = role; this.projectId = projectId; this.label = label; this.createdAt = this.updatedAt = Instant.now();
    }
    public UUID getId() { return id; } public String getSlackTeamId() { return slackTeamId; } public String getSlackChannelId() { return slackChannelId; }
    public SlackChannelKind getChannelKind() { return channelKind; } public AgentRole getPrimaryAgentRole() { return primaryAgentRole; } public UUID getProjectId() { return projectId; } public String getLabel() { return label; }
}
