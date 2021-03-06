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

/**
 * Represents a product in an in-app billing system.
 */
@SuppressWarnings("UnusedDeclaration")
public final class Product {
    /**
     * Indicates a product that can be purchased multiple times by a customer. Examples include
     * fuel and ammo.
     */
    public static int CONSUMABLE = 1;

    /**
     * Indicates a product with recurring, automated billing at the interval you specify. Examples
     * of this product type in games include <em>premium version</em> (extra features), removed
     * ads, etc.
     */
    public static int SUBSCRIPTION = 2;

    /**
     * Indicates any type of content that you sell within your game that requires an access rights.
     * Examples include additional levels for a game, unlocking functionality already in your app,
     * etc. This product type is not supported by all app-stores. It is recommended to use
     * subscriptions instead, unless you only target specific app-stores with support for
     * entitlements.
     */
    public static int ENTITLEMENT = 4;

    /**
     * Holds product SKU as specified in the app-store
     */
    String sku;

    /**
     * Holds product title.
     */
    private String title;

    /**
     * Holds product description.
     */
    private String desc;

    /**
     * Holds product price.
     */
    private String price;

    /**
     * Holds product type.
     */
    private int type;

    /**
     * Holds the receipt for this product if there exists one.
     */
    private ProductReceipt receipt;

    /**
     * Creates a new instance of {@code Product} with the specified information.
     *
     * @param sku   Product SKU as specified in the app-store
     * @param title Product title
     * @param desc  Description
     * @param price Price, including the currency
     * @param type  Product type; it can be one of {@link #CONSUMABLE}, {@link #SUBSCRIPTION}, or
     *              {@link #ENTITLEMENT}
     */
    public Product(String sku, String title, String desc, String price, int type) {
        this.sku = sku;
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.type = type;
    }

    /**
     * Gets this product's SKU (unique stock-keeping unit identifier).
     *
     * @return Product SKU as specified in the app-store
     */
    public String getSKU() {
        return sku;
    }

    /**
     * Gets the type of this product. It can be one of {@link #CONSUMABLE}, {@link #SUBSCRIPTION},
     * or {@link #ENTITLEMENT}.
     *
     * @return Product type
     */
    public int getProductType() {
        return type;
    }

    /**
     * Gets the title of this product.
     *
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the price of this product.
     *
     * @return Price string, including the currency
     */
    public String getPrice() {
        return price;
    }

    /**
     * Gets the description of this product.
     *
     * @return The description
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Gets the product receipt. A product has a receipt if it has been acquired by the user. The
     * receipt contains purchase information.
     *
     * @return Product receipt, or {@code null} if there is no purchase
     *
     * @see ProductReceipt
     */
    public ProductReceipt getReceipt() {
        return receipt;
    }

    /**
     * This method is called internally during a purchase flow, to assign a receipt to this product.
     *
     * @param receipt Product receipt, or {@code null} if there is no purchase
     *
     * @see #getReceipt()
     * @see ProductReceipt
     */
    public void setReceipt(ProductReceipt receipt) {
        this.receipt = receipt;
    }

    /**
     * Indicates whether some other object is equal to this one. Equality in the context of
     * products is closely related to equal SKUs, hence that's what is compared by this method.
     *
     * @param obj Object to compare to
     *
     * @return {@code true} if {@code obj} is a product with the same SKU as this instance,
     * {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Product && ((Product) obj).sku.equals(sku);
    }
}
