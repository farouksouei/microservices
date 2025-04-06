package kripton.jobservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kripton.jobservice.model.dtos.candidate.CandidateDetailsDto;
import kripton.jobservice.model.dtos.job.JobDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IJobService {

    JobDto saveJob(JobDto jobDto ,String idUser,MultipartFile image ) throws IOException;
    JobDto findJobById(Long idJob);
    JobDto updateJob(Long idJob , JobDto jobDto, MultipartFile image) throws IOException;
    void deleteJobById(Long idJob);
    List<JobDto> findJobs(String sortByAttribute);

    void assignCandidateToJob(Long idJob,Long idCandidate) throws JsonProcessingException;
    void assignCandidatesToJob(Long idJob,List<CandidateDetailsDto> candidates) throws JsonProcessingException;

    List<CandidateDetailsDto> findAssignedCandidatesToJob(Long idJob);
    void unassignedCandidate (Long idCandidate, Long idJob);

    void assignCandidateToJobs (Long idCandidate, List<Long> idJobs);

    Map<String,Long> statistics();
}
