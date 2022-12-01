package usw.suwiki.global.util.emailBuild;

public class BuildFindLoginIdForm {
    public String buildEmail(String loginId) {
        return "<center>\n" +
                "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">" +
                "\t<div class=container>\n" +
                "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.\n" +
                "\t\t<p>\n" +
                "                <b>아이디 찾기 결과를 전송해드립니다. </b>\n" +
                "\t\t<br>\n" +
                "\t\t" + loginId + "\n" +
                "                <p>\n" +
                "<br>" +
                "\t\t감사합니다.\n" +
                "\t</div>\n" +
                "</center>";
    }
}
