//package usw.suwiki.domain.admin.admin.controller;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.MediaType;
//import org.springframework.jdbc.datasource.init.ScriptUtils;
//import org.springframework.test.web.servlet.ResultActions;
//import usw.suwiki.global.jwt.jwtAgent;
//import usw.suwiki.global.jwt.jwtAgent;
//
//import java.sql.Connection;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//class UserAdminControllerTest {
//
//    @MockBean
//    jwtAgent jwtAgent;
//
//    @MockBean
//    jwtAgent jwtAgent;
//
//    @BeforeAll
//    public void init() throws Exception {
//        try (Connection conn = dataSource.getConnection()) {
//            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-user.sql"));
//            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-lecture.sql"));
//            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-exampost.sql"));
//            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/data/insert-viewexam.sql"));
//        }
//    }
//
//    @DisplayName("")
//}