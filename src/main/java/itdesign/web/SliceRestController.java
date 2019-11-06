package itdesign.web;

import itdesign.entity.Slice;
import itdesign.repo.SliceRepo;
import itdesign.web.dto.SliceDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import static itdesign.util.Util.first;

@RestController
@RequiredArgsConstructor
public class SliceRestController {
    private static final Logger logger = LoggerFactory.getLogger(SliceRestController.class);
    private final SliceRepo repo;
    private final DozerBeanMapper mapper;


    @PostConstruct
    private void init() {
        logger.debug(getClass() .getName()+ ".init()");

        findById = repo::findOne;
        transformToDto = t -> mapper.map(t, SliceDto.class);
    }

    @GetMapping(value = "/api/v1/slices", produces = "application/json")
    public List<SliceDto> getAll() {
        logger.info(getClass().getName() + ".getAll()");

        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return repo.findAll(sort)
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/api/v1/slices/{id}", produces = "application/json")
    public SliceDto getById(@PathVariable Long id) {
        logger.info(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    private Function<Long, Slice> findById;
    private Function<Slice, SliceDto> transformToDto;
}
