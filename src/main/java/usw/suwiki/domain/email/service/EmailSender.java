package usw.suwiki.domain.email.service;

public interface EmailSender {
    void send(String to, String email);
}
