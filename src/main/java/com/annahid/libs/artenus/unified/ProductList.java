package com.annahid.libs.artenus.unified;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class ProductList {
	private Map<String, Product> mSkuMap = new HashMap<>();

	public void put(Product product) {
		mSkuMap.put(product.sku, product);
	}

	public Product get(String sku) {
		if (mSkuMap.containsKey(sku))
			return mSkuMap.get(sku);

		return null;
	}

	public Collection<Product> getAllProducts() {
		return mSkuMap.values();
	}
}
