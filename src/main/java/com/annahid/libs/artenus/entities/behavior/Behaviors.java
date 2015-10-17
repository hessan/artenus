package com.annahid.libs.artenus.entities.behavior;

/**
 * Entity behaviors. Each entity can support one or more of these behaviors.
 */
public enum Behaviors {
    /**
     * Behavior value for animatable entities. An entity that declares this behavior must also
     * implement {@link Animatable}.
     */
    ANIMATABLE,

    /**
     * Behavior value for renderable entities. An entity that declares this behavior must also
     * implement {@link Renderable}.
     */
    RENDERABLE,

    /**
     * Behavior value for entities that accept touch events. An entity that declares this behavior
     * must also implement {@link Touchable}.
     */
    TOUCHABLE,

    /**
     * Behavior value for entities that can be moved, rotated, or scaled. An entity that declares
     * this behavior must also implement {@link Transformable}.
     */
    TRANSFORMABLE
}
