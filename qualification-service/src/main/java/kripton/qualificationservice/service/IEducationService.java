package kripton.qualificationservice.service;

import kripton.qualificationservice.model.dtos.EducationDto;

import java.util.List;

public interface IEducationService {
    EducationDto saveEducation(EducationDto educationDto,Long idCandidate);
    EducationDto findEducationById(Long idEducation);
    List<EducationDto> findAll();
    List<EducationDto> findEducationByCandidate(Long idCandidate);
    List<EducationDto> findEducationByJob(Long idJob);
    void deleteEducation(Long idEducation);
    void assignEducationToCandidate(Long idCandidate,Long idEducation );
    void assignListOfEducationsToCandidate(Long idCandidate,List<Long>educations);
    void assignListOfDtoEducationsToCandidate(Long idCandidate,List<EducationDto>educationDtos);
}
