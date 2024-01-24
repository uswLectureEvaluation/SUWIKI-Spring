package usw.suwiki.domain.lecture.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import usw.suwiki.domain.timetable.entity.TimetableCellSchedule;
import usw.suwiki.domain.timetable.entity.TimetableDay;

// TODO refactor: TimetableCellSchedule 의존성 제거. 해당 클래스는 스트링 변환 작업만 책임지도록
public final class LectureStringConverter {

    /*
        변환에 필요한 최소한의 스트링 형식입니다.    https://regexr.com/7q2nv
        pass : "강의실107-1(수6,7,8)" "강의실 B215(화5,6,7 수5,6,7)"
        fail : "(월1,2)" "강의실(1,2)" "강의실 월1,2" "강의실107(요일아님6,7,8)"
     */
    private static final String PLACE_SCHEDULE_REGEX = "^([\\s가-힣A-Za-z\\d-]+\\([월화수목금토일]\\d+(?:,\\d+)*.*?\\))+$";

    /**
     * @param scheduleChunk 강의 장소 및 시간 원본 lecture.place_schedule
     * @implNote place_schedule을 TimetableCellSchedule 객체 리스트로 변환
     */
    public static List<TimetableCellSchedule> convertScheduleChunkIntoTimetableCellScheduleList(String scheduleChunk) {
        List<TimetableCellSchedule> scheduleList = new ArrayList<>();

        // TODO refactor: "null" -> null. 데이터 파싱 로직 변경 필요
        if (Objects.equals(scheduleChunk, "null")
                || Objects.isNull(scheduleChunk)
                || !Pattern.matches(PLACE_SCHEDULE_REGEX, scheduleChunk)
        ) {
            return scheduleList;
        }

        List<String> locationAndDaysChunkList = splitScheduleChunkIntoLocationAndDaysChunkList(scheduleChunk);

        for (String locationAndDaysChunk : locationAndDaysChunkList) {
            String location = extractLocationFromLocationAndDaysChunk(locationAndDaysChunk);

            String DaysChunk = extractDaysChunkFromLocationAndDaysElementChunk(locationAndDaysChunk);
            for (String dayAndPeriodsChunk : splitDaysChunkIntoDayAndPeriodsChunkList(DaysChunk)) {

                String day = dayAndPeriodsChunk.substring(0, 1);
                String periodsChunk = dayAndPeriodsChunk.substring(1);

                for (String connectedPeriods : splitUnconnectedPeriodsIntoConnectedPeriodsList(periodsChunk)) {
                    List<Integer> periodList = convertStringListToIntList(connectedPeriods);
                    Integer startPeriod = periodList.get(0);
                    Integer endPeriod = periodList.get(periodList.size() - 1);

                    TimetableCellSchedule schedule = TimetableCellSchedule.builder()
                            .location(location)
                            .day(TimetableDay.ofKorean(day))
                            .startPeriod(startPeriod)
                            .endPeriod(endPeriod)
                            .build();
                    scheduleList.add(schedule);
                }
            }
        }
        return scheduleList;
    }


    private static List<String> splitScheduleChunkIntoLocationAndDaysChunkList(String chunk) {
        // e.g. "IT103(..),IT505(..)" -> [ "IT103(..)", "IT505(..)" ]
        return Arrays.asList(chunk.split(",(?![^()]*\\))"));
    }

    private static String extractLocationFromLocationAndDaysChunk(String chunk) {
        // e.g. "IT103(월1,2, 화1,2)" -> "IT103"
        return chunk.split("\\(")[0];
    }

    private static String extractDaysChunkFromLocationAndDaysElementChunk(String chunk) {
        // e.g. IT103(월1,2, 화1,2) -> "월1,2, 화1,2"
        int start = chunk.indexOf('(') + 1, end = chunk.lastIndexOf(')');
        return chunk.substring(start, end);
    }

    private static List<String> splitDaysChunkIntoDayAndPeriodsChunkList(String chunk) {
        // e.g. "월1,2, 화1,2" -> [ "월1,2", "화1,2" ]
        return Arrays.asList(chunk.split(" "));
    }

    private static List<String> splitUnconnectedPeriodsIntoConnectedPeriodsList(String unconnectedPeriods) {
        // e.g. "1,2,3,5,6,7,9,10" -> [ "1,2,3", "5,6,7", "9,10" ]
        String[] numbers = unconnectedPeriods.split(",");
        Arrays.sort(numbers);

        List<String> connectedPeriodsList = new ArrayList<>();
        List<String> currentGroup = new ArrayList<>();

        for (String number : numbers) {
            int num = Integer.parseInt(number);

            if (currentGroup.isEmpty() || num == Integer.parseInt(currentGroup.get(currentGroup.size() - 1)) + 1) {
                currentGroup.add(number);
            } else {
                connectedPeriodsList.add(String.join(",", currentGroup));
                currentGroup.clear();
                currentGroup.add(number);
            }
        }

        if (!currentGroup.isEmpty()) {
            connectedPeriodsList.add(String.join(",", currentGroup));
        }

        return connectedPeriodsList;
    }

    private static List<Integer> convertStringListToIntList(String stringList) {
        String[] elements = stringList.split(",");

        return Arrays.stream(elements)
                .map(Integer::parseInt)
                .toList();
    }

}
