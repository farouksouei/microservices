package kripton.candidateservice.service;

import kripton.candidateservice.model.dtos.ProjectDto;
import kripton.candidateservice.model.dtos.TaskDto;
import kripton.candidateservice.model.entities.ProjectEntity;
import kripton.candidateservice.model.entities.TaskEntity;
import kripton.candidateservice.model.entities.TaskStatus;
import kripton.candidateservice.model.repositories.ProjectEntityRepository;
import kripton.candidateservice.model.repositories.TaskEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService implements IProjectService{
	private final ProjectEntityRepository projectRepo;
	private final TaskEntityRepository taskRepo;

	private final ModelMapper mapper ;
	@Override
	public ProjectDto addProject (ProjectDto dto) {
		ProjectEntity entity = mapper.map (dto, ProjectEntity.class);
		entity.setCreatedAt (new Date ());
		return mapper.map (projectRepo.save (entity), ProjectDto.class);
	}

	@Override
	public void deleteProject (Long projectId) {
		projectRepo.findById (projectId).ifPresent ((projectEntity -> projectRepo.deleteById (projectId)));
	}

	@Override
	public void addTasksToProject (Long idProject, List<TaskDto> tasks) {
		projectRepo.findById (idProject).ifPresent ((projectEntity -> {
			tasks.forEach (taskDto -> {
				TaskEntity taskEntity = mapper.map (taskDto, TaskEntity.class);
				taskEntity.setCreatedAt (new Date ());
				taskEntity.setProject (projectEntity);
				taskEntity.setModifiedAt (new Date ());
				taskEntity.setProject (projectEntity);
				taskRepo.save (taskEntity);
			});
			log.info ("----------- created new project ---------");
		}));
	}

	@Override
	public List<ProjectDto> getAllProjects (String sortByAttribute) {
		Sort sort = Sort.by (Sort.Direction.DESC,sortByAttribute);
		List<ProjectEntity> all = projectRepo.findAll (sort);

		return all.stream ().map (projectEntity -> {
			ProjectDto projectDto = mapper.map (projectEntity, ProjectDto.class);
			List<TaskDto> projectTasks =
					projectEntity.getTasks ().stream ().map (taskEntity -> mapper.map (taskEntity,TaskDto.class)).toList ();
			projectDto.setTasks (projectTasks);
			return projectDto;
		}).toList ();
	}
}
