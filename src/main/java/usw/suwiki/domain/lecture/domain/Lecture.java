package usw.suwiki.domain.lecture.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import usw.suwiki.domain.evaluatepost.domain.EvaluatePost;
import usw.suwiki.domain.evaluatepost.service.dto.EvaluatePostsToLecture;
import usw.suwiki.domain.lecture.controller.dto.JSONLectureVO;
import usw.suwiki.global.BaseTimeEntity;

@Getter
@Setter // TODO: 제거
@Entity
@NoArgsConstructor  // TODO: access PROTECTED
public class Lecture extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 컬럼 제약 조건 설정
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

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluatePost> evaluatePostList = new ArrayList<>();

    @Builder
    public Lecture(
            String semester,
            String professor,
            String name,
            String majorType,
            String type,
            LectureDetail lectureDetail
    ) {
        this.semester = semester;
        this.professor = professor;
        this.name = name;
        this.majorType = majorType;
        this.type = type;
        this.lectureDetail = lectureDetail;
        // TODO: 생성자 없이 객체 초기화시 EvaluationInfo 필드값들이 어떻게 되는지 확인
        this.lectureEvaluationInfo = new LectureEvaluationInfo();
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    // TODO: 생성자 -> DTO에서 사용
    public static Lecture toEntity(JSONLectureVO vo) {
        Lecture entity = Lecture.builder()
                .name(vo.getLectureName())
                .type(vo.getLectureType())
                .professor(vo.getProfessor())
                .semester(vo.getSelectedSemester())
                .majorType(vo.getMajorType())
                .build();
        entity.createLectureEvaluationInfo();
        entity.createLectureDetail(vo);

        return entity;
    }

    private void createLectureEvaluationInfo() {
        this.lectureEvaluationInfo = new LectureEvaluationInfo();
    }

    // TODO: LectureDetail 생성자 -> DTO 에서 사용
    private void createLectureDetail(JSONLectureVO vo) {
        this.lectureDetail = LectureDetail.builder()
                .code(vo.getLectureCode())
                .grade(vo.getGrade())
                .point(vo.getPoint())
                .diclNo(vo.getDiclNo())
                .evaluateType(vo.getEvaluateType())
                .placeSchedule(vo.getPlaceSchedule())
                .capprType(vo.getCapprType())
                .build();
    }

    /**
     * 연관관계 메서드
     */
    public void addEvaluatePost(EvaluatePost evaluatePost) {
        this.evaluatePostList.add(evaluatePost);
    }

    public void removeEvaluatePost(EvaluatePost evaluatePost) {
        this.evaluatePostList.remove(evaluatePost);
    }

    /**
     * 비즈니스 메서드
     */

    public void handleLectureEvaluationIfNewPost(EvaluatePostsToLecture post) {
        this.addLectureEvaluation(post);
        this.calculateAverage();
    }

    public void handleLectureEvaluationIfUpdatePost(EvaluatePostsToLecture beforeUpdatePost,
                                                    EvaluatePostsToLecture updatePost) {
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


    // legacy: 기존에 잘못 입력된 값들을 정상화
    public void fixOmission(JSONLectureVO vo) {
        if (this.type == null || this.type.isEmpty()) {
            this.type = vo.getLectureType();
        }

        this.lectureDetail.fixOmittedGrade(vo.getGrade());
        this.lectureDetail.fixOmittedPlaceSchedudle(vo.getPlaceSchedule());
    }
}