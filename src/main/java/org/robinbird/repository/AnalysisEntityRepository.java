package org.robinbird.repository;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.robinbird.exception.AlreadyExistingAnalysisEntityException;
import org.robinbird.model.AnalysisEntity;
import org.robinbird.model.AnalysisEntityCategory;
import org.robinbird.model.Relation;
import org.robinbird.repository.dao.AnalysisEntityDao;

@AllArgsConstructor
public class AnalysisEntityRepository {

    private final AnalysisEntityDao analysisEntityDao;

    /**
     * Get existing {@link AnalysisEntity} with the given name.
     * @param name A name of AnalysisEntity.
     * @return Optional of found AnalysisEntity. If this doesn't find anything, returns empty optional.
     */
    public Optional<AnalysisEntity> getAnalysisEntity(@NonNull final String name) {
        return Optional.empty();

    }

    /**
     * Register new {@link AnalysisEntity} with the given name and category.
     * If this is trying to register already existing {@link AnalysisEntity}, it will be just returned.
     * If this is trying to register a name of existing one with different category,
     * {@link AlreadyExistingAnalysisEntityException} will be thrown.
     * @param name
     * @param category
     * @return
     */
    public AnalysisEntity registerAnalysisEntity(@NonNull final String name, @NonNull final AnalysisEntityCategory category) {
        return null;

    }

    /**
     * After creating {@link AnalysisEntity}, {@link Relation} can be added to it.
     * To persist those added new relations, this function should be called.
     * This will add new relations and delete relations which are not hold by the given analysisEntity.
     * In other words, this will make the state of persistent storage same with the analysisEntity in memory.
     * @param analysisEntity an AnalysisEntity want to make its relations synced with persistent storage.
     */
    public void updateAnalysisEntity(@NonNull final AnalysisEntity analysisEntity) {

    }


}
