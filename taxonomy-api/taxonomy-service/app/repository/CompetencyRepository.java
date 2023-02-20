package repository;

import com.google.inject.ImplementedBy;
import models.Competency;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPACompetencyRepository.class)
public interface CompetencyRepository {
    CompletionStage<Competency> add(Competency competency);

    CompletionStage<Stream<Competency>> list();
}
