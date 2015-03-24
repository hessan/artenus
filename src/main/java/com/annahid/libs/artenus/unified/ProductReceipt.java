package com.annahid.libs.artenus.unified;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public interface ProductReceipt {
	int getItemType();

	String getOrderId();

	String getSKU();

	Date getPurchaseDate();

	Date getCancelDate();

	boolean isPurchased();

	String getDeveloperPayload();
}
