package usw.suwiki.domain.user.viewexam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.service.ViewExamCRUDService;
import usw.suwiki.domain.user.viewexam.ViewExam;
import usw.suwiki.domain.user.viewexam.ViewExamRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ViewExamCRUDServiceImpl implements ViewExamCRUDService {
    private final ViewExamRepository viewExamRepository;

    @Override
    public boolean isExist(Long userId, Long lectureId) {
        return viewExamRepository.validateIsExists(userId, lectureId);
    }

    @Override
    @Transactional
    public void save(ViewExam viewExam) {
        viewExamRepository.save(viewExam);
    }

    @Override
    public List<ViewExam> loadViewExamsFromUserIdx(Long userIdx) {
        return viewExamRepository.findByUserId(userIdx);
    }

    @Override
    @Transactional
    public void deleteAllFromUserIdx(Long userIdx) {
        for (ViewExam viewExam : viewExamRepository.findByUserId(userIdx)) {
            viewExamRepository.delete(viewExam);
        }
    }
}
