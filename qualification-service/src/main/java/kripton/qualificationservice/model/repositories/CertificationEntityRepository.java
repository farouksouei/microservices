package kripton.qualificationservice.model.repositories;

import kripton.qualificationservice.model.entities.CertificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertificationEntityRepository extends JpaRepository<CertificationEntity, Long> {
    @Query("select c from CertificationEntity c where c.candidate_id = :candidate_id")
    List<CertificationEntity> findByCandidate_id(@Param("candidate_id") Long candidate_id);

    @Query("select c from CertificationEntity c where c.job_id = :job_id")
    List<CertificationEntity> findByJob_id(@Param("job_id") Long job_id);
}