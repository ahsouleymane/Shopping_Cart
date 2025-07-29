package com.ecom.service;

import com.ecom.model.OrderRequest;

public interface OrderService {
	
	public Boolean saveOrder(Integer userId, OrderRequest orderRequest);

}
