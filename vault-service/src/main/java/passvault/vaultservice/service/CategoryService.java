package passvault.vaultservice.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import passvault.vaultservice.dto.CategoryRequest;
import passvault.vaultservice.dto.CategoryResponse;
import passvault.vaultservice.entity.Category;
import passvault.vaultservice.exception.ResourceNotFoundException;
import passvault.vaultservice.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(UUID userId) {
        Objects.requireNonNull(userId, "userId is required");
        return categoryRepository.findAllByUserIdOrderByNameAsc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(UUID id, UUID userId) {
        Objects.requireNonNull(userId, "userId is required");
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return toResponse(category);
    }

    public CategoryResponse create(UUID userId, CategoryRequest request) {
        Objects.requireNonNull(userId, "userId is required");
        Category category = Category.builder()
                .userId(userId)
                .name(request.getName())
                .build();
        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    public CategoryResponse update(UUID id, UUID userId, CategoryRequest request) {
        Objects.requireNonNull(userId, "userId is required");
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(request.getName());
        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    public void delete(UUID id, UUID userId) {
        Objects.requireNonNull(userId, "userId is required");
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .userId(category.getUserId())
                .name(category.getName())
                .build();
    }
}
