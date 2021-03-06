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

package com.annahid.libs.artenus.data;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;

/**
 * <p>Thread-safe implementation of the {@code Collection} interface. Implements all collection
 * operations, but performance is optimized for iterator-based access and sequential addition (to,
 * both ends of the collection) as it is used as part of the entities and scene logic in Artenus.
 * </p>
 * <p>Java's standard LinkedList implementation is not thread-safe and hence it is programmed to
 * throw exceptions on concurrent modification and access scenarios. There are some alternatives
 * in the standard library, but Artenus uses this minimalistic collection implementation for its
 * internal use. You can use this class if you need a synchronized sequential-access collection.</p>
 * <p>The {@code size}, {@code isEmpty}, {@code iterator}, {@code add}, {@code prepend},
 * {@code getFirst}, and {@code getLast} operations are guaranteed to run in constant time. However,
 * no assumptions should be made about the running time of all the other operations, as this
 * implementation does not guarantee optimized behavior for them. </p>
 * <p>Unlike implementations like {@code LinkedList}, iterators returned by this class's iterator
 * and methods are not fail-fast: if the list is structurally modified after the iterator is
 * created, the iterator will not throw any exceptions, and will adapt to the new state of the
 * collection.</p>
 *
 * @param <T> The type of the elements in this collection
 *
 * @author Hessan Feghhi
 */
public class ConcurrentCollection<T> implements Collection<T> {
    /**
     * Holds the first element in the linked list.
     */
    private Element<T> first;

    /**
     * Holds the last element in the linked list.
     */
    private Element<T> last;

    /**
     * Holds the current collection size.
     */
    private int size = 0;

    /**
     * Inserts an element to the front of the collection.
     *
     * @param object the element to be added
     */
    public void prepend(@NonNull T object) {
        final Element<T> newElement = new Element<>(object);

        synchronized (this) {
            if (first != null) {
                newElement.next = first;
                first.prev = newElement;
            }

            first = newElement;
        }
    }

    /**
     * Adds an element to the back of the collection.
     *
     * @param object The element to be added
     *
     * @return {@code true}
     */
    @Override
    public boolean add(T object) {
        final Element<T> newElement = new Element<>(object);

        if (size == 0) {
            synchronized (this) {
                first = last = newElement;
                size = 1;
            }

            return true;
        }

        synchronized (this) {
            last.next = newElement;
            newElement.prev = last;
            last = newElement;
            size++;
        }

        return true;
    }

    /**
     * Appends all of the elements in the specified collection to the end of this collection, in the
     * order that they are returned by the specified collection's Iterator. The behavior of this
     * operation is undefined if the specified collection is modified while the operation is in
     * progress. (This implies that the behavior of this call is undefined if the specified
     * collection is this list, and this list is nonempty.)
     *
     * @param collection Collection of elements to be added to the collection
     *
     * @return {@code true}
     */
    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        for (T item : collection)
            add(item);

