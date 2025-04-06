package kripton.qualificationservice.service;

import kripton.qualificationservice.model.dtos.CertificationDto;

import java.util.List;

public interface ICertificationService {

    CertificationDto saveCertification(CertificationDto certificationDto,Long idCandidate);
    CertificationDto findCertificationById(Long idCertification);
    List<CertificationDto> findAll();
    List<CertificationDto> findCertificationByCandidate(Long idCandidate);
    List<CertificationDto> findCertificationByJob(Long idJob);
    void deleteCertification(Long idCertification);
    void assignCertificationToCandidate(Long idCandidate,Long idCertification );
    void assignListOfCertificationsToCandidate(Long idCandidate,List<Long>certifications);
    void assignListOfDtoCertificationsToCandidate(Long idCandidate,List<CertificationDto>certificationDtos);
}
