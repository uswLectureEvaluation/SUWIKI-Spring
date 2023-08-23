package usw.suwiki.controller.evaluatepost;

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
import usw.suwiki.domain.evaluatepost.controller.dto.EvaluatePostSaveDto;
import usw.suwiki.global.jwt.JwtAgent;

class EvaluatePostControllerTest extends BaseIntegrationTest {

	@MockBean
	JwtAgent jwtAgent;

	@BeforeAll
	public void init() throws Exception {
		try (Connection conn = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-user.sql"));
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-lecture.sql"));
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-evaluatepost.sql"));
		}
	}

	@Test
	void 이미_작성한유저_강의평가_불러오기() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(1L);

		//when
		ResultActions resultActions = mvc.perform(
				get("/evaluate-posts/?lectureId=1")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		final Long postId = 1L;

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].id").value(postId))
			.andExpect(jsonPath("$.written").value(Boolean.TRUE));

	}

	@Test
	void 작성하지않은유저_강의평가_불러오기() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(2L);

		//when
		ResultActions resultActions = mvc.perform(
				get("/evaluate-posts/?lectureId=1")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		final Long postId = 1L;

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].id").value(postId))
			.andExpect(jsonPath("$.written").value(Boolean.FALSE));
	}

	@Test
	void 강의평가_중첩_작성하기_예외_테스트() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(1L);
		EvaluatePostSaveDto requestBody = EvaluatePostSaveDto.builder()
			.lectureName("testLecture")
			.selectedSemester("2022-1")
			.professor("testProfessor")
			.content("testContent")
			.satisfaction(5.0f)
			.honey(5.0f)
			.learning(5.0f)
			.homework(0)
			.team(0)
			.difficulty(0)
			.build();

		//when
		ResultActions resultActions = mvc.perform(
				post("/evaluate-posts/?lectureId=1")
					.header("Authorization", authorization)
					.content(objectMapper.writeValueAsString(requestBody))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		resultActions
			.andExpect(status().isBadRequest());
	}

	@Test
	void 강의평가_작성하기() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(1L);
		EvaluatePostSaveDto requestBody = EvaluatePostSaveDto.builder()
			.lectureName("testLecture")
			.selectedSemester("2022-1")
			.professor("testProfessor")
			.content("testContent")
			.satisfaction(5.0f)
			.honey(5.0f)
			.learning(5.0f)
			.homework(0)
			.team(0)
			.difficulty(0)
			.build();

		//when
		ResultActions resultActions = mvc.perform(
				post("/evaluate-posts/?lectureId=2")
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
	void 내가작성한_강의평가_조회하기() throws Exception {
		//given
		String authorization = "authorization";
		when(jwtAgent.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);
		when(jwtAgent.getId(authorization)).thenReturn(1L);

		//when
		ResultActions resultActions = mvc.perform(
				get("/evaluate-posts/written")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		resultActions
			.andExpect(status().isOk());

	}

}