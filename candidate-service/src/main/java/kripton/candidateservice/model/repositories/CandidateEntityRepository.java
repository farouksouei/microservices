package kripton.candidateservice.model.repositories;

import kripton.candidateservice.model.entities.CandidateEntity;
import kripton.candidateservice.model.entities.HiringDecision;
import kripton.candidateservice.model.entities.RecruitingStatus;
import kripton.candidateservice.model.entities.SourceOfHire;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CandidateEntityRepository extends JpaRepository<CandidateEntity, Long> {
	@Query ("""
			select c from CandidateEntity c
			where c.firstName like :firstName and c.lastName like :lastName and c.email like :email""")
	CandidateEntity findByFirstNameLikeAndLastNameLikeAndEmailLike (@Param ("firstName") String firstName, @Param ("lastName") String lastName, @Param ("email") String email);

	@NonNull
	List<CandidateEntity> findAll(@NonNull Sort sort);
	@Query ("""
			select (count(c) > 0) from CandidateEntity c
			where c.firstName like :firstName and c.lastName like :lastName and c.email like :email""")
	boolean existsByFirstNameLikeAndLastNameLikeAndEmailLike (@Param ("firstName") String firstName, @Param ("lastName") String lastName, @Param ("email") String email);
    @Query("select c from CandidateEntity c where c.isCompleted = true")
    List<CandidateEntity> findNotCompletedCandidates();
	boolean existsByEmail(String email);
	
	CandidateEntity findByEmail(String email);

	@Query ("select count(c) from CandidateEntity c where c.isCompleted = :isCompleted")
	long countByIsCompleted (@Param ("isCompleted") boolean isCompleted);

	@Query ("select count(c) from CandidateEntity c where c.decision = :decision")
	long countByDecision (@Param ("decision") HiringDecision decision);

	@Query ("select count(c) from CandidateEntity c where c.status = :status")
	long countByStatus (@Param ("status") RecruitingStatus status);

	@Query ("select count(c) from CandidateEntity c where c.source = :source")
	long countBySource (@Param ("source") SourceOfHire source);

	@Query ("select count(c) from CandidateEntity c where c.createdAt between :createdAtStart and :createdAtEnd")
	long countByCreatedAtBetween (@Param ("createdAtStart") Date createdAtStart, @Param ("createdAtEnd") Date createdAtEnd);




}
