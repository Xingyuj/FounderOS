package com.founderos.backend.domain;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="workflow_run")
public class WorkflowRun {
 @Id private UUID id; @Column(name="project_id",nullable=false) private UUID projectId; @Column(name="thread_id",nullable=false,unique=true,length=200) private String threadId; @Enumerated(EnumType.STRING) @Column(nullable=false,length=40) private WorkflowStatus status; @Column(name="current_node",nullable=false,length=150) private String currentNode; @Column(name="error_message",columnDefinition="text") private String errorMessage; @Column(name="created_at",nullable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected WorkflowRun(){} public WorkflowRun(UUID projectId,String threadId){this.id=UUID.randomUUID();this.projectId=projectId;this.threadId=threadId;this.status=WorkflowStatus.RUNNING;this.currentNode="analyse_idea";this.createdAt=this.updatedAt=Instant.now();}
 public void waiting(String node){status=WorkflowStatus.WAITING_FOR_FOUNDER;currentNode=node;touch();} public void completed(){status=WorkflowStatus.COMPLETED;currentNode="end";touch();} public void failed(String message){status=WorkflowStatus.FAILED;errorMessage=message;touch();} private void touch(){updatedAt=Instant.now();}
 public UUID getId(){return id;} public UUID getProjectId(){return projectId;} public String getThreadId(){return threadId;} public WorkflowStatus getStatus(){return status;} public String getCurrentNode(){return currentNode;} public String getErrorMessage(){return errorMessage;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
