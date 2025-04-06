package kripton.jobservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.NotFoundException;
import kripton.jobservice.model.dtos.candidate.CandidateDetailsDto;
import kripton.jobservice.model.dtos.job.JobApplicationDto;
import kripton.jobservice.model.dtos.job.JobDto;
import kripton.jobservice.model.entities.*;
import kripton.jobservice.model.repositories.EntityJobRepository;
import kripton.jobservice.model.repositories.JobApplicationEntityRepository;
import kripton.jobservice.service.feign.CandidateClient;
import kripton.jobservice.service.feign.QualificationClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@Slf4j
public class JobService implements IJobService{
    private final JobApplicationEntityRepository jobApplicationEntityRepository;
    private final FileManagementService fileManagementService;
    public JobService (JobApplicationEntityRepository jobApplicationEntityRepository,FileManagementService fileManagementService,
                       EntityJobRepository entityJobRepository, JobApplicationService jobApplicationService, KafkaTemplate<String, String> kafkaTemplate, QualificationClient qualificationClient, CandidateClient candidateClient, ModelMapper mapper) {
        this.jobApplicationEntityRepository = jobApplicationEntityRepository;
        this.fileManagementService = fileManagementService;
        this.entityJobRepository = entityJobRepository;
        this.jobApplicationService = jobApplicationService;
        this.kafkaTemplate = kafkaTemplate;
        this.qualificationClient = qualificationClient;
        this.candidateClient = candidateClient;
        this.mapper = mapper;
    }



    private final EntityJobRepository entityJobRepository;
    private final JobApplicationService jobApplicationService;
    private final KafkaTemplate<String,String> kafkaTemplate ;
    private final QualificationClient qualificationClient;
    private final CandidateClient candidateClient ;
    private final ModelMapper mapper ;
    @Transactional
    @Override
    public JobDto saveJob(JobDto jobDto,String idUser, MultipartFile image) throws IOException {
        JobEntity entity = mapper.map(jobDto, JobEntity.class);
        entity.setJobApplications(new ArrayList<>());
        entity.setCreatedAt(new Date());
        entity.setModifiedAt(new Date());
        entity.setStatus(StatusOfJob.OPEN);
        entity.setIsValidated (false);
        entity.setUser_id (idUser);
        //image
        if(image != null){
            entity.setImagePath( saveImage(image));
        }
        JobEntity savedEntity = entityJobRepository.save(entity);
        ObjectMapper objectMapper = new ObjectMapper();
        String entityToString = objectMapper.writeValueAsString(savedEntity);
        kafkaTemplate.send("job-posted",entityToString );
        return mapper.map(entity, JobDto.class);
    }

    @Override
    public JobDto findJobById(Long idJob) {
        Optional<JobEntity> jobEntity = entityJobRepository.findById(idJob);
        if (jobEntity.isPresent()){
            JobEntity job = jobEntity.get();
            JobDto dto = mapper.map(job, JobDto.class);

            return dto;
        }
        return null;
    }
    @Transactional
    @Override
    public JobDto updateJob(Long idJob, JobDto jobDto,MultipartFile image) throws IOException {
        Optional<JobEntity> optional = entityJobRepository.findById(idJob);
        if (optional.isPresent()){
            JobEntity jobEntity = optional.get ();
            JobEntity modifiedEntity = mapper.map(jobDto, JobEntity.class);
            modifiedEntity.setId(idJob);
            modifiedEntity.setJobApplications(new ArrayList<>());
            modifiedEntity.setModifiedAt(new Date());
            modifiedEntity.setUser_id (jobEntity.getUser_id ());
            if (image != null){
                modifiedEntity.setImagePath( saveImage(image));
            }else {
                modifiedEntity.setImagePath (jobEntity.getImagePath ());
            }
            JobEntity job = entityJobRepository.save(modifiedEntity);
            JobDto dto = mapper.map(job, JobDto.class);
//            List<CertificationDto> certificationByJob = qualificationClient.findCertificationByJob(job.getId());
//            List<EducationDto> educationByJob = qualificationClient.findEducationByJob(job.getId());
//            List<ExperienceDto> experienceByJob = qualificationClient.findExperienceByJob(job.getId());
//            dto.setRequiredExperience(experienceByJob);
//            dto.setRequiredCertifications(certificationByJob);
//            dto.setRequiredEducation(educationByJob);
            return dto;
        }
        return null;
    }

    @Override
    public void deleteJobById(Long idJob) {
        Optional<JobEntity> optional = entityJobRepository.findById(idJob);
        if (optional.isPresent()){
            JobEntity job = optional.get();
            entityJobRepository.delete(job);
        }

    }
    @Override
    public List<JobDto> findJobs(String sortByAttribute ) {

        Sort sort = Sort.by (Sort.Direction.DESC,sortByAttribute);
        if(sortByAttribute.equals ("title")){
            sort = Sort.by (Sort.Direction.ASC,sortByAttribute);
        }
        List<JobEntity> all = entityJobRepository.findAll(sort);
        return all.stream().map((jobEntity -> mapper.map(jobEntity,JobDto.class))).toList();

//        all.forEach(job -> {
//            JobDto dto = mapper.map(job, JobDto.class);

//            List<CertificationDto> certificationByJob = qualificationClient.findCertificationByJob(job.getId());
//            List<EducationDto> educationByJob = qualificationClient.findEducationByJob(job.getId());
//            List<ExperienceDto> experienceByJob = qualificationClient.findExperienceByJob(job.getId());
//            dto.setRequiredExperience(experienceByJob);
//            dto.setRequiredCertifications(certificationByJob);
//            dto.setRequiredEducation(educationByJob);
//            jobDtos.add(dto);
//        });
//        return jobDtos;
    }
    @Transactional
    @Override
    public void assignCandidateToJob(Long idJob, Long idCandidate) throws JsonProcessingException {
        Optional<JobEntity> jobEntityOptional = entityJobRepository.findById(idJob);
        CandidateDetailsDto candidate = candidateClient.findByCandidateId(idCandidate);
        if (jobEntityOptional.isPresent() && candidate != null){
            JobApplicationDto jobApplicationDto = jobApplicationService.saveJobApplication(new JobApplicationDto(), idCandidate, idJob);
            log.info("success .. assigned candidate {} ----> job {}",idCandidate,idJob);
        }
    }

