package usw.suwiki.domain.user.viewexam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.viewexam.ViewExam;
import usw.suwiki.domain.user.viewexam.ViewExamRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ViewExamCRUDService {
    private final ViewExamRepository viewExamRepository;

    public boolean isExist(Long userId, Long lectureId) {
        return viewExamRepository.validateIsExists(userId, lectureId);
    }

    @Transactional
    public void save(ViewExam viewExam) {
        viewExamRepository.save(viewExam);
    }

    public List<ViewExam> loadViewExamsFromUserIdx(Long userIdx) {
        return viewExamRepository.findByUserId(userIdx);
    }

    @Transactional
    public void deleteAllFromUserIdx(Long userIdx) {
        List<ViewExam> list = viewExamRepository.findByUserId(userIdx);
        for (ViewExam viewExam : list) {
            viewExamRepository.delete(viewExam);
        }
    }
}
