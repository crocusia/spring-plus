package org.example.expert.domain.common.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.entity.Log;
import org.example.expert.domain.common.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logRequest(String action, String message){
        logRepository.save(new Log(action, message));
    }
}
