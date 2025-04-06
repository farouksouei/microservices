package kripton.jobservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kripton.jobservice.model.dtos.candidate.CandidateDetailsDto;
import kripton.jobservice.model.dtos.job.JobApplicationDto;
import kripton.jobservice.model.dtos.job.JobApplicationKafkaDto;
import kripton.jobservice.model.dtos.job.JobDto;
import kripton.jobservice.model.entities.JobApplicationEntity;
import kripton.jobservice.model.entities.JobEntity;
import kripton.jobservice.model.repositories.EntityJobRepository;
import kripton.jobservice.model.repositories.JobApplicationEntityRepository;
import kripton.jobservice.service.feign.CandidateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobApplicationService implements IJobApplicationService{
    private final ModelMapper mapper;
    private final JobApplicationEntityRepository jobAppRepository ;
    private final EntityJobRepository entityJobRepository;

    private final CandidateClient candidateClient;
    private final KafkaTemplate<String,String> kafkaTemplate ;
    @Override
    public JobApplicationDto saveJobApplication(JobApplicationDto jobApplicationdto, Long idCandidate,Long idJob) throws JsonProcessingException {
        JobApplicationEntity jobApplication = mapper.map(jobApplicationdto, JobApplicationEntity.class);
        jobApplication.setCandidate_id(idCandidate);
        CandidateDetailsDto candidateDetailsDto = candidateClient.findByCandidateId(idCandidate);
        Optional<JobEntity> optionalJob = entityJobRepository.findById(idJob);
        if (optionalJob.isPresent()){
        jobApplication.setJob(optionalJob.get());
        jobApplication.setCreatedAt (new Date ());
        //sending notification
        ObjectMapper objectMapper = new ObjectMapper();
        String jobString = objectMapper.writeValueAsString(optionalJob.get());
        String candidateString = objectMapper.writeValueAsString(candidateDetailsDto);
        String jobApplicationString = objectMapper.writeValueAsString(jobApplication);
            JobApplicationKafkaDto kafkaDto = JobApplicationKafkaDto.builder().jobApplication(jobApplicationString).candidate(candidateString).job(jobString).build();
            String kafkaDataString = objectMapper.writeValueAsString(kafkaDto);
        kafkaTemplate.send("job-application",kafkaDataString);
        }
        return mapper.map(jobAppRepository.save(jobApplication), JobApplicationDto.class);
    }

    @Override
    public List<JobApplicationDto> findJobApplication() {
        List<JobApplicationDto> jobApplicationDtos = new ArrayList<>();
        Sort sort = Sort.by (Sort.Direction.DESC,"createdAt");
        for (JobApplicationEntity jobApplication : jobAppRepository.findAll(sort)) {
            JobApplicationDto jobApplicationDto = mapper.map(jobApplication,JobApplicationDto.class);
            JobEntity job = jobApplication.getJob();
            jobApplicationDto.setJob(mapper.map(job, JobDto.class));
            jobApplicationDto.setCandidate(candidateClient.findByCandidateId(jobApplication.getCandidate_id()));
            jobApplicationDtos.add(jobApplicationDto);
        }
        return jobApplicationDtos;
    }


    @Override
    public List<JobApplicationDto> findJobApplicationsByCandidate(Long idCandidate) {
        List<JobApplicationDto> jobApplicationDtos = new ArrayList<>();
        for (JobApplicationEntity jobApplication : jobAppRepository.findByCandidateId(idCandidate)) {
            JobApplicationDto jobApplicationDto = mapper.map(jobApplication,JobApplicationDto.class);
            JobEntity job = jobApplication.getJob();
            jobApplicationDto.setJob(mapper.map(job, JobDto.class));
            jobApplicationDto.setCandidate(candidateClient.findByCandidateId(jobApplication.getCandidate_id()));
            jobApplicationDtos.add(jobApplicationDto);
        }
        return jobApplicationDtos;
    }

    @Override
    public List<JobApplicationDto> findJobApplicationsByJob(Long idJob) {
        List<JobApplicationDto> jobApplicationDtos = new ArrayList<>();
        for (JobApplicationEntity jobApplication : jobAppRepository.findByJobId(idJob)) {
            JobApplicationDto jobApplicationDto = mapper.map(jobApplication,JobApplicationDto.class);
            JobEntity job = jobApplication.getJob();
            jobApplicationDto.setJob(mapper.map(job, JobDto.class));
            jobApplicationDto.setCandidate(candidateClient.findByCandidateId(jobApplication.getCandidate_id()));
            jobApplicationDtos.add(jobApplicationDto);
        }
        return jobApplicationDtos;
    }

    @Override
    public String deleteJobApplication(Long idJobApp) {
        Optional<JobApplicationEntity> optional = jobAppRepository.findById(idJobApp);
        if (optional.isPresent()){
        jobAppRepository.delete(optional.get());
        log.info("deleting job application {}",idJobApp);
        return "deleted successfully !";
        }else {
            log.warn("job application {} not found ",idJobApp);
            return "job application not found !";
        }
    }

    @Override
    public JobApplicationDto updateJobApplication(Long idJobApplication, JobApplicationDto jobApplicationdto) {
        Optional<JobApplicationEntity> optional = jobAppRepository.findById(idJobApplication);
        if (optional.isPresent()){
            JobApplicationEntity jobApplication = optional.get();
            jobApplication.setJob (jobApplication.getJob ());
            jobApplication.setCandidate_id (jobApplication.getCandidate_id ());
            jobApplication.setAvailability(jobApplicationdto.isAvailability());
            jobApplication.setCoverLetter(jobApplicationdto.getCoverLetter());
            jobApplication.setDesiredSalary(jobApplicationdto.getDesiredSalary());
            JobApplicationEntity savedEntity = jobAppRepository.save(jobApplication);
            return mapper.map(jobAppRepository.save(savedEntity), JobApplicationDto.class);
        }
        log.warn("job app not found");
        return null;
    }


    @Override
    public void deleteJobApplicationsByCandidate (Long idCandidate) {
        jobAppRepository.deleteByCandidate_id (idCandidate);
    }

    @Override
    public List<JobDto> findJobApplicationByNotCandidate (Long idCandidate) {

        List<JobEntity> listOfAllJobs = entityJobRepository.findAll ();
        log.info ("---------------------------------------- jobs in db {}",listOfAllJobs.size ());
        List<JobEntity> listOfJobsThatCandidateAppliedFor =
                jobAppRepository.findByCandidateId (idCandidate)
                        .stream ()
                        .map (JobApplicationEntity::getJob)
                        .toList ();
        log.info ("---------------------------------------- jobs candidate applied for {}",listOfJobsThatCandidateAppliedFor.size ());

        listOfAllJobs.removeAll (listOfJobsThatCandidateAppliedFor);

        List<JobDto> allJobApplicationsInDb =
                new ArrayList<> (listOfAllJobs.stream().map (jobEntity -> mapper.map (jobEntity,JobDto.class)).toList ());
        listOfJobsThatCandidateAppliedFor.stream().map ((jobEntity -> mapper.map (jobEntity,JobDto.class))).forEach (jobDto -> {
            jobDto.setCandidateAssignedTo (true);
            allJobApplicationsInDb.add (jobDto);
        });

        return allJobApplicationsInDb ;
    }

    @Override
    public String deleteMultipleJobApplications (List<Long> jobApplicationIds) {
        jobApplicationIds.forEach ((appId)->{
            if(jobAppRepository.findById (appId).isPresent ()){
                jobAppRepository.deleteById (appId);
            }
        });
        return "done !";
    }
}

