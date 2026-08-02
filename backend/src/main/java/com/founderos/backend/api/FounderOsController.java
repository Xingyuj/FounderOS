package com.founderos.backend.api;
import com.founderos.backend.api.ApiModels.*; import com.founderos.backend.service.FounderOsService; import jakarta.validation.Valid; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import java.util.UUID;
@RestController @RequestMapping("/api")
public class FounderOsController {
 private final FounderOsService service; public FounderOsController(FounderOsService service){this.service=service;}
 @PostMapping("/projects") @ResponseStatus(HttpStatus.CREATED) public CreateProjectResponse create(@Valid @RequestBody CreateProjectRequest request){return service.create(request);}
 @GetMapping("/projects/{id}") public ProjectDetail project(@PathVariable UUID id){return service.getProject(id);}
 @PostMapping("/decisions/{id}/resolve") public ResolveDecisionResponse resolve(@PathVariable UUID id,@Valid @RequestBody ResolveDecisionRequest request){return service.resolve(id,request);}
 @GetMapping("/artifacts/{id}") public ArtifactView artifact(@PathVariable UUID id){return service.getArtifact(id);}
}
