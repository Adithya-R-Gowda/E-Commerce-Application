package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for handling Category operations such as retrieval,
 * creation, deletion, and updating.
 *
 * This class provides core business logic and interacts with the
 * CategoryRepository to access data from the database.
 *
 * It uses ModelMapper for seamless DTO to Entity mapping and vice versa.
 *
 * @author Adithya R
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Fetches a paginated and sorted list of all categories.
     * Throws an APIException if no categories exist.
     *
     * @param pageNumber the page number for pagination
     * @param pageSize   the number of items per page
     * @param sortBy     the field to sort the categories by
     * @param sortOrder  the sorting direction (asc/desc)
     * @return a {@link CategoryResponse} containing paginated category data
     */
    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> allCategories = categoryPage.getContent();
        if (allCategories.isEmpty()) {
            throw new APIException("Category is Empty");
        }

        List<CategoryDTO> categoryDTOS = allCategories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    /**
     * Creates a new category and saves it to the database.
     *
     * Throws an APIException if a category with the same name already exists.
     *
     * @param categoryDTO the data transfer object containing category details
     * @return a {@link CategoryDTO} of the saved category
     */
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryFromDb = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (categoryFromDb != null) {
            throw new APIException("Category with name " + categoryDTO.getCategoryName() + " already exists !!!!");
        }
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    /**
     * Deletes a category based on its ID.
     * Throws a ResourceNotFoundException if the category is not found.
     *
     * @param categoryId the ID of the category to delete
     * @return a {@link CategoryDTO} of the deleted category
     */
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category deletedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryRepository.delete(deletedCategory);
        return modelMapper.map(deletedCategory, CategoryDTO.class);
    }

    /**
     * Updates an existing category based on its ID.
     *
     * Throws a ResourceNotFoundException if the category is not found.
     *
     * @param categoryDTO the data transfer object containing updated category details
     * @param categoryId  the ID of the category to update
     * @return a {@link CategoryDTO} of the updated category
     */
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
