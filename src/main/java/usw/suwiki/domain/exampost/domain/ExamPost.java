package usw.suwiki.domain.exampost.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.exampost.controller.dto.ExamPostUpdateDto;
import usw.suwiki.domain.exampost.controller.dto.ExamPostsSaveDto;
import usw.suwiki.domain.user.user.User;
import usw.suwiki.global.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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
