package com.linh.ecommerce.size;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;
    private final SizeMapper sizeMapper;

    // Create
    public SizeResponse createSize(SizeRequest request) {
        Size size = sizeMapper.toSize(request);
        Size savedSize = sizeRepository.save(size);
        return sizeMapper.toSizeResponse(savedSize);
    }

    // Read (Get by ID)
    public SizeResponse getSizeById(UUID id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found with id: " + id));
        return sizeMapper.toSizeResponse(size);
    }

    // Read (Get all)
    public List<SizeResponse> getAllSizes() {
        return sizeRepository.findAll()
                .stream()
                .map(sizeMapper::toSizeResponse)
                .collect(Collectors.toList());
    }

    // Update
    public SizeResponse updateSize(UUID id, SizeRequest request) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found with id: " + id));
        size.setType(request.type());
        size.setValue(request.value());
        Size updatedSize = sizeRepository.save(size);
        return sizeMapper.toSizeResponse(updatedSize);
    }

    // Delete
    public void deleteSize(UUID id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found with id: " + id));
        sizeRepository.delete(size);
    }
}