package com.ecom.controller;

import org.springframework.stereotype.Controller;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

=======
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecom.model.Category;
import com.ecom.service.CategoryService;

import jakarta.servlet.http.HttpSession;

>>>>>>> 611a288 (frontend design and package creating)
@Controller
@RequestMapping("/admin")
public class AdminController {
	
<<<<<<< HEAD
=======
	private CategoryService categoryService;
	
>>>>>>> 611a288 (frontend design and package creating)
	@GetMapping("/")
	public String index() {
		return "admin/index";
	}
	
	@GetMapping("/loadAddProduct")
	public String loadAddProduct() {
		return "admin/add_product";
	}
	
	@GetMapping("/category")
	public String category() {
		return "admin/category";
	}
<<<<<<< HEAD
=======
	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, HttpSession session) {
		
		Boolean existCategory = categoryService.existCategory(category.getName());
		
		if(existCategory) {
			session.setAttribute("errorMsg", "Category Name already exists");
		} else {
			
			Category saveCategory = categoryService.saveCategory(category);
			
			if(ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved ! internal server error");
			} else {
				session.setAttribute("succesMsg", "Saved successfully");
			}

		}
		
		return "redirect:/category";
		
	}
>>>>>>> 611a288 (frontend design and package creating)

}
