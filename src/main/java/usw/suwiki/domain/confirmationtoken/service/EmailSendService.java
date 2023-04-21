package usw.suwiki.domain.confirmationtoken.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import usw.suwiki.global.exception.errortype.MailException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static usw.suwiki.global.exception.ErrorType.SEND_MAIL_FAILED;

@Service
@AllArgsConstructor
public class EmailSendService implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            mimeMessageHelper.setText(email, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("수원대학교 강의평가 플랫폼 SUWIKI 입니다.");
            mimeMessageHelper.setFrom("uswsuwiki.gmail.com");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailException(SEND_MAIL_FAILED);
        }
    }
}
