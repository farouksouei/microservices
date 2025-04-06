package kripton.candidateservice.model.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "candidates")
public class CandidateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String country ;
    private String city ;
    private String designation;
    @Column(length = 500)
    private String description ;
    private int zipCode ;
    private String firstName ;
    private String lastName ;
    private String phone ;
    private String email ;
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth ;
    private boolean isCompleted ;
    @Enumerated(EnumType.STRING)
    private HiringDecision decision ;
    @Enumerated(EnumType.STRING)
    private RecruitingStatus status ;
    @Enumerated(EnumType.STRING)
    private SourceOfHire source ;

    private double rating ;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt ;
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt ;

    private String imagePath;


    @ElementCollection
    @CollectionTable(name = "candidate_skills",joinColumns = @JoinColumn(name="candidate_id"))
    @Column(name="skill")
    private Set<String> skills = new HashSet<> ();
    @Column(name = "user_id")
    private String user;
    private String score;
    private int totalExp;
    @ElementCollection
    @CollectionTable(name = "candidate_languages",joinColumns = @JoinColumn(name="candidate_id"))
    @Column(name="language")
    private Set<String> languages = new HashSet<> ();}