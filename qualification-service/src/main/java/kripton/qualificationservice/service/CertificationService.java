package kripton.qualificationservice.service;

import kripton.qualificationservice.model.dtos.CertificationDto;
import kripton.qualificationservice.model.entities.CertificationEntity;
import kripton.qualificationservice.model.repositories.CertificationEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationService implements ICertificationService{
    private final ModelMapper mapper;
    private final CertificationEntityRepository certificationRepository;
    @Override
    public CertificationDto saveCertification(CertificationDto certificationDto,Long idCandidate) {
        CertificationEntity entity = mapper.map(certificationDto, CertificationEntity.class);
        entity.setCandidate_id(idCandidate);
        return mapper.map(certificationRepository.save(entity),CertificationDto.class);
    }

    @Override
    public CertificationDto findCertificationById(Long idCertification) {
        Optional<CertificationEntity> entity = certificationRepository.findById(idCertification);
        return mapper.map(entity,CertificationDto.class);
    }

    @Override
    public List<CertificationDto> findAll() {
        List<CertificationEntity> entityList = certificationRepository.findAll();
        return entityList.stream().map(certificationEntity -> mapper.map(certificationEntity,CertificationDto.class)).toList();
    }

    @Override
    public List<CertificationDto> findCertificationByCandidate(Long idCandidate) {
        List<CertificationEntity> entityList = certificationRepository.findByCandidate_id(idCandidate);
        return entityList.stream().map(certificationEntity -> mapper.map(certificationEntity,CertificationDto.class)).toList();
    }

    @Override
    public List<CertificationDto> findCertificationByJob(Long idJob) {
        List<CertificationEntity> entityList = certificationRepository.findByJob_id(idJob);
        return entityList.stream().map(certificationEntity -> mapper.map(certificationEntity,CertificationDto.class)).toList();
    }

    @Override
    public void deleteCertification(Long idCertification) {
        Optional<CertificationEntity> entity = certificationRepository.findById(idCertification);
        if (entity.isPresent()){
            certificationRepository.delete(entity.get());
        }
        else {
            log.warn("entity with id {} not found in DB",idCertification);
        }
    }

    @Override
    public void assignCertificationToCandidate(Long idCandidate, Long idCertification) {
        Optional<CertificationEntity> entity = certificationRepository.findById(idCertification);
        if (entity.isPresent()){
            CertificationEntity certificationEntity = entity.get();
            certificationEntity.setCandidate_id(idCandidate);
            certificationRepository.save(certificationEntity);
        }
        else {
            log.warn("entity with id {} not found in DB",idCertification);
        }
    }

    @Override
    public void assignListOfCertificationsToCandidate(Long idCandidate, List<Long> certifications) {
        List<CertificationEntity> entityList = certificationRepository.findAllById(certifications);
        entityList.stream().forEach(certificationEntity -> {
            certificationEntity.setCandidate_id(idCandidate);
            certificationRepository.save(certificationEntity);
        });
    }

    @Override
    public void assignListOfDtoCertificationsToCandidate(Long idCandidate, List<CertificationDto> certificationDtos) {
        List<CertificationEntity> certificationEntities = certificationDtos.stream().map(certificationDto -> mapper.map(certificationDto, CertificationEntity.class)).toList();
        certificationEntities.forEach(certificationEntity -> {
            certificationEntity.setCandidate_id(idCandidate);
            certificationRepository.save(certificationEntity);
        });
    }
}
