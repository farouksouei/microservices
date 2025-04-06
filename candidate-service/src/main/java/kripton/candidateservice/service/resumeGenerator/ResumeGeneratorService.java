package kripton.candidateservice.service.resumeGenerator;

import com.lowagie.text.DocumentException;
import kripton.candidateservice.model.dtos.CandidateDetailsDto;
import kripton.candidateservice.model.entities.CandidateEntity;
import kripton.candidateservice.model.repositories.CandidateEntityRepository;
import kripton.candidateservice.service.ICandidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeGeneratorService {

	private final ICandidateService candidateService ;

	public String generatePdfResume(Long candidateId) throws IOException, DocumentException {
		CandidateDetailsDto candidate = candidateService.findCandidateWithDetails (candidateId);

			Map<String,Object> candidateValues = new HashMap<> ();
			candidateValues.put ("firstName",candidate.getFirstName ());
			candidateValues.put ("lastName",candidate.getLastName ());
			candidateValues.put ("email",candidate.getEmail ());
			candidateValues.put ("phone",candidate.getPhone ());
			candidateValues.put ("country",candidate.getCountry ());
			candidateValues.put ("designation",candidate.getDesignation ());
			candidateValues.put ("skills",candidate.getSkills ());
			candidateValues.put ("description",candidate.getDescription ());
			candidateValues.put ("experiences",candidate.getExperiences ());
			candidateValues.put ("educations",candidate.getEducations ());
			ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver ();
			templateResolver.setSuffix (".html");
			templateResolver.setTemplateMode (TemplateMode.HTML);
			TemplateEngine templateEngine = new TemplateEngine ();
			templateEngine.setTemplateResolver (templateResolver);
			Context context = new Context ();
			context.setVariables (candidateValues);
			String html = templateEngine.process ("resume/resume_template", context);

			String outputFolder =
					System.getProperty("user.home") + File.separator +
							candidate.getLastName ().toUpperCase ()
							+'.'+
							candidate.getFirstName ().toUpperCase ()
							+".pdf";
			OutputStream outputStream = new FileOutputStream (outputFolder);
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(html);
			renderer.layout();
			renderer.createPDF(outputStream);
			outputStream.close();

			return outputFolder;
	}

}
