package usw.suwiki.domain.lecture.dto;

import usw.suwiki.domain.lecture.Lecture;

import java.util.List;

public record Lectures(
  List<Lecture> content,
  Long count
) {
}
