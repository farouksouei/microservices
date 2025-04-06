package kripton.communicationservice.dtos.job;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import kripton.communicationservice.dtos.qualification.CertificationDto;
import kripton.communicationservice.dtos.qualification.EducationDto;
import kripton.communicationservice.dtos.qualification.ExperienceDto;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobDto implements Serializable {
    private Long id;
    private String title;
    private String description;
    private String location;
    private EmploymentType employmentType;
    private ExperienceLevel experienceLevel;
    private long proposedSalary;

    private String userId;
    private String imagePath ;
    private String company ;
    private Date startDate;
    private StatusOfJob status;
    private Date createdAt;
    private Date modifiedAt;
    private List<JobApplicationDto> jobApplications;
    private List<ExperienceDto> requiredExperience=new ArrayList<>();
    private List<CertificationDto>requiredCertifications=new ArrayList<>();
    private List<EducationDto>requiredEducation=new ArrayList<>();

    private Set<String> requiredSkills = new HashSet<> ();
}
