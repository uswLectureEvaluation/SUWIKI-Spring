package usw.suwiki.domain.exam.controller;

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
import usw.suwiki.global.jwt.JwtAgent;

class ExamPostsControllerTest extends BaseIntegrationTest {

	@MockBean
	JwtAgent jwtAgent;

	@BeforeAll
	public void init() throws Exception {
		try (Connection conn = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-user.sql"));
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-lecture.sql"));
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-exampost.sql"));
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-viewexam.sql"));
		}
	}

	@Test
	void 시험정보_불러오기_권한_있는사람() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(1L);

		//when
		ResultActions resultActions = mvc.perform(
				get("/exam-posts/?lectureId=1")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.canRead").value(Boolean.TRUE))
			.andExpect(jsonPath("$.examDataExist").value(Boolean.TRUE));
	}

	@Test
	void 시험정보_불러오기_권한_없는사람() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(2L);

		//when
		ResultActions resultActions = mvc.perform(
				get("/exam-posts/?lectureId=1")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.canRead").value(Boolean.FALSE))
			.andExpect(jsonPath("$.examDataExist").value(Boolean.TRUE))
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	void 시험정보_중복구매_예외_테스트() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(1L);

		//when
		ResultActions resultActions = mvc.perform(
				post("/exam-posts/purchase/?lectureId=1")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		resultActions
			.andExpect(status().isBadRequest());
	}

	@Test
	void 시험정보_존재하지않을때_정상동작_테스트() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(2L);

		//when
		ResultActions resultActions = mvc.perform(
				get("/exam-posts/?lectureId=2")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isEmpty())
			.andExpect(jsonPath("$.examDataExist").value(Boolean.FALSE));
	}
}