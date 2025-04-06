package kripton.qualificationservice.model.repositories;

import kripton.qualificationservice.model.entities.EducationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EducationEntityRepository extends JpaRepository<EducationEntity, Long> {
    @Query("select e from EducationEntity e where e.job_id = :job_id")
    List<EducationEntity> findByJob_id(@Param("job_id") Long job_id);
    @Query("select e from EducationEntity e where e.candidate_id = :candidate_id")
    List<EducationEntity> findByCandidate_id(@Param("candidate_id") Long candidate_id);
}