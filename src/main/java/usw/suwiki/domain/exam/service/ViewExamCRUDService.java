package usw.suwiki.domain.exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import usw.suwiki.domain.exam.domain.viewexam.repository.ViewExamRepository;
import usw.suwiki.domain.exam.controller.dto.viewexam.PurchaseHistoryDto;
import usw.suwiki.domain.exam.domain.viewexam.ViewExam;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewExamCRUDService {

    private final ViewExamRepository viewExamRepository;

    public boolean isExist(Long userId, Long lectureId) {
        return viewExamRepository.validateIsExists(userId, lectureId);
    }

    public void save(ViewExam viewExam) {
        viewExamRepository.save(viewExam);
    }

    public List<ViewExam> loadViewExamsFromUserIdx(Long userIdx) {
        return viewExamRepository.findByUserId(userIdx);
    }

    public void deleteAllFromUserIdx(Long userIdx) {
        List<ViewExam> list = viewExamRepository.findByUserId(userIdx);
        for (ViewExam viewExam : list) {
            viewExamRepository.delete(viewExam);
        }
    }
}
