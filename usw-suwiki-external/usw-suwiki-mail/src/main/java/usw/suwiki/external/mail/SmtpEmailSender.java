package usw.suwiki.external.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Objects;
import java.util.function.Consumer;

// todo: bean으로 설정해주기
@Service
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {
    private final ServerProperties serverProperties;
    private final RawMailSender rawMailSender;

    @Override
    public void send(String to, MailType mailType, String param) {
        Objects.requireNonNull(param, "parameter cannot be null to this request");
        String argument = mailType.isEmailAuth() ? serverProperties.redirectUrl(param) : param;
        send(to, mailType.template(), context -> context.setVariable(mailType.parameter(), argument));
    }

    @Override
    public void send(String to, MailType mailType) {
        send(to, mailType.template(), context -> {});
    }

    private void send(String to, String template, Consumer<Context> consumer) {
        Context context = new Context();
        consumer.accept(context);
        rawMailSender.send(new Mail(to, template, context));
    }
}
