package usw.suwiki.domain.exam.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import usw.suwiki.BaseIntegrationTest;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.global.jwt.JwtResolver;

/**
 * Lecture 도메인 통합테스트
 */
class LectureControllerTest extends BaseIntegrationTest{

	@MockBean
	JwtResolver jwtResolver;

	@Autowired
	private EntityManager entityManager;

	@BeforeAll
	void setupOnce() {
		System.out.println("tre : " + entityManager.getTransaction());

		Lecture entity = new Lecture();
		entityManager.persist(entity);
	}

	// @BeforeAll
	// public void init() throws Exception {
	// 	try (Connection conn = dataSource.getConnection()) {
	// 		ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-lecture.sql"));
	// 	}
	// }

	@Test
	void 전체강의불러오기_강의평가개수우선_그다음_시간순_페이징_10개씩() throws Exception {
		//when
		ResultActions resultActions = mvc.perform(
				get("/lecture/all/")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		final int RESPONSE_DATA_SIZE = 3;
		final int RESPONSE_FIRST_ID = 2;
		final int RESPONSE_SECOND_ID = 1;
		final int RESPONSE_THIRD_ID = 3;

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data", hasSize(RESPONSE_DATA_SIZE)))
			.andExpect(jsonPath("$.data[0].id").value(RESPONSE_FIRST_ID))
			.andExpect(jsonPath("$.data[1].id").value(RESPONSE_SECOND_ID))
			.andExpect(jsonPath("$.data[2].id").value(RESPONSE_THIRD_ID));
	}

	@Test
	void 전체강의불러오기_Best강의순_페이징_10개씩() throws Exception {
		//given

		//when
		ResultActions resultActions = mvc.perform(
				get("/lecture/all/?option=lectureTotalAvg")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		final int RESPONSE_DATA_SIZE = 3;
		final int RESPONSE_FIRST_ID = 1;
		final int RESPONSE_SECOND_ID = 2;
		final int RESPONSE_THIRD_ID = 3;

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data", hasSize(RESPONSE_DATA_SIZE)))
			.andExpect(jsonPath("$.data[0].id").value(RESPONSE_FIRST_ID))
			.andExpect(jsonPath("$.data[1].id").value(RESPONSE_SECOND_ID))
			.andExpect(jsonPath("$.data[2].id").value(RESPONSE_THIRD_ID));
	}

	@Test
	void ID로_특정강의_불러오기() throws Exception {
		//given
		String authorization = "authorization";

		//when
		when(jwtResolver.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);

		ResultActions resultActions = mvc.perform(
				get("/lecture/?lectureId=1")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		final int RESPONSE__ID = 1;
		resultActions
			.andExpect(status().isOk());
	}

	@Test
	void 키워드로_강의_검색하기() throws Exception {
		//given
		String authorization = "authorization";

		//when
		when(jwtResolver.getUserIsRestricted(authorization)).thenReturn(Boolean.FALSE);

		ResultActions resultActions = mvc.perform(
				get("/lecture/search/?searchValue=1")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		final int RESPONSE_DATA_SIZE = 1;
		final int RESPONSE_ID = 1;

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data", hasSize(RESPONSE_DATA_SIZE)))
			.andExpect(jsonPath("$.data[0].id").value(RESPONSE_ID));
	}
}