package kripton.candidateservice.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table (name = "projects")
public class ProjectEntity {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	private String title;
	@Column(length = 500)
	private String description ;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;


	@OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
	private List<TaskEntity> tasks = new ArrayList<> ();
}
