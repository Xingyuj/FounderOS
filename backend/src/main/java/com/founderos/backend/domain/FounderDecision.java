package com.founderos.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity @Table(name="founder_decision")
public class FounderDecision {
    @Id private UUID id;
    @Column(name="project_id",nullable=false) private UUID projectId;
    @Column(name="workflow_thread_id",nullable=false,length=200) private String workflowThreadId;
    @Column(nullable=false,columnDefinition="text") private String question;
    @Column(nullable=false,columnDefinition="jsonb") @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON) private List<String> options;
    @Column(columnDefinition="text") private String recommendation;
    @Column(columnDefinition="text") private String context;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=40) private DecisionStatus status;
    @Column(name="selected_option",columnDefinition="text") private String selectedOption;
    @Column(name="founder_comment",columnDefinition="text") private String founderComment;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    @Column(name="resolved_at") private Instant resolvedAt;
    protected FounderDecision() {}
    public FounderDecision(UUID projectId,String threadId,String question,List<String> options,String recommendation,String context){this.id=UUID.randomUUID();this.projectId=projectId;this.workflowThreadId=threadId;this.question=question;this.options=List.copyOf(options);this.recommendation=recommendation;this.context=context;this.status=DecisionStatus.OPEN;this.createdAt=Instant.now();}
    public void resolve(String option,String comment){if(status!=DecisionStatus.OPEN)throw new IllegalStateException("Decision is already resolved");if(!options.contains(option))throw new IllegalArgumentException("Selected option is not one of the available options");selectedOption=option;founderComment=comment;status=DecisionStatus.RESOLVED;resolvedAt=Instant.now();}
    public UUID getId(){return id;} public UUID getProjectId(){return projectId;} public String getWorkflowThreadId(){return workflowThreadId;} public String getQuestion(){return question;} public List<String> getOptions(){return options;} public String getRecommendation(){return recommendation;} public String getContext(){return context;} public DecisionStatus getStatus(){return status;} public String getSelectedOption(){return selectedOption;} public String getFounderComment(){return founderComment;} public Instant getCreatedAt(){return createdAt;} public Instant getResolvedAt(){return resolvedAt;}
}
