package passvault.vaultservice.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import passvault.vaultservice.dto.CategoryRequest;
import passvault.vaultservice.dto.CategoryResponse;
import passvault.vaultservice.service.CategoryService;

@RestController
@RequestMapping("/api/vault/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> list(@RequestHeader("X-User-Id") UUID userId) {
        return categoryService.list(userId);
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        return categoryService.getById(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.create(userId, request);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, userId, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        categoryService.delete(id, userId);
    }
}
