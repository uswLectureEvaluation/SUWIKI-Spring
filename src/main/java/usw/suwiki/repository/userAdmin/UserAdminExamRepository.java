package usw.suwiki.repository.userAdmin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.exam.ExamPosts;

@Repository
public interface UserAdminExamRepository extends JpaRepository<ExamPosts, Long> {

    void deleteById(Long Id);
}
