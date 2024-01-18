package usw.suwiki.domain.lecture.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class LectureDetail {
	@Column(name = "place_schedule")
	private String placeSchedule;

	@Column(name = "lecture_code")
	private String code;

	@Column(name = "point")
	private double point;

	@Column(name = "cappr_type")
	private String capprType;

	@Column(name = "dicl_no")
	private String diclNo;

	@Column(name = "grade")
	private int grade;

	@Column(name = "evaluate_type")
	private String evaluateType;

	@Builder
	public LectureDetail(String placeSchedule, String code, double point, String capprType, String diclNo,
						 int grade, String evaluateType) {
		this.placeSchedule = placeSchedule;
		this.code = code;
		this.point = point;
		this.capprType = capprType;
		this.diclNo = diclNo;
		this.grade = grade;
		this.evaluateType = evaluateType;
	}

	public void fixOmittedGrade(int grade) {
		if (this.grade == 0) {
			this.grade = grade;
		}
	}

	public void fixOmittedPlaceSchedudle(String placeSchedule) {
		if (Objects.equals(this.placeSchedule, "null")) {
			this.placeSchedule = placeSchedule;
		}
	}


}
