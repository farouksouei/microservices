package kripton.candidateservice.service.feign;

import kripton.candidateservice.model.dtos.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient (name = "user-service")

public interface UserClient {
	@GetMapping("api/users/manage/id/{user-id}")
	public UserDto getUserById(@PathVariable ("user-id") String userId);

	@PostMapping("api/users/manage")
	public String addUser(@RequestBody UserDto userDto);


}
