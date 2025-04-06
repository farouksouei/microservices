package kripton.jobservice.config.batch;

import kripton.jobservice.model.entities.JobEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

//@Configuration
//@EnableBatchProcessing
public class SpringBatchConfig {
//    @Autowired
//    private JobProcessor itemProcessor;
//    @Autowired
//    private  DBWriter itemWriter;
//    @Bean
//    public Job startJjob(){
//        Step step = new StepBuilder("CSV-file-load")
//                .<JobEntity,JobEntity>chunk(100)
//                .reader(jobItemReader())
//                .processor(itemProcessor)
//                .writer(itemWriter)
//                .build();
//
//        return new JobBuilder("JOB-Load").incrementer(new RunIdIncrementer())
//                .start(step).build();
//
//    }
//    @Bean
//    public ItemReader<JobEntity> jobItemReader() {
//        FlatFileItemReader<JobEntity> flatFileItemReader = new FlatFileItemReader<>();
//        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/jobs.csv"));
//        flatFileItemReader.setName("CSV-Reader");
//        flatFileItemReader.setLinesToSkip(1);
//        flatFileItemReader.setLineMapper(lineMapper());
//        return flatFileItemReader;
//    }
//
//    @Bean
//    public LineMapper<JobEntity> lineMapper() {
//
//        DefaultLineMapper<JobEntity> defaultLineMapper = new DefaultLineMapper<>();
//        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
//
//        lineTokenizer.setDelimiter(",");
//        lineTokenizer.setStrict(false);
//        lineTokenizer.setNames("id", "title", "description", "company","proposedSalary","experienceLevel","EmploymentType","location");
//
//        BeanWrapperFieldSetMapper<JobEntity> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
//        fieldSetMapper.setTargetType(JobEntity.class);
//
//        defaultLineMapper.setLineTokenizer(lineTokenizer);
//        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
//
//        return defaultLineMapper;
//    }

}
