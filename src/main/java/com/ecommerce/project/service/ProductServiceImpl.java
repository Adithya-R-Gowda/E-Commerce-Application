package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for handling product-related operations, such as adding, updating, deleting, and retrieving products.
 * This service also supports operations like searching products by category and keyword, along with uploading images for products.
 *
 * @author Adithya R
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    /**
     * Adds a new product under the given category.
     * Checks if a product with the same name already exists in the category to avoid duplication.
     *
     * @param categoryId ID of the category where the product is being added.
     * @param productDTO Product data transfer object containing the product details.
     * @return The added product's DTO.
     * @throws APIException If the product already exists in the category.
     */
    @Override
    @CacheEvict(value = "product", allEntries = true)
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        // Fetch category by ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        // Check if product already exists in category
        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }

        if (isProductNotPresent) {
            // Create and save new product
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            Double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        } else {
            throw new APIException("Product Already Exists");
        }
    }

    /**
     * Retrieves all products with pagination and sorting.
     *
     * @param pageNumber The page number to fetch.
     * @param pageSize The number of records per page.
     * @param sortBy The field to sort by.
     * @param sortOrder The sorting order (ascending or descending).
     * @return A response containing a paginated list of products.
     * @throws APIException If no products are found.
     */
    @Override
   // @Cacheable(value = "product", key = "'page_' + #pageNumber + '_size_' + #pageSize")
    @Cacheable(value = "product", keyGenerator = "customKeyGenerator")
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();

        // If no products found, throw exception
        if (products.isEmpty()) {
            throw new APIException("Product is empty");
        }

        // Convert to DTOs and set response
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    /**
     * Retrieves products under a specific category with pagination and sorting.
     *
     * @param categoryId The category ID to fetch products for.
     * @param pageNumber The page number to fetch.
     * @param pageSize The number of records per page.
     * @param sortBy The field to sort by.
     * @param sortOrder The sorting order (ascending or descending).
     * @return A response containing a paginated list of products under the category.
     * @throws APIException If no products are found for the given category.
     */
    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Fetch category by ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        // Define pagination and sorting
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

        // Fetch products and prepare response
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException(category.getCategoryName() + " no product found for the given category");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    /**
     * Retrieves products based on a search keyword with pagination and sorting.
     *
     * @param keyword The search keyword.
     * @param pageNumber The page number to fetch.
     * @param pageSize The number of records per page.
     * @param sortBy The field to sort by.
     * @param sortOrder The sorting order (ascending or descending).
     * @return A response containing a paginated list of products matching the keyword.
     * @throws APIException If no products are found for the given keyword.
     */
    @Override
    public ProductResponse searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> products = productPage.getContent();

        // If no products found, throw exception
        if (products.isEmpty()) {
            throw new APIException("Product Not found for the given keyword " + keyword);
        }

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    /**
     * Updates the details of a product.
     *
     * @param productId The product ID to update.
     * @param productDTO Product data transfer object containing the new product details.
     * @return The updated product's DTO.
     * @throws ResourceNotFoundException If the product does not exist.
     */
    @Override
    @CacheEvict(value = "product", allEntries = true)
    public ProductDTO updateProducts(Long productId, ProductDTO productDTO) {
        // Fetch product by ID
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Update product details
        Product product = modelMapper.map(productDTO, Product.class);
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        Product savedProduct = productRepository.save(productFromDb);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOS = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> productDTOS = cart.getCartItems().stream().
                    map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).toList();

            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();

        cartDTOS.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId The ID of the product to delete.
     * @return The deleted product's DTO.
     * @throws ResourceNotFoundException If the product does not exist.
     */
    @Override
    @CacheEvict(value = "product", allEntries = true)
    public ProductDTO deleteProducts(Long productId) {
        // Fetch product by ID
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        // Delete the product
        productRepository.delete(productFromDb);
        return modelMapper.map(productFromDb, ProductDTO.class);
    }

    /**
     * Updates the image of a product.
     *
     * @param productId The ID of the product whose image is being updated.
     * @param image The image file.
     * @return The product with the updated image.
     * @throws IOException If an error occurs during image upload.
     * @throws ResourceNotFoundException If the product does not exist.
     */
    @Override
    @CacheEvict(value = "product", allEntries = true)
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Fetch product by ID
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        // Upload the new image and update product's image URL
        String fileName = fileService.uploadImage(path, image);
        productFromDb.setImage(fileName);
        Product updatedProduct = productRepository.save(productFromDb);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}
