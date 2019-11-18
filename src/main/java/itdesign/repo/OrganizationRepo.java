package itdesign.repo;

import itdesign.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface OrganizationRepo extends JpaRepository<Organization, Long> {
    List<Organization> findAllByGroupReportAndLang(String groupReport, String lang);
    Organization findByCodeAndLang(String code, String lang);
    List<Organization> findAllByLang(String lang);
}
