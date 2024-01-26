# 문제

- `@ActiveProfiles()` 제거해야 함
- 통합 테스트들이 정상 동작하지 않음 : `BlacklistDomainControllerV2TestBase`, `ConfirmationTokenControllerTestBase`, `EvaluatePostControllerTestBase`, `ExamPostControllerTestBase`, `LectureControllerTestBase`, `NoticeControllerTestBase`, `UserIsolationSchedulingServiceTestBase`, `UserControllerTestBase`, `AdminControllerV2TestBase`
  - 이유 : SQL 파일들 문제... H2 연동되어서 그런가? 이거 JPA로 바꾸는게 나아보인다.
- Mockito 테스트 에러 : `UserBusinessServiceTest`
- 

# 리팩토링 
template -> fixture

# 궁금한 점

- 왜 SuwikiApplicationTests가 없는지? 근데 필요 없나?