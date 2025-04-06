package kripton.qualificationservice.model.repositories;

import kripton.qualificationservice.model.entities.ExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExperienceEntityRepository extends JpaRepository<ExperienceEntity, Long> {
    @Query("select e from ExperienceEntity e where e.candidate_id = :candidate_id")
    List<ExperienceEntity> findByCandidate_id(@Param("candidate_id") Long candidate_id);

    @Query("select e from ExperienceEntity e where e.job_id = :job_id")
    List<ExperienceEntity> findByJob_id(@Param("job_id") Long job_id);

}