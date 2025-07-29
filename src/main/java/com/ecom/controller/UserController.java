package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping("/")
	public String home() {
		return "user/home";
	}
	
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
	
	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, 
			HttpSession session) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String userEmail = auth.getName();
	    
	    UserDtls user = userService.getUserByEmail(userEmail);

	    if (user == null) {
	        session.setAttribute("errorMsg", "Veuillez vous connecter !");
	        return "redirect:/signin";
	    }
		
		Cart saveCart = cartService.saveCart(pid, user.getId());
		
		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Echec lors de l'ajout du produit au panier !");
		} else {
			session.setAttribute("successMsg", "Produit ajouté au panier avec succes !");
		}
		
		return "redirect:/product/" + pid;
		
	}
	
	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {
		
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		
		if (carts.size() > 0) {
			Double orderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
			Double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice() + 2000 + 500;
			m.addAttribute("orderPrice", orderPrice);
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		
		return "/user/cart";
		
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
	
	@GetMapping("/cartQuantityUpdate")
	private String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}
	
	@GetMapping("/orders")
	public String orderPage() {
		return "/user/order";
	}
	
	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p, HttpSession session) {
		
		System.out.println(request);
		
		UserDtls user = getLoggedInUserDetails(p);
		Boolean isOrderSaved = orderService.saveOrder(user.getId(), request);
		
		if (isOrderSaved) {
            session.setAttribute("successMsg", "Commande passée avec succès !");
        } else {
        	session.setAttribute("errorMsg", "Échec de la commande");
        }
		
		return "redirect:/user/cart";
		
	}
	
	
	
	
	
	
	
	
}
