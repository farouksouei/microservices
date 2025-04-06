package kripton.communicationservice.services;

import kripton.communicationservice.mailConfig.EmailDetails;
import org.springframework.kafka.annotation.KafkaListener;

public interface IMailService {


    @KafkaListener(topics = "created-mail",groupId = "group-id")
    void creationNotification(String data);

    @KafkaListener(topics = "completion-mail",groupId = "group-id")
    void completionNotification(String data);

    @KafkaListener(topics = "job-posted",groupId = "group-id")
    void jobPostedNotification(String data);

    @KafkaListener(topics = "job-application",groupId = "group-id")
    void appliedForJobNotification(String data);

    String sendSimpleMail(EmailDetails details);

    String sendMailWithAttachment(EmailDetails details);
    
    String sendMissingDataEmail(EmailDetails details);
}
