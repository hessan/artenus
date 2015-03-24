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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class GoogleInventoryManager extends InventoryManager {
	private IabHelper mHelper;

	@Override
	public void onCreate(Context context) {
		super.onCreate(context);

		String base64EncodedPublicKey;

		try {
			base64EncodedPublicKey = Security.getLicenseKey(context, UnifiedServices.Store.GOOGLE);
		} catch (Exception ex) {
			return;
		}

		mHelper = new GoogleIabHelper(context, base64EncodedPublicKey);

		mHelper.startSetup(new GoogleIabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {

				if (!result.isSuccess()) {

					if (mHelper != null)
						mHelper.dispose();

					mHelper = null;
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null) return;

				final List<String> skuList = new ArrayList<>();
				Collections.addAll(skuList, getSKUs());
				mHelper.queryInventoryAsync(true, skuList, mGotInventoryListener);
			}
		});
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return mHelper != null && mHelper.handleActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy(Context context) {
		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
	}

	@Override
	public boolean isBillingSupported() {
		return mHelper != null;
	}

	@Override
	public void subscribe(String sku) {
		mHelper.launchPurchaseFlow((Activity) getContext(),
				sku, GoogleIabHelper.ITEM_TYPE_SUBS,
				RC_REQUEST, mPurchaseFinishedListener, "");
	}

	@Override
	public void purchase(String sku) {
		mHelper.launchPurchaseFlow((Activity) getContext(),
				sku, RC_REQUEST, mPurchaseFinishedListener, "");
	}

	@Override
	public void consume(ProductReceipt purchase) {
		mHelper.consumeAsync(((CommonProductReceipt) purchase).receipt, mConsumeFinishedListener);
	}

	// Listener that's called when we finish querying the items and subscriptions we own
	GoogleIabHelper.QueryInventoryFinishedListener mGotInventoryListener = new GoogleIabHelper.QueryInventoryFinishedListener() {
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

			final ProductList list = new ProductList();

			for (String sku : getSKUs()) {
				if (inventory.hasDetails(sku)) {
					IabSkuDetails details = inventory.getSkuDetails(sku);

					Product product = new Product(
							sku,
							details.getTitle(),
							details.getDescription(),
							details.getPrice(),
							details.getType().equals(GoogleIabHelper.ITEM_TYPE_SUBS) ?
									Product.SUBSCRIPTION : Product.CONSUMABLE
					);

					if (inventory.hasPurchase(sku))
						product.setReceipt(new CommonProductReceipt(inventory.getPurchase(sku)));

					list.put(product);
				}
			}

			listener.onInventoryLoaded(list);
		}
	};

	@SuppressWarnings("UnusedDeclaration")
	private boolean verifyDeveloperPayload(IabPurchase p) {
		return true;
	}

	// Callback for when a purchase is finished
	GoogleIabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new GoogleIabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, IabPurchase purchase) {

			// if we were disposed of in the meantime, quit.
			if (mHelper == null) return;

			InventoryListener listener = getListener();

			if (listener == null)
				return;

			if (result.isFailure()) {
				listener.onPurchaseFailure(null);
				return;
			}

			final CommonProductReceipt receipt = new CommonProductReceipt(purchase);

			if (!verifyDeveloperPayload(purchase)) {
				listener.onPurchaseFailure(receipt);
				return;
			}

			listener.onPurchased(receipt);
		}
	};

	// Called when consumption is complete
	GoogleIabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new GoogleIabHelper.OnConsumeFinishedListener() {
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
