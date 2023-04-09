package usw.suwiki.domain.lecture.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import usw.suwiki.domain.evaluation.EvaluatePostsToLecture;
import usw.suwiki.domain.lecture.controller.dto.JsonToLectureForm;
import usw.suwiki.global.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Lecture extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "semester_list")
    private String semester;

    private String professor;

    @Column(name = "lecture_name")
    private String name;

    @Column(name = "major_type")
    private String majorType;

    @Column(name = "lecture_type")
    private String type;

    @Embedded
    private LectureEvaluationInfo lectureAverage;

    @Embedded()
    private LectureDetail lectureDetail;

    private int postsCount = 0;

    @LastModifiedDate // 조회한 Entity값을 변경할때 ,시간이 자동 저장된다.
    private LocalDateTime modifiedDate;

    public void setSemester(String semester) {
        this.semester = semester;
    }


    public void toEntity(JsonToLectureForm dto) {
        this.name = dto.getLectureName();
        this.semester = dto.getSelectedSemester();
        this.professor = dto.getProfessor();
        this.majorType = dto.getMajorType();
        this.type = dto.getLectureType();
        createLectureDetail(dto);
        createLectureAverage();
    }

    private void createLectureAverage() {
        this.lectureAverage = new LectureEvaluationInfo();
    }

    private void createLectureDetail(JsonToLectureForm dto) {
        this.lectureDetail = LectureDetail.builder()
            .code(dto.getLectureCode())
            .grade(dto.getGrade())
            .point(dto.getPoint())
            .diclNo(dto.getDiclNo())
            .evaluateType(dto.getEvaluateType())
            .placeSchedule(dto.getPlaceSchedule())
            .capprType(dto.getCapprType())
            .build();
    }

    public void handleLectureEvaluationIfNewPost(EvaluatePostsToLecture post) {
        this.addLectureEvaluation(post);
        this.calculateAverage();
    }

    public void handleLectureEvaluationIfUpdatePost(EvaluatePostsToLecture beforeUpdatePost, EvaluatePostsToLecture updatePost) {
        this.cancelLectureEvaluation(beforeUpdatePost);
        this.addLectureEvaluation(updatePost);
        this.calculateAverage();
    }

    public void handleLectureEvaluationIfDeletePost(EvaluatePostsToLecture post) {
        this.cancelLectureEvaluation(post);
        this.calculateAverage();
    }

    private void addLectureEvaluation(EvaluatePostsToLecture dto) {
        this.lectureAverage.addLectureValue(dto);
        increasePostCount();
    }

    private void cancelLectureEvaluation(EvaluatePostsToLecture dto) {
        this.lectureAverage.cancelLectureValue(dto);
        decreasePostCount();
    }

    private void calculateAverage() {
        if (postsCount < 1) {
            this.lectureAverage.calculateIfPostCountLessThanOne();
            return;
        }
        this.lectureAverage.calculateLectureAverage(this.postsCount);
    }

    private void increasePostCount() {
        this.postsCount += 1;
    }

    private void decreasePostCount() {
        this.postsCount -= 1;
    }
}