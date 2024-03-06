package usw.suwiki.external.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.MailException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
class EmailSendService implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Async
    @Override
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
            throw new MailException(ExceptionType.SEND_MAIL_FAILED);
        }
    }
}
