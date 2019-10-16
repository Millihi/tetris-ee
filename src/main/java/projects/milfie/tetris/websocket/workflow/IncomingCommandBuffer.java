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
import projects.milfie.tetris.websocket.rpc.CommandSpec;

import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Queue;

final class IncomingCommandBuffer
   implements Buffer<Command>
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public IncomingCommandBuffer () {
      this.values = new EnumMap<> (CommandSpec.class);
      this.keys = new LinkedList<> ();
   }

   @Override
   public synchronized Buffer<Command> put (final Command command) {
      if (command == null) {
         throw new IllegalArgumentException ("Given command is null.");
      }

      putInternal (command);

      return this;
   }

   @Override
   public synchronized Buffer<Command> putAll (final Command[] commands) {
      if (commands == null) {
         throw new IllegalArgumentException ("Given command array is null.");
      }

      for (final Command command : commands) {
         putInternal (command);
      }

      return this;
   }

   @Override
   public synchronized boolean isEmpty () {
      return keys.isEmpty ();
   }

   @Override
   public synchronized Command pop () {
      if (keys.isEmpty ()) {
         throw new IllegalStateException ("Buffer is empty.");
      }

      return popInternal ();
   }

   @Override
   public synchronized Command[] popAll () {
      final Command[] commands = new Command[keys.size ()];
      int i = 0;

      while (keys.size () > 0) {
         commands[i++] = popInternal ();
      }

      return commands;
   }

   @Override
   public synchronized Buffer<Command> transferTo
      (final Collection<Command[]> batches)
   {
      if (batches == null) {
         throw new IllegalArgumentException ("Given collection is null.");
      }

      if (keys.isEmpty ()) {
         return this;
      }

      batches.add (popAll ());

      return this;
   }

   @Override
   public synchronized Buffer<Command> clear () {
      keys.clear ();
      values.clear ();

      return this;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final EnumMap<CommandSpec, Command> values;
   private final Queue<CommandSpec>            keys;

   public void putInternal (final Command command) {
      assert (command != null) : "Command is null.";

      final CommandSpec spec = command.getSpec ();

      if (values.get (spec) == null) {
         keys.add (spec);
         values.put (spec, command);
      }
   }

   public Command popInternal () {
      assert (!keys.isEmpty ()) : "Buffer is empty.";
      return values.remove (keys.remove ());
   }
}
