package kripton.candidateservice.config.batch;

import kripton.candidateservice.model.entities.CandidateEntity;
import kripton.candidateservice.model.entities.HiringDecision;
import kripton.candidateservice.model.entities.RecruitingStatus;
import kripton.candidateservice.model.entities.SourceOfHire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import java.util.Date;

@Slf4j
public class CandidateProcessor implements ItemProcessor<CandidateEntity,CandidateEntity> {

	private String userId;
	@BeforeStep
	public void beforeStep(StepExecution stepExecution){
	userId = stepExecution.getJobParameters ().getString ("userId");
	}
	@Override
	public CandidateEntity process (CandidateEntity candidate) throws Exception {

		candidate.setCreatedAt (new Date ());
		candidate.setModifiedAt (new Date ());
		candidate.setSource (SourceOfHire.COLLABORATOR);
		candidate.setDecision (HiringDecision.PENDING);
		candidate.setCompleted (false);
		candidate.setStatus (RecruitingStatus.RESUME_REVIEW);
		candidate.setUser (userId);
		return candidate;
	}
}
