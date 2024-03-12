package usw.suwiki.auth.token.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConfirmResponse {
  SUCCESS("""
      <center>
        <img class="suwikilogo" src="https://avatars.githubusercontent.com/u/96416159?s=200&v=4" style="display:block; " alt="SUWIKILOGO">
        <div class="container">
            <p>안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.
            <p>이메일 인증이 완료되었습니다. 이제 서비스를 이용할 수 있습니다!</p>
            <p>감사합니다.</p>
        </div>
      </center>
    """),
  EXPIRED("""
      <center>
        <img class="suwikilogo" src="https://avatars.githubusercontent.com/u/96416159?s=200&v=4" style="display:block; " alt="SUWIKILOGO">
        <div class="container">
            <p>안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.</p>
            <p>해당 링크는 인증 시간이 지나 만료된 링크입니다. 회원가입을 처음부터 다시 진행해 주세요.</p>
            <p>감사합니다.</p>
        </div>
      </center>
    """),
  ERROR("""
      <center>
        <img class="suwikilogo" src="https://avatars.githubusercontent.com/u/96416159?s=200&v=4" style="display:block; " alt="SUWIKILOGO">
        <div class="container">
            <p>안녕하세요. 수원대학교 강의평가 플랫폼 SUWIKI 입니다.</p>
            <p>예기지 못한 오류로 인증을 수행하지 못했습니다. 관리자에게 문의 부탁드립니다.</p>
            <p>감사합니다.</p>
        </div>
      </center>
    """),
  ;

  private final String content;
}
