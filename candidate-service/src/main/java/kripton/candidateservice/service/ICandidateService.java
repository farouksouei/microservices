package kripton.candidateservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import kripton.candidateservice.model.dtos.CandidateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import kripton.candidateservice.model.dtos.CandidateDetailsDto;

public interface ICandidateService {

    void deleteCandidate (Long idCandidate);
    List<CandidateDetailsDto>findAllCandidatesWithDetails(String sortByAttribute);
    public void sendMultipleEmails(List<Long> candidates,String topic);

    CandidateDetailsDto findCandidateWithDetails(Long idCandidate);
    CandidateDetailsDto saveCandidateWithDetails(CandidateDetailsDto candidateDetailsDto,String idUser, MultipartFile image) throws IOException, IllegalAccessException;
    CandidateDetailsDto updateCandidateWithDetails(CandidateDetailsDto candidateDetailsDto,Long idCandidate, MultipartFile image) throws IOException,IllegalAccessException;
    void sendEmailOfCompletionToCandidates(List<Long> idCandidates);
    List<String> checkIfCandidateInfoIsCompleted(Long idCandidate) throws IllegalAccessException;
    List<CandidateDetailsDto>findAllCandidatesNotCompleted();

    CandidateDetailsDto createCandidateAuto(String firstName,String lastName,String email);
    CandidateDetailsDto findCandidateByEmailFirstNameLastName(String firstName,String lastName,String email);
//    ResponseEntity<?> createCandidateFromResume(MultipartFile file) throws IOException;
//    ResponseEntity<?> recommendCandidates(String jobDescription,int nombreOfProfiles) throws JsonProcessingException, IOException;


    Map<String,Long> getStats();



}


