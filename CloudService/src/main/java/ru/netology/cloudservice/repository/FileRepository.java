package ru.netology.cloudservice.repository;

import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findByUser(UserEntity user);

    Optional<FileEntity> findByFilenameAndUser(
            String filename,
            UserEntity user);
}