package itdesign.repo;

import itdesign.entity.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SliceRepo extends JpaRepository<Slice, Long> {

    @EntityGraph(value = "Slice.allJoins" , type= EntityGraph.EntityGraphType.FETCH)
    @Query("select t from Slice t order by t.id")
    List<Slice> findAll();
}
