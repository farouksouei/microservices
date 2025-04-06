package kripton.jobservice.config.batch;

import kripton.jobservice.model.dtos.job.JobDto;
import kripton.jobservice.model.entities.JobEntity;
import kripton.jobservice.model.entities.StatusOfJob;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemProcessor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class JobProcessor implements ItemProcessor<JobEntity,JobEntity>{


    @Override
    public JobEntity process(JobEntity item) throws Exception {
        item.setStartDate(new Date());
        item.setStatus(StatusOfJob.OPEN);
        /**
         * TIMESTAMPS
         */
        item.setCreatedAt(new Date());
        item.setModifiedAt(new Date());
        return item;
    }
}
