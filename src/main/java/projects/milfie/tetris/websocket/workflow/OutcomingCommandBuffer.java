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

import projects.milfie.tetris.websocket.rpc.Command;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

final class OutcomingCommandBuffer
   implements Buffer<Command>
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public OutcomingCommandBuffer () {
      this.queue = new LinkedList<> ();
   }

   @Override
   public synchronized Buffer<Command> put (final Command element) {
      if (element == null) {
         throw new IllegalArgumentException ("Given element is null.");
      }

      queue.add (element);

      return this;
   }

   @Override
   public synchronized Buffer<Command> putAll (final Command[] elements) {
      if (elements == null) {
         throw new IllegalArgumentException ("Given element array is null.");
      }

      Collections.addAll (queue, elements);

      return this;
   }

   @Override
   public synchronized boolean isEmpty () {
      return queue.isEmpty ();
   }

   @Override
   public synchronized Command pop () {
      if (queue.isEmpty ()) {
         throw new IllegalStateException ("Buffer is empty.");
      }

      return queue.remove ();
   }

   @Override
   public synchronized Command[] popAll () {
      final Command[] elements = queue.toArray (new Command[queue.size ()]);

      queue.clear ();

      return elements;
   }

   @Override
   public synchronized Buffer<Command> transferTo
      (final Collection<Command[]> batches)
   {
      if (batches == null) {
         throw new IllegalArgumentException ("Given collection is null.");
      }

      if (queue.isEmpty ()) {
         return this;
      }

      batches.add (popAll ());

      return this;
   }

   @Override
   public synchronized Buffer<Command> clear () {
      queue.clear ();

      return this;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Queue<Command> queue;
}
