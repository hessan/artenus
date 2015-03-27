package com.annahid.libs.artenus.unified;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.annahid.libs.artenus.Artenus;

/**
 * Super-class for are in-app billing managers. A unified services implementation should return an
 * instance of this class if it handles in-app products and subscriptions.
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class InventoryManager {
	/**
	 * Sets the list of SKUs of interest. In order for the system to load SKUs at startup, they
	 * should all be included for this call.
	 *
	 * @param inventorySKUs	List of product SKUs
	 */
	public final void setSKUs(String[] inventorySKUs) {
		this.inventorySKUs = inventorySKUs;
	}

	/**
	 * Appoints a new listener to handle in-app billing events for this instance.
	 *
	 * @param listener	The new inventory listener, or {@code null} to remove the listener
	 */
	public final void setListener(InventoryListener listener) {
		this.listener = listener;
	}

	/**
	 * This method is internally called as part of {@link UnifiedServices#onCreate(Context)}.
	 *
	 * @param context	Artenus context
	 */
	public abstract void onCreate(Context context);

	/**
	 * This method is internally called as part of {@link UnifiedServices#onCreate(Context)}.
	 *
	 * @param context	Artenus context
	 */
	public abstract void onDestroy(Context context);

	/**
	 * This method is internally called by {@link UnifiedServices#onActivityResult(int, int, Intent)}
	 * to handle activity results for in-app billing.
	 *
	 * @param requestCode	The integer request code
	 * @param resultCode	The integer result code returned by the child activity
	 * @param data			An Intent, which can return result data to the caller
	 * @return	{@code true} if handled by inventory manager, {@code false} otherwise
	 */
	public abstract boolean onActivityResult(int requestCode, int resultCode, Intent data);

	/**
	 * Determines whether the store supports in-app billing.
	 *
	 * @return	{@code true} if in-app billing is supported, {@code false} otherwise
	 */
	public abstract boolean isBillingSupported();

	/**
	 * Launches a subscription flow for the specified subscription SKU.
	 *
	 * @param sku    Product SKU corresponding to the desired subscription
	 */
	public abstract void subscribe(String sku);

	/**
	 * Launches a purchase flow for the specified product. The result will be reported to the
	 * inventory listener associated with this instance.
	 *
	 * @param sku	Product SKU
	 * @see   InventoryListener
	 */
	public abstract void purchase(String sku);

	/**
	 * Consumes a previously purchased product.
	 *
	 * @param receipt	The receipt corresponding to the purchased product
	 * @see   InventoryListener
	 */
	public abstract void consume(ProductReceipt receipt);

	/**
	 * Constructs an empty instance of {@code InventoryManager}.
	 */
	protected InventoryManager() { }

	/**
	 * Gets the list of SKUs currently handled by this instance.
	 *
	 * @return	List of product SKUs
	 */
	protected final String[] getSKUs() {
		return inventorySKUs;
	}

	/**
	 * Gets the current inventory listener responsible for this instance.
	 *
	 * @return	Current inventory listener, or {@code null} if non-existent
	 */
	protected final InventoryListener getListener() {
		return listener;
	}

	private InventoryListener listener;
	private String[] inventorySKUs;
}
