package com.annahid.libs.artenus.unified;

import java.util.Date;

/**
 * Represents a receipt for a purchased product. It provides information about the purchase. A
 * product has a receipt if it is purchased, or has been purchased and canceled.
 *
 * @see Product
 */
@SuppressWarnings("UnusedDeclaration")
public interface ProductReceipt {
    /**
     * Gets the type of the corresponding product. It can be one of {@link Product#CONSUMABLE},
     * {@link Product#SUBSCRIPTION}, or {@link Product#ENTITLEMENT}.
     *
     * @return Product type
     */
    int getProductType();

    /**
     * Gets store-specific order identifier associated with this receipt.
     *
     * @return Order identifier
     */
    String getOrderId();

    /**
     * Gets the SKU of the corresponding product.
     *
     * @return Product SKU
     */
    String getSKU();

    /**
     * Gets the date of purchase.
     *
     * @return Purchase date
     */
    Date getPurchaseDate();

    /**
     * If this purchase has been canceled, gets the date it has been canceled.
     *
     * @return Cancel date, or {@code null} if not canceled
     */
    Date getCancelDate();

    /**
     * Indicates whether the product is currently in the possession of the user.
     *
     * @return    {@code true} if purchased, {@code false} otherwise
     */
    boolean isPurchased();

    /**
     * Gets developer payload associated with this purchase.
     *
     * @return Developer payload string
     */
    String getDeveloperPayload();
}
