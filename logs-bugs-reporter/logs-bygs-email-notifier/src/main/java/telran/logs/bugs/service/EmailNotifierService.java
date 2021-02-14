package telran.logs.bugs.service;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.dto.LogDto;

@Service
@Slf4j
public class EmailNotifierService {


	@Value("${app-email-notifier-subject:exception}")
	String subject;

	@Autowired
	EmailProviderClient emailClient;

	@Autowired
	JavaMailSender mailSender;
	
	enum MailTo {
		ASSIGNER ("Opened Bugs Assigner"), PROGRAMMER ("Programmer");
		String text;
		MailTo(String s) {
			text = s;
		}
	}

	@Bean
	Consumer<LogDto> getExceptionsConsumer() {
		return this::takeLogAndSendMail;
	}

	void takeLogAndSendMail(LogDto logDto) {
		log.debug(">>>> recievd LogDto {} to takeLogAndSendMail()", logDto);
		String email = emailClient.getEmailByArtifact(logDto.artifact);
		log.debug(">>>> recievd email {} from repo", email);
		MailTo recipient = MailTo.PROGRAMMER;
		if (email.isEmpty()) {
			log.debug(">>>> recievd email is empty", email);
			email = emailClient.getAssignerMail();
			log.debug(">>>> recievd Assigner email {} from repo", email);
			recipient = MailTo.ASSIGNER;
		}
		if (email.isEmpty() || email == null) {
			log.error("email ‘to’ has received neither from logs-bugs-email-provider nor from logs-bugs-assigner-mail-provider");
		} else {
			sendMail(logDto, email, recipient);
			log.debug(">>>> sended email about bug to {} on email: {}", recipient.text, email);
		}
	}

	private String  messageTextFormatter(LogDto logDto, MailTo mailTo) {
		StringBuilder stb = new StringBuilder();
		log.debug(">>>> called messegeTexFormatter() method");
		String line1 = String.format("%nHello, %s%n", mailTo.text);
		String line2 = String.format("Exception has been received %n");
		String line3 = String.format("Date:%s%n", logDto.dateTime);
		String line4 = String.format("Exception type: %s%n", logDto.logType);
		String line5 = String.format("Artifact: %s%n", logDto.artifact);
		String line6 = String.format("Explanation: %s%n", logDto.result);
		return stb.append(line1).append(line2).append(line3).append(line4).append(line5).append(line6).toString();
	}


	private void sendMail(LogDto logDto, String email, MailTo recipient) {
		log.debug(">>>> called sendMail() method");
		String messegeText = messageTextFormatter (logDto, recipient);
		log.debug(">>>> recievd messegeText: {}", messegeText);
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setSubject(subject);
		message.setTo(email);
		message.setText(messegeText);
		
		mailSender.send(message);
		log.debug(">>>> sendMail() method finished");
	}
}
