package itdesign.service;

import itdesign.entity.Status;

public interface CachedStatusService {
    Status getStatus(Long statusId);
}
