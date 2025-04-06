package kripton.qualificationservice.service;

import kripton.qualificationservice.model.dtos.ExperienceDto;
import kripton.qualificationservice.model.entities.ExperienceEntity;
import kripton.qualificationservice.model.repositories.ExperienceEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ExperienceService implements IExperienceService{
    private final ExperienceEntityRepository experienceRepository;
    private final ModelMapper mapper;

    @Override
    public ExperienceDto saveExperience(ExperienceDto experienceDto,Long idCandidate) {
        ExperienceEntity entity = mapper.map(experienceDto, ExperienceEntity.class);
        entity.setCandidate_id(idCandidate);
        return mapper.map (experienceRepository.save (entity),ExperienceDto.class);
    }



    @Override
    public List<ExperienceDto> findExperienceByCandidate(Long idCandidate) {
        List<ExperienceEntity> all = experienceRepository.findByCandidate_id(idCandidate);
        return all.stream().map (experienceEntity -> mapper.map (experienceEntity,ExperienceDto.class)).toList ();
    }

    @Override
    public List<ExperienceDto> findExperienceByJob(Long idJob) {
        List<ExperienceEntity> entityList = experienceRepository.findByJob_id(idJob);
        return entityList.stream().map(experienceEntity -> mapper.map(experienceEntity, ExperienceDto.class)).toList();
    }

    @Override
    public void deleteExperience(Long idExp) {
        if (experienceRepository.findById(idExp).isPresent()){
            experienceRepository.deleteById(idExp);
        }else {
            log.error("not found in database : {}",idExp);
        }
    }

    @Override
    public void assignExperienceToCandidate(Long idCandidate, Long idExp) {
        ExperienceEntity experienceEntity = experienceRepository.findById(idExp).orElse(null);
        if (experienceEntity != null){
            experienceEntity.setCandidate_id(idCandidate);
            experienceRepository.save(experienceEntity);
        }else {
            log.warn("Experience entity {} not found in DataBase !",idExp);
        }
    }

    @Override
    public void assignListOfExperiencesToCandidate(Long idCandidate, List<Long> experiences) {
        List<ExperienceEntity> experienceEntities = experienceRepository.findAllById(experiences);
        experienceEntities.forEach(experienceEntity -> {
            experienceEntity.setCandidate_id(idCandidate);
            experienceRepository.save(experienceEntity);
        });
        log.info("assign list of experiences to candidate {} was successful !",idCandidate);
    }

    @Override
    public void assignListOfDtoExperiencesToCandidate(Long idCandidate, List<ExperienceDto> experienceDtos) {
        experienceDtos.forEach(experienceDto -> {
            ExperienceEntity entity = mapper.map(experienceDto,ExperienceEntity.class);
            entity.setCandidate_id(idCandidate);
            experienceRepository.save(entity);
        });
    }
}
