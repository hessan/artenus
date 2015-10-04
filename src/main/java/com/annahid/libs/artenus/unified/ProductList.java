package com.annahid.libs.artenus.unified;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a group of products, accessible using their SKUs.
 *
 * @see InventoryListener#onInventoryLoaded(ProductList)
 */
@SuppressWarnings("UnusedDeclaration")
public class ProductList {
    /**
     * Adds a new product to the group.
     *
     * @param product The new product
     */
    public void put(Product product) {
        mSkuMap.put(product.sku, product);
    }

    /**
     * Retrieves a product from the group.
     *
     * @param sku SKU of the desired product
     * @return The desired product, or {@code null} if it doesn't exist in the group
     */
    public Product get(String sku) {
        if (mSkuMap.containsKey(sku))
            return mSkuMap.get(sku);

        return null;
    }

    /**
     * Retrieves all products in the group.
     *
     * @return A collection of products
     */
    public Collection<Product> getAllProducts() {
        return mSkuMap.values();
    }

    private Map<String, Product> mSkuMap = new HashMap<>(8);
}
