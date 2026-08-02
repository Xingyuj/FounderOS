package com.founderos.backend.domain;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="artifact")
public class Artifact {
 @Id private UUID id; @Column(name="project_id",nullable=false) private UUID projectId; @Column(nullable=false,length=100) private String type; @Column(nullable=false,length=255) private String title; @Column(nullable=false,columnDefinition="text") private String content; @Column(nullable=false) private int version; @Column(name="created_at",nullable=false) private Instant createdAt;
 protected Artifact(){} public Artifact(UUID projectId,String type,String title,String content,int version){this.id=UUID.randomUUID();this.projectId=projectId;this.type=type;this.title=title;this.content=content;this.version=version;this.createdAt=Instant.now();}
 public UUID getId(){return id;} public UUID getProjectId(){return projectId;} public String getType(){return type;} public String getTitle(){return title;} public String getContent(){return content;} public int getVersion(){return version;} public Instant getCreatedAt(){return createdAt;}
}
