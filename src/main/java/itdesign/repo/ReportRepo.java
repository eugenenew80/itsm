package itdesign.repo;

import itdesign.entity.Report;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepo extends JpaRepository<Report, Long> {
    @EntityGraph(value = "Report.allJoins" , type= EntityGraph.EntityGraphType.FETCH)
    List<Report> findAllBySliceId(Long sliceId);
}
