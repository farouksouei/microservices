package kripton.candidateservice.model.dtos;

import kripton.candidateservice.model.entities.HiringDecision;
import kripton.candidateservice.model.entities.RecruitingStatus;
import kripton.candidateservice.model.entities.SourceOfHire;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDetailsDto implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String country ;
    private String city ;
    private String designation;
    private String description;
    private int zipCode ;
    private String phone;
    private String email;
    private Date dateOfBirth;
    private HiringDecision decision;
    private RecruitingStatus status;
    private SourceOfHire source;
    private boolean isSelected ;
    private String imagePath ;
    private double rating;
    private Date createdAt;
    private Date modifiedAt;
    private String user;
    private boolean isCompleted ;
    private long score ;
    private List<ExperienceDto> experiences = new ArrayList<>();
    private List<EducationDto> educations = new ArrayList<>();
    private List<CertificationDto> certifications = new ArrayList<>();
    private Set<String> skills = new HashSet<> ();
}
