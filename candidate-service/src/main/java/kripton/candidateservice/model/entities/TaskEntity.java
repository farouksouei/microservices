package kripton.candidateservice.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table (name = "tasks")
public class TaskEntity {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	private String title ;
	@Temporal(TemporalType.DATE)
	private Date deadline ;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt ;
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedAt ;
	@Enumerated(EnumType.STRING)
	private TaskStatus status;

	@Enumerated(EnumType.STRING)
	private Priority priority;
	private String creatorId ;
	@ElementCollection
	@CollectionTable(name = "task_assigned_users",joinColumns = @JoinColumn(name="task_id"))
	@Column(name="user")
	private Set<String> users = new HashSet<> ();
	@Column(length = 500)
	private String description ;

	@ManyToOne
	private ProjectEntity project;

}
