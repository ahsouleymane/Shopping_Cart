package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
		
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
		
	}
	
	@GetMapping("/")
	public String index() {
		return "admin/index";
	}
	
	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "admin/add_product";
	}
	
	@GetMapping("/category")
	public String category(Model m) {
		
		m.addAttribute("categorys", categoryService.getAllCategory());
		return "admin/category";
		
	}
	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session) throws IOException {
		
		String imageName = imageFile != null ? imageFile.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);
		
		Boolean existCategory = categoryService.existCategory(category.getName());
		
		if(existCategory) {
			session.setAttribute("errorMsg", "La catégorie existe déja !");
		} else {
			
			Category saveCategory = categoryService.saveCategory(category);
			
			if(ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Echec de l'enregistrement ! erreur interne du serveur");
			} else {
					
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath() + 
						File.separator + "category_img" + 
						File.separator + imageFile.getOriginalFilename());
				
				System.out.println(path);
				Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				
				session.setAttribute("succesMsg", "Enregistrer avec succès");
				
			}

		}
		
		return "redirect:/admin/category";
		
	}
	
	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id,  HttpSession session) {
		
		Boolean deleteCategory = categoryService.deleteCategory(id);
		
		if (deleteCategory) {
			session.setAttribute("successMsg", "category supprimé avec succès !");
		} else {
			session.setAttribute("errorMsg", "Erreur sur le serveur");
		}
		
		return "redirect:/admin/category";
		
	}
	
	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
		
	}
	
	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("imageFile") MultipartFile imageFile, 
			HttpSession session) throws IOException {
		
		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName = imageFile.isEmpty() ? oldCategory.getImageName() : imageFile.getOriginalFilename();
		
		if (!ObjectUtils.isEmpty(category)) {
			
			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
			
		}
		
		Category updateCategory = categoryService.saveCategory(oldCategory);
		
		if (!ObjectUtils.isEmpty(updateCategory)) {
			
			if (!imageFile.isEmpty()) {
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath() + 
						File.separator + "category_img" + 
						File.separator + imageFile.getOriginalFilename());
				
				System.out.println(path);
				Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
			}
			
			session.setAttribute("successMsg", "category modifié avec succès !");
			
		} else {
			session.setAttribute("errorMsg", "Erreur sur le serveur");
		}
		
		return "redirect:/admin/loadEditCategory/" + category.getId();
		
	}
	
	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product,
			@RequestParam("productImage") MultipartFile productImageFile, HttpSession session) throws IOException {
		
		String imageName = productImageFile.isEmpty() ? "default.jpg" : productImageFile.getOriginalFilename();
		
		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		
		Product saveProduct = productService.saveProduct(product);
		
		if (!ObjectUtils.isEmpty(saveProduct)) {
			
			File saveFile = new ClassPathResource("static/img").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath() + 
					File.separator + "product_img" + 
					File.separator + productImageFile.getOriginalFilename());
			
			System.out.println(path);
			Files.copy(productImageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			session.setAttribute("successMsg", "Produit enregistré avec succès !");
			
		} else {
			session.setAttribute("errorMsg", "Erreur sur le serveur");
		}
		
		return "redirect:/admin/loadAddProduct";
		
	}
	
	@GetMapping("/products")
	public String loadViewProduct(Model m) {
		m.addAttribute("products", productService.getAllProducts());
		return "admin/products";
	}
	
	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		
		Boolean deleteProduct = productService.deleteProduct(id);
		
		if (deleteProduct) {
			
			session.setAttribute("successMsg", "Produit supprimé avec succès !");
			
		} else {
			session.setAttribute("errorMsg", "Erreur sur le serveur");
		}
		
		return "redirect:/admin/products";
		
	}
	
	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}
	
	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, HttpSession session, 
			@RequestParam("productImage") MultipartFile productImage, Model m) {
		
		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Rémise invalide !");
		} else {
		
			Product updateProduct = productService.updateProduct(product, productImage);
			
			if (!ObjectUtils.isEmpty(updateProduct)) {
				
				session.setAttribute("successMsg", "Produit modifié avec succès !");
				
			} else {
				session.setAttribute("errorMsg", "Erreur sur le serveur");
			}
			
		}
		
		return "redirect:/admin/editProduct/" + product.getId();
		
	}
	
	@GetMapping("/users")
	public String getAllUsers(Model m) {
		List<UserDtls> users = userService.getUsers("ROLE_USER");
		m.addAttribute("users", users);
		return "/admin/users";
	}
	
	@GetMapping("/updateStatus")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, 
			HttpSession session) {
		
		Boolean f = userService.updateAccountStatus(id, status);
		
		if (f) {
			session.setAttribute("successMsg", "Statut du compte mis à jour !");
		} else {
			session.setAttribute("errorMsg", "Erreur sur le serveur");
		}
		
		return "redirect:/admin/users";
		
	}
	
	
	
	

}
