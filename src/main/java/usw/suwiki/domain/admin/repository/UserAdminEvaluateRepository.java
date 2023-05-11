package usw.suwiki.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.evaluation.domain.EvaluatePosts;

@Repository
public interface UserAdminEvaluateRepository extends JpaRepository<EvaluatePosts, Long> {
    void deleteById(Long id);
}
