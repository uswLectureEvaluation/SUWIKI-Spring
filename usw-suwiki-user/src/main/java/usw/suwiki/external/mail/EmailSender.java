package usw.suwiki.external.mail;

/**
 * @see MailType
 */
public interface EmailSender {
    // 부가 정보를 추가해서 메일을 전송
    void send(String to, MailType mailType, String param);

    // 부가 정보 없이 전송
    void send(String to, MailType mailType);
}
