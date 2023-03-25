package usw.suwiki.domain.lecture.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import usw.suwiki.domain.evaluation.EvaluatePostsToLecture;
import usw.suwiki.domain.lecture.dto.JsonToLectureDto;
import usw.suwiki.global.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
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

    @Embedded
    LectureAverage lectureAverage;

    @Embedded
    LectureDetail lectureDetail;

    private int postsCount = 0;

    @LastModifiedDate // 조회한 Entity값을 변경할때 ,시간이 자동 저장된다.
    private LocalDateTime modifiedDate;

    public void setSemester(String semester) {
        this.semester = semester;
    }


    public void toEntity(JsonToLectureDto dto) {
        this.name = dto.getLectureName();
        this.semester = dto.getSelectedSemester();
        this.professor = dto.getProfessor();
        this.majorType = dto.getMajorType();
        createLectureDetail(dto);
        createLectureAverage();
    }

    public void addLectureValue(EvaluatePostsToLecture dto) {
        this.lectureAverage.addLectureValue(dto);
        this.postsCount += 1;
    }

    public void cancelLectureValue(EvaluatePostsToLecture dto) {
        this.lectureAverage.cancelLectureValue(dto);
        this.postsCount -= 1;
    }

    public void getLectureAvg() {
        if (this.postsCount < 1) {
            this.lectureAverage.calculateIfPostCountLessThanOne();
            return;
        }
        this.lectureAverage.calculateLectureAverage(this.postsCount);
    }

    private void createLectureAverage() {
        this.lectureAverage = new LectureAverage();
    }

    private void createLectureDetail(JsonToLectureDto dto) {
        this.lectureDetail = LectureDetail.builder()
            .code(dto.getLectureCode())
            .grade(dto.getGrade())
            .point(dto.getPoint())
            .type(dto.getLectureType())
            .diclNo(dto.getDiclNo())
            .evaluateType(dto.getEvaluateType())
            .placeSchedule(dto.getPlaceSchedule())
            .capprType(dto.getCapprType())
            .build();
    }
}