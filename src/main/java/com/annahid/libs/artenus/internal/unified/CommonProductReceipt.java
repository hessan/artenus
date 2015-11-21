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

package com.annahid.libs.artenus.internal.unified;

import com.annahid.libs.artenus.unified.Product;
import com.annahid.libs.artenus.unified.ProductReceipt;

import java.util.Date;

final class CommonProductReceipt implements ProductReceipt {
    IabPurchase receipt;

    public CommonProductReceipt(IabPurchase receipt) {
        this.receipt = receipt;
    }

    @Override
    public int getProductType() {
        final String itemType = receipt.getItemType();

        if (itemType.equals(IabHelper.ITEM_TYPE_SUBS))
            return Product.SUBSCRIPTION;
        else return Product.CONSUMABLE;
    }

    @Override
    public String getOrderId() {
        return receipt.getOrderId();
    }

    @Override
    public String getSKU() {
        return receipt.getSku();
    }

    @Override
    public Date getPurchaseDate() {
        return new Date(receipt.getPurchaseTime());
    }

    @Override
    public Date getCancelDate() {
        return null;
    }

    public boolean isPurchased() {
        return receipt.getPurchaseState() == 0;
    }

    public String getDeveloperPayload() {
        return receipt.getDeveloperPayload();
    }
}
