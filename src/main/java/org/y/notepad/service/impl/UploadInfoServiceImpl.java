package org.y.notepad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.y.notepad.model.entity.UploadInfo;
import org.y.notepad.repository.UploadInfoRepository;
import org.y.notepad.service.UploadInfoService;

@Service
public class UploadInfoServiceImpl implements UploadInfoService {

    private final UploadInfoRepository uploadInfoRepository;

    @Autowired
    public UploadInfoServiceImpl(UploadInfoRepository uploadInfoRepository) {
        this.uploadInfoRepository = uploadInfoRepository;
    }

    @Override
    public void upload(UploadInfo info) {
        uploadInfoRepository.JPA.save(info);
    }

    @Override
    public UploadInfo getById(int id) {
        return uploadInfoRepository.JPA.findById(id);
    }
}
