package kripton.candidateservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lowagie.text.DocumentException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import kripton.candidateservice.config.restTemplate.CandidateResponse;
import kripton.candidateservice.model.dtos.CandidateDetailsDto;
import kripton.candidateservice.model.dtos.MultipleEmailsDto;
import kripton.candidateservice.service.FileManagementService;
import kripton.candidateservice.service.ICandidateService;
import kripton.candidateservice.service.resumeGenerator.ResumeGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/candidates")
@Slf4j
public class CandidateController {
    private final ICandidateService candidateService ;
    private final Job job ;
    private final JobLauncher jobLauncher;
    private final ResumeGeneratorService resumeGeneratorService ;
    private final RestTemplate restTemplate;
    private final String matcher_parser_serverUrl;
    private final FileManagementService fileManagementService;

    public CandidateController(ICandidateService candidateService, Job job, JobLauncher jobLauncher,
                               ResumeGeneratorService resumeGeneratorService, RestTemplate restTemplate,
                               @Value("${matcher_parser_serverUrl}")String matcher_parser_serverUrl,
                               FileManagementService fileManagementService) {
        this.candidateService = candidateService;
        this.job = job;
        this.jobLauncher = jobLauncher;
        this.resumeGeneratorService = resumeGeneratorService;
        this.restTemplate = restTemplate;
        this.matcher_parser_serverUrl = matcher_parser_serverUrl;
        this.fileManagementService = fileManagementService;
    }

