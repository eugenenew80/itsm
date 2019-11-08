package itdesign.web;

import itdesign.entity.Group;
import itdesign.entity.Slice;
import itdesign.entity.Status;
import itdesign.repo.GroupRepo;
import itdesign.repo.SliceRepo;
import itdesign.repo.StatusRepo;
import itdesign.web.dto.GroupDto;
import itdesign.web.dto.LongDto;
import itdesign.web.dto.NewSliceDto;
import itdesign.web.dto.SliceDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import static itdesign.util.Util.first;

@RestController
@RequiredArgsConstructor
public class SliceRestController {
    private static final Logger logger = LoggerFactory.getLogger(SliceRestController.class);
    private final SliceRepo repo;
    private final StatusRepo statusRepo;
    private final GroupRepo groupRepo;
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
    @GetMapping(value = "/api/v1/slices/max", produces = MediaType.APPLICATION_JSON_VALUE)
    public LongDto getMax() {
        logger.info(getClass().getName() + ".getMax()");

        return new LongDto(9999l);
    }

    @GetMapping(value = "/api/v1/slices/{id}", produces = "application/json")
    public SliceDto getById(@PathVariable Long id) {
        logger.info(getClass().getName() + ".getById()");

        return first(findById)
            .andThen(transformToDto)
            .apply(id);
    }

    @GetMapping(value = "/api/v1/slices/test", produces = "application/json")
    public NewSliceDto getTest() {
        NewSliceDto sliceDto = new NewSliceDto();

        //sliceDto.setStartDate(LocalDate.now());
        //sliceDto.setEndDate(LocalDate.now());
        sliceDto.setMaxNumber(9999l);
        sliceDto.setRegion("19");
        sliceDto.setGroups(Collections.singleton(1l));

        return sliceDto;
    }

    @PostMapping(value = "/api/v1/slices", produces = "application/json")
    public List<SliceDto> create(@RequestBody NewSliceDto newSliceDto) {
        logger.info(getClass().getName() + ".create()");

        if (newSliceDto == null)
            return null;

        if (newSliceDto.getGroups() == null || newSliceDto.getGroups().size()== 0)
            return null;

        Status status = statusRepo.findOne(0l);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        List<Slice> slices = newSliceDto.getGroups().stream()
            .map(t -> {
                Slice slice = new Slice();
                slice.setStartDate(LocalDate.parse(newSliceDto.getStartDate(), formatter));
                slice.setEndDate(LocalDate.parse(newSliceDto.getEndDate(), formatter));
                slice.setCreatedDate(LocalDateTime.now());
                slice.setMaxRecNum(newSliceDto.getMaxNumber());
                slice.setRegion(newSliceDto.getRegion());
                slice.setStatus(status);
                Group group = groupRepo.findOne(t);
                slice.setGroup(group);
                return slice;
            })
            .collect(Collectors.toList());

        repo.save(slices);

        return slices.stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    private Function<Long, Slice> findById;
    private Function<Slice, SliceDto> transformToDto;
}
