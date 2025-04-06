package kripton.qualificationservice.controller;

import kripton.qualificationservice.model.dtos.CertificationDto;
import kripton.qualificationservice.model.dtos.EducationDto;
import kripton.qualificationservice.model.dtos.ExperienceDto;
import kripton.qualificationservice.service.ICertificationService;
import kripton.qualificationservice.service.IEducationService;
import kripton.qualificationservice.service.IExperienceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qualifications")
@RequiredArgsConstructor
@Slf4j
public class QualificationController {
    private final IExperienceService experienceService;
    private final IEducationService educationService;
    private final ICertificationService certificationService;


    //Get By Candidate
    @GetMapping("/candidate/experience/{id-candidate}")
    List<ExperienceDto> findExperienceByCandidate(@PathVariable(value = "id-candidate")Long idCandidate){
        log.info("fetching candidate's experience : {} ",idCandidate);
        return experienceService.findExperienceByCandidate(idCandidate);
    }
    @GetMapping("/candidate/education/{id-candidate}")
    List<EducationDto> findEducationByCandidate(@PathVariable(value = "id-candidate")Long idCandidate){
        log.info("fetching candidate's education : {} ",idCandidate);
        return educationService.findEducationByCandidate(idCandidate);
    }
    @GetMapping("/candidate/certification/{id-candidate}")
    List<CertificationDto> findCertificationByCandidate(@PathVariable(value = "id-candidate")Long idCandidate){
        log.info("fetching candidate's certification : {} ",idCandidate);
        return certificationService.findCertificationByCandidate(idCandidate);
    }

    //Get By Job
    @GetMapping("/job/experience/{id-job}")
    List<ExperienceDto> findExperienceByJob(@PathVariable(value = "id-job")Long idJob){
        log.info("fetching job's experience : {} ",idJob);
        return experienceService.findExperienceByJob(idJob);
    }
    @GetMapping("/job/education/{id-job}")
    List<EducationDto> findEducationByJob(@PathVariable(name = "id-job")Long idJob){
        log.info("fetching job's education : {} ",idJob);
        return educationService.findEducationByJob(idJob);
    }
    @GetMapping("/job/certification/{id-job}")
    List<CertificationDto> findCertificationByJob(@PathVariable(name = "id-job")Long idJob){
        log.info("fetching job's certification : {} ",idJob);
        return certificationService.findCertificationByJob(idJob);
    }
    //Post
    @PostMapping("/experience/{id-candidate}")
    ExperienceDto saveExperienceByCandidate(@PathVariable(name = "id-candidate")Long idCandidate, @RequestBody ExperienceDto experienceDto){
        return experienceService.saveExperience(experienceDto,idCandidate);
    }
    @PostMapping("/education/{id-candidate}")
    EducationDto saveEducationByCandidate(@PathVariable(name = "id-candidate")Long idCandidate,@RequestBody EducationDto educationDto){
        return educationService.saveEducation(educationDto,idCandidate);
    }
    @PostMapping("/certification/{id-candidate}")
    CertificationDto saveCertificationByCandidate(@PathVariable(name = "id-candidate")Long idCandidate,@RequestBody CertificationDto certificationDto){
        return certificationService.saveCertification(certificationDto,idCandidate);
    }

    //Post List of dtos and assign them to a certain candidate
    @PostMapping("/dtos-experience/{id-candidate}")
    void assignDtosExperienceByCandidate(@PathVariable(name = "id-candidate")Long idCandidate, @RequestBody List<ExperienceDto> experienceDtos){
        experienceService.assignListOfDtoExperiencesToCandidate(idCandidate,experienceDtos);
    }
    @PostMapping("/dtos-education/{id-candidate}")
    void assignDtosEducationByCandidate(@PathVariable(name = "id-candidate")Long idCandidate,@RequestBody List<EducationDto> educationDtos){
        educationService.assignListOfDtoEducationsToCandidate(idCandidate, educationDtos);
    }
    @PostMapping("/dtos-certification/{id-candidate}")
    void assignDtosCertificationByCandidate(@PathVariable(name = "id-candidate")Long idCandidate,@RequestBody List<CertificationDto> certificationDtos){
        certificationService.assignListOfDtoCertificationsToCandidate(idCandidate, certificationDtos);
    }


}
