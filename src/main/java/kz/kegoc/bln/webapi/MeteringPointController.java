package kz.kegoc.bln.webapi;

import kz.kegoc.bln.entity.MeteringPoint;
import kz.kegoc.bln.repo.MeteringPointRepo;
import kz.kegoc.bln.webapi.dto.MeteringPointDto;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class MeteringPointController {

    @RequestMapping(method = RequestMethod.GET, value = "/meteringPoints")
    public List<MeteringPointDto> getAll() {
        List<MeteringPointDto> list = StreamSupport.stream(repo.findAll().spliterator(), false)
            .map(t -> mapper.map(t, MeteringPointDto.class))
            .collect(Collectors.toList());

        return list;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/meteringPoints/{id}")
    public MeteringPointDto getOne(@PathVariable Long id) {
        return mapper.map(repo.findOne(id), MeteringPointDto.class);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/meteringPoints")
    public MeteringPointDto create(@RequestBody MeteringPoint meteringPoint) {
        return mapper.map(repo.save(meteringPoint), MeteringPointDto.class);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/meteringPoints/{id}")
    public MeteringPointDto create(@PathVariable Long id, @RequestBody MeteringPoint meteringPoint) {
        return mapper.map(repo.save(meteringPoint), MeteringPointDto.class);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/meteringPoints/{id}")
    public void create(@PathVariable Long id) {
        repo.delete(id);
        return;
    }

    @Autowired
    private MeteringPointRepo repo;

    @Autowired
    private DozerBeanMapper mapper;
}
