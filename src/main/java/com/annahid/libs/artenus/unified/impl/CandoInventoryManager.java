package com.annahid.libs.artenus.unified.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.annahid.libs.artenus.unified.UnifiedServices;
import com.annahid.libs.artenus.unified.InventoryListener;
import com.annahid.libs.artenus.unified.InventoryManager;
import com.annahid.libs.artenus.unified.Product;
import com.annahid.libs.artenus.unified.ProductList;
import com.annahid.libs.artenus.unified.ProductReceipt;

final class CandoInventoryManager extends InventoryManager {
	private IabHelper mHelper;

	@Override
	public void onCreate(Context context) {
		super.onCreate(context);

		String base64EncodedPublicKey;

		try {
			base64EncodedPublicKey = Security.getLicenseKey(context, UnifiedServices.Store.CANDO);
		} catch (Exception ex) {
			return;
		}

		// Create the helper, passing it our context and the public key to verify signatures with
		mHelper = new CandoIabHelper(context, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set this to false).
		mHelper.enableDebugLogging(true);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		mHelper.startSetup(new CandoIabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {

				if (!result.isSuccess()) {

					if (mHelper != null)
						mHelper.dispose();

					mHelper = null;
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null) return;

				// IAB is fully set up. Now, let's get an inventory of stuff we own.
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}

	@Override
	public void onDestroy(Context context) {
		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return mHelper != null && mHelper.handleActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean isBillingSupported() {
		return mHelper != null;
	}

	@Override
	public void subscribe(String sku) {
		mHelper.launchPurchaseFlow((Activity) getContext(),
				sku, CandoIabHelper.ITEM_TYPE_SUBS,
				RC_REQUEST, mPurchaseFinishedListener, "");
	}

	@Override
	public void purchase(String sku) {
		mHelper.launchPurchaseFlow((Activity) getContext(),
				sku, RC_REQUEST, mPurchaseFinishedListener, "");
	}

	@Override
	public void consume(ProductReceipt receipt) {
		mHelper.consumeAsync(((CommonProductReceipt) receipt).receipt, mConsumeFinishedListener);
	}

	@SuppressWarnings("UnusedDeclaration")
	private boolean verifyDeveloperPayload(IabPurchase p) {
		return true;
	}

	// Listener that's called when we finish querying the items and subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null) return;

			InventoryListener listener = getListener();

			if (listener == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				listener.onInventoryFailure();
				return;
			}

			ProductList list = new ProductList();

			for (String sku : getSKUs()) {
				if (inventory.hasDetails(sku)) {
					IabSkuDetails details = inventory.getSkuDetails(sku);

					Product product = new Product(
							sku,
							details.getTitle(),
							details.getDescription(),
							details.getPrice(),
							details.getType().equals(CandoIabHelper.ITEM_TYPE_SUBS) ?
									Product.SUBSCRIPTION : Product.CONSUMABLE
					);

					if (inventory.hasPurchase(sku)) {
						product.setReceipt(new CommonProductReceipt(inventory.getPurchase(sku)));
					}
				}
			}

			listener.onInventoryLoaded(list);
		}
	};

	// Callback for when a purchase is finished
	CandoIabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new CandoIabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, IabPurchase purchase) {

			// if we were disposed of in the meantime, quit.
			if (mHelper == null) return;

			InventoryListener listener = getListener();

			if (listener == null)
				return;

			if (result.isFailure()) {
				listener.onPurchaseFailure(new CommonProductReceipt(purchase));
				return;
			}

			if (!verifyDeveloperPayload(purchase)) {
				listener.onPurchaseFailure(new CommonProductReceipt(purchase));
				return;
			}

			listener.onPurchased(new CommonProductReceipt(purchase));
		}
	};

	// Called when consumption is complete
	CandoIabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new CandoIabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(IabPurchase purchase, IabResult result) {

			// if we were disposed of in the meantime, quit.
			if (mHelper == null) return;

			InventoryListener listener = getListener();

			if (listener == null)
				return;

			if (result.isSuccess()) {
				listener.onConsumed(new CommonProductReceipt(purchase));
			}
		}
	};
}
