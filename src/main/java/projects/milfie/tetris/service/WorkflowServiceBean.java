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

package projects.milfie.tetris.service;

import projects.milfie.tetris.websocket.workflow.Transmitter;
import projects.milfie.tetris.websocket.workflow.Workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.servlet.http.HttpSession;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static javax.ejb.LockType.READ;

@Singleton
public class WorkflowServiceBean {
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public WorkflowServiceBean () {
      this.workflowMap = new HashMap<> ();
   }

   @Lock (READ)
   public boolean exists (final String workflowId) {
      return (workflowId != null && workflowMap.containsKey (workflowId));
   }

   @Lock (READ)
   public Workflow find (final String workflowId) {
      return (workflowId == null ? null : workflowMap.get (workflowId));

   }

   public void attach (final Workflow workflow) {
      if (workflow == null) {
         throw new IllegalArgumentException ("Given workflow is null.");
      }

      final String workflowId = workflow.getId ();

      if (workflowMap.containsKey (workflowId)) {
         throw new IllegalStateException
            ("The " + workflowId + " workflow already exists.");
      }

      workflowMap.put (workflowId, workflow);
   }

   public Workflow detach (final String workflowId) {
      if (workflowId == null) {
         throw new IllegalArgumentException ("Given workflowId is null.");
      }

      return workflowMap.remove (workflowId);
   }

   public Workflow detach (final Workflow workflow) {
      if (workflow == null) {
         throw new IllegalArgumentException ("Given workflow is null.");
      }

      return workflowMap.remove (workflow.getId ());
   }

   public void destroy (final String workflowId) {
      destroyInternal (detach (workflowId));
   }

   public void destroy (final Workflow workflow) {
      destroyInternal (detach (workflow));
   }

   public void sendAll (final String message) {
      if (message == null || message.isEmpty ()) {
         throw new IllegalArgumentException ("Given message is null.");
      }

      for (final Workflow workflow : workflowMap.values ()) {
         final Transmitter transmitter = workflow.getTransmitter ();

         if (transmitter.canTransmit ()) {
            final Session session = transmitter.getSession ();
            final RemoteEndpoint.Basic remote = session.getBasicRemote ();
            try {
               remote.sendText (message);
            }
            catch (final Throwable e) {
               LOG.log (Level.WARNING, "An error occured while sending: ", e);
            }
         }
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Map<String, Workflow> workflowMap;

   private void destroyInternal (final Workflow workflow) {
      if (workflow == null) {
         return;
      }

      LOG.info ("Destroyed workflow " + workflow.getId ());
      workflow.shutdownSuccessful ();
   }

   @Schedule
      (hour = "*",
       minute = "*",
       second = "0/" + PING_DELAY,
       persistent = false)
   private void pingActiveConnections () {
      int count = 0;

      for (final Workflow workflow : workflowMap.values ()) {
         final Transmitter transmitter = workflow.getTransmitter ();

         if (transmitter.canTransmit ()) {
            final Session session = transmitter.getSession ();
            final RemoteEndpoint.Basic remote = session.getBasicRemote ();
            try {
               remote.sendText ("PING");
            }
            catch (final Throwable e) {
               LOG.log (Level.WARNING, "An error occured while sending: ", e);
            }
            ++count;
         }
      }

      if (count > 0) {
         LOG.info ("Pinged " + count + " connected connections.");
      }
   }

   @Schedule
      (hour = "*",
       minute = "0/" + CLOSE_DELAY,
       persistent = false)
   private void closeStalledConnections () {
      final long currentTime = System.currentTimeMillis ();

      int count = 0;

      for (final Workflow workflow : workflowMap.values ()) {
         final HttpSession session = workflow.getHttpSession ();

         if (workflow.isSuspended () && workflow.getWorker ().isRun ()) {
            final long inactiveTime =
               (currentTime - workflow.getLastSuspendTime ()) / 60_000;

            if (inactiveTime > CLOSE_DELAY) {
               workflow.shutdownWithError
                  ("Inactive timeout exceed for session " + session.getId ());
               ++count;
            }
         }
      }

      if (count > 0) {
         LOG.info ("Closed " + count + " timedout connections.");
      }
   }

   @Schedule
      (hour = "*",
       minute = "0/" + UPDATE_DELAY,
       persistent = false)
   private void updateConnectionStates () {
      int count = 0;

      for (final Workflow workflow : workflowMap.values ()) {
         final HttpSession session = workflow.getHttpSession ();

         if (workflow.getWorker ().isRun ()) {
            session.setMaxInactiveInterval
               (session.getMaxInactiveInterval () + UPDATE_DELTA);
            ++count;
         }
      }

      if (count > 0) {
         LOG.info ("Updated " + count + " runned connections.");
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   public static final int PING_DELAY   = 15; // [s]
   public static final int CLOSE_DELAY  = 5;  // [m]
   public static final int UPDATE_DELAY = 15; // [m]
   public static final int UPDATE_DELTA = UPDATE_DELAY * 60 + 1; // [s]

   private static final Logger LOG =
      Logger.getLogger (WorkflowServiceBean.class.getSimpleName ());
}
