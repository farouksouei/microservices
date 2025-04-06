package kripton.jobservice.service.feign;

import kripton.jobservice.model.dtos.candidate.CandidateDetailsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "candidate-service")
public interface CandidateClient {
    @GetMapping("/api/candidates/{candidate-id}")
    public CandidateDetailsDto findByCandidateId(@PathVariable(value = "candidate-id")Long idCandidate);
}
