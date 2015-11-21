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
	 * Called to report that a previous purchase has succeeded. It provides information about the
	 * purchase. If the product is a consumable which needs to be consumed after purchase, this
	 * would be a good place to consume it.
	 *
	 * @param purchase Purchase information
	 * @see InventoryManager#consume(ProductReceipt)
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
