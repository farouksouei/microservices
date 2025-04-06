package kripton.candidateservice.model.repositories;

import kripton.candidateservice.model.entities.ProjectEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectEntityRepository extends JpaRepository<ProjectEntity,Long> {
	@NonNull
	List<ProjectEntity> findAll(@NonNull Sort sort);
}
