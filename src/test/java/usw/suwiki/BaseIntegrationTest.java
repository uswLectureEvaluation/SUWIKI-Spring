package usw.suwiki;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import usw.suwiki.global.exception.GlobalExceptionHandler;

@SpringBootTest
@Disabled
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseIntegrationTest {
	@Autowired
	protected MockMvc mvc;
	@Autowired
	protected ObjectMapper objectMapper;
	@Autowired
	protected EntityManagerFactory entityManagerFactory;
	@Autowired
	protected DataSource dataSource;
	protected EntityManager entityManager;
}