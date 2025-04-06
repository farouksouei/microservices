package kripton.candidateservice.model.repositories;

import kripton.candidateservice.model.entities.Priority;
import kripton.candidateservice.model.entities.TaskEntity;
import kripton.candidateservice.model.entities.TaskStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskEntityRepository extends JpaRepository<TaskEntity,Long> {
	@NonNull
	List<TaskEntity> findAll(@NonNull Sort sort);

	@Query ("select t from TaskEntity t where t.creatorId like :creatorId")
	List<TaskEntity> findByCreatorIdLike (@Param ("creatorId") String creatorId, Sort sort);


	@Query ("select t from TaskEntity t where t.deadline between :deadlineStart and :deadlineEnd")
	List<TaskEntity> findByDeadlineBetween (@Param ("deadlineStart") Date deadlineStart, @Param ("deadlineEnd") Date deadlineEnd);

	@Query ("select t from TaskEntity t where t.deadline < :date")
	List<TaskEntity> findByDeadlineLessThan (@Param ("date") Date date);

	@Query ("select t from TaskEntity t where t.project.id = :id")
	List<TaskEntity> findByProject_Id (@Param ("id") Long id, Sort sort);

	@Query ("select count(t) from TaskEntity t where t.project.id = :id and t.status = :status")
	long countByProject_IdAndStatus (@Param ("id") Long id, @Param ("status") TaskStatus status);

	@Query ("select count(t) from TaskEntity t where t.priority = :priority and t.project.id = :id")
	long countByPriorityAndProject_Id (@Param ("priority") Priority priority, @Param ("id") Long id);









}
