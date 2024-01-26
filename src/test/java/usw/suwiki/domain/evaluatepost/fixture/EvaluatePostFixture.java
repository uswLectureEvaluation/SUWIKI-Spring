package usw.suwiki.domain.evaluatepost.fixture;

import usw.suwiki.domain.evaluatepost.domain.EvaluatePost;
import usw.suwiki.domain.lecture.domain.Lecture;
import usw.suwiki.domain.user.user.User;

public class EvaluatePostFixture {
    private static final String LECTURE_NAME = "데이터 구조"; //과목
    private static final String SELECTED_SEMESTER = "2023-2";
    private static final String PROFESSOR = "신호진";   //교수
    public static final String CONTENT = "어렵지만 너무 좋았어요!! 역시 신호진 교수님. 다음 알고리즘 수업도 너무 기대됩니다~~~~~~!";

    private static final float SATISFACTION = 2;    //수업 만족도
    private static final float LEARNING = 5; //배움지수
    private static final float HONEY = 3;    //꿀강지수
    private static final float TOTAL_AVG = 3;   // 평균지수

    private static final int DIFFICULTY = 1;   //성적비율
    private static final int HOMEWORK = 3;   //과제량
    private static final int TEAM = 2;    //조모임 횟수

    public static EvaluatePost createFirstDummyEvaluatePost(User user, Lecture lecture) {
        return createDummyEvaluatePost(
                user,
                lecture,
                LECTURE_NAME,
                SELECTED_SEMESTER,
                PROFESSOR,
                CONTENT,
                SATISFACTION,
                LEARNING,
                HONEY,
                TOTAL_AVG,
                DIFFICULTY,
                HOMEWORK,
                TEAM
        );
    }

    public static EvaluatePost createDummyEvaluatePost(
            User user,
            Lecture lecture,
            String lectureName,
            String selectedSemester,
            String professor,
            String content,
            float satisfaction,
            float learning,
            float honey,
            float totalAvg,
            int difficulty,
            int homework,
            int team
    ) {
        EvaluatePost evaluatePost = EvaluatePost.builder()
                .lectureName(lectureName)
                .selectedSemester(selectedSemester)
                .professor(professor)
                .content(content)
                .satisfaction(satisfaction)
                .learning(learning)
                .honey(honey)
                .totalAvg(totalAvg)
                .difficulty(difficulty)
                .homework(homework)
                .team(team)
                .build();
        evaluatePost.associateUser(user);
        evaluatePost.associateLecture(lecture);
        return evaluatePost;
    }
}
