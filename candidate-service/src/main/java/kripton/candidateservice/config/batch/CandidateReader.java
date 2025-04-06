package kripton.candidateservice.config.batch;

import kripton.candidateservice.model.entities.CandidateEntity;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
public class CandidateReader implements ItemReader<CandidateEntity> {

	private final FlatFileItemReader<CandidateEntity> reader;

	public CandidateReader(String filePath, String fieldNames) {
		String[] fieldNamesArray = fieldNames.split(",");
		this.reader = new FlatFileItemReaderBuilder<CandidateEntity>()
				.name("csvReader")
				.resource(new FileSystemResource(new File(filePath)))
				.delimited()
				.names(fieldNamesArray)
				.fieldSetMapper(new BeanWrapperFieldSetMapper<CandidateEntity>() {{
					setTargetType(CandidateEntity.class);
					setStrict(false);
				}}).build();
		this.reader.setLinesToSkip(1);
	}

	@Override
	public CandidateEntity read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		return this.reader.read();
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.reader.open(new ExecutionContext());
	}

	@AfterStep
	public ExitStatus afterStep(StepExecution stepExecution) {
		this.reader.close();
		return stepExecution.getExitStatus();
	}
}
