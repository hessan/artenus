package com.annahid.libs.artenus.unified;

import android.content.Context;
import android.content.Intent;

@SuppressWarnings("UnusedDeclaration")
public abstract class InventoryManager {
	protected static final int RC_REQUEST = 10002;

	private Context mContext;
	private InventoryListener listener;
	private String[] inventorySKUs;

	protected InventoryManager() {
	}

	public final void setSKUs(String[] inventorySKUs) {
		this.inventorySKUs = inventorySKUs;
	}

	public final Context getContext() {
		return mContext;
	}

	public final void setListener(InventoryListener listener) {
		this.listener = listener;
	}

	protected final InventoryListener getListener() {
		return listener;
	}

	protected final String[] getSKUs() {
		return inventorySKUs;
	}

	public void onCreate(Context context) {
		mContext = context;
	}

	public abstract void onDestroy(Context context);

	public abstract boolean onActivityResult(int requestCode, int resultCode, Intent data);

	public abstract boolean isBillingSupported();

	public abstract void subscribe(String sku);

	public abstract void purchase(String sku);

	public abstract void consume(ProductReceipt receipt);
}
