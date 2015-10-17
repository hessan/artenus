package com.annahid.libs.artenus.entities;

import com.annahid.libs.artenus.core.Scene;
import com.annahid.libs.artenus.entities.behavior.Behaviors;

/**
 * Interface for all entities that can be added to a scene. Entities provide various functionality,
 * including graphical, physical, or user interaction. A {@link Scene} is made up of a list of
 * entities, and it uses them for processing graphics, animation, and user input.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.core.Scene
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

    /**
     * Indicates whether this entity has the specified behavior. If it does, it can be cast to the
     * corresponding interface. Note that the return value can be transient and a fixed return value
     * for a behavior is not guaranteed by the framework.
     *
     * @param behavior Behavior to be checked
     * @return {@code true} if this entity has the behavior, {@code false} otherwise
     * @see Behaviors
     */
    boolean hasBehavior(Behaviors behavior);
}
