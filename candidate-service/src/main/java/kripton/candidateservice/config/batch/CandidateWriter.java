package kripton.candidateservice.config.batch;

import kripton.candidateservice.model.entities.CandidateEntity;
import kripton.candidateservice.model.repositories.CandidateEntityRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class CandidateWriter implements ItemWriter<CandidateEntity> {

	private final CandidateEntityRepository candidateEntityRepository ;
	public CandidateWriter (CandidateEntityRepository repository){
		this.candidateEntityRepository = repository  ;
	}
	@Override
	public void write (Chunk<? extends CandidateEntity>  chunk) throws Exception {
		for (CandidateEntity candidate : chunk){

			if(candidateEntityRepository.existsByFirstNameLikeAndLastNameLikeAndEmailLike (candidate.getFirstName (), candidate.getLastName (), candidate.getEmail ())){
				continue;
			}
			candidateEntityRepository.save (candidate);
		}
	}

}
