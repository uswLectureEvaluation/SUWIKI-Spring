package usw.suwiki.domain.userlecture.viewexam.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.userlecture.viewexam.ViewExam;
import usw.suwiki.domain.userlecture.viewexam.repository.ViewExamRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ViewExamCRUDService {

    private final ViewExamRepository viewExamRepository;

    public boolean isExist(
        Long userId,
        Long lectureId
    ) {
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
