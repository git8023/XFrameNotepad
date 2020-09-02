package org.y.notepad.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y.notepad.model.entity.UploadInfo;

@Repository
public interface UploadInfoJpa extends JpaRepository<UploadInfo, Integer> {

    UploadInfo findById(int id);

}
