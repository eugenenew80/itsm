package itdesign.repo;

import itdesign.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationRepo extends JpaRepository<Organization, Long> {
    List<Organization> findAllByGroupReportAndLang(String groupReport, String lang);
    Organization findByCodeAndLang(String code, String lang);
    List<Organization> findAllByLang(String lang);
}
