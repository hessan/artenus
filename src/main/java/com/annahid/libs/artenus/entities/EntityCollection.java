package com.annahid.libs.artenus.entities;

import android.opengl.GLES10;
import android.support.annotation.NonNull;

import com.annahid.libs.artenus.data.ConcurrentCollection;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.graphics.Effect;
import com.annahid.libs.artenus.input.Touchable;
import com.annahid.libs.artenus.ui.Scene;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * This class holds a resizable array of {@code Entity} objects and provides methods to
 * render or advance their animation as a whole. It implements {@link Entity}, so it can
 * be added to {@code Scene} like any other entity.
 *
 * @author Hessan Feghhi
 * @see Entity
 * @see com.annahid.libs.artenus.ui.Scene
 */
public class EntityCollection
		extends ConcurrentCollection<Entity>
		implements Entity, Touchable {

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
			if(lastReturned == null)
				return;

			if(scene != null)
				lastReturned.onDetach(scene);

			it.remove();

			lastReturned = null;
		}

		private Entity lastReturned;
		private Iterator<Entity> it;
	}

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

			if(it == null)
				throw new NoSuchElementException();

			lastReturned = it.next();
			lastIterator = it;

			if(lastReturned instanceof EntityCollection)
				stack.push(((EntityCollection)lastReturned).iterator());

			return lastReturned;
		}

		@Override
		public void remove() {
			if(lastReturned == null)
				return;

			if(scene != null)
				lastReturned.onDetach(scene);

			lastIterator.remove();
			lastReturned = null;
			lastIterator = null;
		}

		private Iterator<Entity> getCurrentIterator() {
			if(stack.isEmpty())
				return null;

			Iterator<Entity> it = stack.peek();

			while(!it.hasNext()) {
				stack.pop();

				if(stack.isEmpty())
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
	 * Constructs an {@code EntityCollection} linked to the given scene.
	 *
	 * @param parentScene     The scene that contains this {@code EntityCollection}
	 */
	public EntityCollection(Scene parentScene) {
		pos = new Point2D(0, 0);
		scale = new Point2D(1, 1);
		scene = parentScene;
	}

	/**
	 * Removes this collection from its current scene and associates it with a new one.
	 *
	 * @param newScene The new scene to associate with.
	 */
	public void setScene(Scene newScene) {
		if (scene != null)
			onDetach(scene);

		scene = newScene;
		onAttach(newScene);
	}

	/**
	 * Clears all entities from this collection. The collection will be empty.
	 */
	@Override
	public void clear() {
		if (scene != null) {
			Iterator<Entity> it = super.iterator();

			while(it.hasNext()) {
				it.next().onDetach(scene);
			}
		}

		super.clear();
	}

	@NonNull
	@Override
	public Iterator<Entity> iterator() {
		return new BasicIterator(super.iterator());
	}

	@NonNull
	@SuppressWarnings("unused")
	public Iterator<Entity> recursiveIterator() {
		return new RecursiveIterator(super.iterator());
	}

	/**
	 * Brings an entity to the end of its collection, causing it to appear above all other
	 * entities. Entities in above collection will still cover the entity.
	 *
	 * @param entity The entity to bring to front.
	 */
	public final boolean bringToFront(Entity entity) {
		if(entity == getLast())
			return true;

		Iterator<Entity> it = super.iterator();

		while(it.hasNext()) {
			Entity temp = it.next();

			if(temp == entity) {
				it.remove();
				super.add(entity);
				return true;
			}
			else if (temp instanceof EntityCollection &&
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
	 * @param entity	The sprite to send to back.
	 */
	@SuppressWarnings("unused")
	public final boolean sendToBack(Entity entity) {
		if(entity == getFirst())
			return true;

		Iterator<Entity> it = super.iterator();

		while(it.hasNext()) {
			Entity temp = it.next();

			if(temp == entity) {
				it.remove();
				super.prepend(entity);
				return true;
			}
			else if (temp instanceof EntityCollection &&
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
		if (scene != null)
			for (Entity entity : this)
				entity.onAttach(scene);
	}

	@Override
	public void onDetach(Scene scene) {
		for (Entity entity : this)
			entity.onDetach(scene);
	}

	/**
	 * Renders all the entities in the collection onto the given OpenGL context.
	 */
	@Override
	public void render(int flags) {
		if (effect != null && (flags & FLAG_IGNORE_EFFECTS) == 0)
			effect.render(this, 1);
		else try {
			GLES10.glPushMatrix();
			GLES10.glTranslatef(pos.x, pos.y, 0);
			GLES10.glRotatef(rotation, 0, 0, 1);
			GLES10.glScalef(scale.x, scale.y, 0);

			for (Entity entity : this)
				entity.render(flags);

			GLES10.glPopMatrix();
		} catch (Exception ex) {
			// Do nothing
		}
	}

	@Override
	public boolean add(Entity entity) {
		if(super.add(entity)) {
			if(scene != null)
				entity.onAttach(scene);

			return true;
		}

		return false;
	}

	@Override
	public void prepend(@NonNull Entity object) {
		super.prepend(object);

		if(scene != null)
			object.onAttach(scene);
	}

	public final boolean recursiveRemove(Object o) {
		Iterator<Entity> it = super.iterator();

		while(it.hasNext()) {
			final Entity entity = it.next();

			if (entity.equals(o)) {
				it.remove();

				if (scene != null) {
					entity.onDetach(scene);
				}

				return true;
			} else if (entity instanceof EntityCollection &&
					((EntityCollection) entity).recursiveRemove(o))
				return true;
		}

		return false;
	}

	@Override
	public void advance(float elapsedTime) {
		if (anim != null)
			anim.advance(this, elapsedTime);

		for (Entity entity : this)
			entity.advance(elapsedTime);
	}

	@Override
	public boolean handleTouch(int action, float x, float y) {
		for (Entity entity : this)
			if (entity instanceof Touchable &&
					((Touchable) entity).handleTouch(action, x, y))
				return true;

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

	private RGB colorFilter = new RGB(1, 1, 1);
	private final Point2D pos, scale;
	private float rotation;
	private Scene scene = null;
	private Effect effect;

	protected AnimationHandler anim;
}
