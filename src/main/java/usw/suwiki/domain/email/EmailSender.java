package usw.suwiki.domain.email;

public interface EmailSender {
    void send(String to, String email);
}
