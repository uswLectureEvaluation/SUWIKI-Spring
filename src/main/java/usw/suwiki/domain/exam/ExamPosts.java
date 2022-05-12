package usw.suwiki.domain.exam;

import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.BaseTimeEntity;
import usw.suwiki.domain.user.User;
import usw.suwiki.dto.exam_info.ExamPostsSaveDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import usw.suwiki.dto.exam_info.ExamPostsUpdateDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class ExamPosts extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lectureName; //과목
    private String selectedSemester;
    private String professor;   //교수

    private String examType;
    private String examInfo;    //시험방식
    private String examDifficulty;    //난이도

    @LastModifiedDate // 조회한 Entity값을 변경할때 ,시간이 자동 저장된다.
    private LocalDateTime modifiedDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Builder
    public ExamPosts(ExamPostsSaveDto dto) {
        this.lectureName = dto.getLectureName();
        this.selectedSemester = dto.getSelectedSemester();
        this.professor = dto.getProfessor();
        this.examType = dto.getExamType();
        this.examInfo = dto.getExamInfo();
        this.examDifficulty = dto.getExamDifficulty();
        this.content = dto.getContent();
    }

    public void update(ExamPostsUpdateDto dto){
        this.selectedSemester = dto.getSelectedSemester();
        this.examType = dto.getExamType();
        this.examInfo = dto.getExamInfo();
        this.examDifficulty = dto.getExamDifficulty();
        this.content = dto.getContent();
    }
}
