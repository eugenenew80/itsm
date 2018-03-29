package kz.kegoc.bln.web.arcType;

import kz.kegoc.bln.entity.ArcType;
import kz.kegoc.bln.repo.ArcTypeRepo;
import kz.kegoc.bln.web.dto.ArcTypeDto;
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
public class ArcTypeController {
    private final ArcTypeRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        findById = repo::findOne;
        save = repo::save;
        transformToEntity = t -> mapper.map(t, ArcType.class);
        transformToDto = t -> mapper.map(t, ArcTypeDto.class);
    }

    @RequestMapping("/arcTypes")
    public String list(Model model) {
        List<ArcTypeDto> list = repo.findAll()
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());

        model.addAttribute("arcTypes", list);
        return "arcType/list";
    }


    private UnaryOperator<ArcType> save;
    private Function<String, ArcType> findById;
    private Function<ArcTypeDto, ArcType> transformToEntity;
    private Function<ArcType, ArcTypeDto> transformToDto;}
