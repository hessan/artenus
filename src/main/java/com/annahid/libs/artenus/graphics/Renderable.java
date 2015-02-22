package com.annahid.libs.artenus.graphics;

public interface Renderable {
	public static final int FLAG_IGNORE_COLOR_FILTER = 1;
	public static final int FLAG_IGNORE_EFFECTS = 2;

	public void render(int flags);
}
