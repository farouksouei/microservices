package kripton.candidateservice.config.batch;


import kripton.candidateservice.model.entities.CandidateEntity;
import kripton.candidateservice.model.repositories.CandidateEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Configuration
@RequiredArgsConstructor
//@EnableBatchProcessing
public class SpringBatchConfig {



	private final CandidateEntityRepository candidateEntityRepository;


//	@Bean
//	@StepScope
//	public FlatFileItemReader<CandidateEntity> reader(@Value ("#{jobParameters[fullPathFileName]}")String filePath){
//		FlatFileItemReader<CandidateEntity> reader =
//				new FlatFileItemReaderBuilder<CandidateEntity> ()
//				.name ("csvReader")
//				.resource (new FileSystemResource (new File (filePath)))
//				.delimited ()
//				.names ("firstName", "lastName", "email", "phone", "country","designation")
//				.fieldSetMapper (new BeanWrapperFieldSetMapper<CandidateEntity> () {{
//					setTargetType (CandidateEntity.class);
//					setStrict (false);
//				}}).build ();
//		reader.setLinesToSkip (1);
//		return reader;
//	}


	@Bean
	public CandidateProcessor processor() {
		return new CandidateProcessor ();
	}


	@Bean
	@StepScope
	public CandidateReader reader(@Value ("#{jobParameters[fullPathFileName]}")String filePath,
	                              @Value ("#{jobParameters[fieldNames]}") String fieldNames) {
		return new CandidateReader (filePath, fieldNames);
	}
	@Bean
	public Job importUserJob(JobRepository jobRepository, Step step1) {
		return new JobBuilder("importUserJob", jobRepository)
				.incrementer(new RunIdIncrementer ())
				.flow(step1)
				.end()
				.build();
	}

	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                  CandidateReader reader) {
		return new StepBuilder("step1", jobRepository)
				.<CandidateEntity, CandidateEntity> chunk(10, transactionManager)
				.reader(reader)
				.processor(processor())
				.writer(new CandidateWriter (candidateEntityRepository))
				.taskExecutor (taskExecutor ())
				.allowStartIfComplete (true)
				.build();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(10);
		return asyncTaskExecutor;
	}

}
