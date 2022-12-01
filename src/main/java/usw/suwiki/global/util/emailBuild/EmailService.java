package usw.suwiki.global.util.emailBuild;


import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import usw.suwiki.domain.email.service.EmailSender;
import usw.suwiki.exception.errortype.MailException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static usw.suwiki.exception.ErrorType.SEND_MAIL_FAILED;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("수원대학교 강의평가 사이트 SUWIKI 입니다.");
            helper.setFrom("uswsuwiki.gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailException(SEND_MAIL_FAILED);
        }
    }
}
