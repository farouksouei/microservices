package kripton.candidateservice.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kripton.candidateservice.exception.EmailAlreadyExistsException;
import kripton.candidateservice.model.dtos.*;
import kripton.candidateservice.model.entities.CandidateEntity;
import kripton.candidateservice.model.entities.HiringDecision;
import kripton.candidateservice.model.entities.RecruitingStatus;
import kripton.candidateservice.model.entities.SourceOfHire;
import kripton.candidateservice.model.repositories.CandidateEntityRepository;
import kripton.candidateservice.service.feign.JobClient;
import kripton.candidateservice.service.feign.QualificationClient;
import kripton.candidateservice.service.feign.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CandidateService implements ICandidateService{
    public CandidateService (FileManagementService fileManagementService,
                             CandidateEntityRepository candidateRepository, UserClient userClient,
                             QualificationClient qualificationClient, ModelMapper mapper, KafkaTemplate<String, String> kafkaTemplate, JobClient jobClient, RestTemplate restTemplate) {
        this.fileManagementService = fileManagementService;
        this.candidateRepository = candidateRepository;
        this.qualificationClient = qualificationClient;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.jobClient = jobClient;
        this.restTemplate = restTemplate;
        this.userClient = userClient;
    }
    private final UserClient userClient;
    private final FileManagementService fileManagementService;
    private final CandidateEntityRepository candidateRepository;
    private final QualificationClient qualificationClient;

    private final ModelMapper mapper ;
    private final KafkaTemplate<String,String> kafkaTemplate;

    private final JobClient jobClient;
    

    private final RestTemplate restTemplate;
    private static final String FLASK_ENDPOINT_URL = "http://localhost:5000/uploader";

    @Transactional
    @Override
    public void deleteCandidate(Long idCandidate) {
        Optional<CandidateEntity> entityOptional = candidateRepository.findById(idCandidate);
        if (entityOptional.isPresent()){
            candidateRepository.delete(entityOptional.get());
            log.info ("deleting job applications ...");
            jobClient.deleteJobApplicationOfCandidate (idCandidate);
        }
        else log.warn("candidate with id {} not found in DB",idCandidate);
    }

    @Override
    public List<CandidateDetailsDto> findAllCandidatesWithDetails(String sortByAttribute) {
        List<CandidateDetailsDto> candidateDtos = new ArrayList<>();
        Sort sort = Sort.by (Sort.Direction.DESC,sortByAttribute);
        if(sortByAttribute.equals ("title")){
            sort = Sort.by (Sort.Direction.ASC,sortByAttribute);
        }
        candidateRepository.findAll(sort).forEach(candidate -> {
            CandidateDetailsDto detailsDto = mapper.map(candidate, CandidateDetailsDto.class);
            List<EducationDto> educationByCandidate = qualificationClient.findEducationByCandidate(candidate.getId());
            detailsDto.setEducations(educationByCandidate);
            List<CertificationDto> certificationByCandidate = qualificationClient.findCertificationByCandidate(candidate.getId());
            detailsDto.setCertifications(certificationByCandidate);
            List<ExperienceDto> experienceByCandidate = qualificationClient.findExperienceByCandidate(candidate.getId());
            detailsDto.setExperiences(experienceByCandidate);
            log.info("finding Details of candidate with id : {}",candidate.getId());
            candidateDtos.add(detailsDto);
        });
        return candidateDtos ;
    }

    @Override
    public CandidateDetailsDto findCandidateWithDetails(Long idCandidate) {
        CandidateEntity candidate = candidateRepository.findById(idCandidate).orElse(null);
        if (candidate != null){
            CandidateDetailsDto detailsDto = mapper.map(candidate, CandidateDetailsDto.class);
            List<EducationDto> educationByCandidate = qualificationClient.findEducationByCandidate(idCandidate);
            detailsDto.setEducations(educationByCandidate);
            List<CertificationDto> certificationByCandidate = qualificationClient.findCertificationByCandidate(idCandidate);
            detailsDto.setCertifications(certificationByCandidate);
            List<ExperienceDto> experienceByCandidate = qualificationClient.findExperienceByCandidate(idCandidate);
            detailsDto.setExperiences(experienceByCandidate);
            return detailsDto;
        }
        return null;
    }
    @Transactional
    @Override
    public CandidateDetailsDto saveCandidateWithDetails(CandidateDetailsDto candidateDetailsDto ,String idUser, MultipartFile image) throws IOException, IllegalAccessException {
        CandidateEntity candidateEntityBeforeSave = mapper.map(candidateDetailsDto, CandidateEntity.class);
        candidateEntityBeforeSave.setCreatedAt(new Date(System.currentTimeMillis()));
        candidateEntityBeforeSave.setModifiedAt(new Date(System.currentTimeMillis()));
        candidateEntityBeforeSave.setUser(idUser);
        candidateEntityBeforeSave.setStatus(RecruitingStatus.RESUME_REVIEW);
        candidateEntityBeforeSave.setDecision(HiringDecision.PENDING);
        // Save the uploaded image to the server
        if (image != null){
            candidateEntityBeforeSave.setImagePath (saveImage(image));
        }else {
            candidateEntityBeforeSave.setImagePath("");
        }
        List<String> nullOrEmptyAttributes = checkNullOrEmptyAttributes(candidateEntityBeforeSave);
        if (candidateDetailsDto.getCertifications() != null && !candidateDetailsDto.getCertifications().isEmpty() ||
                candidateDetailsDto.getEducations() != null && !candidateDetailsDto.getEducations().isEmpty() ||
                candidateDetailsDto.getExperiences() != null && !candidateDetailsDto.getExperiences().isEmpty()){
        candidateEntityBeforeSave.setCompleted(nullOrEmptyAttributes.isEmpty());
        }else {
            candidateEntityBeforeSave.setCompleted(false);
        }
        CandidateEntity entity = candidateRepository.save(candidateEntityBeforeSave);
        log.info("candidate with id : {} saved successfully created at {}!",entity.getId(),new Date());
        //kafka send notification
        ObjectMapper objectMapper = new ObjectMapper();
        String candidateString = objectMapper.writeValueAsString(entity);
        kafkaTemplate.send("created-mail",candidateString);
        String userId = userClient.addUser (UserDto.builder ()
                .userName (entity.getFirstName () + " " + entity.getLastName ())
                .firstname (entity.getFirstName ())
                .emailId (entity.getEmail ())
                .lastname (entity.getLastName ())
                .build ());

        return getCandidateDetailsDto(candidateDetailsDto, entity);
    }
    @Transactional
    @Override
    public CandidateDetailsDto updateCandidateWithDetails(CandidateDetailsDto candidateDetailsDto, Long idCandidate,MultipartFile image) throws IllegalAccessException {
        CandidateEntity candidateEntityBeforeSave = mapper.map(candidateDetailsDto, CandidateEntity.class);
        Optional<CandidateEntity> optional = candidateRepository.findById(idCandidate);
        if (optional.isPresent()){
            CandidateEntity candidateEntity = optional.get ();
            candidateEntityBeforeSave.setUser (candidateEntity.getUser ());
            candidateEntityBeforeSave.setCreatedAt (candidateEntity.getCreatedAt ());
            if (candidateDetailsDto.getDecision () == null || candidateDetailsDto.getDecision ().toString ().equals ("")){
                candidateEntityBeforeSave.setDecision (candidateEntity.getDecision ());
            }
            if (candidateDetailsDto.getStatus () == null || candidateDetailsDto.getStatus ().toString ().equals ("")){
                candidateEntityBeforeSave.setStatus (candidateEntity.getStatus ());
            }
            candidateEntityBeforeSave.setId (idCandidate);
            candidateEntityBeforeSave.setModifiedAt(new Date(System.currentTimeMillis()));
            if (image != null){
                candidateEntityBeforeSave.setImagePath (saveImage(image));
            }else {
                candidateEntityBeforeSave.setImagePath(candidateEntity.getImagePath ());
            }
            List<String> nullOrEmptyAttributes = checkNullOrEmptyAttributes(candidateEntityBeforeSave);
            candidateEntityBeforeSave.setCompleted(nullOrEmptyAttributes.isEmpty() && optional.get().isCompleted());
            CandidateEntity entity = candidateRepository.save(candidateEntityBeforeSave);
            log.info("candidate with id : {} updated successfully modified at {}!",entity.getId(),new Date());
            return getCandidateDetailsDto(candidateDetailsDto, entity);
        }
        return null;
    }

    @Override
    public void sendEmailOfCompletionToCandidates(List<Long>idCandidates) {
        List<CandidateEntity> entities = candidateRepository.findAllById(idCandidates);
        ObjectMapper objectMapper = new ObjectMapper();
        entities.forEach(candidateEntity -> {
            try {
                String candidateString = objectMapper.writeValueAsString(candidateEntity);
                kafkaTemplate.send("completion-email",candidateString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    private CandidateDetailsDto getCandidateDetailsDto(CandidateDetailsDto candidateDetailsDto, CandidateEntity entity) {
        List<ExperienceDto> experiences = candidateDetailsDto.getExperiences();
        List<EducationDto> educations = candidateDetailsDto.getEducations();
        List<CertificationDto> certifications = candidateDetailsDto.getCertifications();
        if (experiences.size() > 0){
            log.info("assigning list of experiences to candidate with id {}",entity.getId());
            qualificationClient.assignDtosExperienceByCandidate(entity.getId(),experiences);
        }
        if (educations.size() > 0){
            log.info("assigning list of education to candidate with id {}",entity.getId());
            qualificationClient.assignDtosEducationByCandidate(entity.getId(),educations);
        }
        if (certifications.size() > 0){
            log.info("assigning list of certification to candidate with id {}",entity.getId());
            qualificationClient.assignDtosCertificationByCandidate(entity.getId(),certifications);
        }
        log.info("success !");
        CandidateDetailsDto map = mapper.map(entity, CandidateDetailsDto.class);
        map.setCertifications(certifications);
        map.setEducations(educations);
        map.setExperiences(experiences);
        return map ;
    }
    @Override
    public List<String> checkIfCandidateInfoIsCompleted (Long idCandidate) throws IllegalAccessException {
        CandidateEntity candidateEntity = candidateRepository.findById(idCandidate).orElse(null);
        assert candidateEntity != null;
        return checkNullOrEmptyAttributes(candidateEntity);
    }

    @Override
    public void sendMultipleEmails(List<Long> candidates,String topic){
        candidates.forEach (candidate -> {
            Optional<CandidateEntity> optional = candidateRepository.findById (candidate);
            if (optional.isPresent ()){
                CandidateEntity entity = optional.get ();
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    String candidateString = objectMapper.writeValueAsString(entity);
                    kafkaTemplate.send (topic,candidateString);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException (e);
                }
            }
        });
    }

    @Override
    public List<CandidateDetailsDto> findAllCandidatesNotCompleted() {
         return candidateRepository.findNotCompletedCandidates()
                 .stream()
                 .map(candidateEntity -> mapper.map(candidateEntity,CandidateDetailsDto.class)).toList();
    }

    @Override
    public CandidateDetailsDto createCandidateAuto (String firstName, String lastName, String email) {
        CandidateDetailsDto dto = CandidateDetailsDto.builder ()
                .createdAt (new Date ())
                .email (email)
                .firstName (firstName)
                .lastName (lastName).source (SourceOfHire.DIRECT_CONTACT)
                .decision (HiringDecision.PENDING)
                .status (RecruitingStatus.SCREENING).modifiedAt (new Date ()).build ();
        CandidateEntity entity = candidateRepository.save (mapper.map (dto, CandidateEntity.class));
        return mapper.map (entity,CandidateDetailsDto.class);
    }

    @Override
    public CandidateDetailsDto findCandidateByEmailFirstNameLastName (String firstName, String lastName, String email) {
        CandidateEntity candidate = candidateRepository.findByFirstNameLikeAndLastNameLikeAndEmailLike (firstName,
                lastName, email);
        if(candidate != null){
            CandidateDetailsDto detailsDto = mapper.map(candidate, CandidateDetailsDto.class);
            List<EducationDto> educationByCandidate = qualificationClient.findEducationByCandidate(candidate.getId ());
            detailsDto.setEducations(educationByCandidate);
            List<CertificationDto> certificationByCandidate = qualificationClient.findCertificationByCandidate(candidate.getId ());
            detailsDto.setCertifications(certificationByCandidate);
            List<ExperienceDto> experienceByCandidate = qualificationClient.findExperienceByCandidate(candidate.getId ());
            detailsDto.setExperiences(experienceByCandidate);
            return detailsDto;

        }else{
            return createCandidateAuto (firstName,lastName,email);
        }
    }

    public static List<String> checkNullOrEmptyAttributes(Object obj) throws IllegalAccessException {
        List<String> nullOrEmptyAttributes = new ArrayList<>();
        Class<?> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(obj);
            if (value == null || value.toString().isEmpty() ) {
                nullOrEmptyAttributes.add(field.getName());
            }
        }
        nullOrEmptyAttributes.removeIf(s -> s.equals("id"));
        return nullOrEmptyAttributes;
    }

    private String saveImage(MultipartFile image) {
        return fileManagementService.uploadToAzure (image, "candidates");
    }

    
//    public ResponseEntity<?> createCandidateFromResume(MultipartFile file) throws IOException {
//        RestTemplate restTemplate = new RestTemplate();
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", new ByteArrayResource(file.getBytes()) {
//            @Override
//            public String getFilename() {
//                return file.getOriginalFilename();
//            }
//        });
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//        ResponseEntity<String> response = restTemplate.exchange(FLASK_ENDPOINT_URL, HttpMethod.POST, requestEntity, String.class);
//
//        // deserialize the JSON response into a CandidateDto object
//        ObjectMapper objectMapper = new ObjectMapper();
//        CandidateDto candidate = objectMapper.readValue(response.getBody(), CandidateDetailsDto.class);
//        // create new candidate and save it
//        CandidateEntity newCandidate = new CandidateEntity() ;
//
//        try {
//        	// check if email already exists
//            if (candidateRepository.existsByEmail(candidate.getEmail())) {
//                throw  new EmailAlreadyExistsException(candidate.getEmail());
//            }
//            newCandidate.setDesignation(candidate.getDesignition());
//            newCandidate.setCountry(candidate.getAddress());
//            String[] fullname = candidate.getName().split(" ");
//            newCandidate.setFirstName(fullname[0]);
//            newCandidate.setFirstName(fullname[1]);
//            //newCandidate.setEmail(candidate.getEmail());
//
//            newCandidate.setPhone(candidate.getPhone());
//           // newCandidate.setSkills((Set<String>) candidate.getSkills());
//            newCandidate.setSkills(new HashSet<>(candidate.getSkills()));
//            newCandidate.setCreatedAt(new Date());
//            newCandidate.setTotalExp(candidate.getTotal_exp());
//            newCandidate.setLanguages((Set<String>) candidate.getLanguages ());
//            candidateRepository.save(newCandidate);
//            return new ResponseEntity<>(candidate, HttpStatus.OK);
//		} catch (EmailAlreadyExistsException e) {
//			return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
//		}
//    }
//

	public ResponseEntity<?> recommendCandidates(String jobDescription,int nombreOfProfiles) throws IOException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("jobDescription", jobDescription);
		formData.add("nombreOfProfiles", Integer.toString(nombreOfProfiles));

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

		String url = "http://localhost:5000/data/";
		ResponseEntity<Object[]> response = restTemplate.postForEntity(url, request, Object[].class);

		if (response.getStatusCode() == HttpStatus.OK) {
			//return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
			Object[] data = response.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(data); // Convert Object[] array to JSON string
			JsonParser jsonParser = new JsonFactory().createParser(json); // Create JsonParser from JSON string			
			RecommendationResponseDto[] recommendationResponseDto = objectMapper.readValue(jsonParser, RecommendationResponseDto[].class); // Deserialize JSON data into RecommendationResponseDto object

			List<CandidateEntity> recommendedCandidates = Arrays.stream(recommendationResponseDto)
			        .map(RecommendationResponseDto::getEmail)
			        .map(candidateRepository::findByEmail)
			        .collect(Collectors.toList());
			
			for (int i = 0; i < recommendedCandidates.size(); i++) {
			    recommendedCandidates.get(i).setScore(recommendationResponseDto[i].getScore());
			}

			System.out.println("DATA *************" + recommendationResponseDto);
			return new ResponseEntity<>(recommendedCandidates, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Error occurred while calling Flask endpoint",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    @Override
    public Map<String, Long> getStats () {
        Map<String,Long> statistics = new HashMap<> ();

        statistics.put ("completedCandidates",candidateRepository.countByIsCompleted (true));
        statistics.put ("decisionPending",candidateRepository.countByDecision (HiringDecision.PENDING));
        statistics.put ("decisionNotHired",candidateRepository.countByDecision (HiringDecision.NOT_HIRED));
        statistics.put ("decisionPlaced",candidateRepository.countByDecision (HiringDecision.PLACED));
        statistics.put ("decisionWaitList",candidateRepository.countByDecision (HiringDecision.WAIT_LIST));
        statistics.put ("decisionHired",candidateRepository.countByDecision (HiringDecision.HIRED));
        statistics.put ("statusResumeReview",candidateRepository.countByStatus (RecruitingStatus.RESUME_REVIEW));
        statistics.put ("statusOffer",candidateRepository.countByStatus (RecruitingStatus.OFFER));
        statistics.put ("statusInPersonInterview",
                candidateRepository.countByStatus (RecruitingStatus.IN_PERSON_INTERVIEW));
        statistics.put ("sourceCollaborator",candidateRepository.countBySource (SourceOfHire.COLLABORATOR));
        statistics.put ("sourceDirectContact",candidateRepository.countBySource (SourceOfHire.DIRECT_CONTACT));
        statistics.put ("sourceRecommendation",candidateRepository.countBySource (SourceOfHire.RECOMMENDATION));
        statistics.put ("sourceJobBoard",candidateRepository.countBySource (SourceOfHire.JOB_BOARD));
        LocalDate currentDate = LocalDate.now ();
        LocalDate lastMonthDate = currentDate.minusMonths (1).with (TemporalAdjusters.lastDayOfMonth ());
        Date currentDateAsDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date lastMonthDateAsDate = Date.from(lastMonthDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        statistics.put ("lastMonthCreatedCandidates",candidateRepository.countByCreatedAtBetween (lastMonthDateAsDate,
                currentDateAsDate));
        return statistics;
    }


}




















