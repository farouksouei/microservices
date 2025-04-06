package kripton.candidateservice.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient (name = "job-service")
public interface JobClient {






	@DeleteMapping ("/api/jobs/job-applications/candidate/{candidate-id}")
	void deleteJobApplicationOfCandidate(@PathVariable ("candidate-id") Long idCandidate);
}
