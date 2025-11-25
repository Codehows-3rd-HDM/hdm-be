package com.hdmbe.service;


import com.hdmbe.dto.ProcessRequestDto;
import com.hdmbe.dto.ProcessResponseDto;
import com.hdmbe.entity.ProcessEntity;
import com.hdmbe.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository repository;

    public ProcessResponseDto create(ProcessRequestDto requestDto) {
        ProcessEntity process = new ProcessEntity();
        process.setProcessName(requestDto.getProcessName());

        repository.save(process);

        ProcessResponseDto responseDto = new ProcessResponseDto();
        responseDto.setId(process.getId());
        responseDto.setProcessName(process.getProcessName());
        return responseDto;
    }
}


