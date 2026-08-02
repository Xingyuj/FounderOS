package com.founderos.backend.client;
import org.springframework.beans.factory.annotation.Value; import org.springframework.http.MediaType; import org.springframework.http.client.SimpleClientHttpRequestFactory; import org.springframework.stereotype.Component; import org.springframework.web.client.*; import java.util.*;
@Component
public class OrchestratorClient {
 private final RestClient client;
 public OrchestratorClient(RestClient.Builder builder,@Value("${orchestrator.base-url}") String baseUrl){this.client=builder.requestFactory(new SimpleClientHttpRequestFactory()).baseUrl(baseUrl).build();}
 public StartResponse start(UUID projectId,String threadId,String name,String idea){try{return client.post().uri("/internal/workflows").contentType(MediaType.APPLICATION_JSON).body(new StartRequest(projectId.toString(),threadId,name,idea)).retrieve().body(StartResponse.class);}catch(RestClientResponseException e){throw new OrchestratorException("Orchestrator start failed: "+e.getResponseBodyAsString(),e);}catch(RestClientException e){throw new OrchestratorException("Orchestrator start failed: "+e.getMessage(),e);}}
 public ResumeResponse resume(String threadId,String selectedOption,String comment){try{return client.post().uri("/internal/workflows/{threadId}/resume",threadId).contentType(MediaType.APPLICATION_JSON).body(new ResumeRequest(selectedOption,comment)).retrieve().body(ResumeResponse.class);}catch(RestClientResponseException e){throw new OrchestratorException("Orchestrator resume failed: "+e.getResponseBodyAsString(),e);}catch(RestClientException e){throw new OrchestratorException("Orchestrator resume failed: "+e.getMessage(),e);}}
 public record StartRequest(String projectId,String threadId,String projectName,String idea){} public record ResumeRequest(String selectedOption,String comment){}
 public record DecisionPayload(String question,List<String> options,String recommendation,String context){} public record ArtifactPayload(String type,String title,String content){}
 public record StartResponse(String threadId,String status,String currentNode,DecisionPayload decision){} public record ResumeResponse(String threadId,String status,String currentNode,ArtifactPayload artifact){}
 public static class OrchestratorException extends RuntimeException{public OrchestratorException(String message,Throwable cause){super(message,cause);}}
}
