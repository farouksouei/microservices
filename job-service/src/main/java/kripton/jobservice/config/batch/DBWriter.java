package kripton.jobservice.config.batch;
import kripton.jobservice.model.entities.JobEntity;
import kripton.jobservice.model.repositories.EntityJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DBWriter implements ItemWriter<JobEntity> {

    private final EntityJobRepository repository;


    @Override
    public void write(Chunk<? extends JobEntity> chunk) throws Exception {
        List<? extends JobEntity> jobEntities = chunk.getItems();
        log.info(jobEntities.get(0).toString());
        repository.saveAll(jobEntities);
    }
}