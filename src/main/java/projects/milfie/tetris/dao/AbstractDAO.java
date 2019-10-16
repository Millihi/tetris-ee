/*****************************************************************************
 * "THE CAKE-WARE LICENSE" (Revision 42):                                    *
 *                                                                           *
 *     Milfie <mail@milfie.uu.me> wrote this file. As long as you retain     *
 * this notice you can do whatever you want with this stuff. If we meet      *
 * some day, and you think this stuff is worth it, you must buy me a cake    *
 * in return.                                                                *
 *                                                                           *
 *     Milfie.                                                               *
 *****************************************************************************/

package projects.milfie.tetris.dao;

import java.io.Serializable;

public interface AbstractDAO<K, E extends Serializable> {

   public E find (final K key);

   public E update (final E entity);

   public void persist (final E entity);

   public void refresh (final E entity);

   public void remove (final E entity);
}
