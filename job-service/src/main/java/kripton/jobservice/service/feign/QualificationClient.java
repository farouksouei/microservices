package kripton.jobservice.service.feign;


import kripton.jobservice.model.dtos.qualification.CertificationDto;
import kripton.jobservice.model.dtos.qualification.EducationDto;
import kripton.jobservice.model.dtos.qualification.ExperienceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "qualification-service")
public interface QualificationClient {

    //Get By Job
    @GetMapping("api/qualifications/job/experience/{id-Job}")
    List<ExperienceDto> findExperienceByJob(@PathVariable(name = "id-Job")Long idJob);
    @GetMapping("api/qualifications/job/education/{id-Job}")
    List<EducationDto> findEducationByJob(@PathVariable(name = "id-Job")Long idJob);
    @GetMapping("api/qualifications/job/certification/{id-Job}")
    List<CertificationDto> findCertificationByJob(@PathVariable(name = "id-Job")Long idJob);
      //Post
    @PostMapping("api/qualifications/experience/{id-candidate}")
    List<ExperienceDto> saveExperienceByCandidate(@PathVariable(name = "id-candidate")Long idCandidate, @RequestBody ExperienceDto experienceDto);

    @PostMapping("api/qualifications/education/{id-candidate}")
    List<EducationDto> saveEducationByCandidate(@PathVariable(name = "id-candidate")Long idCandidate,@RequestBody EducationDto educationDto);

    @PostMapping("api/qualifications/certification/{id-candidate}")
    List<CertificationDto> saveCertificationByCandidate(@PathVariable(name = "id-candidate")Long idCandidate,@RequestBody CertificationDto certificationDto);

    //Post List of dtos and assign them to a certain candidate
    @PostMapping("api/qualifications/dtos-experience/{id-candidate}")
    void assignDtosExperienceByCandidate(@PathVariable(name = "id-candidate")Long idCandidate, @RequestBody List<ExperienceDto> experienceDtos);
    @PostMapping("api/qualifications/dtos-education/{id-candidate}")
    void assignDtosEducationByCandidate(@PathVariable(name = "id-candidate")Long idCandidate,@RequestBody List<EducationDto> educationDtos);
    @PostMapping("api/qualifications/dtos-certification/{id-candidate}")
    void assignDtosCertificationByCandidate(@PathVariable(name = "id-candidate")Long idCandidate,@RequestBody List<CertificationDto> certificationDtos);
}
