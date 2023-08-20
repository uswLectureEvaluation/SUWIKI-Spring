package usw.suwiki.domain.lecture.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import usw.suwiki.domain.evaluatepost.service.dto.EvaluatePostsToLecture;
import usw.suwiki.domain.lecture.controller.dto.JsonToLectureForm;
import usw.suwiki.global.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
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
    private LectureEvaluationInfo lectureEvaluationInfo;

    @Embedded
    private LectureDetail lectureDetail;

    private int postsCount = 0;

    @LastModifiedDate // 조회한 Entity값을 변경할때 ,시간이 자동 저장된다.
    private LocalDateTime modifiedDate;

    public void setSemester(String semester) {
        this.semester = semester;
    }


    public static Lecture toEntity(JsonToLectureForm dto) {
        Lecture entity = Lecture.builder()
                .name(dto.getLectureName())
                .type(dto.getLectureType())
                .professor(dto.getProfessor())
                .semester(dto.getSelectedSemester())
                .majorType(dto.getMajorType())
                .build();
        entity.createLectureEvaluationInfo();
        entity.createLectureDetail(dto);

        return entity;
    }

    @Builder
    public Lecture(String semester, String professor, String name, String majorType, String type) {
        this.name = name;
        this.semester = semester;
        this.professor = professor;
        this.majorType = majorType;
        this.type = type;
    }

    private void createLectureEvaluationInfo() {
        this.lectureEvaluationInfo = new LectureEvaluationInfo();
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
        this.lectureEvaluationInfo.addLectureValue(dto);
        increasePostCount();
    }

    private void cancelLectureEvaluation(EvaluatePostsToLecture dto) {
        this.lectureEvaluationInfo.cancelLectureValue(dto);
        decreasePostCount();
    }

    private void calculateAverage() {
        if (postsCount < 1) {
            this.lectureEvaluationInfo.calculateIfPostCountLessThanOne();
            return;
        }
        this.lectureEvaluationInfo.calculateLectureAverage(this.postsCount);
    }

    private void increasePostCount() {
        this.postsCount += 1;
    }

    private void decreasePostCount() {
        this.postsCount -= 1;
    }
}