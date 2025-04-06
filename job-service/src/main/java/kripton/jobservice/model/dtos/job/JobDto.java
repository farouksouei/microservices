package kripton.jobservice.model.dtos.job;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kripton.jobservice.model.dtos.qualification.CertificationDto;
import kripton.jobservice.model.dtos.qualification.EducationDto;
import kripton.jobservice.model.dtos.qualification.ExperienceDto;
import kripton.jobservice.model.entities.EmploymentType;
import kripton.jobservice.model.entities.ExperienceLevel;
import kripton.jobservice.model.entities.StatusOfJob;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobDto implements Serializable {
    private Long id;
    private String title;
    private String description;
    private String location;
    private EmploymentType employmentType;
    private ExperienceLevel experienceLevel;
    private long proposedSalary;

    private String imagePath ;
    private String company ;
    private Date startDate;
    private StatusOfJob status;
    private Date createdAt;
    private Date modifiedAt;

    private String userId;
    private Boolean isValidated;
    private int jobAppCount;
    private boolean isCandidateAssignedTo;

    private Set<String> requiredSkills = new HashSet<> ();


}
