/*
 *  This file is part of the Artenus 2D Framework.
 *  Copyright (C) 2015  Hessan Feghhi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.annahid.libs.artenus.internal.unified;

import android.content.Context;
import android.content.Intent;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.unified.Stores;
import com.annahid.libs.artenus.unified.InventoryListener;
import com.annahid.libs.artenus.unified.InventoryManager;
import com.annahid.libs.artenus.unified.Product;
import com.annahid.libs.artenus.unified.ProductList;
import com.annahid.libs.artenus.unified.ProductReceipt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class BazaarInventoryManager extends InventoryManager {

    private static final int RC_REQUEST = 10002;

    private IabHelper mHelper;

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
                            details.getType().equals(BazaarIabHelper.ITEM_TYPE_SUBS) ?
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
    BazaarIabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new BazaarIabHelper.OnIabPurchaseFinishedListener() {
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
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
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

    @Override
    public void onCreate(Context context) {
        String base64EncodedPublicKey;

        try {
            base64EncodedPublicKey = Security.getLicenseKey(context, Stores.BAZAAR);
        } catch (Exception ex) {
            return;
        }

        mHelper = new BazaarIabHelper(context, base64EncodedPublicKey);

        mHelper.startSetup(new BazaarIabHelper.OnIabSetupFinishedListener() {
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
        mHelper.launchPurchaseFlow(Artenus.getInstance(),
                sku, BazaarIabHelper.ITEM_TYPE_SUBS,
                RC_REQUEST, mPurchaseFinishedListener, "");
    }

    @Override
    public void purchase(String sku) {
        mHelper.launchPurchaseFlow(Artenus.getInstance(),
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
}
