package usw.suwiki.global.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class BadWordFiltering {

    // 반환형태 --> String : String --> 실패사유 : ~~~ 욕설 사용
    public Optional<String> filtering(String text) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/BadWordList.txt"));

        // 욕설 단어를 하나씩 담을 리스트
        ArrayList<String> badWordList = new ArrayList<>();

        // 욕설 텍스트 문서에 있는 내용 하나씩 리스트에 담기
        while (br.readLine() != null) {
            badWordList.add(br.readLine());
        }

        // badWordList 에 들어있는 값들과, 입력한 텍스트와 비교
        for (String badWord : badWordList) {
            if (text.contains(badWord)) {
                return Optional.of(badWord);
            }
        }

        return Optional.empty();

    }
}
