package usw.suwiki.domain.exampost;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import usw.suwiki.domain.exampost.dto.ExamPostUpdateDto;
import usw.suwiki.domain.exampost.dto.ExamPostsSaveDto;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;
import usw.suwiki.infra.jpa.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamPost extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lectureName; //과목
    private String selectedSemester;
    private String professor;   //교수

    private String examType;
    private String examInfo;    //시험방식
    private String examDifficulty;    //난이도

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @Builder
    public ExamPost(ExamPostsSaveDto dto) {
        this.lectureName = dto.getLectureName();
        this.selectedSemester = dto.getSelectedSemester();
        this.professor = dto.getProfessor();
        this.examType = dto.getExamType();
        this.examInfo = dto.getExamInfo();
        this.examDifficulty = dto.getExamDifficulty();
        this.content = dto.getContent();
    }

    public void update(ExamPostUpdateDto dto) {
        this.selectedSemester = dto.getSelectedSemester();
        this.examType = dto.getExamType();
        this.examInfo = dto.getExamInfo();
        this.examDifficulty = dto.getExamDifficulty();
        this.content = dto.getContent();
    }
}
