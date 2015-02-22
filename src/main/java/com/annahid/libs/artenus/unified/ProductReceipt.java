package com.annahid.libs.artenus.unified;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public interface ProductReceipt {
	public int getItemType();

	public String getOrderId();

	public String getSKU();

	public Date getPurchaseDate();

	public Date getCancelDate();

	public boolean isPurchased();

	public String getDeveloperPayload();
}
