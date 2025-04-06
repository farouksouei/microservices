package kripton.jobservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kripton.jobservice.model.dtos.candidate.CandidateDetailsDto;
import kripton.jobservice.model.dtos.job.JobApplicationDto;
import kripton.jobservice.model.dtos.job.JobDto;
import kripton.jobservice.service.IJobApplicationService;
import kripton.jobservice.service.IJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {
    private final IJobService jobService ;
    private final IJobApplicationService jobApplicationService;

    @GetMapping
    public ResponseEntity<List<JobDto>> findAll(@RequestParam("sortByAttribute") String sort){
        return ResponseEntity.status(200).body(jobService.findJobs(sort));
    }
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public  ResponseEntity<JobDto> saveJob(@RequestPart("job") JobDto jobDto,
                                           @RequestPart("user")String userId,
                                           @RequestPart(value = "image" , required = false)MultipartFile image ) throws IOException {
        return ResponseEntity.status(201).body(jobService.saveJob(jobDto,userId,image));
    }
    @GetMapping("{job-id}")
    public  ResponseEntity<JobDto> findJobById(@PathVariable(value = "job-id") Long idJob){
        return ResponseEntity.status(200).body(jobService.findJobById(idJob));
    }
    @DeleteMapping("{job-id}")
    public ResponseEntity<?> deleteJobById(@PathVariable(value = "job-id")Long idJob){
        jobService.deleteJobById(idJob);
        return ResponseEntity.status(200).body(new SimpleResponseWrapper("success !"));
    }

    @PutMapping("/{job-id}")
    public ResponseEntity<JobDto> updateJob(@PathVariable("job-id") Long idJob,
                                            @RequestPart("job") JobDto jobDto,
                                            @RequestPart(value = "image",required = false)MultipartFile image) throws IOException{
        return ResponseEntity.ok(jobService.updateJob(idJob,jobDto,image));
    }
    @PutMapping("/assign-job/{job-id}/to-candidate/{candidate-id}")
    public ResponseEntity<?> assignCandidateToJob(@PathVariable("candidate-id") Long idCandidate, @PathVariable("job-id") Long idJob) throws JsonProcessingException {
        try {
            jobService.assignCandidateToJob(idJob,idCandidate);
            return ResponseEntity.status(200).body(new SimpleResponseWrapper("success !"));
        }catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    @PutMapping("/assign-multiple/{job-id}")
    public ResponseEntity<?> assignMultipleCandidatesToJob(@PathVariable("job-id")Long idJob,@RequestBody List<CandidateDetailsDto> candidates) throws JsonProcessingException {
        try {
            jobService.assignCandidatesToJob(idJob,candidates);
            return ResponseEntity.status(200).body(new SimpleResponseWrapper("success !"));
        }catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/assigned-candidates/{job-id}")
    public ResponseEntity<List<CandidateDetailsDto>> findAssignedCandidatesToJob(@PathVariable("job-id") Long idJob){
        return ResponseEntity.status(200).body(jobService.findAssignedCandidatesToJob(idJob)) ;
    }
    @GetMapping("/candidate/{candidate-id}")
    public ResponseEntity<List<JobDto>> findJobsThatTheCandidateIsNotAssignedTo(@PathVariable ("candidate-id") Long idCandidate){
        List<JobDto> jobApplications = jobApplicationService.findJobApplicationByNotCandidate (idCandidate);
        return ResponseEntity.ok(jobApplications);
    }

    @PutMapping("/assign-candidate/{candidate-id}/jobs")
    public ResponseEntity<SimpleResponseWrapper> assignCandidateToMultipleJobs(@PathVariable ("candidate-id") Long idCandidate,@RequestBody List<Long> idJobs){
        jobService.assignCandidateToJobs (idCandidate,idJobs);
        return ResponseEntity.ok (new SimpleResponseWrapper ("success"));
    }
    @DeleteMapping("/unassign-candidate/{candidate-id}/job/{job-id}")
    public ResponseEntity<SimpleResponseWrapper> removeAssignment(@PathVariable("candidate-id") Long idCandidate, @PathVariable("job-id") Long idJob){
        jobService.unassignedCandidate (idCandidate,idJob);
        return ResponseEntity.status(200).body(new SimpleResponseWrapper("assignment deleted successfully"));
    }
//    @PostMapping("/import-jobs")
//    public ResponseEntity<BatchStatus> importJobsFromCSV() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
//        JobParameters parameters = new JobParameters(new HashMap<>());
//        JobExecution jobExecution = jobLauncher.run(job, parameters);
//        log.warn("JobExecution: " + jobExecution.getStatus());
//
//        log.warn("Batch is Running...");
//        return ResponseEntity.ok(jobExecution.getStatus());
//    }

    @GetMapping("/job-applications/job/{job-id}")
    public ResponseEntity<List<JobApplicationDto>> findJobApplicationsOfJob(@PathVariable("job-id") Long idJob){
        return ResponseEntity.ok(jobApplicationService.findJobApplicationsByJob(idJob));
    }

    @PostMapping("/job-applications/candidate/{candidate-id}/job/{job-id}")
    public ResponseEntity<JobApplicationDto> saveJobApplication(@PathVariable ("candidate-id") Long idCandidate,
                                                                @PathVariable ("job-id") Long idJob,
                                                                @RequestBody JobApplicationDto dto) throws JsonProcessingException {
        return ResponseEntity.ok (jobApplicationService.saveJobApplication (dto,idCandidate,idJob));
    }
    @GetMapping("/job-applications/candidate/{candidate-id}")
    public ResponseEntity<List<JobApplicationDto>> findJobApplicationsOfCandidate(@PathVariable("candidate-id") Long idCandidate){
        return ResponseEntity.ok(jobApplicationService.findJobApplicationsByCandidate(idCandidate));
    }
    @PutMapping("/job-applications/{application-id}")
    public ResponseEntity<JobApplicationDto> updateJobApplication(@PathVariable("application-id") Long idApplication,@RequestBody JobApplicationDto application){
        return ResponseEntity.ok(jobApplicationService.updateJobApplication(idApplication,application ));
    }
    @GetMapping("/job-applications")
    public ResponseEntity<List<JobApplicationDto>> getAllJobApplications(){
        List<JobApplicationDto> jobApplications = jobApplicationService.findJobApplication();
        return ResponseEntity.ok(jobApplications);
    }

    @GetMapping("/job-applications/not-candidate/{id-candidate}")
    public ResponseEntity<List<JobDto>> getAllJobApplicationsByNotCandidate(@PathVariable ("id-candidate") Long idCandidate){
        List<JobDto> jobApplications = jobApplicationService.findJobApplicationByNotCandidate (idCandidate);
        return ResponseEntity.ok(jobApplications);
    }

    @DeleteMapping("/job-applications/{application-id}")
    public ResponseEntity<SimpleResponseWrapper> deleteJobApplication(@PathVariable("application-id") Long idApplication){
        return ResponseEntity.ok(new SimpleResponseWrapper(jobApplicationService.deleteJobApplication(idApplication)));
    }
    @PostMapping("/job-applications/multiple")
    public ResponseEntity<SimpleResponseWrapper> deleteJobApplication(@RequestBody List<Long> idsApplication){
        return ResponseEntity.ok(new SimpleResponseWrapper(jobApplicationService.deleteMultipleJobApplications (idsApplication)));
    }
    @DeleteMapping("/job-applications/candidate/{candidate-id}")
    public ResponseEntity<?>deleteJobApplicationOfCandidate(@PathVariable ("candidate-id") Long idCandidate){
        jobApplicationService.deleteJobApplicationsByCandidate (idCandidate);
        return ResponseEntity.ok (new SimpleResponseWrapper ("Job Application deleted successfully !"));
    }
    public CompletableFuture<String> fallbackMethod(RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(()-> "Something went wrong : " +runtimeException.getMessage());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String,Long>> getStats(){
        return ResponseEntity.ok (jobService.statistics ());
    }
}
