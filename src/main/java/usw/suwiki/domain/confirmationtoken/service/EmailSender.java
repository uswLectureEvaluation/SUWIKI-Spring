package usw.suwiki.domain.confirmationtoken.service;

public interface EmailSender {
    void send(String to, String email);
}
