package kripton.jobservice.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "jobs")
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title ;
    @Column(length = 2000)
    private String description ;
    private String location ;
    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType ;
    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel ;
    private long proposedSalary ;
    private String imagePath ;
    private String company ;
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Enumerated(EnumType.STRING)
    private StatusOfJob status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    private String user_id;
    private Boolean isValidated;
    @OneToMany(mappedBy = "job",cascade = CascadeType.ALL)
    List<JobApplicationEntity> jobApplications = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "job_required_skills",joinColumns = @JoinColumn(name="job_id"))
    @Column(name="skill")
    private Set<String> requiredSkills = new HashSet<> ();
}