package com.annahid.libs.artenus.unified.impl;

import android.content.Context;
import android.content.Intent;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserDataResponse;
import com.annahid.libs.artenus.unified.InventoryListener;
import com.annahid.libs.artenus.unified.InventoryManager;
import com.annahid.libs.artenus.unified.Product;
import com.annahid.libs.artenus.unified.ProductList;
import com.annahid.libs.artenus.unified.ProductReceipt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class AmazonInventoryManager extends InventoryManager {
	class MyPurchasingListener implements PurchasingListener {
		ProductList products;
		boolean reset = true;

		public void onUserDataResponse(final UserDataResponse response) {
			final UserDataResponse.RequestStatus status = response.getRequestStatus();

			switch (status) {
				case SUCCESSFUL:
					currentUserId = response.getUserData().getUserId();
					currentMarketplace = response.getUserData().getMarketplace();
					products = new ProductList();

					String[] SKUs = getSKUs();
					Set<String> skuList = new HashSet<>();
					Collections.addAll(skuList, SKUs);
					PurchasingService.getProductData(skuList);
					break;

				case FAILED:
				case NOT_SUPPORTED:
					currentUserId = null;
					break;
			}
		}

		public void onProductDataResponse(final ProductDataResponse response) {
			if (response.getRequestStatus() != ProductDataResponse.RequestStatus.SUCCESSFUL) {
				final InventoryListener listener = getListener();

				if (listener != null)
					listener.onInventoryFailure();

				return;
			}

			Map<String, com.amazon.device.iap.model.Product> data = response.getProductData();

			for (String sku : getSKUs()) {
				if (!response.getUnavailableSkus().contains(sku)) {
					com.amazon.device.iap.model.Product amazonProduct = data.get(sku);

					final int type;

					switch (amazonProduct.getProductType()) {
						case CONSUMABLE:
							type = Product.CONSUMABLE;
							break;
						case SUBSCRIPTION:
							type = Product.SUBSCRIPTION;
							break;
						default:
							type = Product.ENTITLEMENT;
					}

					Product product = new Product(
							sku,
							amazonProduct.getTitle(),
							amazonProduct.getDescription(),
							amazonProduct.getPrice(),
							type
					);

					products.put(product);
				}
			}

			PurchasingService.getPurchaseUpdates(reset);
		}

		public void onPurchaseUpdatesResponse(final PurchaseUpdatesResponse response) {
			if (products == null)
				return;

			switch (response.getRequestStatus()) {
				case SUCCESSFUL:
					for (final Receipt r : response.getReceipts()) {
						Product product = products.get(r.getSku());

						if (product != null) {
							if (product.getReceipt() != null) {
								if (product.getReceipt().getPurchaseDate().after(r.getPurchaseDate()))
									continue;
							}

							product.setReceipt(new AmazonProductReceipt(r));
						}
					}

					if (response.hasMore()) {
						PurchasingService.getPurchaseUpdates(reset);
					} else {
						final InventoryListener listener = getListener();

						if (listener != null) {
							listener.onInventoryLoaded(products);
						}
					}

					break;
				case FAILED:
					final InventoryListener listener = getListener();

					if (listener != null)
						listener.onInventoryFailure();

					break;
			}
		}

		public void onPurchaseResponse(final PurchaseResponse response) {
			InventoryListener listener = getListener();

			if (listener == null)
				return;

			if (response.getRequestStatus() == PurchaseResponse.RequestStatus.SUCCESSFUL) {
				listener.onPurchased(new AmazonProductReceipt(response.getReceipt()));
			} else listener.onPurchaseFailure(new AmazonProductReceipt(response.getReceipt()));
		}
	}

	private String currentUserId = null, currentMarketplace = null;

	@Override
	public void onCreate(Context context) {
		PurchasingService.registerListener(context.getApplicationContext(), new MyPurchasingListener());
	}

	public void onResume() {
		PurchasingService.getUserData();
	}

	@Override
	public void onDestroy(Context context) {
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return false;
	}

	@Override
	public boolean isBillingSupported() {
		return currentUserId != null && currentMarketplace != null;
	}

	@Override
	public void subscribe(String sku) {
		PurchasingService.purchase(sku);
	}

	@Override
	public void purchase(String sku) {
		PurchasingService.purchase(sku);
	}

	@Override
	public void consume(ProductReceipt receipt) {
		if (receipt != null) {
			if (receipt.getCancelDate() == null) {
				PurchasingService.notifyFulfillment(receipt.getSKU(), FulfillmentResult.FULFILLED);

				if (getListener() != null)
					getListener().onConsumed(receipt);
			}
		}
	}
}
