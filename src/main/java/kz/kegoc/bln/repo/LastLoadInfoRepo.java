package kz.kegoc.bln.repo;

import kz.kegoc.bln.entity.LastLoadInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LastLoadInfoRepo extends JpaRepository<LastLoadInfo, String> { }
