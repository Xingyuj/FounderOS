package com.founderos.backend.service;
import com.founderos.backend.domain.*; import org.junit.jupiter.api.Test; import java.util.*; import static org.assertj.core.api.Assertions.*;
class DomainRulesTest {
 @Test void projectTransitionsAreExplicit(){Project p=new Project("Tradigo","An idea");p.waiting("thread");p.defining();p.completed();assertThat(p.getStatus()).isEqualTo(ProjectStatus.COMPLETED);}
 @Test void projectRejectsInvalidTransition(){Project p=new Project("Tradigo","An idea");assertThatThrownBy(p::completed).isInstanceOf(IllegalStateException.class);}
 @Test void decisionValidatesOptionAndDuplicateResolution(){FounderDecision d=new FounderDecision(UUID.randomUUID(),"thread","Question",List.of("A","B"),"A",null);assertThatThrownBy(()->d.resolve("C",null)).isInstanceOf(IllegalArgumentException.class);d.resolve("A","direction");assertThatThrownBy(()->d.resolve("A",null)).isInstanceOf(IllegalStateException.class);}
}
