package kz.kegoc.bln.web.telemetry;

import kz.kegoc.bln.entity.Telemetry;
import kz.kegoc.bln.repo.TelemetryRepo;
import kz.kegoc.bln.web.dto.TelemetryDto;
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
public class TelemetryController {
    private final TelemetryRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        save = repo::save;
        transformToEntity = t -> mapper.map(t, Telemetry.class);
        transformToDto = t -> mapper.map(t, TelemetryDto.class);
    }

    @RequestMapping("/telemetry")
    public String list(Model model) {
        List<TelemetryDto> list = repo.findAll()
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());

        model.addAttribute("telemetry", list);
        return "telemetry/list";
    }


    private UnaryOperator<Telemetry> save;
    private Function<Long, Telemetry> findById;
    private Function<TelemetryDto, Telemetry> transformToEntity;
    private Function<Telemetry, TelemetryDto> transformToDto;}
