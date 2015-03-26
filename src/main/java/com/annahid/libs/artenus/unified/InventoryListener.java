package com.annahid.libs.artenus.unified;

/**
 * Interface for classes that listen to in-app billing events.
 */
public interface InventoryListener {
	/**
	 * Called to report that the request to load the product list has failed. This means that
	 * the in-app billing is not available for the current session.
	 */
	void onInventoryFailure();

	/**
	 * Called when the list of items in the inventory is ready. For items owned by the user,
	 * the {@link Product#getReceipt()} returns the corresponding receipt.
	 *
	 * @param inventory	The list of products in the inventory
	 * @see ProductReceipt
	 */
	void onInventoryLoaded(ProductList inventory);

	/**
	 * Called to report that a previous purchase has succeeded.
	 *
	 * @param purchase Purchase information
	 */
	void onPurchased(ProductReceipt purchase);

	/**
	 * Called to report that a previous purchase has failed. Application should then abort the
	 * transaction, clear any waiting dialogs, and report the failure to the user.
	 *
	 * @param purchase The corresponding product receipt
	 */
	void onPurchaseFailure(ProductReceipt purchase);

	/**
	 * Called to report that a previous consumption request has succeeded.
	 *
	 * @param purchase The corresponding product receipt
	 */
	void onConsumed(ProductReceipt purchase);
}
