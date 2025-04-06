package kripton.jobservice.model.repositories;

import kripton.jobservice.model.entities.JobApplicationEntity;
import kripton.jobservice.model.entities.JobEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface JobApplicationEntityRepository extends JpaRepository<JobApplicationEntity,Long> {
    @Query ("select count(j) from JobApplicationEntity j")
    long countFirstBy ();

    @Query ("select count(j) from JobApplicationEntity j where j.createdAt between :createdAtStart and :createdAtEnd")
    long countByCreatedAtBetween (@Param ("createdAtStart") Date createdAtStart, @Param ("createdAtEnd") Date createdAtEnd);

    @Query("select j from JobApplicationEntity j where j.candidate_id = :candidate_id")
    List<JobApplicationEntity> findByCandidateId(@Param("candidate_id") Long candidate_id);

    @NonNull
    List<JobApplicationEntity> findAll(@NonNull Sort sort);

    @Query("select j from JobApplicationEntity j where j.job.id = :job_id")
    List<JobApplicationEntity> findByJobId(@Param("job_id") Long job_id);

    @Transactional
    @Modifying
    @Query ("delete from JobApplicationEntity j where j.candidate_id = :candidate_id")
    void deleteByCandidate_id (Long candidate_id);


    @Query("select j.candidate_id from JobApplicationEntity j where j.job.id = :id")
    List<Long> findAssignedCandidatesToJob(@Param("id")Long idJob);

    @Transactional
    @Modifying
    @Query("delete from JobApplicationEntity j where j.candidate_id = :candidate_id and j.job = :job")
    void deleteByCandidate_idAndJob(Long candidate_id, JobEntity job);




}
