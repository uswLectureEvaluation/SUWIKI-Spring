package usw.suwiki.external.mail.form;


import org.springframework.stereotype.Component;

@Component
public class UserAutoDeletedWarningForm {

    public String buildEmail() {
        return "<center>\n" +
            "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">"
            +
            "\t<div class=container>\n" +
            "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.\n" +
            "\t\t<p>\n" +
            "SUWIKI 서비스에 3년 접속하지 않아 30일 후 계정이 삭제됩니다.<br>" +
            "(이 안내는 매일 발송 되기 때문에, 휴면계정 전환 시점은 첫 안내를 수신한 시점부터 30일 입니다.)<br>" +
            "만약 삭제를 원치 않으시다면, SUWIKI 로그인을 해주세요.\n" +
            "<br>" +
            "\t\t<p>\n" +
            "\t\t감사합니다.\n" +
            "\t</div>\n" +
            "</center>";
    }
}
