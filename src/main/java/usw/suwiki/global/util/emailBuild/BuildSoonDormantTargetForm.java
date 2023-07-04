package usw.suwiki.global.util.emailBuild;

import org.springframework.stereotype.Component;

@Component
public class BuildSoonDormantTargetForm {

    public String buildEmail() {
        return "<center>\n" +
                "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">" +
                "\t<div class=container>\n" +
                "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.<br>" +
                "\t\t<p>\n" +
                "11개월간 접속하지 않아 메일 수신일로부터 30일 후 휴면계정으로 전환됨을 알려드립니다.<br>\n" +
                "만약 휴면 계정 전환을 원치 않으시다면, SUWIKI 로그인을 해주세요." +
                "\t\t<br>\n" +
                "\t\t감사합니다.\n" +
                "\t</div>\n" +
                "</center>";
    }
}
