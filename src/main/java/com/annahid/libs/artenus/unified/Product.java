package com.annahid.libs.artenus.unified;

@SuppressWarnings("UnusedDeclaration")
public final class Product {
	public static int TYPE_CONSUMABLE = 1;
	public static int TYPE_SUBSCRIPTION = 2;
	public static int TYPE_ENTITLEMENT = 4;

	String sku;

	private String title;
	private String desc;
	private String price;
	private int type;
	private ProductReceipt receipt;

	public Product(String sku, String title, String desc, String price, int type) {
		this.sku = sku;
		this.title = title;
		this.desc = desc;
		this.price = price;
		this.type = type;
	}

	public String getSKU() {
		return sku;
	}

	public int getItemType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getPrice() {
		return price;
	}

	public String getDescription() {
		return desc;
	}

	public void setReceipt(ProductReceipt receipt) {
		this.receipt = receipt;
	}

	public ProductReceipt getReceipt() {
		return receipt;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Product && ((Product) obj).sku.equals(sku);
	}
}
