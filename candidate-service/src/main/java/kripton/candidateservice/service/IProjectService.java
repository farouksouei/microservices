package kripton.candidateservice.service;

import kripton.candidateservice.model.dtos.ProjectDto;
import kripton.candidateservice.model.dtos.TaskDto;

import java.util.List;

public interface IProjectService {
	ProjectDto addProject(ProjectDto dto);
	void deleteProject(Long projectId);
	void addTasksToProject(Long idProject , List<TaskDto> tasks);

	List<ProjectDto> getAllProjects(String sortByAttribute);
}
