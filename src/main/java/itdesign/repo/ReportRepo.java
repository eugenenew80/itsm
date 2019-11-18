package itdesign.repo;

import itdesign.entity.Report;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ReportRepo extends JpaRepository<Report, Long> {
    @EntityGraph(value = "Report.allJoins" , type= EntityGraph.EntityGraphType.FETCH)
    List<Report> findAllBySliceId(Long sliceId);
}
