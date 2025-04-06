package kripton.qualificationservice.service;

import kripton.qualificationservice.model.dtos.EducationDto;
import kripton.qualificationservice.model.entities.EducationEntity;
import kripton.qualificationservice.model.repositories.EducationEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EducationService implements IEducationService{
    private final EducationEntityRepository educationRepository;
    private final ModelMapper mapper;
    @Override
    public EducationDto saveEducation(EducationDto educationDto,Long idCandidate) {
        EducationEntity entity = mapper.map(educationDto, EducationEntity.class);
        entity.setCandidate_id(idCandidate);
        educationRepository.save(entity);
        return mapper.map(entity,EducationDto.class);
    }

    @Override
    public EducationDto findEducationById(Long idEducation) {
        Optional<EducationEntity> entity = educationRepository.findById(idEducation);
        if (entity.isPresent()){
            return mapper.map(entity.get(),EducationDto.class);
        }
        return null;
    }

    @Override
    public List<EducationDto> findAll() {
        List<EducationEntity> entityList = educationRepository.findAll();
        return entityList.stream().map(educationEntity -> mapper.map(educationEntity, EducationDto.class)).toList();
    }

    @Override
    public List<EducationDto> findEducationByCandidate(Long idCandidate) {
        List<EducationEntity> entityList = educationRepository.findByCandidate_id(idCandidate);
        return entityList.stream().map(educationEntity -> mapper.map(educationEntity,EducationDto.class)).toList();
    }

    @Override
    public List<EducationDto> findEducationByJob(Long idJob) {
        List<EducationEntity> entityList = educationRepository.findByJob_id(idJob);
        return entityList.stream().map(educationEntity -> mapper.map(educationEntity,EducationDto.class)).toList();
    }

    @Override
    public void deleteEducation(Long idEducation) {
        Optional<EducationEntity> entity = educationRepository.findById(idEducation);
        if (entity.isPresent()){
            educationRepository.delete(entity.get());
        }else {
            log.warn("education with Id {} not found in DB",idEducation);
        }
    }

    @Override
    public void assignEducationToCandidate(Long idCandidate, Long idEducation) {
        Optional<EducationEntity> entity = educationRepository.findById(idEducation);
        if (entity.isPresent()){
            EducationEntity educationEntity = entity.get();
            educationEntity.setCandidate_id(idCandidate);
            educationRepository.save(educationEntity);
        }else {
            log.warn("education with Id {} not found in DB",idEducation);
        }

    }

    @Override
    public void assignListOfEducationsToCandidate(Long idCandidate, List<Long> educations) {
        List<EducationEntity> entityList = educationRepository.findAllById(educations);
        entityList.stream().forEach(educationEntity -> {
            educationEntity.setCandidate_id(idCandidate);
            educationRepository.save(educationEntity);
        });
    }

    @Override
    public void assignListOfDtoEducationsToCandidate(Long idCandidate, List<EducationDto> educationDtos) {
        List<EducationEntity> educationEntities = educationDtos.stream().map(educationDto -> mapper.map(educationDto, EducationEntity.class)).toList();
        educationEntities.forEach(educationEntity -> {
            educationEntity.setCandidate_id(idCandidate);
            educationRepository.save(educationEntity);
        });
    }
}
