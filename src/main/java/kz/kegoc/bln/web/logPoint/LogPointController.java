package kz.kegoc.bln.web.logPoint;

import kz.kegoc.bln.entity.LogPoint;
import kz.kegoc.bln.repo.LogPointRepo;
import kz.kegoc.bln.web.dto.LogPointDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class LogPointController {
    private final LogPointRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        save = repo::save;
        transformToEntity = t -> mapper.map(t, LogPoint.class);
        transformToDto = t -> mapper.map(t, LogPointDto.class);
    }

    @RequestMapping("/logPoints")
    public String list(Model model) {
        List<LogPointDto> list = repo.findAll()
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());

        model.addAttribute("logPoints", list);
        return "logPoint/list";
    }


    private UnaryOperator<LogPoint> save;
    private Function<Long, LogPoint> findById;
    private Function<LogPointDto, LogPoint> transformToEntity;
    private Function<LogPoint, LogPointDto> transformToDto;}
