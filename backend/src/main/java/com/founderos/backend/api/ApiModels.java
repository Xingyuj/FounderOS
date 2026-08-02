package com.founderos.backend.api;
import com.founderos.backend.domain.*; import jakarta.validation.constraints.*; import java.time.Instant; import java.util.*;
public final class ApiModels {
 private ApiModels(){}
 public record CreateProjectRequest(@NotBlank @Size(max=150) String name,@NotBlank @Size(max=10000) String idea){}
 public record ResolveDecisionRequest(@NotBlank String selectedOption,@Size(max=5000) String comment){}
 public record ProjectView(UUID id,String name,String idea,ProjectStatus status,String workflowThreadId,Instant createdAt,Instant updatedAt){public static ProjectView of(Project p){return new ProjectView(p.getId(),p.getName(),p.getIdea(),p.getStatus(),p.getWorkflowThreadId(),p.getCreatedAt(),p.getUpdatedAt());}}
 public record DecisionView(UUID id,String question,List<String> options,String recommendation,String context,DecisionStatus status,String selectedOption,String founderComment,Instant createdAt,Instant resolvedAt){public static DecisionView of(FounderDecision d){return new DecisionView(d.getId(),d.getQuestion(),d.getOptions(),d.getRecommendation(),d.getContext(),d.getStatus(),d.getSelectedOption(),d.getFounderComment(),d.getCreatedAt(),d.getResolvedAt());}}
 public record ArtifactView(UUID id,String type,String title,String content,int version,Instant createdAt){public static ArtifactView of(Artifact a){return new ArtifactView(a.getId(),a.getType(),a.getTitle(),a.getContent(),a.getVersion(),a.getCreatedAt());}}
 public record WorkflowView(UUID id,String threadId,WorkflowStatus status,String currentNode,String errorMessage,Instant createdAt,Instant updatedAt){public static WorkflowView of(WorkflowRun w){return new WorkflowView(w.getId(),w.getThreadId(),w.getStatus(),w.getCurrentNode(),w.getErrorMessage(),w.getCreatedAt(),w.getUpdatedAt());}}
 public record CreateProjectResponse(ProjectView project,DecisionView decision){}
 public record ProjectDetail(ProjectView project,List<DecisionView> decisions,WorkflowView workflow,List<ArtifactView> artifacts){}
 public record ResolveDecisionResponse(UUID projectId,ProjectStatus status,ArtifactView artifact){}
}
