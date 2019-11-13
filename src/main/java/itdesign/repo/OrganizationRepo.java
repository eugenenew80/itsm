package itdesign.repo;

import itdesign.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationRepo extends JpaRepository<Organization, Long> {
    List<Organization> findAllByGroupOrg(String groupOrg);
    List<Organization> findAllByGroupReport(String groupReport);
}
