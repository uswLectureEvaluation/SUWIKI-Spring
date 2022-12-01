package usw.suwiki.global.util.emailBuild;

import org.springframework.stereotype.Service;

@Service
public class BuildEmailAuthSuccessFormService {

    public String buildEmail() {
        return "<center>\n" +
                "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">" +
                "\t<div class=container>\n" +
                "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.\n" +
                "\t\t<p>\n" +
                "이메일 인증이 완료되었습니다. 이제 서비스를 이용할 수 있습니다! " +
                "<br>" +
                "\t\t<p>\n" +
                "\t\t감사합니다.\n" +
                "\t</div>\n" +
                "</center>";
    }
}