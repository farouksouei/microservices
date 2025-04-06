package kripton.jobservice.model.repositories;

import kripton.jobservice.model.entities.EmploymentType;
import kripton.jobservice.model.entities.ExperienceLevel;
import kripton.jobservice.model.entities.JobEntity;
import kripton.jobservice.model.entities.StatusOfJob;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;

public interface EntityJobRepository extends JpaRepository<JobEntity, Long> {

	@NonNull
	List<JobEntity> findAll(@NonNull Sort sort);

	@Query ("select count(j) from JobEntity j where j.employmentType = :employmentType")
	long countByEmploymentType (@Param ("employmentType") EmploymentType employmentType);

	@Query ("select count(j) from JobEntity j where j.status = :status")
	long countByStatus (@Param ("status") StatusOfJob status);

	@Query ("select count(j) from JobEntity j where j.experienceLevel = :experienceLevel")
	long countByExperienceLevel (@Param ("experienceLevel") ExperienceLevel experienceLevel);








}