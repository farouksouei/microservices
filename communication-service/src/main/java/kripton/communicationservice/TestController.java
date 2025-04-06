package kripton.communicationservice;

import kripton.communicationservice.mailConfig.EmailDetails;
import kripton.communicationservice.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-mail")
@RequiredArgsConstructor
public class TestController {

    private final MailService emailService;
    // Sending a simple Email
    @PostMapping("/sendMail")
    public String
    sendMail(@RequestBody EmailDetails details)
    {
        return emailService.sendSimpleMail(details);
    }

    // Sending email with attachment
    @PostMapping("/sendMailWithAttachment")
    public String sendMailWithAttachment(@RequestBody EmailDetails details)
    {
        return emailService.sendMailWithAttachment(details);
    }
    
    @PostMapping("/sendMissingDataEmail")
    public String sendMissingDataEmail(@RequestBody EmailDetails details)
    {
        return emailService.sendMissingDataEmail(details);
    }

}
