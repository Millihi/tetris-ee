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

package projects.milfie.tetris.websocket.workflow;

import java.util.Collection;

public interface Buffer<E> {

   public Buffer<E> put (final E element);

   public Buffer<E> putAll (final E[] elements);

   public boolean isEmpty ();

   public E pop ();

   public E[] popAll ();

   public Buffer<E> transferTo (final Collection<E[]> batches);

   public Buffer<E> clear ();
}
