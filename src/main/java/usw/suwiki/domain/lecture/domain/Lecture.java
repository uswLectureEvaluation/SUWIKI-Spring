package usw.suwiki.domain.lecture.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.evaluatepost.domain.EvaluatePost;
import usw.suwiki.domain.evaluatepost.service.dto.EvaluatePostsToLecture;
import usw.suwiki.global.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO refactor: Embeddable 객체로 분리
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

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureSchedule> scheduleList = new ArrayList<>();

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
        this.lectureEvaluationInfo = new LectureEvaluationInfo();
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

    public void addSchedule(LectureSchedule lectureSchedule) {
        this.scheduleList.add(lectureSchedule);
    }

    public void removeSchedule(LectureSchedule lectureSchedule) {
        this.scheduleList.remove(lectureSchedule);
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

    public void addSemester(String singleSemester) {
        validateSingleSemester(singleSemester);
        if (this.semester.contains(singleSemester)) {
            return;
        }

        this.semester = buildAddedSemester(this.semester, singleSemester);
    }

    private void validateSingleSemester(String candidate) {
        boolean matches = Pattern.matches("^(2\\d{3})-(1|2)$", candidate);
        if (!matches) {
            throw new IllegalArgumentException("invalid semester");
        }
    }

    private static String buildAddedSemester(String originalSemesters, String semester) {
        return originalSemesters + ", " + semester;
    }

}