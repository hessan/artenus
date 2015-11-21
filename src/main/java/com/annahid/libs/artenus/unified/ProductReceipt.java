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
