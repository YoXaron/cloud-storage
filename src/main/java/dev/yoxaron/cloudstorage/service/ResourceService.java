package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.dto.ResourceResponseDto;
import dev.yoxaron.cloudstorage.entity.Resource;
import dev.yoxaron.cloudstorage.exception.ResourceNotFoundException;
import dev.yoxaron.cloudstorage.mapper.ResourceMapper;
import dev.yoxaron.cloudstorage.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public ResourceResponseDto getResourceInfo(String path, String name, Long userId) {
        Optional<Resource> maybeResource =
                resourceRepository.findResourceByPathAndNameAndUserId(path, name, userId);

        if (maybeResource.isEmpty()) {
            throw new ResourceNotFoundException("Resource not found");
        }

        return resourceMapper.toResourceDto(maybeResource.get());
    }
}
