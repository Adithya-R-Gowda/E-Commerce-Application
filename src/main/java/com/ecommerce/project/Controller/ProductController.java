package com.ecommerce.project.Controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * ProductController handles HTTP requests related to managing products in the e-commerce application.
 * It provides endpoints for adding, updating, retrieving, and deleting products,
 * along with options for searching by categories and keywords.
 *
 * Additionally, it supports product image updates through file uploads.
 *
 * Endpoints include public endpoints for searching and viewing products and admin-protected endpoints for managing them.
 *
 * @author Adithya R
 */
@RestController
@RequestMapping("/api")
public class ProductController {

    /**
     * Service layer dependency to handle business logic related to product operations.
     */
    @Autowired
    private ProductService productService;

    /**
     * Endpoint to add a new product to a specific category. Restricted to admin users.
     *
     * @param productDTO the data transfer object containing product details
     * @param categoryId the ID of the category the product belongs to
     * @return a ResponseEntity containing the created ProductDTO
     */
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve all products with pagination, sorting, and ordering options.
     *
     * @param pageNumber the page number to retrieve (default is specified in AppConstants)
     * @param pageSize the number of products per page (default is specified in AppConstants)
     * @param sortBy the attribute to sort products by (default is specified in AppConstants)
     * @param sortOrder the order of sorting (ascending/descending; default is specified in AppConstants)
     * @return a ResponseEntity containing a paginated list of products wrapped in a ProductResponse
     */
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve products for a specific category with pagination, sorting, and ordering options.
     *
     * @param categoryId the ID of the category
     * @param pageNumber the page number to retrieve (default is specified in AppConstants)
     * @param pageSize the number of products per page (default is specified in AppConstants)
     * @param sortBy the attribute to sort products by (default is specified in AppConstants)
     * @param sortOrder the order of sorting (ascending/descending; default is specified in AppConstants)
     * @return a ResponseEntity containing a paginated list of products for the category wrapped in a ProductResponse
     */
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    /**
     * Endpoint to search for products by a keyword with pagination, sorting, and ordering options.
     *
     * @param keyword the search keyword
     * @param pageNumber the page number to retrieve (default is specified in AppConstants)
     * @param pageSize the number of products per page (default is specified in AppConstants)
     * @param sortBy the attribute to sort products by (default is specified in AppConstants)
     * @param sortOrder the order of sorting (ascending/descending; default is specified in AppConstants)
     * @return a ResponseEntity containing a paginated list of products matching the keyword wrapped in a ProductResponse
     */
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = productService.searchByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    /**
     * Endpoint to update a product by its ID. Restricted to admin users.
     *
     * @param productId the ID of the product to update
     * @param productDTO the data transfer object containing updated product details
     * @return a ResponseEntity containing the updated ProductDTO
     */
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProducts(@Valid @PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProductDTO = productService.updateProducts(productId, productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    /**
     * Endpoint to delete a product by its ID. Restricted to admin users.
     *
     * @param productId the ID of the product to delete
     * @return a ResponseEntity containing the details of the deleted ProductDTO
     */
    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> deleteProducts(@PathVariable Long productId) {
        ProductDTO deletedProductDTO = productService.deleteProducts(productId);
        return new ResponseEntity<>(deletedProductDTO, HttpStatus.OK);
    }

    /**
     * Endpoint to update a product's image by uploading a new file.
     *
     * @param productId the ID of the product
     * @param image the MultipartFile representing the new product image
     * @return a ResponseEntity containing the updated ProductDTO with the image details
     * @throws IOException if an error occurs during file upload
     */
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
