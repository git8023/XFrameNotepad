package org.y.notepad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.y.notepad.repository.jpa.UploadInfoJpa;
import org.y.notepad.repository.mapper.UploadInfoMapper;

@Component
public class UploadInfoRepository {
    public final UploadInfoMapper MAPPER;
    public final UploadInfoJpa JPA;

    @Autowired
    public UploadInfoRepository(UploadInfoMapper MAPPER, UploadInfoJpa JPA) {
        this.MAPPER = MAPPER;
        this.JPA = JPA;
    }
}
