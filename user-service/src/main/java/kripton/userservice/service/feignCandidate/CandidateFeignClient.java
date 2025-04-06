package kripton.userservice.service.feignCandidate;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient (name = "candidate-service")

public interface CandidateFeignClient {
	@PostMapping("api/candidates/create-candidate-on-register")
	public CandidateDetailsDto autoCreatedCandidate(@RequestParam ("firstName")String firstName,
	                                                @RequestParam("lastName")String lastName,
	                                                @RequestParam("email")String email);


}