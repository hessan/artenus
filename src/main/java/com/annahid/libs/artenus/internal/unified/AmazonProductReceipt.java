package com.annahid.libs.artenus.internal.unified;

import com.amazon.device.iap.model.Receipt;
import com.annahid.libs.artenus.unified.Product;
import com.annahid.libs.artenus.unified.ProductReceipt;

import java.util.Date;

final class AmazonProductReceipt implements ProductReceipt {
	Receipt receipt;

	public AmazonProductReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	@Override
	public int getProductType() {
		switch (receipt.getProductType()) {
			case CONSUMABLE:
				return Product.CONSUMABLE;
			case SUBSCRIPTION:
				return Product.SUBSCRIPTION;
			default:
				return Product.ENTITLEMENT;
		}
	}

	@Override
	public String getOrderId() {
		return receipt.getReceiptId();
	}

	@Override
	public String getSKU() {
		return receipt.getSku();
	}

	@Override
	public Date getPurchaseDate() {
		return receipt.getPurchaseDate();
	}

	@Override
	public Date getCancelDate() {
		return receipt.getCancelDate();
	}

	public boolean isPurchased() {
		return getCancelDate() == null;
	}

	public String getDeveloperPayload() {
		return null;
	}
}