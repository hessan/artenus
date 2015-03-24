package com.annahid.libs.artenus.unified;

public interface InventoryListener {
	void onInventoryFailure();

	void onInventoryLoaded(ProductList inventory);

	void onPurchased(ProductReceipt purchase);

	void onPurchaseFailure(ProductReceipt purchase);

	void onConsumed(ProductReceipt purchase);
}
