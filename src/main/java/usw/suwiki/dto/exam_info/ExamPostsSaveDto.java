package usw.suwiki.dto.exam_info;

import lombok.Getter;
import lombok.NoArgsConstructor;

//ExamInfo Column은 EvaluatePosts 에 있다.
@Getter
@NoArgsConstructor
public class ExamPostsSaveDto {
    private String lectureName; //과목
    private String semester;
    private String professor;   //교수

    private String examInfo;    //시험방식
    private String examDifficulty;    //난이도

    private String content;

    public ExamPostsSaveDto( String lectureName, String semester, String professor,String examInfo,
                            String examDifficulty, String content) {
        this.lectureName = lectureName;
        this.semester = semester;
        this.professor = professor;
        this.examInfo = examInfo;
        this.examDifficulty = examDifficulty;
        this.content = content;
    }
}
