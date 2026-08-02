package com.founderos.backend.api;
import com.founderos.backend.client.OrchestratorClient.OrchestratorException; import com.founderos.backend.service.*; import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.net.URI;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(NotFoundException.class) ProblemDetail notFound(NotFoundException e){return problem(HttpStatus.NOT_FOUND,"Not found",e.getMessage());}
 @ExceptionHandler(ConflictException.class) ProblemDetail conflict(ConflictException e){return problem(HttpStatus.CONFLICT,"Conflict",e.getMessage());}
 @ExceptionHandler({IllegalArgumentException.class,MethodArgumentNotValidException.class}) ProblemDetail invalid(Exception e){String detail=e instanceof MethodArgumentNotValidException m?m.getBindingResult().getAllErrors().getFirst().getDefaultMessage():e.getMessage();return problem(HttpStatus.BAD_REQUEST,"Invalid request",detail);}
 @ExceptionHandler(OrchestratorException.class) ProblemDetail unavailable(OrchestratorException e){return problem(HttpStatus.SERVICE_UNAVAILABLE,"Orchestrator unavailable",e.getMessage());}
 @ExceptionHandler(IllegalStateException.class) ProblemDetail workflow(IllegalStateException e){return problem(HttpStatus.BAD_GATEWAY,"Workflow failure",e.getMessage());}
 private ProblemDetail problem(HttpStatus status,String title,String detail){ProblemDetail p=ProblemDetail.forStatusAndDetail(status,detail);p.setTitle(title);p.setType(URI.create("https://founderos.local/problems/"+status.value()));return p;}
}
