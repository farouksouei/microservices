package kripton.qualificationservice.service;

import kripton.qualificationservice.model.dtos.ExperienceDto;

import java.util.List;

public interface IExperienceService {
    ExperienceDto saveExperience(ExperienceDto experienceDto,Long idCandidate);
    List<ExperienceDto> findExperienceByCandidate(Long idCandidate);
    List<ExperienceDto> findExperienceByJob(Long idJob);
    void deleteExperience(Long idExp);
    void assignExperienceToCandidate(Long idCandidate,Long idExp );
    void assignListOfExperiencesToCandidate(Long idCandidate,List<Long>experiences);
    void assignListOfDtoExperiencesToCandidate(Long idCandidate,List<ExperienceDto> experienceDtos);
}
