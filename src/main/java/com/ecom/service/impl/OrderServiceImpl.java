package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.Cart;
import com.ecom.model.OrderAdress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.OrderService;
import com.ecom.util.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService{
	
	@Autowired
	private ProductOrderRepository orderRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Override
	public Boolean saveOrder(Integer userId, OrderRequest orderRequest) {
		
		List<Cart> carts = cartRepository.findByUserId(userId);
		
		 for (Cart cart : carts) {
			 
			 ProductOrder order = new ProductOrder();
			 
			 order.setOrderId(UUID.randomUUID().toString());
			 order.setOrderDate(new Date());
			 
			 order.setProduct(cart.getProduct());
			 order.setPrice(cart.getProduct().getDiscountPrice());
			 
			 order.setQuantity(cart.getQuantity());
			 order.setUser(cart.getUser());
			 
			 order.setStatus(OrderStatus.IN_PROGRESS.getName());
			 order.setPaymentType(orderRequest.getPaymentType());
			 
			 OrderAdress address = new OrderAdress();
			 address.setFirstName(orderRequest.getFirstName());
			 address.setLastName(orderRequest.getLastName());
			 address.setEmail(orderRequest.getEmail());
			 address.setMobileNo(orderRequest.getMobileNo());
			 address.setAddress(orderRequest.getAddress());
			 address.setCity(orderRequest.getCity());
			 address.setState(orderRequest.getState());
			 address.setPincode(orderRequest.getPincode());
			 
			 order.setOrderAdress(address);
			 
			 orderRepository.save(order);
			 
		 }
		 
		 return true;
				
	}
	
	
	
	
	
	
	
	
	

}
