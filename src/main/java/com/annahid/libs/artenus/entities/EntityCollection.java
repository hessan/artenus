package com.annahid.libs.artenus.entities;

import android.support.annotation.NonNull;

import com.annahid.libs.artenus.entities.behavior.Behaviors;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.input.TouchEvent;
import com.annahid.libs.artenus.data.ConcurrentCollection;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.entities.behavior.Animatable;
import com.annahid.libs.artenus.entities.behavior.Transformable;
import com.annahid.libs.artenus.graphics.Effect;
import com.annahid.libs.artenus.entities.behavior.Touchable;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.core.Scene;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * <p>
 * A resizable list of {@code Entity} objects that implements {@link Entity}, so it can be added to
 * a scene as a single entity. When added as an entity, it renders all contained entities when
 * its {@code render} method is called.</p>
 * <p>
 * Entity collections have their own position, rotation, and scaling factors. The transformations
 * of the entities added to a collection are relative to the global transformations of the
 * collection. Effects in an entity collection are also added to the collection, and not to
 * individual entities.
 * </p>
 *
 * @author Hessan Feghhi
 * @see Entity
 * @see com.annahid.libs.artenus.entities.EntityPair
 * @see com.annahid.libs.artenus.core.Scene
 */
public class EntityCollection
        extends ConcurrentCollection<Entity>
        implements Entity, Animatable, Touchable, Transformable, Renderable {
    /**
     * Provides basic iterator functionality for {@code EntityCollection}.
     */
    private final class BasicIterator implements Iterator<Entity> {
        BasicIterator(Iterator<Entity> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Entity next() {
            lastReturned = it.next();
            return lastReturned;
        }

        @Override
        public void remove() {
            if (lastReturned == null)
                return;

            if (scene != null)
                lastReturned.onDetach(scene);

            it.remove();

            lastReturned = null;
        }

        private Entity lastReturned;
        private Iterator<Entity> it;
    }

    /**
     * Provides a special kind of iterator that digs deep into sub-collections in a depth-first
     * fashion.
     */
    private final class RecursiveIterator implements Iterator<Entity> {
        RecursiveIterator(Iterator<Entity> it) {
            stack.push(it);
        }

        @Override
        public boolean hasNext() {
            final Iterator<Entity> it = getCurrentIterator();
            return it != null && it.hasNext();
        }

        @Override
        public Entity next() {
            Iterator<Entity> it = getCurrentIterator();

            if (it == null)
                throw new NoSuchElementException();

            lastReturned = it.next();
            lastIterator = it;

            if (lastReturned instanceof EntityCollection)
                stack.push(((EntityCollection) lastReturned).iterator());

            return lastReturned;
        }

        @Override
        public void remove() {
            if (lastReturned == null)
                return;

            if (scene != null)
                lastReturned.onDetach(scene);

            lastIterator.remove();
            lastReturned = null;
            lastIterator = null;
        }

        private Iterator<Entity> getCurrentIterator() {
            if (stack.isEmpty())
                return null;

            Iterator<Entity> it = stack.peek();

            while (!it.hasNext()) {
                stack.pop();

                if (stack.isEmpty())
                    break;

                it = stack.peek();
            }

            return null;
        }

        private Entity lastReturned;
        private Iterator<Entity> lastIterator;
        private Stack<Iterator<Entity>> stack = new Stack<>();
    }

    /**
     * Constructs an {@code EntityCollection}.
     */
    public EntityCollection() {
        pos = new Point2D(0, 0);
        scale = new Point2D(1, 1);
    }

    /**
     * Clears all entities from this collection. The collection will be empty.
     */
    @Override
    public void clear() {
        if (scene != null) {
            Iterator<Entity> it = super.iterator();

            while (it.hasNext()) {
                it.next().onDetach(scene);
            }
        }

        super.clear();
    }

    /**
     * Returns a basic iterator over the elements.
     *
     * @return an iterator over only the direct children of this entity
     */
    @NonNull
    @Override
    public Iterator<Entity> iterator() {
        return new BasicIterator(super.iterator());
    }

    /**
     * Returns an iterator over the elements. This iterator supports removal, like
     * {@link EntityCollection#iterator()}. The main difference is that it does not only search the
     * items directly contained in this collection. Instead, it recognizes entity collections and
     * iterates recursively through them as well.
     *
     * @return an iterator over the elements in this list and all sub-lists
     */
    @NonNull
    @SuppressWarnings("unused")
    public Iterator<Entity> recursiveIterator() {
        return new RecursiveIterator(super.iterator());
    }

    /**
     * Brings an entity to the end of its collection, causing it to appear above all other
     * entities. Entities in above collection will still cover the entity.
     *
     * @param entity The entity to bring to front
     */
    @SuppressWarnings("unused")
    public final boolean bringToFront(Entity entity) {
        if (entity == getLast())
            return true;

        Iterator<Entity> it = super.iterator();

        while (it.hasNext()) {
            Entity temp = it.next();

            if (temp == entity) {
                it.remove();
                super.add(entity);
                return true;
            } else if (temp instanceof EntityCollection &&
                    ((EntityCollection) temp).bringToFront(entity))
                return true;
        }

        return false;
    }

    /**
     * Sends an entity to the beginning of its collection, causing it to be processed first and
     * displayed below all other entities. Sprites in bottom layers will still be covered by the
     * sprite.
     *
     * @param entity The sprite to send to back
     */
    @SuppressWarnings("unused")
    public final boolean sendToBack(Entity entity) {
        if (entity == getFirst())
            return true;

        Iterator<Entity> it = super.iterator();

        while (it.hasNext()) {
            Entity temp = it.next();

            if (temp == entity) {
                it.remove();
                super.prepend(entity);
                return true;
            } else if (temp instanceof EntityCollection &&
                    ((EntityCollection) temp).sendToBack(entity))
                return true;
        }

        return false;
    }

    @Override
    public void setAnimation(AnimationHandler animation) {
        anim = animation;
    }

    @Override
    public AnimationHandler getAnimation() {
        return anim;
    }

    @Override
    public Point2D getPosition() {
        return pos;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public Point2D getScale() {
        return scale;
    }

    @Override
    public void setRotation(float angle) {
        rotation = angle;
    }

    @Override
    public void setScale(float scaleValue) {
        scale.x = scale.y = scaleValue;
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        scale.x = scaleX;
        scale.y = scaleY;
    }

    @Override
    public void rotate(float angle) {
        rotation += angle;
    }

    @Override
    public void move(float amountX, float amountY) {
        pos.x += amountX;
        pos.y += amountY;
    }

    @Override
    public void setPosition(Point2D position) {
        setPosition(position.x, position.y);
    }

    @Override
    public void setPosition(float x, float y) {
        pos.x = x;
        pos.y = y;
    }

    @Override
    public void setColorFilter(float r, float g, float b) {
        colorFilter.r = r;
        colorFilter.g = g;
        colorFilter.b = b;
    }

    @Override
    public void setColorFilter(RGB rgb) {
        setColorFilter(rgb.r, rgb.g, rgb.b);
    }

    @Override
    public RGB getColorFilter() {
        return colorFilter;
    }

    @Override
    public void onAttach(Scene scene) {
        if (scene != null) {
            if (this.scene == null) {
                for (Entity entity : this)
                    entity.onAttach(scene);
            }

            this.scene = scene;
        }
    }

    @Override
    public void onDetach(Scene scene) {
        if (scene != null && scene == this.scene) {
            for (Entity entity : this)
                entity.onDetach(scene);

            this.scene = null;
        }
    }

    @Override
    public boolean hasBehavior(Behaviors behavior) {
        return true;
    }

    /**
     * Gets the scene this collection is currently attached to.
     *
     * @return The scene
     */
    protected Scene getScene() {
        return this.scene;
    }

    /**
     * Renders all the entities in the collection onto the given OpenGL context.
     */
    @Override
    public void render(RenderingContext ctx, int flags) {
        if (effect != null && (flags & FLAG_IGNORE_EFFECTS) == 0)
            effect.render(ctx, this, 1);
        else try {
            ctx.pushMatrix();
            ctx.translate(pos.x, pos.y);
            ctx.rotate(rotation);
            ctx.scale(scale.x, scale.y);

            for (Entity entity : this)
                if (entity.hasBehavior(Behaviors.RENDERABLE))
                    ((Renderable) entity).render(ctx, flags);

            ctx.popMatrix();
        } catch (Exception ex) {
            // Do nothing
        }
    }

    @Override
    public boolean add(Entity entity) {
        if (super.add(entity)) {
            if (scene != null)
                entity.onAttach(scene);

            return true;
        }

        return false;
    }

    @Override
    public void prepend(@NonNull Entity object) {
        super.prepend(object);

        if (scene != null)
            object.onAttach(scene);
    }

    /**
     * Removes an entity from this collection, searching recursively through all items. The
     * difference between this method and {@link EntityCollection#remove(Object)} is that this
     * method recognizes entity collections added to this collection, and beside considering
     * themselves, it searches recursively through them as well, until it finds {@code e}.
     *
     * @param e The entity to be removed
     * @return {@code true} if the entity is removed, and {@code false} if it doesn't exist in
     * this collection
     */
    public final boolean recursiveRemove(@NonNull Entity e) {
        Iterator<Entity> it = super.iterator();

        while (it.hasNext()) {
            final Entity entity = it.next();

            if (entity.equals(e)) {
                it.remove();

                if (scene != null) {
                    entity.onDetach(scene);
                }

                return true;
            } else if (entity instanceof EntityCollection &&
                    ((EntityCollection) entity).recursiveRemove(e))
                return true;
        }

        return false;
    }

    /**
     * Advances the associated animation handler, and then advances all entities added to this
     * collection.
     *
     * @param elapsedTime the amount of time since last call to this method
     */
    @Override
    public void advance(float elapsedTime) {
        if (anim != null)
            anim.advance(this, elapsedTime);

        for (Entity entity : this) {
            if (entity.hasBehavior(Behaviors.ANIMATABLE))
                ((Animatable) entity).advance(elapsedTime);
        }
    }

    @Override
    public boolean handleTouch(TouchEvent event) {
        for (Entity entity : this) {
            if (entity.hasBehavior(Behaviors.TOUCHABLE) && ((Touchable) entity).handleTouch(event))
                return true;
        }
        return false;
    }

    @Override
    public Effect getEffect() {
        return effect;
    }

    @Override
    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    @Override
    public void setAlpha(float alpha) {
    }

    @Override
    public float getAlpha() {
        return 1;
    }

    private RGB colorFilter = new RGB(1, 1, 1);
    private final Point2D pos, scale;
    private float rotation;
    private Scene scene = null;
    private Effect effect;

    protected AnimationHandler anim;
}