        return true;
    }

    /**
     * Removes all of the elements from this collection. The collection will be empty after this
     * call returns. However, existing iterators would not notice this change and would function as
     * they would before this call.
     */
    @Override
    public void clear() {
        first = last = null;
        size = 0;
    }

    /**
     * Returns {@code true} if this list contains the specified element. More formally, returns true
     * if and only if this list contains at least one element e such that <tt>(o==null ? e==null :
     * o.equals(e))</tt>.
     *
     * @param object Element whose presence in this list is to be tested
     *
     * @return {@code true} if this list contains the specified element
     */
    @Override
    public boolean contains(Object object) {
        if (first == null)
            return false;

        Element temp = first;

        while (temp != null && !temp.equals(object))
            temp = temp.next;

        return temp != null;
    }

    /**
     * <p>Returns {@code true} if this collection contains all of the elements in the specified
     * collection.</p> <p>This implementation iterates over the specified collection, checking each
     * element returned by the iterator in turn to see if it's contained in this collection. If all
     * elements are so contained true is returned, otherwise false.</p>
     *
     * @param collection Collection to be checked for containment in this collection
     *
     * @return {@code true} if this collection contains all of the elements in the specified
     * collection
     */
    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        for (Object item : collection) {
            if (!contains(item))
                return false;
        }
        return true;
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence. The returned iterator
     * supports removal, and will not fail in case of concurrent modification in the collection.
     *
     * @return An iterator over the elements in this list in proper sequence
     */
    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new It(first);
    }

    /**
     * Removes the first occurrence of the specified element from this collection, if it is present.
     * If the list does not contain the element, it is unchanged. More formally, removes the element
     * with the lowest distance from the front of the collection such that (o==null ? get(i)==null :
     * o.equals(get(i))) (if such an element exists). Returns true if this list contained the
     * specified element (or equivalently, if this list changed as a result of the call).
     *
     * @param object Element to be removed from this collection, if present
     *
     * @return {@code true} if an element was removed as a result of this call
     */
    @Override
    public boolean remove(Object object) {
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            T item = it.next();
            if (item.equals(object)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Removes from this collection all of its elements that are contained in the specified
     * collection.
     *
     * @param collection Collection containing elements to be removed from this list collection
     *
     * @return {@code true} if this list changed as a result of the call
     */
    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        boolean ret = false;

        for (Object object : collection) {
            ret = ret || remove(object);
        }

        return ret;
    }

    /**
     * Retains only the elements in this list that are contained in the specified collection. In
     * other words, removes from this list all of its elements that are not contained in the
     * specified collection.
     *
     * @param collection Collection containing elements to be retained in this list
     *
     * @return {@code true} if this list changed as a result of the call
     */
    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        Element<T> current = first;
        boolean ret = false;

        while (current != null) {
            if (!collection.contains(current.value)) {
                synchronized (this) {
                    if (current.next != null)
                        current.next.prev = current.prev;
                    else last = current;

                    if (current.prev != null)
                        current.prev.next = current.next;
                    else first = current;
                }

                ret = true;
            }

            current = current.next;
        }

        return ret;
    }

    /**
     * Returns the number of elements in this collection.
     *
     * @return The number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Indicates whether this collection contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns an array containing all of the elements in this collection in proper sequence (from
     * first to last element). The returned array will be "safe" in that no references to it are
     * maintained by this collection. (In other words, this method must allocate a new array). The
     * caller is thus free to modify the returned array. This method acts as bridge between
     * array-based and collection-based APIs.
     *
     * @return An array containing all of the elements in this list in proper sequence
     */
    @NonNull
    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int index = 0;

        for (T obj : this)
            array[index++] = obj;

        return array;
    }

    /**
     * This method is not implemented by this class, and should not be called.
     *
     * @param array Not used.
     * @param <T1>  Not used.
     *
     * @return Nothing; it only throws an {@code UnsupportedOperationException}
     *
     * @throws UnsupportedOperationException
     */
    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] array) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the first element in this collection (the element at the front of the collection).
     *
     * @return The first element of this collection
     */
    public T getFirst() {
        return first == null ? null : first.value;
    }

    /**
     * Gets the last element in this collection (the element at the back of the collection).
     *
     * @return The last element of this collection
     */
    public T getLast() {
        return last == null ? null : last.value;
    }

    /**
     * Represents an element in the linked list. ConcurrentCollection uses a linked list internally.
     *
     * @param <T> The type of the element in the linked list
     */
    private static final class Element<T> {
        T value;

        Element<T> prev, next;

        Element(T value) {
            this.value = value;
            prev = next = null;
        }
    }

    /**
     * Represents an iterator for a concurrent collection. An instance of this iterator is returned
     * by the {@link ConcurrentCollection#iterator()} method.
     */
    private final class It implements Iterator<T> {
        /**
         * Holds the current element this iterator points to. This element will be returned by the
         * next call to {@link #next()}.
         */
        Element<T> current;

        /**
         * Holds the latest element that was returned by this iterator.
         */
        Element<T> recent;

        /**
         * Creates an iterator starting at the given element.
         *
         * @param start Starting element
         */
        It(Element<T> start) {
            current = start;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            recent = current;
            current = current.next;
            return recent.value;
        }

        @Override
        public void remove() {
            if (recent == null)
                return;

            synchronized (ConcurrentCollection.this) {
                if (recent.prev == null)
                    first = recent.next;
                else recent.prev.next = recent.next;

                if (recent.next == null)
                    last = recent.prev;
                else recent.next.prev = recent.prev;

                if (first == recent)
                    first = recent.next;

                if (last == recent)
                    last = recent.prev;

                size--;
            }

            current = recent.next;
            recent = null;
        }
    }
}
