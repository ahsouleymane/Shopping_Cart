package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
		}
		
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
		
	}
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@GetMapping("/products")
	public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category) {
		System.out.println("category="+category);
		List<Category> categories = categoryService.getAllActiveCategory();
		List<Product> products = productService.getAllActiveProducts(category);
		m.addAttribute("categories", categories);
		m.addAttribute("products", products);
		m.addAttribute("paramValue", category);
		return "product";
	}
	
	@GetMapping("/product/{id}")
	public String product(@PathVariable int id, Model m) {
		Product productById = productService.getProductById(id);
		m.addAttribute("product", productById);
		return "view_product";
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("profileImage") MultipartFile profileImageFile, 
			HttpSession session) throws IOException {
		
		String imageName = profileImageFile.isEmpty() ? "default.jpg" : profileImageFile.getOriginalFilename();
		user.setImage(imageName);
		UserDtls saveUser = userService.saveUser(user);
		
		if (!ObjectUtils.isEmpty(saveUser)) {
			
			if (!profileImageFile.isEmpty()) {
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath() + 
						File.separator + "profil_img" + 
						File.separator + profileImageFile.getOriginalFilename());
				
				System.out.println(path);
				Files.copy(profileImageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
			}
			
			session.setAttribute("successMsg", "Enregistrer avec succès");
			
		} else {
			session.setAttribute("errorMsg", "Erreur sur le serveur");
		}
		
		return "redirect:/register";
		
	}
	
	// Mot de passe oublié
	
	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		return "forgot_password.html";
	}
	
	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, 
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
		UserDtls userByEmail = userService.getUserByEmail(email);
		
		if (ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "Email invalide !");
		} else {
			
			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);
			
			// Generate URL : http://localhost:8080/reset-password?token=ybfkjweyfbkwfkfkwkkr
			
			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;
			
			Boolean sendMail = commonUtil.sendMail(url, email);
			
			if (sendMail) {
				session.setAttribute("successMsg", "S'il vous plait verifier votre mail, le lien de réinitialisation a été envoyé.");
			} else {
				session.setAttribute("errorMsg", "Erreur sur le serveur ! Email non envoyé.");
			}
			
		}
		
		return "redirect:/forgot-password";
		
	}
	
	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {
		
		UserDtls userByToken = userService.getUserByToken(token);
		
		if (userByToken == null) {
			m.addAttribute("msg", "Le lien est invalide ou a expiré !");
			return "message";
		}
		
		m.addAttribute("token", token);
		return "reset_password";
		
	}
	
	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token, @RequestParam String password,
			HttpSession session, Model m) {
		
		UserDtls userByToken = userService.getUserByToken(token);
		
		if (userByToken == null) {
			m.addAttribute("errorMsg", "Le lien est invalide ou a expiré !");
			return "message";
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			//session.setAttribute("successMsg", "Mot de passe modifié avec succes !");
			m.addAttribute("msg", "Mot de passe modifié avec succes !");
			return "message";
		}
		
		
	}
	
	
	
	
	

}
