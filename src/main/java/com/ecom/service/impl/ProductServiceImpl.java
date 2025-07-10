package com.ecom.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	private ProductRepository productRepository;

	@Override
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Boolean deleteProduct(Integer id) {
		
		Product product = productRepository.findById(id).orElse(null);
		
		if (!ObjectUtils.isEmpty(product)) {
			
			productRepository.delete(product);
			return true;
			
		}
		
		return false;
		
	}

	@Override
	public Product getProductById(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		return product;
	}

	@Override
	public Product updateProduct(Product product, MultipartFile imageFile) {
		
		Product produit = getProductById(product.getId());
		
		String imageName = imageFile.isEmpty() ? produit.getImage() : imageFile.getOriginalFilename();
		
		produit.setTitle(product.getTitle());
		produit.setDescription(product.getDescription());
		produit.setCategory(product.getCategory());
		produit.setPrice(product.getPrice());
		produit.setStock(product.getStock());
		produit.setImage(imageName);
		
		produit.setDiscount(product.getDiscount());
		
		// 5=100*(5/100); 100-5=95
		Double discount = product.getPrice()*(product.getDiscount()/100.0);
		Double discountPrice = product.getPrice()-discount;
		produit.setDiscountPrice(discountPrice);
		
		Product updateProduct = productRepository.save(produit);
		
		if (!ObjectUtils.isEmpty(updateProduct)) {
			
			if (!imageFile.isEmpty()) {
				
				try {
					
					File saveFile = new ClassPathResource("static/img").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + 
							File.separator + "product_img" + 
							File.separator + imageFile.getOriginalFilename());
					
					System.out.println(path);
					Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			return product;
			
		}
		
		return null;
		
	}

}