    private final String TEMP_DIRECTORY = new File ("").getAbsolutePath ();
    @GetMapping
    public ResponseEntity<List<CandidateDetailsDto>> findCandidates(@RequestParam("sortByAttribute")String sort){
        return ResponseEntity.status(200).body(candidateService.findAllCandidatesWithDetails(sort));
    }
    @GetMapping("/{candidate-id}")
    public ResponseEntity<CandidateDetailsDto> findCandidateDetails(@PathVariable(value = "candidate-id")Long idCandidate){
        return ResponseEntity.status(200).body(candidateService.findCandidateWithDetails(idCandidate));
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<CandidateDetailsDto> saveCandidateWithDetails(@RequestPart(value = "candidate")CandidateDetailsDto dto,
                                                                         @RequestPart(value = "image",required = false)MultipartFile image,
                                                                        @RequestPart("idUser")String userId){
            try {
                return ResponseEntity.status(201).body(candidateService.saveCandidateWithDetails(dto,userId,image));
            } catch (JsonProcessingException | IllegalAccessException e) {
                log.error (e.getMessage ());
                return ResponseEntity.status(400).body(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }
    @PostMapping(value = "/send-emails")
    public ResponseEntity<?> sendMultipleEmails(@RequestBody MultipleEmailsDto emailsDto){
        candidateService.sendMultipleEmails (emailsDto.getCandidates (),emailsDto.getTopic ());
        return ResponseEntity.status (200).body (new SimpleResponseWrapper ("success !"));
    }
    @PutMapping(value = "/{candidate-id}",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<CandidateDetailsDto> updateCandidateWithDetails( @PathVariable("candidate-id")Long idCandidate,
                                                                           @RequestPart(value = "candidate")CandidateDetailsDto dto,
                                                                          @RequestPart(value = "image",required = false)MultipartFile image
                                                                         ){
            try {

                return ResponseEntity.status(200).body(candidateService.updateCandidateWithDetails(dto,idCandidate,image));
            }catch (IllegalAccessException e) {
                log.error (e.getMessage ());
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
    @DeleteMapping("/{candidate-id}")
    public ResponseEntity<?> deleteCandidateById(@PathVariable(value = "candidate-id")Long idCandidate){
        candidateService.deleteCandidate(idCandidate);
       return ResponseEntity.status(200).body(new SimpleResponseWrapper("success !"));
    }
    @PostMapping("/completion-mail")
    @CircuitBreaker(name = "breaker",fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "breaker")
    public CompletableFuture<?> sendCompletionMailToListOfCandidates(@RequestBody List<Long> idCandidates){
        return CompletableFuture.supplyAsync(()->{
            try {
                candidateService.sendEmailOfCompletionToCandidates(idCandidates);
            }catch (Exception e){
                log.error (e.getMessage ());
            }
            return null;
        });
    }

    @GetMapping("/not-completed")
    @CircuitBreaker(name = "breaker",fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "breaker")
    public ResponseEntity<List<CandidateDetailsDto>> findNotCompletedCandidates(){
        return ResponseEntity.status(200).body(candidateService.findAllCandidatesNotCompleted());
    }
    @GetMapping("/listOfNullAttributes/{id-candidate}")
    public ResponseEntity<List<String>> checkIfCandidateInfoIsCompleted(@PathVariable("id-candidate")Long idCandidate) throws IllegalAccessException {
        return ResponseEntity.status(200).body(candidateService.checkIfCandidateInfoIsCompleted(idCandidate));
    }



    @GetMapping("/upload")
    public ResponseEntity<SimpleResponseWrapper> uploadCandidatesWithBatch(){
        JobParameters jobParameters =
                new JobParametersBuilder ()
                        .addLong ("startAt",System.currentTimeMillis ())
                        .toJobParameters ();
        try{
            jobLauncher.run (job,jobParameters);
            return ResponseEntity.ok (new SimpleResponseWrapper ("it worked !"));
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            e.printStackTrace ();
            return ResponseEntity.status (500).body (new SimpleResponseWrapper (e.getMessage ()));
        }
    }
        @PostMapping(value = "/upload-batch",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
        public ResponseEntity<SimpleResponseWrapper> startBatch(@RequestPart("file") MultipartFile file,
                                            @RequestPart("userId")String userId,
                                                                @RequestPart("fieldNames")String fieldNames){
        try{
        String originalFileName = file.getOriginalFilename ();
        File fileToImport = new File (TEMP_DIRECTORY + originalFileName);
        Path path = Paths.get (TEMP_DIRECTORY +"\\batch-files\\"+ originalFileName);
        Files.deleteIfExists (path);
        file.transferTo (fileToImport);
        JobParameters jobParameters = new JobParametersBuilder ()
                .addString ("fullPathFileName",TEMP_DIRECTORY+originalFileName)
                .addString ("userId",userId)
                .addString ("fieldNames",fieldNames)
                .addLong ("startAt",System.currentTimeMillis ()).toJobParameters ();
        JobExecution execution = jobLauncher.run (job,jobParameters);
        if(execution.getExitStatus ().getExitCode ().equals (ExitStatus.COMPLETED.toString ())){
            Files.deleteIfExists (path);
        }
        return ResponseEntity.ok (new SimpleResponseWrapper ("success !"));
        }catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | IOException e)
        {
            e.printStackTrace();
            return ResponseEntity.status (500).body (new SimpleResponseWrapper ("Error while batching ..."+ e.getMessage ()));
        }
    }

    @PostMapping(value = "/upload-candidate-cv",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<CandidateResponse> uploadCandidateFromCv(@RequestPart("file") MultipartFile file){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<> ();
        try {
            ByteArrayResource fileContentResource = new ByteArrayResource (file.getBytes ()){
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", fileContentResource) ;
        } catch (IOException e) {
            log.error (e.getMessage ());
        }
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<CandidateResponse> parsedCandidateResponse =
                restTemplate.postForEntity(matcher_parser_serverUrl+"api/users",
                requestEntity, CandidateResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(parsedCandidateResponse.getBody());
    }


    @GetMapping(value = "/recommend-candidates/{job-id}/{number-of-candidates}")
    public ResponseEntity<?> recommendCandidatesToJob(@PathVariable("job-id")String idJob,
                                                   @PathVariable("number-of-candidates")String numberOfCandidates){
    // define endpoint URL and path variables
        log.info ("----------------- CALLING recommendCandidatesToJob FROM THIS METHOD ------------------------------");
        String url = matcher_parser_serverUrl+"api/recommend/{job_id}/{number_of_candidates}";
        Map<String, String> pathVariables = new HashMap<> ();
        pathVariables.put("job_id", idJob);
        pathVariables.put("number_of_candidates", numberOfCandidates);
        List candidates = restTemplate.getForObject(url,List.class, pathVariables);
        return ResponseEntity.status(HttpStatus.OK).body(candidates);

    }

    @GetMapping(value = "/recommend-jobs/{candidate-id}/{number-of-jobs}")
    public ResponseEntity<?> recommendJobsToCandidate(@PathVariable("candidate-id")String idCandidate,
                                                      @PathVariable("number-of-jobs")String numberOfJobs){
        // define endpoint URL and path variables
        log.info ("----------------- CALLING recommendJobsToCandidate From THIS METHOD ------------------------------");
        String url = matcher_parser_serverUrl+"api/recommend_job/{candidate_id}/{number_of_jobs}";
        Map<String, String> pathVariables = new HashMap<> ();
        pathVariables.put("candidate_id", idCandidate);
        pathVariables.put("number_of_jobs", numberOfJobs);
        List jobs = restTemplate.getForObject(url,List.class, pathVariables);
        return ResponseEntity.status(HttpStatus.OK).body(jobs);
    }
    @GetMapping("/stats")
    public ResponseEntity<Map<String,Long>> getStats(){
        return ResponseEntity.ok (candidateService.getStats ());
    }


    @GetMapping("/generate-resume/{candidate-id}")
    public ResponseEntity<String> generateCandidateResume(@PathVariable("candidate-id") Long candidateId) throws DocumentException, IOException {
        String resumePath = resumeGeneratorService.generatePdfResume(candidateId);
        File file = new File(resumePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());

        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN); // Set the content type to plain text

        return ResponseEntity.ok()
                .headers(headers)
                .body(encodedString);
    }

    @PostMapping (value = "/add-file")
    public ResponseEntity<?> uploadAndDownload(@RequestParam ("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(fileManagementService.uploadToAzure (file,"files"));
        } catch (Exception e) {
            return ResponseEntity.ok("Error while processing file");
        }
    }






    @PostMapping("/create-candidate-on-register")
    public CandidateDetailsDto saveCandidateAuto(@RequestParam("firstName")String firstName,
                                                 @RequestParam("lastName")String lastName,
                                                 @RequestParam("email")String email){
        return candidateService.createCandidateAuto (firstName,lastName,email);
    }
    @GetMapping("/get-candidate-by-fle")
    public CandidateDetailsDto findCandidateByFirstNameLastNameEmail(@RequestParam("firstName")String firstName,
                                                                     @RequestParam("lastName")String lastName,
                                                                     @RequestParam("email")String email){
        return candidateService.findCandidateByEmailFirstNameLastName (firstName,lastName,email);
    }








    public CompletableFuture<String> fallbackMethod(RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(()-> "Something went wrong : " +runtimeException.getMessage());
    }
    
    
//    @PostMapping("/upload-resume")
//    public ResponseEntity<?> createCandidateFromResume(@RequestParam("file") MultipartFile file) throws IOException{
//    	return candidateService.createCandidateFromResume(file);
//    }
//
//    @PostMapping("/recommand")
//    public ResponseEntity<?> recommendCandidates(@RequestParam(name="jobDescription") String jobDescription,
//            @RequestParam(name="nombreOfProfiles") int nombreOfProfiles) throws JsonProcessingException, IOException{
//    	return candidateService.recommendCandidates(jobDescription, nombreOfProfiles);
//    }
}
