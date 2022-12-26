package usw.suwiki.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.EvaluatePostNoProblemForm;
import usw.suwiki.domain.admin.dto.UserAdminRequestDto.ExamPostNoProblemForm;
import usw.suwiki.domain.postreport.repository.EvaluateReportRepository;
import usw.suwiki.domain.postreport.repository.ExamReportRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAdminNoProblemPostService {

    private final EvaluateReportRepository evaluateReportRepository;
    private final ExamReportRepository examReportRepository;

    public Map<String, Boolean> executeEvaluatePost(EvaluatePostNoProblemForm evaluatePostNoProblemForm) {
        evaluateReportRepository.deleteByEvaluateIdx(evaluatePostNoProblemForm.getEvaluateIdx());
        return new HashMap<>() {{
            put("Success", true);
        }};
    }

    public Map<String, Boolean> executeExamPost(ExamPostNoProblemForm examPostRestrictForm) {
        examReportRepository.deleteByExamIdx(examPostRestrictForm.getExamIdx());
        return new HashMap<>() {{
            put("Success", true);
        }};
    }
}
