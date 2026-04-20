package dev.yoxaron.cloudstorage.repository;

import dev.yoxaron.cloudstorage.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findResourceByPathAndNameAndUserId(String path, String name, Long userId);
}
