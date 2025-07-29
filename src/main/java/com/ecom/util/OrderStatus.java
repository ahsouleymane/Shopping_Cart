package com.ecom.util;

public enum OrderStatus {
	
	IN_PROGRESS(1, "En progression"),
	ORDER_RECIVED(2, "Commande Reçue"),
	PRODUCT_PACKED(3, "Produit emballé"),
	OUT_FOR_DELIVERY(4, "En cours de livraison"),
	DELIVERED(5, "Livré");
	
	private Integer id;
	
	private String name;

	private OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
