package com.specsense.controller;

import com.specsense.config.JwtConfig;
import com.specsense.model.dto.ApplicationDTO;
import com.specsense.model.dto.CategoryDTO;
import com.specsense.model.dto.ProductDTO;
import com.specsense.model.vo.Result;
import com.specsense.model.entity.*;
import com.specsense.service.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminCrudController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private CompanyInfoService companyInfoService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private NavigationMenuService navigationMenuService;

    @Autowired
    private JwtConfig jwtConfig;

    private boolean authenticate(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtConfig.parseToken(token);
            return claims != null;
        } catch (Exception e) {
            return false;
        }
    }

    // Category CRUD
    @GetMapping("/categories")
    public Result<List<Category>> listCategories(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(categoryService.getAll());
    }

    @GetMapping("/categories/tree")
    public Result<List<CategoryDTO>> getCategoryTree(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(categoryService.getCategoryTree("en"));
    }

    @PostMapping("/categories")
    public Result<Void> createCategory(@RequestBody Category category, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        categoryService.save(category);
        return Result.success();
    }

    @PutMapping("/categories/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody Category category, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        category.setId(id);
        categoryService.update(category);
        return Result.success();
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        try {
            categoryService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    // Product CRUD
    @GetMapping("/products")
    public Result<List<ProductDTO>> listProducts(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(productService.getList(null, null, null, null, 1, 1000, "en").getData());
    }

    @PostMapping("/products")
    public Result<Void> createProduct(@RequestBody Product product, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        productService.save(product);
        return Result.success();
    }

    @PutMapping("/products/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        product.setId(id);
        productService.update(product);
        return Result.success();
    }

    @DeleteMapping("/products/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        productService.deleteById(id);
        return Result.success();
    }

    // News CRUD
    @GetMapping("/news")
    public Result<List<News>> listNews(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(newsService.getList(null, 1, 1000, "en").getData());
    }

    @PostMapping("/news")
    public Result<Void> createNews(@RequestBody News news, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        newsService.save(news);
        return Result.success();
    }

    @PutMapping("/news/{id}")
    public Result<Void> updateNews(@PathVariable Long id, @RequestBody News news, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        news.setId(id);
        newsService.update(news);
        return Result.success();
    }

    @DeleteMapping("/news/{id}")
    public Result<Void> deleteNews(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        newsService.deleteById(id);
        return Result.success();
    }

    // Application CRUD
    @GetMapping("/applications")
    public Result<List<Application>> listApplications(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(applicationService.getAll("en"));
    }

    private String extractImageUrl(Object imageUrl) {
        if (imageUrl == null) return null;
        if (imageUrl instanceof String) {
            return (String) imageUrl;
        }
        if (imageUrl instanceof java.util.Map) {
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) imageUrl;
            Object url = map.get("url");
            return url != null ? url.toString() : null;
        }
        return imageUrl.toString();
    }

    @PostMapping("/applications")
    public Result<Void> createApplication(@RequestBody ApplicationDTO dto, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        Application application = new Application();
        application.setType(dto.getType());
        application.setTitleEn(dto.getTitleEn());
        application.setTitleZh(dto.getTitleZh());
        application.setDescriptionEn(dto.getDescriptionEn());
        application.setDescriptionZh(dto.getDescriptionZh());
        application.setIcon(dto.getIcon());
        application.setLinkedCategoryKeys(dto.getLinkedCategoryKeys());
        application.setImageUrl(extractImageUrl(dto.getImageUrl()));
        applicationService.save(application);
        return Result.success();
    }

    @PutMapping("/applications/{id}")
    public Result<Void> updateApplication(@PathVariable Long id, @RequestBody ApplicationDTO dto, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        Application application = new Application();
        application.setId(id);
        application.setType(dto.getType());
        application.setTitleEn(dto.getTitleEn());
        application.setTitleZh(dto.getTitleZh());
        application.setDescriptionEn(dto.getDescriptionEn());
        application.setDescriptionZh(dto.getDescriptionZh());
        application.setIcon(dto.getIcon());
        application.setLinkedCategoryKeys(dto.getLinkedCategoryKeys());
        application.setImageUrl(extractImageUrl(dto.getImageUrl()));
        applicationService.update(application);
        return Result.success();
    }

    @DeleteMapping("/applications/{id}")
    public Result<Void> deleteApplication(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        applicationService.deleteById(id);
        return Result.success();
    }

    // Service CRUD
    @GetMapping("/services")
    public Result<List<com.specsense.model.entity.ServiceEntity>> listServices(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(serviceService.getAll("en"));
    }

    @PostMapping("/services")
    public Result<Void> createService(@RequestBody com.specsense.model.entity.ServiceEntity service, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        serviceService.save(service);
        return Result.success();
    }

    @PutMapping("/services/{id}")
    public Result<Void> updateService(@PathVariable Long id, @RequestBody com.specsense.model.entity.ServiceEntity service, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        service.setId(id);
        serviceService.update(service);
        return Result.success();
    }

    @DeleteMapping("/services/{id}")
    public Result<Void> deleteService(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        serviceService.deleteById(id);
        return Result.success();
    }

    // Download CRUD
    @GetMapping("/downloads")
    public Result<List<Download>> listDownloads(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(downloadService.getAll(null, "en"));
    }

    @PostMapping("/downloads")
    public Result<Void> createDownload(@RequestBody Download download, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        downloadService.save(download);
        return Result.success();
    }

    @PutMapping("/downloads/{id}")
    public Result<Void> updateDownload(@PathVariable Long id, @RequestBody Download download, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        download.setId(id);
        downloadService.update(download);
        return Result.success();
    }

    @DeleteMapping("/downloads/{id}")
    public Result<Void> deleteDownload(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        downloadService.deleteById(id);
        return Result.success();
    }

    // Company Info
    @GetMapping("/company-info")
    public Result<CompanyInfo> getCompanyInfo(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(companyInfoService.get("en"));
    }

    @PutMapping("/company-info")
    public Result<Void> updateCompanyInfo(@RequestBody CompanyInfo companyInfo, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        companyInfoService.update(companyInfo);
        return Result.success();
    }

    // Contact Inquiries
    @GetMapping("/inquiries")
    public Result<List<ContactInquiry>> listInquiries(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(contactService.getAll());
    }

    @GetMapping("/inquiries/{id}")
    public Result<ContactInquiry> getInquiry(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(contactService.getById(id));
    }

    @DeleteMapping("/inquiries/{id}")
    public Result<Void> deleteInquiry(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        contactService.deleteById(id);
        return Result.success();
    }

    // Navigation Menu CRUD
    @GetMapping("/menus")
    public Result<List<NavigationMenu>> listMenus(HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(navigationMenuService.getAll());
    }

    @GetMapping("/menus/{id}")
    public Result<NavigationMenu> getMenu(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        return Result.success(navigationMenuService.getById(id));
    }

    @PostMapping("/menus")
    public Result<Void> createMenu(@RequestBody NavigationMenu menu, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        navigationMenuService.save(menu);
        return Result.success();
    }

    @PutMapping("/menus/{id}")
    public Result<Void> updateMenu(@PathVariable Long id, @RequestBody NavigationMenu menu, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        menu.setId(id);
        navigationMenuService.update(menu);
        return Result.success();
    }

    @DeleteMapping("/menus/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id, HttpServletRequest request) {
        if (!authenticate(request)) return Result.error(401, "Unauthorized");
        navigationMenuService.deleteById(id);
        return Result.success();
    }
}
