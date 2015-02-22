package com.annahid.libs.artenus.unified;

public interface InventoryListener {
	public void onInventoryFailure();

	public void onInventoryLoaded(ProductList inventory);

	public void onPurchased(ProductReceipt purchase);

	public void onPurchaseFailure(ProductReceipt purchase);

	public void onConsumed(ProductReceipt purchase);
}
