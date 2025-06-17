package com.ecommerce.project.Controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CategoryController is responsible for handling HTTP requests related to category management
 * in the e-commerce application. It provides endpoints for retrieving all categories,
 * creating a new category, updating an existing category, and deleting a category.
 *
 * The controller includes public endpoints accessible to all users and
 * admin-protected endpoints restricted to authorized users.
 *
 * @author Adithya R
 */
@RestController
@RequestMapping("/api")
public class CategoryController {

    /**
     * Service layer dependency for performing business logic related to category operations.
     */
    @Autowired
    private CategoryService categoryService;

    /**
     * Endpoint to retrieve all categories with pagination, sorting, and ordering options.
     *
     * @param pageNumber the page number to retrieve (default is specified in AppConstants)
     * @param pageSize the number of categories per page (default is specified in AppConstants)
     * @param sortBy the attribute to sort categories by (default is specified in AppConstants)
     * @param sortOrder the order of sorting (ascending/descending; default is specified in AppConstants)
     * @return a ResponseEntity containing a paginated list of categories wrapped in a CategoryResponse
     */
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    /**
     * Endpoint to create a new category. Restricted to admin users.
     *
     * @param categoryDTO the data transfer object containing category details
     * @return a ResponseEntity containing the created CategoryDTO
     */
    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedcategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedcategoryDTO, HttpStatus.CREATED);
    }

    /**
     * Endpoint to delete a category by its ID. Restricted to admin users.
     *
     * @param categoryId the ID of the category to delete
     * @return a ResponseEntity containing the details of the deleted CategoryDTO
     */
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    /**
     * Endpoint to update an existing category by its ID. Restricted to admin users.
     *
     * @param categoryDTO the data transfer object containing updated category details
     * @param categoryId the ID of the category to update
     * @return a ResponseEntity containing the updated CategoryDTO
     */
    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId) {
        CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
    }
}
