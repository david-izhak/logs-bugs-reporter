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
	
	@Test
	void normalFlow() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn(EMAIL);
		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertEquals(EMAIL, message.getAllRecipients()[0].toString());
		assertEquals("exception", message.getSubject());
		textMassageTest(logException, message, "Programmer");
	}
	
	@Test
	void assignerFlow() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn("");
		when(client.getAssignerMail()).thenReturn(EMAIL);
		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact000", 0, "result");
		input.send(new GenericMessage<LogDto>(logException));
		MimeMessage message = greenMail.getReceivedMessages()[0];
		assertEquals(EMAIL, message.getAllRecipients()[0].toString());
		assertEquals("exception", message.getSubject());
		textMassageTest(logException, message, "Opened Bugs Assigne");
	}

	@Test
	void badFlow() throws MessagingException {
		when(client.getEmailByArtifact(anyString())).thenReturn("");
		when(client.getAssignerMail()).thenReturn("");
		LogDto logException = new LogDto(new Date(), LogType.AUTHENTICATION_EXCEPTION, "artifact", 0, "result");
		input.send(new GenericMessage<LogDto>(logException));
		assertEquals(0, greenMail.getReceivedMessages().length);
	}

	private void textMassageTest(LogDto logException, MimeMessage message, String recipient) {
		String textMessageRecieved = GreenMailUtil.getBody(message);
		textMassageTestExecute(textMessageRecieved, "Hello, ");
		textMassageTestExecute(textMessageRecieved, recipient);
		textMassageTestExecute(textMessageRecieved, "Exception has been received");
		textMassageTestExecute(textMessageRecieved, "Date:");
		textMassageTestExecute(textMessageRecieved, "Exception type: ");
		textMassageTestExecute(textMessageRecieved, "Artifact: ");
		textMassageTestExecute(textMessageRecieved, "Explanation: ");
		textMassageTestExecute(textMessageRecieved, logException.dateTime.toString());
		textMassageTestExecute(textMessageRecieved, logException.logType.toString());
		textMassageTestExecute(textMessageRecieved, logException.artifact);
		textMassageTestExecute(textMessageRecieved, logException.result);
	}
	private void textMassageTestExecute(String textMessageRecieved, String textMessageExpected) {
		assertTrue(textMessageRecieved.contains(textMessageExpected));
	}
}
