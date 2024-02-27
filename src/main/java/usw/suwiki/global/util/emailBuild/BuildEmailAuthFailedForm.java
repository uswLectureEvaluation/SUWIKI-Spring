package usw.suwiki.global.util.emailBuild;

import org.springframework.stereotype.Component;

@Component
public class BuildEmailAuthFailedForm {

    public String tokenIsAlreadyUsed() {
        return "<center>\n" +
            "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">"
            +
            "\t<div class=container>\n" +
            "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.\n" +
            "\t\t<p>\n" +
            "해당 링크는 이미 인증된 링크입니다. 서비스로 돌아가 로그인을 수행해주세요." +
            "<br>" +
            "\t\t<p>\n" +
            "\t\t감사합니다.\n" +
            "\t</div>\n" +
            "</center>";
    }

    public String tokenIsExpired() {
        return "<center>\n" +
            "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">"
            +
            "\t<div class=container>\n" +
            "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.\n" +
            "\t\t<p>\n" +
            "해당 링크는 인증 시간이 지나 만료된 링크입니다. 회원가입을 처음부터 다시 진행해 주세요." +
            "<br>" +
            "\t\t<p>\n" +
            "\t\t감사합니다.\n" +
            "\t</div>\n" +
            "</center>";
    }

    public String internalError() {
        return "<center>\n" +
            "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">"
            +
            "\t<div class=container>\n" +
            "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.\n" +
            "\t\t<p>\n" +
            "예기지 못한 오류로 인증을 수행하지 못했습니다. 관리자에게 문의 부탁드립니다." +
            "<br>" +
            "\t\t<p>\n" +
            "\t\t감사합니다.\n" +
            "\t</div>\n" +
            "</center>";
    }
}