package repository;

import models.Competency;
import models.DatabaseExecutionContext;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
public class JPACompetencyRepository implements  CompetencyRepository{
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPACompetencyRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Competency> add(Competency competency) {
        return supplyAsync(() -> wrap(em -> insert(em, competency)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Competency>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Competency insert(EntityManager em, Competency competency) {
        em.persist(competency);
        return competency;
    }

    private Stream<Competency> list(EntityManager em) {
        List<Competency> competencies = em.createQuery("select * from Competency competency", Competency.class).getResultList();
        return competencies.stream();
    }
}
