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

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

public final class Transmitter {
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Transmitter (final Workflow workflow) {
      if (workflow == null) {
         throw new IllegalArgumentException ("Given workflow is null.");
      }
      this.workflow = workflow;
      this.queue = new LinkedList<> ();
   }

   public Session getSession () {
      return session;
   }

   public synchronized void setSession (final Session session) {
      if (session == null) {
         throw new IllegalArgumentException ("Given session is null.");
      }
      this.session = session;
   }

   public synchronized void closeSession () {
      if (session == null) {
         return;
      }
      try {
         if (session.isOpen ()) {
            session.close ();
         }
      }
      catch (final IOException e) {
         LOG.log (Level.WARNING, "An I/O error occured: ", e);
      }
      finally {
         session = null;
      }
   }

   public synchronized boolean canTransmit () {
      return (session != null && session.isOpen ());
   }

   public synchronized void transmit (final Command[] commands) {
      if (commands == null) {
         throw new IllegalArgumentException ("Given commands array is null.");
      }

      if (commands.length > 0) {
         queue.add (commands);
      }

      if (session == null || !session.isOpen () || queue.isEmpty ()) {
         return;
      }

      final RemoteEndpoint.Basic receiver = session.getBasicRemote ();

      do {
         try {
            receiver.sendObject (queue.element ());
         }
         catch (final IOException e) {
            LOG.log (Level.SEVERE, "An I/O error occured while send: ", e);

            workflow.suspend ();

            return;
         }
         catch (final EncodeException e) {
            final String error = "An encoding error occured while send: ";
            LOG.log (Level.SEVERE, error, e);

            workflow.shutdownWithError (error + e.getMessage ());

            return;
         }
         catch (final Throwable e) {
            final String error = "An unexpected error occured while send: ";
            LOG.log (Level.SEVERE, error, e);

            workflow.shutdownWithError (error + e.getMessage ());

            return;
         }

         queue.remove ();
      }
      while (!queue.isEmpty ());
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private Session session;

   private final Workflow         workflow;
   private final Queue<Command[]> queue;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Logger LOG =
      Logger.getLogger (Transmitter.class.getSimpleName ());
}
