package kz.kegoc.bln.repo;

import kz.kegoc.bln.entity.LastLoadInfo;
import org.springframework.data.repository.CrudRepository;

public interface LastLoadInfoRepo extends CrudRepository<LastLoadInfo, String> { }
