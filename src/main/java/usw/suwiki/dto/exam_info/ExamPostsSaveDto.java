package usw.suwiki.dto.exam_info;

import lombok.Getter;
import lombok.NoArgsConstructor;

//ExamInfo Column은 EvaluatePosts 에 있다.
@Getter
@NoArgsConstructor
public class ExamPostsSaveDto {
    private String lectureName; //과목
    private String selectedSemester;
    private String professor;   //교수

    private String examType;
    private String examInfo;    //시험방식
    private String examDifficulty;    //난이도

    private String content;

    public ExamPostsSaveDto( String lectureName, String selectedSemester, String professor,String examType, String examInfo,
                            String examDifficulty, String content) {
        this.lectureName = lectureName;
        this.selectedSemester = selectedSemester;
        this.professor = professor;
        this.examType = examType;
        this.examInfo = examInfo;
        this.examDifficulty = examDifficulty;
        this.content = content;
    }
}
