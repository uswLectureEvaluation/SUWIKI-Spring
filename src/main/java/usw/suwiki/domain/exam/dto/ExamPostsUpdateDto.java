package usw.suwiki.domain.exam.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

//ExamInfo Column은 EvaluatePosts 에 있다.
@Getter
@NoArgsConstructor
public class ExamPostsUpdateDto {

    private String selectedSemester;
    private String examType;    //시험방식
    private String examInfo;    //시험방식
    private String examDifficulty;    //난이도

    private String content;

    public ExamPostsUpdateDto(String selectedSemester, String examType, String examInfo, String examDifficulty, String content) {
        this.selectedSemester = selectedSemester;
        this.examType = examType;
        this.examInfo = examInfo;
        this.examDifficulty = examDifficulty;
        this.content = content;
    }
}
