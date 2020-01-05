/* Created by PietElite */

package com.minecraftonline.griefalert.api.structures;

import java.util.Optional;

/**
 * An interface for mapping a key to a stack of a certain type. This was
 * designed to have an interface such that keys are always assumed to exist
 * and added if needed behind the scenes.
 *
 * @param <K> The key type
 * @param <V> The value type to be held in a stack
 */
public interface MapStack<K, V> {


  /**
   * Remove the key from the top of the stack.
   *
   * @param key The key to find the stack
   * @return An optional of the value which is removed from the stack.
   *         Returns an empty optional if no value is found, such as if the
   *         key was not found in the map or the stack is empty.
   */
  Optional<V> pop(K key);


  /**
   * Peek at the top of the stack mapped from the given key.
   *
   * @param key The key to find the stack
   * @return An optional of the value at the top of the stack.
   *         Returns an empty optional if no value is found, such as if the
   *         key was not found in the map or the stack is empty.
   */
  Optional<V> peek(K key);


  /**
   * Get the size of the stack mapped from the given key.
   *
   * @param key The key to find the stack
   * @return The size of the stack. Returns zero if the stack is empty
   *         or the key is not found.
   */
  Integer size(K key);


  /**
   * Push a value to the stack mapped from the given key.
   *
   * @param key   The key to find the stack
   * @param value The value to push. If the key was not previously found,
   *              the stack is added and the value is pushed as the first
   *              item.
   */
  void push(K key, V value);


  /**
   * Check if there are no values in a stack mapped from the given key.
   *
   * @param key The key to find the stack
   * @return true if there are no items in the stack or if no key was
   *         found in the map.
   */
  boolean isEmpty(K key);


  /**
   * Clear the stack mapped from the given key.
   * @param key The key to find the stack
   */
  void clear(K key);
}
