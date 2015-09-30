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
