package usw.suwiki.global.util.emailBuild;

import org.springframework.stereotype.Component;

@Component
public class BuildPersonalInformationUsingNotifyForm {

    private final String POLICY_PRIVACY = "https://sites.google.com/view/suwiki-policy-privacy";

    public String buildEmail() {
        return "<center>\n" +
                "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">" +
                "\t<div class=container>\n" +
                "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.\n" +
                "\t\t<p>\n" +
                "본 메일은 개인정보보호법 제 39조의 8(개인정보 이용 내역의 통지)에 따라, 이메일 수신동의 여부와 관계 없이 연 1회 전체 회원에게 발송됩니다. \n" +
                "자세한 내역은 개인정보처리방침을 통해 확인하실 수 있습니다.\n" +
                "\t\t" + "<a href=" + POLICY_PRIVACY + ">SUWIKI 개인정보 처리 방침 상세" + "\n" +
                "\t\t<br>\n" +
                "\t\t감사합니다.\n" +
                "\t</div>\n" +
                "</center>";
    }
}
