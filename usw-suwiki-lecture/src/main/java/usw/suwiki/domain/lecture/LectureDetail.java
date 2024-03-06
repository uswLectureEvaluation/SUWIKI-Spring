package usw.suwiki.domain.lecture;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class LectureDetail {	// TODO refactor: placeSchedule, grade, point 은 Lecture로 이동. 혹은 전체 이동.

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
	public LectureDetail(String code, double point, String capprType, String diclNo,
						 int grade, String evaluateType) {
		this.code = code;
		this.point = point;
		this.capprType = capprType;
		this.diclNo = diclNo;
		this.grade = grade;
		this.evaluateType = evaluateType;
	}

}
