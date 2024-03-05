package usw.suwiki.external.mail.form;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import usw.suwiki.domain.confirmationtoken.ConfirmationToken;
import usw.suwiki.global.properties.ServerProperties;

@Component  // TODO refactor: Component 제거. -> ServerProperties 빈 주입 방법 고민
@RequiredArgsConstructor
public class BuildEmailAuthForm {

    private static final String CONFIRMATION_TOKEN_URL = "/v2/confirmation-token/verify/?token=";
    private final ServerProperties serverProperties;

    public String buildEmail(ConfirmationToken confirmationToken) {
        String redirectUrl = buildRedirectUrl(confirmationToken);

        return "<center>\n" +
            "\t<img class=\"suwikilogo\"src=\"https://avatars.githubusercontent.com/u/96416159?s=200&v=4\" style=\"display:block; \"alt=\"SUWIKILOGO\">"
            +
            "\t<div class=container>\n" +
            "\t\t안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.\n" +
            "\t\t<p>\n" +
            "                <b>재학생 인증 메일 전송해드립니다. </b>\n" +
            "\t\t<br>\n" +
            "\t\t" + "<a href=" + redirectUrl + ">클릭하여 이메일 인증하기</a>" + "\n" +
            "                <p>\n" +
            "<br>" +
            "                위 링크를 클릭하시면 정상적으로 서비스 이용이 가능합니다." + "\n" +
            "<br>" +
            "\t\t인증이 정상적으로 수행되지 않을 시, uswsuwiki@gmail.com 으로 문의 부탁드립니다. \n" +
            "\t\t<p>\n" +
            "\t\t감사합니다.\n" +
            "\t</div>\n" +
            "</center>";
    }

    private String buildRedirectUrl(ConfirmationToken confirmationToken) {
        return serverProperties.getDomain() + CONFIRMATION_TOKEN_URL + confirmationToken.getToken();
    }
}
