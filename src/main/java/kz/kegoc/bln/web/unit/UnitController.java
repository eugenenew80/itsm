package kz.kegoc.bln.web.unit;

import kz.kegoc.bln.entity.Unit;
import kz.kegoc.bln.repo.UnitRepo;
import kz.kegoc.bln.web.dto.UnitDto;
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
public class UnitController {
    private final UnitRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        save = repo::save;
        transformToEntity = t -> mapper.map(t, Unit.class);
        transformToDto = t -> mapper.map(t, UnitDto.class);
    }

    @RequestMapping("/unit")
    public String list(Model model) {
        List<UnitDto> list = repo.findAll()
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());

        model.addAttribute("units", list);
        return "unit/list";
    }


    private UnaryOperator<Unit> save;
    private Function<Long, Unit> findById;
    private Function<UnitDto, Unit> transformToEntity;
    private Function<Unit, UnitDto> transformToDto;}
