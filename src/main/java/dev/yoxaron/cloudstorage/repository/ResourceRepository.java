package dev.yoxaron.cloudstorage.repository;

import dev.yoxaron.cloudstorage.entity.Resource;
import dev.yoxaron.cloudstorage.entity.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findResourceByPathAndNameAndUserId(String path, String name, Long userId);

    @Modifying
    @Query("update Resource r set r.status = :status where r.uuid in (:uuids)")
    int updateStatuses(@Param("uuids") List<UUID> uuids, @Param("status") ResourceStatus status);

    @Modifying
    @Query(value = "update Resource r set r.status = :status where r.uuid = :uuid")
    int updateStatus(@Param("uuid") UUID uuid, @Param("status") ResourceStatus status);

    @Modifying
    void deleteAllByUuidIn(List<UUID> uuid);
}
