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
    @Query("update Resource r set r.status = :status where r.uuid in (:uuids) and r.userId = :userId")
    void updateStatuses(@Param("uuids") List<UUID> uuids,
                        @Param("status") ResourceStatus status,
                        @Param("userId") Long userId);

    @Modifying
    @Query("update Resource r set r.status = :status where r.uuid = :uuid and r.userId = :userId")
    void updateStatus(@Param("uuid") UUID uuid,
                      @Param("status") ResourceStatus status,
                      @Param("userId") Long userId);

    @Modifying
    void deleteAllByUuidIn(List<UUID> uuid);

    @Modifying
    @Query("delete from Resource r where r.path = :path " +
            "and r.name = :name " +
            "and r.type = 'DIRECTORY' " +
            "and r.userId = :userId")
    void deleteDirectory(@Param("path") String path,
                         @Param("name") String name,
                         @Param("userId") Long userId);

    @Modifying
    @Query("delete from Resource r where r.path like concat(:prefix, '%') " +
            "and r.type = 'DIRECTORY' " +
            "and r.userId = :userId")
    void deleteNestedDirectories(@Param("prefix") String prefix,
                                 @Param("userId") Long userId);

    @Modifying
    @Query("update Resource r set r.status = 'DELETED' " +
            "where r.path like concat(:prefix, '%') " +
            "and r.type = 'FILE' " +
            "and r.userId = :userId")
    void markAllFilesAsDeleted(@Param("prefix") String prefix,
                               @Param("userId") Long userId);

    @Query("select r from Resource r " +
            "where lower(r.name) like lower(concat('%', :query, '%')) " +
            "and r.status = 'READY' " +
            "and r.userId = :userId")
    List<Resource> findAllByQueryAndUserId(@Param("query") String query,
                                           @Param("userId") Long userId);
}
