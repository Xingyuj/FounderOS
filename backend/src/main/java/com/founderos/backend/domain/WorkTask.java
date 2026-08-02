package com.founderos.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "work_task")
public class WorkTask {
    @Id private UUID id;
    @Column(name = "project_id") private UUID projectId;
    @Column(nullable = false, length = 255) private String title;
    @Column(nullable = false, columnDefinition = "text") private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40) private TaskStatus status;
    @Enumerated(EnumType.STRING) @Column(name = "accountable_agent_role", nullable = false, length = 40) private AgentRole accountableAgentRole;
    @Enumerated(EnumType.STRING) @Column(name = "created_by", nullable = false, length = 20) private TaskCreator createdBy;
    @Column(name = "source_conversation_id") private UUID sourceConversationId;
    @Column(name = "parent_task_id") private UUID parentTaskId;
    @Column(columnDefinition = "text") private String result;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Column(name = "completed_at") private Instant completedAt;
    protected WorkTask() {}
    public WorkTask(UUID projectId, String title, String description, AgentRole role, TaskCreator createdBy, UUID conversationId) {
        this.id = UUID.randomUUID(); this.projectId = projectId; this.title = title; this.description = description; this.status = TaskStatus.ASSIGNED; this.accountableAgentRole = role; this.createdBy = createdBy; this.sourceConversationId = conversationId; this.createdAt = this.updatedAt = Instant.now();
    }
    public UUID getId() { return id; } public UUID getProjectId() { return projectId; } public String getTitle() { return title; } public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; } public AgentRole getAccountableAgentRole() { return accountableAgentRole; } public TaskCreator getCreatedBy() { return createdBy; } public UUID getSourceConversationId() { return sourceConversationId; } public String getResult() { return result; } public Instant getCreatedAt() { return createdAt; }
}
