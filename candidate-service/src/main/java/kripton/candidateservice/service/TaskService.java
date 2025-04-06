package kripton.candidateservice.service;

import kripton.candidateservice.model.dtos.TaskDto;
import kripton.candidateservice.model.dtos.UserDto;
import kripton.candidateservice.model.entities.Priority;
import kripton.candidateservice.model.entities.TaskEntity;
import kripton.candidateservice.model.entities.TaskStatus;
import kripton.candidateservice.model.repositories.ProjectEntityRepository;
import kripton.candidateservice.model.repositories.TaskEntityRepository;
import kripton.candidateservice.service.feign.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService implements ITaskService{

	private final TaskEntityRepository taskRepository;
	private final UserClient userClient ;
	private final ModelMapper mapper ;
	private final ProjectEntityRepository projectRepo;

	@Override
	public TaskDto addTask (TaskDto taskDto,Long projectId) {
		TaskEntity entity = mapper.map (taskDto, TaskEntity.class);
		entity.setCreatedAt (new Date ());
		entity.setModifiedAt (new Date ());
		entity.setCreatorId (taskDto.getCreatorId ());


		// check if user there !
		projectRepo.findById (projectId).ifPresent (entity::setProject);
		List<UserDto> usersAssign = new ArrayList<> ();
		taskDto.getUsers ().forEach (s -> {
			UserDto userById = userClient.getUserById (s);
			if(userById != null){
				usersAssign.add (userById);
			}
		});
		entity.setUsers (taskDto.getUsers ());
		TaskEntity save = taskRepository.save (entity);
		TaskDto dto = mapper.map (save, TaskDto.class);
		dto.setUserDtos (usersAssign);
		return dto ;
	}

	@Override
	public List<TaskDto> getTaskDtosByProject (String sortByAttribute, Long projectId) {

		Sort sort = Sort.by (Sort.Direction.DESC,sortByAttribute);
		if(sortByAttribute.equals ("title")){
			sort = Sort.by (Sort.Direction.ASC,sortByAttribute);
		}
		List<TaskEntity> all = taskRepository.findByProject_Id (projectId,sort);
		List<TaskDto> taskDtos = getTaskDtos (all);
		// for each dto set users objects
		taskDtos.forEach (taskDto -> {
			List<UserDto> usersAssign = new ArrayList<> ();
			taskDto.getUsers ().forEach (user -> {
				UserDto userById = userClient.getUserById (user);
				if(userById != null){
					usersAssign.add (userById);
				}
			});
			taskDto.setUserDtos (usersAssign);
		});
		return taskDtos;
	}

	@Override
	public List<TaskDto> getTaskDtos (String sortByAttribute) {
		Sort sort = Sort.by (Sort.Direction.DESC,sortByAttribute);
		if(sortByAttribute.equals ("title")){
			sort = Sort.by (Sort.Direction.ASC,sortByAttribute);
		}
		List<TaskEntity> all = taskRepository.findAll (sort);
		List<TaskDto> taskDtos = getTaskDtos (all);
		// for each dto set users objects
		taskDtos.forEach (taskDto -> {
			List<UserDto> usersAssign = new ArrayList<> ();
			taskDto.getUsers ().forEach (user -> {
				UserDto userById = userClient.getUserById (user);
				if(userById != null){
					usersAssign.add (userById);
				}
			});
			taskDto.setUserDtos (usersAssign);
		});
		return taskDtos;
	}

	@Override
	public List<TaskDto> getTaskDtosForUser (String userId,Long projectId,String sortByAttribute) {
		Sort sort = Sort.by (Sort.Direction.DESC,sortByAttribute);
		if(sortByAttribute.equals ("title")){
			sort = Sort.by (Sort.Direction.ASC,sortByAttribute);
		}
		List<TaskEntity> all = taskRepository.findByProject_Id (projectId,sort);
		List<TaskEntity> userTasks = all.stream ().filter (taskEntity -> !taskEntity.getUsers ().isEmpty () && taskEntity.getUsers ().contains (userId)).toList ();
		List<TaskDto> taskDtos = getTaskDtos (userTasks);
		// for each dto set users objects
		taskDtos.forEach (taskDto -> {
			List<UserDto> usersAssign = new ArrayList<> ();
			taskDto.getUsers ().forEach (user -> {
				UserDto userById = userClient.getUserById (user);
				if(userById != null){
					usersAssign.add (userById);
				}
			});
			taskDto.setUserDtos (usersAssign);
		});


		return taskDtos;
	}

	@Override
	public void deleteTasks (List<Long> tasks) {
		tasks.forEach (task -> {
			if (taskRepository.findById (task).isPresent ()){
				taskRepository.deleteById (task);
			}
		});
	}

	@Override
	public TaskDto updateTask (TaskDto taskDto, Long taskId) {
		Optional<TaskEntity> optional = taskRepository.findById (taskId);
		if(optional.isPresent ()){
			TaskEntity entity = mapper.map (taskDto, TaskEntity.class);
			entity.setModifiedAt (new Date());
			entity.setId (taskId);
			entity.setProject (optional.get ().getProject ());
			entity.setCreatedAt (optional.get ().getCreatedAt ());
			entity.setCreatorId (optional.get ().getCreatorId ());
			TaskEntity savedEntity = taskRepository.save (entity);
			return mapper.map (savedEntity,TaskDto.class);
		}
		log.warn ("task was not found in db");
		return null;
	}

	@Override
	public void assignTasksToUser (List<Long> tasks, String userId) {
		tasks.forEach (task ->{
			Optional<TaskEntity> entity = taskRepository.findById (task);
			if(entity.isPresent ()){
				TaskEntity taskEntity = entity.get ();
				taskEntity.getUsers ().add (userId);
				taskEntity.setModifiedAt (new Date());
				taskRepository.save (taskEntity);
			}
		});
	}

	@Override
	public List<TaskDto> findTasksByCreator (String creatorId, String sortByAttribute) {
		Sort sort = Sort.by (Sort.Direction.DESC,sortByAttribute);
		List<TaskEntity> entities = taskRepository.findByCreatorIdLike (creatorId,sort);
		List<TaskDto> taskDtos = getTaskDtos (entities);
		// for each dto set users objects
		taskDtos.forEach (taskDto -> {
			List<UserDto> usersAssign = new ArrayList<> ();
			taskDto.getUsers ().forEach (user -> {
				UserDto userById = userClient.getUserById (user);
				if(userById != null){
					usersAssign.add (userById);
				}
			});
			taskDto.setUserDtos (usersAssign);
		});
		return taskDtos;
	}

	@Override
	public List<TaskDto> findTasksByDeadLineBetween (Date start, Date end) {
		List<TaskEntity> entities = taskRepository.findByDeadlineBetween (start, end);
		List<TaskDto> taskDtos = getTaskDtos (entities);
		// for each dto set users objects
		taskDtos.forEach (taskDto -> {
			List<UserDto> usersAssign = new ArrayList<> ();
			taskDto.getUsers ().forEach (user -> {
				UserDto userById = userClient.getUserById (user);
				if(userById != null){
					usersAssign.add (userById);
				}
			});
			taskDto.setUserDtos (usersAssign);
		});
		return taskDtos;
	}

	private List<TaskDto> getTaskDtos (List<TaskEntity> entities) {
		return entities.stream ().map (entity -> mapper.map (entity, TaskDto.class)).toList ();
	}

	@Override
	public List<TaskDto> findTasksWithDeadlineBeforeDate (Date date) {
		List<TaskEntity> entities = taskRepository.findByDeadlineLessThan (date);
		List<TaskDto> taskDtos = getTaskDtos (entities);
		// for each dto set users objects
		taskDtos.forEach (taskDto -> {
			List<UserDto> usersAssign = new ArrayList<> ();
			taskDto.getUsers ().forEach (user -> {
				UserDto userById = userClient.getUserById (user);
				if(userById != null){
					usersAssign.add (userById);
				}
			});
			taskDto.setUserDtos (usersAssign);
		});
		return taskDtos;
	}

	@Override
	public void setTaskStatusCompleted (Long taskId) {
		Optional<TaskEntity> optional = taskRepository.findById (taskId);
		optional.ifPresent (taskEntity ->{
			taskEntity.setStatus (TaskStatus.COMPLETED);
			taskRepository.save (taskEntity);
		});
	}

	@Override
	public void setTaskStatusInProgress (Long taskId) {
		Optional<TaskEntity> optional = taskRepository.findById (taskId);
		optional.ifPresent (taskEntity ->{
			taskEntity.setStatus (TaskStatus.IN_PROGRESS);
			taskRepository.save (taskEntity);
		});
	}

	@Override
	public Map<String, Long> statistics (Long projectId) {
		Map<String,Long> stats = new HashMap<> ();
		stats.put ("statusPending",taskRepository.countByProject_IdAndStatus (projectId,TaskStatus.PENDING));
		stats.put ("statusInProgress",taskRepository.countByProject_IdAndStatus (projectId,TaskStatus.IN_PROGRESS));
		stats.put ("statusCompleted",taskRepository.countByProject_IdAndStatus (projectId,TaskStatus.COMPLETED));
		stats.put ("priorityHigh",taskRepository.countByPriorityAndProject_Id ( Priority.HIGH,projectId));
		stats.put ("priorityMedium",taskRepository.countByPriorityAndProject_Id ( Priority.MEDIUM,projectId));
		stats.put ("priorityLow",taskRepository.countByPriorityAndProject_Id ( Priority.LOW,projectId));

		return stats;
	}

}
