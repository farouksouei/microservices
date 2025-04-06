package kripton.jobservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kripton.jobservice.model.dtos.job.JobApplicationDto;
import kripton.jobservice.model.dtos.job.JobDto;

import java.util.List;

public interface IJobApplicationService {
    JobApplicationDto saveJobApplication(JobApplicationDto jobApplication ,Long idCandidate,Long idJob) throws JsonProcessingException;

    List<JobApplicationDto> findJobApplication();
    List<JobApplicationDto> findJobApplicationsByCandidate(Long idCandidate);

    List<JobApplicationDto> findJobApplicationsByJob(Long idJob);

    String deleteJobApplication(Long idJobApp);
    JobApplicationDto updateJobApplication(Long idJobApplication,JobApplicationDto jobApplication);


    void deleteJobApplicationsByCandidate(Long idCandidate);

    List<JobDto> findJobApplicationByNotCandidate(Long idCandidate);

    String deleteMultipleJobApplications(List<Long>jobApplicationIds);
}
