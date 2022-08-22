package testPacakageSearch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.SuwikiApplication;

@SpringBootTest(classes = SuwikiApplication.class)
public class MainTest {

    @Test
    void test() {
        System.out.println("test");
    }
}