    @Override
    public void assignCandidatesToJob(Long idJob, List<CandidateDetailsDto> candidates) throws JsonProcessingException {
        Optional<JobEntity> jobEntityOptional = entityJobRepository.findById(idJob);
        if (jobEntityOptional.isPresent()){
            for(CandidateDetailsDto c : candidates){
                    JobApplicationDto jobApplicationDto = jobApplicationService.saveJobApplication(new JobApplicationDto(), c.getId(), idJob);
                    log.info("success .. assigned candidate {} ----> job {}",c.getId(),idJob);
            }
        }else {
            throw new NotFoundException("job not found in DB");
        }
    }

    @Override
    public List<CandidateDetailsDto> findAssignedCandidatesToJob(Long idJob) {
        List<Long> assignedCandidatesToJob = jobApplicationEntityRepository.findAssignedCandidatesToJob(idJob);
        List<CandidateDetailsDto> candidates = new ArrayList<>();
        if (assignedCandidatesToJob != null){
            assignedCandidatesToJob.forEach((idCandidate)->{
                CandidateDetailsDto candidateDetailsDto = candidateClient.findByCandidateId(idCandidate);
                candidates.add(candidateDetailsDto);
            });
            return candidates;
        }
        return null;
    }

    @Override
    public void unassignedCandidate (Long idCandidate, Long idJob) {
        Optional<JobEntity> optional = entityJobRepository.findById(idJob);
        optional.ifPresent(jobEntity -> {
            jobApplicationEntityRepository.deleteByCandidate_idAndJob(idCandidate, jobEntity);
            log.info("deleting assignment of candidate {} to job : {}",idCandidate,jobEntity.getTitle());
        });
    }

    @Override
    public void assignCandidateToJobs (Long idCandidate, List<Long> idJobs) {
        CandidateDetailsDto candidateDetailsDto  = candidateClient.findByCandidateId (idCandidate);
        if (candidateDetailsDto != null){
            idJobs.forEach ((id)->{
                Optional<JobEntity> jobEntity = entityJobRepository.findById (id);
                if (jobEntity.isPresent ()){
                    jobApplicationEntityRepository.save (
                            JobApplicationEntity.builder ()
                                    .candidate_id (idCandidate)
                                    .job (jobEntity.get ())
                                    .createdAt (new Date())
                            .build ());
                }else {
                    log.warn("something went wrong , job not found in db");
                }
            });
        }

    }

    @Override
    public Map<String, Long> statistics () {
        Map<String,Long> stats = new HashMap<> ();
        stats.put ("openJobs",entityJobRepository.countByStatus (StatusOfJob.OPEN));
        stats.put ("closedJobs",entityJobRepository.countByStatus (StatusOfJob.CLOSED));
        stats.put ("filledJobs",entityJobRepository.countByStatus (StatusOfJob.FILLED));
        stats.put ("fullTimeJobs",entityJobRepository.countByEmploymentType (EmploymentType.FULL_TIME));
        stats.put ("contractJobs",entityJobRepository.countByEmploymentType (EmploymentType.CONTRACT));
        stats.put ("partTimeJobs",entityJobRepository.countByEmploymentType (EmploymentType.PART_TIME));
        stats.put ("temporaryJobs",entityJobRepository.countByEmploymentType (EmploymentType.TEMPORARY));
        stats.put ("seniorJobs",entityJobRepository.countByExperienceLevel (ExperienceLevel.SENIOR));
        stats.put ("entryJobs",entityJobRepository.countByExperienceLevel (ExperienceLevel.ENTRY_LEVEL));
        stats.put ("midJobs",entityJobRepository.countByExperienceLevel (ExperienceLevel.MID_LEVEL));
        stats.put ("allJobApps",jobApplicationEntityRepository.countFirstBy ());
        stats.put("allJobs",entityJobRepository.count ());
        // last month
        LocalDate currentDate = LocalDate.now ();
        LocalDate lastMonthDate = currentDate.minusMonths (1).with (TemporalAdjusters.lastDayOfMonth ());
        Date currentDateAsDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date lastMonthDateAsDate = Date.from(lastMonthDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        stats.put ("applicationLastMonth",
                jobApplicationEntityRepository.countByCreatedAtBetween (lastMonthDateAsDate,currentDateAsDate
                ));



        return stats;
    }
    private String saveImage(MultipartFile image) {
        return fileManagementService.uploadToAzure (image,"jobs");
    }
}
