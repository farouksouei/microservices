package kripton.candidateservice.service;

import kripton.candidateservice.model.dtos.TaskDto;
import kripton.candidateservice.model.dtos.TaskRequestDto;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ITaskService {
	TaskDto addTask(TaskDto taskDto,Long projectId);

	List<TaskDto> getTaskDtosByProject(String sortByAttribute,Long projectId);
	List<TaskDto> getTaskDtos(String sortByAttribute);

	List<TaskDto> getTaskDtosForUser(String userId,Long projectId,String sortByAttribute);

	void deleteTasks(List<Long> tasks);

	TaskDto updateTask(TaskDto taskDto , Long taskId);

	void assignTasksToUser(List<Long> tasks,String userId);

	List<TaskDto> findTasksByCreator(String creatorId,String sortByAttribute);

	List<TaskDto> findTasksByDeadLineBetween(Date start,Date end);
	List<TaskDto> findTasksWithDeadlineBeforeDate(Date date);

	void setTaskStatusCompleted(Long taskId);
	void setTaskStatusInProgress(Long taskId);

	Map<String,Long> statistics (Long projectId);
}
