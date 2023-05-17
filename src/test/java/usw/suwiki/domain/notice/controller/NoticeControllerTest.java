package usw.suwiki.domain.notice.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Connection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;

import usw.suwiki.BaseIntegrationTest;
import usw.suwiki.domain.notice.controller.dto.NoticeSaveOrUpdateDto;
import usw.suwiki.global.jwt.JwtAgent;

class NoticeControllerTest extends BaseIntegrationTest {

	@MockBean
	JwtAgent jwtAgent;

	@BeforeAll
	public void init() throws Exception {
		try (Connection conn = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-user.sql"));
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-notice.sql"));
		}
	}

	@Test
	void 공지사항_모두_불러오기() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(1L);

		//when
		ResultActions resultActions = mvc.perform(
				get("/notice/all")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		final Long noticeId = 1L;
		final int RESPONSE_DATA_SIZE = 1;

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data", hasSize(RESPONSE_DATA_SIZE)))
			.andExpect(jsonPath("$.data[0].id").value(noticeId));
	}

	@Test
	void 공지사항_작성하기() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserRole(authorization)).thenReturn("ADMIN");

		NoticeSaveOrUpdateDto requestBody = new NoticeSaveOrUpdateDto("testTitle", "testContent");

		//when
		ResultActions resultActions = mvc.perform(
				post("/notice/")
					.header("Authorization", authorization)
					.content(objectMapper.writeValueAsString(requestBody))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		resultActions
			.andExpect(status().isOk());
	}


	@Test
	void 공지사항_상세_불러오기() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(1L);

		//when
		ResultActions resultActions = mvc.perform(
				get("/notice/?noticeId=1")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		final Long noticeId = 1L;

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(noticeId));
	}
}