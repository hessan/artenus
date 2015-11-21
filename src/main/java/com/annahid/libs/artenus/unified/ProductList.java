/*
 *  This file is part of the Artenus 2D Framework.
 *  Copyright (C) 2015  Hessan Feghhi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

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
