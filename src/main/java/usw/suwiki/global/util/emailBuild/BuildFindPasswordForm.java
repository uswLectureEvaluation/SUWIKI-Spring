package usw.suwiki.global.util.emailBuild;

import org.springframework.stereotype.Component;

@Component
public class BuildFindPasswordForm {

    public String buildEmail(String password) {
        return "<center>\n" +
            "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">"
            +
            "\t<div class=container>\n" +
            "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.\n" +
            "\t\t<p>\n" +
            "                <b>비밀번호 찾기 결과를 전송해드립니다. </b>\n" +
            "\t\t<br>\n" +
            "\t\t" + password + "\n" +
            "                <p>\n" +
            "<br>" +
            "로그인 후 반드시 비밀번호를 변경해 주시길 바랍니다. <br>" +
            "\t\t감사합니다.\n" +
            "\t</div>\n" +
            "</center>";
    }
}
