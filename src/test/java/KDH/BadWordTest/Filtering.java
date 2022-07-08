package KDH.BadWordTest;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Filtering {

    @Test
    boolean filter() throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/BadWordList.txt"));

        ArrayList<String> badWordList = new ArrayList<>();

        while (br.readLine() != null) {
            badWordList.add(br.readLine());
        }

        String testText = " shit 같은 수업임";
        String testText1 = "하 ㅅㅂ ㅈ같네";

        boolean isBadWord = false;

        for (String badWord : badWordList) {
            if (testText1.contains(badWord)) {
                System.out.println("bad");
                isBadWord = true;
                break;
            }
        }

        return isBadWord;

    }

}

