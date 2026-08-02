package com.founderos.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "slack_conversation")
public class SlackConversation {
    @Id private UUID id;
    @Column(name = "slack_team_id", nullable = false, length = 40) private String slackTeamId;
    @Column(name = "slack_channel_id", nullable = false, length = 40) private String slackChannelId;
    @Column(name = "slack_thread_ts", nullable = false, length = 40) private String slackThreadTs;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40) private SlackConversationKind kind;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "task_id") private UUID taskId;
    @Enumerated(EnumType.STRING) @Column(name = "agent_role", length = 40) private AgentRole agentRole;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected SlackConversation() {}
    public SlackConversation(String teamId, String channelId, String threadTs, SlackConversationKind kind, UUID projectId, AgentRole role) {
        this.id = UUID.randomUUID(); this.slackTeamId = teamId; this.slackChannelId = channelId; this.slackThreadTs = threadTs; this.kind = kind; this.projectId = projectId; this.agentRole = role; this.createdAt = this.updatedAt = Instant.now();
    }
    public void attachTask(UUID taskId) { this.taskId = taskId; this.updatedAt = Instant.now(); }
    public UUID getId() { return id; } public String getSlackTeamId() { return slackTeamId; } public String getSlackChannelId() { return slackChannelId; } public String getSlackThreadTs() { return slackThreadTs; }
    public SlackConversationKind getKind() { return kind; } public UUID getProjectId() { return projectId; } public UUID getTaskId() { return taskId; } public AgentRole getAgentRole() { return agentRole; }
}
