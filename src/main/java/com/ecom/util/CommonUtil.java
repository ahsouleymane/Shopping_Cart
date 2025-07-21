package com.ecom.util;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public Boolean sendMail(String url, String recipientEmail) throws UnsupportedEncodingException, MessagingException {
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom("djangoresetpass@gmail.com", "habu");
		helper.setTo(recipientEmail);
		
		String content = "<p>Bonjour,</p>" + "<p>vous avez envoyez une requete de réinitialisation de votre mot de passe.</p>"
				+ "<p>Cliquer sur le lien ci-dessous pour modifier votre mot de passe:</p>"
				+ "<p><a href=\""
				+ url
				+ "\">Modifier mon mot de passe</a></p>";
		helper.setSubject("Réinitialisation du mot de passe");
		helper.setText(content, true);
		mailSender.send(message);
		
		return true;
		
	}

	public static String generateUrl(HttpServletRequest request) {
		
		// http://localhost:8080/reset-password
		String siteUrl = request.getRequestURL().toString();
		
		return siteUrl.replace(request.getServletPath(), "");
	}

}
