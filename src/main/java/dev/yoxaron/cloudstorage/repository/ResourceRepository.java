package dev.yoxaron.cloudstorage.repository;

import dev.yoxaron.cloudstorage.entity.Resource;
import dev.yoxaron.cloudstorage.entity.ResourceStatus;
import dev.yoxaron.cloudstorage.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    boolean existsByPathAndNameAndTypeAndUserId(String path, String name, ResourceType type, Long userId);

    boolean existsByPathAndNameAndTypeAndStatusAndUserId(String path, String name,
                                                         ResourceType type, ResourceStatus status, Long userId);

    Optional<Resource> findResourceByPathAndNameAndTypeAndUserId(
            String path, String name, ResourceType type, Long userId);

    Optional<Resource> findResourceByPathAndNameAndTypeAndStatusAndUserId(
            String path, String name, ResourceType type, ResourceStatus status, Long userId);

    @Query("select r from Resource r where r.path = :path " +
            "and r.userId = :userId " +
            "and (r.status = :status OR r.status IS NULL) " +
            "and not (r.path = '/' and r.name = '/')")
    List<Resource> getDirectoryContents(@Param("path") String path,
                                        @Param("status") ResourceStatus status,
                                        @Param("userId") Long userId);

    List<Resource> findAllByPathStartingWithAndTypeAndUserId(String path, ResourceType type, Long userId);

    @Query("select r from Resource r " +
            "where lower(r.name) like lower(concat('%', :query, '%')) " +
            "and (r.status = :status OR r.status IS NULL) " +
            "and r.userId = :userId")
    List<Resource> findAllByQueryAndUserId(@Param("query") String query,
                                           @Param("status") ResourceStatus status,
                                           @Param("userId") Long userId);

    @Query("select r from Resource r where r.status in :statuses and r.createdAt < :threshold")
    List<Resource> findResourcesToCleanup(@Param("statuses") List<ResourceStatus> statuses,
                                          @Param("threshold") Instant threshold);

    @Modifying
    @Query("update Resource r set r.status = :status where r.uuid in (:uuids) and r.userId = :userId")
    void updateStatuses(@Param("uuids") List<UUID> uuids,
                        @Param("status") ResourceStatus status,
                        @Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("update Resource r set r.path = replace(r.path, :oldPrefix, :newPrefix) " +
            "where r.path like concat(:oldPrefix, '%') " +
            "and r.userId = :userId")
    void updateNestedPaths(@Param("oldPrefix") String oldPrefix,
                           @Param("newPrefix") String newPrefix,
                           @Param("userId") Long userId);

    @Modifying
    @Query("delete from Resource r where r.path = :path " +
            "and r.name = :name " +
            "and r.type = :type " +
            "and r.userId = :userId")
    void deleteDirectory(@Param("path") String path,
                         @Param("name") String name,
                         @Param("type") ResourceType type,
                         @Param("userId") Long userId);

    @Modifying
    @Query("delete from Resource r where r.path like concat(:prefix, '%') " +
            "and r.type = :type " +
            "and r.userId = :userId")
    void deleteNestedDirectories(@Param("prefix") String prefix,
                                 @Param("type") ResourceType type,
                                 @Param("userId") Long userId);

    @Modifying
    @Query("update Resource r set r.status = :status " +
            "where r.path like concat(:prefix, '%') " +
            "and r.type = :type " +
            "and r.userId = :userId")
    void markAllFilesAsDeleted(@Param("prefix") String prefix,
                               @Param("status") ResourceStatus status,
                               @Param("type") ResourceType type,
                               @Param("userId") Long userId);
}
