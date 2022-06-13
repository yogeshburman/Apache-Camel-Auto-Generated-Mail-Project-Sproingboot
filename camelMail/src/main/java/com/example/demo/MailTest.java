package com.example.demo;

import java.io.IOException;
import java.util.Map;

import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.attachment.AttachmentMessage;
import org.apache.camel.attachment.DefaultAttachment;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

@Component
public class MailTest extends RouteBuilder{

	@Override
	public void configure() throws Exception {
	
		from(Constants.testMessage1)
		.to("jms:queue:MailTest")
		.log("Mail test log =====> ${body}")
		.to("jms:topic:MailTest2")
		.log("Mail2 test log =====> ${body}")
		.doTry()
//		.setHeader("to", simple("yogeshburman2@gmail.com,ishant@1team.ai,gondsourabh40@gmail.com"))
//		.setHeader("to", simple("yogeshburman2@gmail.com,mmanisha7431@gmail.com"))
		.setHeader("Content-Type", constant("text/html; charset=UTF-8"))
		//.bean(this , "prepareEmailMessage")
		//.bean(this , "sendAttachment")
		.bean(this ,"htmlEmail")
		.to("smtps://smtp.gmail.com:465?username=yogesh.burman@1team.ai&password=lqgqgglizzvsfway")
		.log("message headers is ==========> ${headers}")
		.log("message body is =====> ${body}");
		
	}
	
	// sending mail threw using this method 
	public void prepareEmailMessage(Exchange exchange, String activityLog){
		Message in = exchange.getIn();
		String body = in.getBody(String.class);
		System.out.println("body before email : "+body);

		Map<String, Object> headers = in.getHeaders();
		Configuration conf = Configuration.defaultConfiguration().setOptions(Option.SUPPRESS_EXCEPTIONS);
		DocumentContext dc = JsonPath.using(conf).parse(body);
				
		String subject = " This is the Test Message ";
		
		headers.put("to", "yogeshburman2@gmail.com");
		headers.put("cc", "yogeshburman2@gmail.com");
		headers.put("Subject", subject);
        in.setHeaders(headers);
        in.setBody(body);
    }
	
	// sending attachment using this method 
    public void sendAttachment(Exchange exchange) throws MessagingException, IOException {
    	
    	Message message = exchange.getIn();
    	Map<String, Object> headers = message.getHeaders();
		
		// use for attaching files
		AttachmentMessage in = exchange.getIn(AttachmentMessage.class);
		in.setBody("Listen this song ");
		DefaultAttachment att = new DefaultAttachment(new FileDataSource("/home/yogesh/Downloads/Dil Galti Kar Baitha Hai(PagalWorld.com.se).mp3"));
		att.addHeader("Content-Description", "some sample content");
		in.addAttachmentObject("Dil Galti Kar Baitha Hai(PagalWorld.com.se).mp3", att);
		
		
        String subject = " This is the Test Message ";
		
		headers.put("to", "yogeshburman2@gmail.com,lookup.shivam@gmail.com");
		headers.put("cc", "yogeshburman2@gmail.com");
		headers.put("Subject", subject);
		message.setHeaders(headers);				

    	
    }
	
	// generating html email 
    public void htmlEmail(Exchange exchange) {
    	
		Message message = exchange.getIn();
		String body = message.getBody(String.class);
		System.out.println("body before email : "+body);

		Map<String, Object> headers = message.getHeaders();
		Configuration conf = Configuration.defaultConfiguration().setOptions(Option.SUPPRESS_EXCEPTIONS);
		DocumentContext dc = JsonPath.using(conf).parse(body);
				
		String subject = " This is the Test Message ";
		
//		headers.put("to", "yogeshburman2@gmail.com,ishant@1team.ai,gondsourabh40@gmail.com,mmanisha7431@gmail.com");
		headers.put("to", "yogeshburman2@gmail.com");
		headers.put("cc", "yogeshburman2@gmail.com");
		headers.put("Subject", subject);
		message.setHeaders(headers);
		message.setBody(body);
		message.setBody("<!DOCTYPE html>\n"
				+ "<html>\n"
				+ "   <head>\n"
				+ "      <title>HEllo</title>\n"
				+ "   </head>\n"
				+ "   <body>\n"
				+ "      heLLO:<br>\n"
				+ "      <H1> HELLO </H1>\n"
				+ "      <a href=\\\"https://in.linkedin.com/in/ishant-jain-a97623113\\\">\\n\"\n"
				+ "         <img alt=\"Please click the link\" src=\"/home/yogesh/Desktop/1627902164023.jpeg\"\n"
				+ "         width=150\" height=\"70\">\n"
				+ "      </a>\n"
				+ "   </body>\n"
				+ "</html>");
    	
    }
	

}
