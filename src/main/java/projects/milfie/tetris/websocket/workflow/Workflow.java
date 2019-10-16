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

import projects.milfie.tetris.game.Worker;
import projects.milfie.tetris.service.StateKeeperBean;
import projects.milfie.tetris.websocket.rpc.Command;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

public class Workflow {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static class Builder {
      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public Builder setId (final String id) {
         this.id = id;
         return this;
      }

      public Builder setHttpSession (final HttpSession httpSession) {
         this.httpSession = httpSession;
         return this;
      }

      public Builder setWsSession (final Session wsSession) {
         this.wsSession = wsSession;
         return this;
      }

      public Builder setStateKeeper (final StateKeeperBean stateKeeper) {
         this.stateKeeper = stateKeeper;
         return this;
      }

      public Workflow build () {
         return new Workflow (this.validate ());
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      private String          id;
      private HttpSession     httpSession;
      private Session         wsSession;
      private StateKeeperBean stateKeeper;

      private Builder validate () {
         if (id == null || id.isEmpty ()) {
            throw new IllegalArgumentException
               ("Given id is empty.");
         }
         if (httpSession == null) {
            throw new IllegalArgumentException
               ("Null HTTP session given.");
         }
         if (stateKeeper == null) {
            throw new IllegalArgumentException
               ("Null state keeper given.");
         }
         return this;
      }
   }

   public static Builder newBuilder () {
      return new Builder ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public String getId () {
      return id;
   }

   public HttpSession getHttpSession () {
      return httpSession;
   }

   public StateKeeperBean getStateKeeper () {
      return stateKeeper;
   }

   public Buffer<Command> getIncomings () {
      return incomings;
   }

   public Buffer<Command> getOutcomings () {
      return outcomings;
   }

   public Transmitter getTransmitter () {
      return transmitter;
   }

   public Worker getWorker () {
      return worker;
   }

   public long getLastSuspendTime () {
      return lastSuspendTime;
   }

   public boolean isSuspended () {
      return suspended.get ();
   }

   public synchronized Workflow suspend () {
      LOG.info ("Workflow is requested to suspend.");

      suspended.set (true);
      worker.sleep ();
      transmitter.closeSession ();
      lastSuspendTime = System.currentTimeMillis ();

      return this;
   }

   public synchronized Workflow resume (final Session wsSession) {
      LOG.info ("Workflow is requested to resume.");

      transmitter.setSession (wsSession);
      worker.restore ();
      suspended.set (false);

      return this;
   }

   public synchronized Workflow resume () {
      LOG.info ("Workflow is requested to resume.");

      worker.restore ();
      suspended.set (false);

      return this;
   }

   public synchronized Workflow shutdownSuccessful () {
      LOG.info ("Workflow is requested to successful completion.");

      worker.stop ();
      stateKeeper.setError (null);

      return this;
   }

   public synchronized Workflow shutdownWithError (final String message) {
      LOG.info ("Workflow is requested to erroneous completion.");

      worker.stop ();
      stateKeeper.setError (message);

      return this;
   }

   public synchronized Workflow flush () {
      transmitter.transmit (outcomings.popAll ());

      return this;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final AtomicBoolean suspended = new AtomicBoolean (false);

   private final String          id;
   private final HttpSession     httpSession;
   private final StateKeeperBean stateKeeper;
   private final Worker          worker;
   private final Buffer<Command> incomings;
   private final Buffer<Command> outcomings;
   private final Transmitter     transmitter;

   private long lastSuspendTime;

   private Workflow (final Builder builder) {
      this.id = builder.id;
      this.httpSession = builder.httpSession;
      this.stateKeeper = builder.stateKeeper;
      this.incomings = new IncomingCommandBuffer ();
      this.outcomings = new OutcomingCommandBuffer ();
      this.transmitter = new Transmitter (this);
      this.worker = new Worker (this);
      this.lastSuspendTime = System.currentTimeMillis ();

      if (builder.wsSession != null) {
         this.transmitter.setSession (builder.wsSession);
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Logger LOG =
      Logger.getLogger (Workflow.class.getSimpleName ());
}
