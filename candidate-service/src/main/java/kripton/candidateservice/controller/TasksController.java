package kripton.candidateservice.controller;

import kripton.candidateservice.model.dtos.TaskDto;
import kripton.candidateservice.model.dtos.TaskRequestDto;
import kripton.candidateservice.service.ITaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TasksController {
	private final ITaskService taskService ;

	@GetMapping
	public ResponseEntity<List<TaskDto>> getAllTasks(@RequestParam("sortByAttribute")String sort){
		log.info ("--------- getting all tasks ----------");
		return ResponseEntity.ok (taskService.getTaskDtos (sort));
	}
	@GetMapping("/{project-id}")
	public ResponseEntity<List<TaskDto>> getAllTasksByProject(@RequestParam("sortByAttribute")String sort,
	                                                          @PathVariable ("project-id") Long projectId){
		log.info ("--------- getting all tasks by project ----------");
		return ResponseEntity.ok (taskService.getTaskDtosByProject (sort,projectId));
	}
	@GetMapping("/project/{project-id}/user/{user-id}")
	public ResponseEntity<List<TaskDto>> getTasksByUser(@PathVariable ("user-id") String userId,@RequestParam("sortByAttribute")String sort,
	                                                    @PathVariable ("project-id") Long projectId){
		log.info ("--------- getting user tasks ----------");
		return ResponseEntity.ok (taskService.getTaskDtosForUser (userId,projectId,sort));
	}

	@PostMapping("/project/{project-id}")
	public ResponseEntity<TaskDto> addTask(@RequestBody TaskRequestDto taskRequest,
	                                       @PathVariable ("project-id") Long projectId){
		log.info ("--------- adding new Task ----------");
		return ResponseEntity.ok (taskService.addTask (taskRequest.getTaskDto (),projectId));
	}

	@DeleteMapping("/delete")
	public void deleteTasks(@RequestBody List<Long> tasks){
		log.info ("--------- deleting Tasks ----------");
		taskService.deleteTasks (tasks);
	}

	@PutMapping("/update/{task-id}")
	public ResponseEntity<TaskDto> updateTask(@PathVariable ("task-id") Long taskId,@RequestBody TaskDto taskDto){
		log.info ("--------- updating Task ----------");
		return ResponseEntity.ok (taskService.updateTask (taskDto,taskId));
	}

	@PutMapping("/assign-to-user/{user-id}")
	public void assignTasksToUser(@RequestBody List<Long> tasks,
	                              @PathVariable ("user-id") String userId){
		taskService.assignTasksToUser (tasks,userId);
	}

	@GetMapping("/creator/{creator-id}")
	public ResponseEntity<List<TaskDto>> getTasksByCreator(@PathVariable ("creator-id") String userId,
	                                       @RequestParam("sortByAttribute")String sort){
		log.info ("--------- getting creator tasks ----------");
		return ResponseEntity.ok (taskService.findTasksByCreator (userId,sort));
	}
	@GetMapping("/tasks-between")
	public ResponseEntity<List<TaskDto>> getTasksBetweenTwoDates(@RequestParam ("start") @DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
	                                             @RequestParam ("end") @DateTimeFormat(pattern = "yyyy-MM-dd")Date end){
		log.info ("--------- getting tasks between ----------");
		return ResponseEntity.ok (taskService.findTasksByDeadLineBetween (start,end));
	}
	@GetMapping("/tasks-before")
	public List<TaskDto> getTasksWithDeadlineBefore(@RequestParam ("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
		log.info ("--------- getting tasks before----------");
		return taskService.findTasksWithDeadlineBeforeDate (date);
	}

	@PutMapping("/completed/{task-id}")
	public ResponseEntity<?> setTaskCompleted(@PathVariable ("task-id") Long idTask){
		taskService.setTaskStatusCompleted (idTask);
		return ResponseEntity.ok ().build ();
	}
	@PutMapping("/in-progress/{task-id}")
	public ResponseEntity<?> setTaskInProgress(@PathVariable ("task-id") Long idTask){
		taskService.setTaskStatusInProgress (idTask);
		return ResponseEntity.ok ().build ();
	}

	@GetMapping("/stats/{project-id}")
	public ResponseEntity<Map<String,Long>>  getStatistics(@PathVariable ("project-id") Long projectId){
		return ResponseEntity.ok (taskService.statistics (projectId));
	}
}
