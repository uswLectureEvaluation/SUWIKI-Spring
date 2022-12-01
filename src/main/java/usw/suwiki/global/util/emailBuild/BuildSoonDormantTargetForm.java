package usw.suwiki.global.util.emailBuild;

import org.springframework.stereotype.Component;

@Component
public class BuildSoonDormantTargetForm {

    public String buildEmail() {
        return "<center>\n" +
                "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">" +
                "\t<div class=container>\n" +
                "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.\n" +
                "\t\t<p>\n" +
                "11개월간 접속하지 않아 30일 후 휴면계정으로 전환됨을 알려드립니다.(이 안내는 매일 발송 되기 때문에, 휴면계정 전환 시점은 첫 안내를 수신한 시점부터 30일 입니다.) \n" +
                "만약 휴면 계정 전환을 원치 않으시다면, SUWIKI 로그인을 해주세요.\n" +
                "\t\t<br>\n" +
                "\t\t감사합니다.\n" +
                "\t</div>\n" +
                "</center>";
    }
}
