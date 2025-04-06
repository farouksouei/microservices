package kripton.candidateservice.controller;

import kripton.candidateservice.model.dtos.ProjectDto;
import kripton.candidateservice.model.dtos.TaskDto;
import kripton.candidateservice.service.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

	private final IProjectService projectService ;

	@GetMapping
	public ResponseEntity<List<ProjectDto>> getAllProjects(@RequestParam ("sortByAttribute")String sort){
		log.info ("--------- getting all projects ----------");
		return ResponseEntity.ok (projectService.getAllProjects (sort));
	}

	@PostMapping
	public ResponseEntity<ProjectDto> addProject(@RequestBody ProjectDto dto){
		log.info("adding new project");

		return ResponseEntity.status(201).body (projectService.addProject (dto));
	}

	@DeleteMapping("/{project-id}")
	public ResponseEntity<?> deleteProject(@PathVariable ("project-id") Long idProject){
		log.info("---- deleting project -----");
		projectService.deleteProject (idProject);
		return ResponseEntity.ok (new SimpleResponseWrapper ("success!"));
	}
	@PutMapping("/add-tasks-project/{project-id}")
	public ResponseEntity<?> addTasksToProject(@RequestBody List<TaskDto>taskDtos,
	                                           @PathVariable ("project-id") Long idProject){
		log.info ("------ adding tasks to project -------");
		projectService.addTasksToProject (idProject,taskDtos);
		return ResponseEntity.ok (new SimpleResponseWrapper ("success!"));
	}
}
