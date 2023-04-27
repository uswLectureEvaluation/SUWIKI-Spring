package usw.suwiki.domain.lecture.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.sql.Connection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import usw.suwiki.BaseIntegrationTest;
import usw.suwiki.domain.lecture.controller.dto.JsonToLectureForm;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.lecture.domain.repository.LectureRepository;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.global.jwt.JwtTokenResolver;
import usw.suwiki.global.jwt.JwtTokenValidator;

/**
 * Lecture 도메인 통합테스트
 */
class LectureControllerTest extends BaseIntegrationTest{

	@Mock
	JwtTokenValidator jwtTokenValidator;

	@Mock
	JwtTokenResolver jwtTokenResolver;

	@Autowired
	LectureController lectureController;

	@Autowired
	LectureService lectureService;

	@Autowired
	LectureRepository lectureRepository;

	@BeforeAll
	public void init() throws Exception {
		try (Connection conn = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-lecture.sql"));
		}
	}

	@Test
	@DisplayName("")
	void 전체강의불러오기_시간순_페이징_10개씩() throws Exception {
		//given

		//when
		ResultActions resultActions = mvc.perform(
				get("/lecture/all/")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		resultActions
			.andExpect(status().isOk());
	}

	@Test
	void 전체강의불러오기_Best강의순_페이징_10개씩() throws Exception {
		//given

		//when
		ResultActions resultActions = mvc.perform(
				get("/lecture/all/order=?")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		resultActions
			.andExpect(status().isOk());
	}

	@Test
	void ID로_특정강의_불러오기() throws Exception {
		//given
		String authorization = "authorization";

		//when
		when(jwtTokenValidator.validateAccessToken(any())).thenReturn(Boolean.TRUE);
		when(jwtTokenResolver.getUserIsRestricted(any())).thenReturn(Boolean.FALSE);

		ResultActions resultActions = mvc.perform(
				get("/lecture?lectureId=1")
					.header("Authorization", authorization)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
			.andDo(print());

		//then
		resultActions
			.andExpect(status().isOk());
	}

	@Test
	void findAllLectureApi() {
	}

	@Test
	void findLectureByLectureId() {
	}

	private Lecture createLecture(String sequence) {
		return Lecture.toEntity(readForm(sequence));
	}

	private JsonToLectureForm readForm(String sequence) {
		JsonToLectureForm form = JsonToLectureForm.builder()
			.capprType("cType0" + sequence)
			.evaluateType("eType0" + sequence)
			.lectureCode("code0" + sequence)
			.selectedSemester("2022-" + sequence)
			.grade(1)
			.lectureType("type0" + sequence)
			.placeSchedule("placeSchedule0" + sequence)
			.diclNo("diclNo0" + sequence)
			.majorType("majorType0" + sequence)
			.point(1)
			.professor("Professor0" + sequence)
			.lectureName("Lecture0" + sequence)
			.build();

		return form;
	}
}