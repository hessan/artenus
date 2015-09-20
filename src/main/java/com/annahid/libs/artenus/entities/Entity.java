package com.annahid.libs.artenus.entities;

import com.annahid.libs.artenus.ui.Scene;

/**
 * Interface for all entities that can be added to a scene. Entities provide various functionality,
 * including graphical, physical, or user interaction. A {@link Scene} is made up of a list of
 * entities, and it uses them for processing graphics, animation, and user input.
 *
 * @see com.annahid.libs.artenus.ui.Scene
 * @author Hessan Feghhi
 */
public interface Entity {
	/**
	 * Called when this entity is associated with a scene.
	 *
	 * @param scene The scene that currently contains the entity.
	 */
	void onAttach(Scene scene);

	/**
	 * Called when this entity is dissociated with a scene.
	 *
	 * @param scene The scene from which the entity is removed.
	 */
	void onDetach(Scene scene);
}
