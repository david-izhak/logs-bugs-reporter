package telran.logs.bugs;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import telran.logs.bugs.client.EmailProviderClient;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@SpringBootTest
@Import({ TestChannelBinderConfiguration.class, MailSenderValidatorAutoConfiguration.class })
public class EmailNotifierTest {
	
	private static final String EMAIL = "moshe@gmail.com";

	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
			.withConfiguration(GreenMailConfiguration.aConfig().withUser("log", "logs-bugs"));
	
	@MockBean
	EmailProviderClient client;
	
	@Autowired
	InputDestination input;
	
//	private String  messegeTexFormatter(LogDto logDto, String recipient) {
//		String line1 = String.format("\nHello, %s\n", recipient);
//		String line2 = "Exception has been received\n";
//		String line3 = String.format("Date:%s\n", logDto.dateTime);
//		String line4 = String.format("Exception type: %s\n", logDto.logType);
//		String line5 = String.format("Artifact: %s\n", logDto.artifact);
//		String line6 = String.format("Explanation: %s", logDto.result);
//		return line1 + line2 + line3 + line4 + line5 + line6;
//	}
	
	@Test
	void normalFlow() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn(EMAIL);
		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
//		String textExpected = messegeTexFormatter(logException, "Programmer");
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertEquals(EMAIL, message.getAllRecipients()[0].toString());
		assertEquals("exception", message.getSubject());
//		assertEquals(textExpected, GreenMailUtil.getBody(message));
		textMassageTest(logException, message, "Programmer");
	}
	
	@Test
	void assignerFlow() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn("");
		when(client.getAssignerMail()).thenReturn(EMAIL);
		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact000", 0, "result");
//		String textExpected = messegeTexFormatter(logException, "Programmer");
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertEquals(EMAIL, message.getAllRecipients()[0].toString());
		assertEquals("exception", message.getSubject());
//		assertEquals(textExpected, GreenMailUtil.getBody(message));
		textMassageTest(logException, message, "Opened Bugs Assigne");
	}

	private void textMassageTest(LogDto logException, MimeMessage message, String recipien) {
		assertTrue(GreenMailUtil.getBody(message).contains("Hello, "));
		assertTrue(GreenMailUtil.getBody(message).contains(recipien));
		assertTrue(GreenMailUtil.getBody(message).contains("Exception has been received"));
		assertTrue(GreenMailUtil.getBody(message).contains("Date:"));
		assertTrue(GreenMailUtil.getBody(message).contains("Exception type: "));
		assertTrue(GreenMailUtil.getBody(message).contains("Artifact: "));
		assertTrue(GreenMailUtil.getBody(message).contains("Explanation: "));
		assertTrue(GreenMailUtil.getBody(message).contains(logException.dateTime.toString()));
		assertTrue(GreenMailUtil.getBody(message).contains(logException.logType.toString()));
		assertTrue(GreenMailUtil.getBody(message).contains(logException.artifact));
		assertTrue(GreenMailUtil.getBody(message).contains(logException.result));
	}
}
