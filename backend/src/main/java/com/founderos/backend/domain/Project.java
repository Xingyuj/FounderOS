package com.founderos.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="project")
public class Project {
    @Id private UUID id;
    @Column(nullable=false, length=150) private String name;
    @Column(nullable=false, columnDefinition="text") private String idea;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=40) private ProjectStatus status;
    @Column(name="workflow_thread_id", length=200) private String workflowThreadId;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
    protected Project() {}
    public Project(String name, String idea) { this.id=UUID.randomUUID(); this.name=name; this.idea=idea; this.status=ProjectStatus.DISCOVERY; this.createdAt=this.updatedAt=Instant.now(); }
    public void waiting(String threadId) { require(ProjectStatus.DISCOVERY); workflowThreadId=threadId; status=ProjectStatus.WAITING_FOR_FOUNDER; touch(); }
    public void defining() { require(ProjectStatus.WAITING_FOR_FOUNDER); status=ProjectStatus.PRODUCT_DEFINITION; touch(); }
    public void completed() { require(ProjectStatus.PRODUCT_DEFINITION); status=ProjectStatus.COMPLETED; touch(); }
    public void failed() { status=ProjectStatus.FAILED; touch(); }
    private void require(ProjectStatus expected) { if(status!=expected) throw new IllegalStateException("Project must be "+expected+" but was "+status); }
    private void touch(){updatedAt=Instant.now();}
    public UUID getId(){return id;} public String getName(){return name;} public String getIdea(){return idea;} public ProjectStatus getStatus(){return status;} public String getWorkflowThreadId(){return workflowThreadId;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
