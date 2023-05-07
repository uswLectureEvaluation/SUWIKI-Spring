package usw.suwiki.domain.admin.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.evaluation.entity.EvaluatePosts;

@Repository
public interface UserAdminEvaluateRepository extends JpaRepository<EvaluatePosts, Long> {
    void deleteById(Long id);
}
