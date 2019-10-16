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

package projects.milfie.tetris.websocket.endpoint;

import projects.milfie.tetris.service.StateKeeperBean;
import projects.milfie.tetris.service.WorkflowServiceBean;
import projects.milfie.tetris.websocket.rpc.Command;
import projects.milfie.tetris.websocket.rpc.JSONSpecGenerator;
import projects.milfie.tetris.websocket.workflow.Workflow;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import static javax.websocket.CloseReason.CloseCodes.CLOSED_ABNORMALLY;
import static javax.websocket.CloseReason.CloseCodes.UNEXPECTED_CONDITION;

@ServerEndpoint
   (value = "/gameplay",
    configurator = AppEndpointConfigurator.class,
    decoders = {CommandDecoder.class},
    encoders = {CommandEncoder.class})
public class AppEndpoint {
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @OnOpen
   public void onOpen (final Session wsSession,
                       final EndpointConfig config)
      throws IOException
   {
      LOG.info
         ("Session " + wsSession.getId () + " opened.");

      final HttpSession httpSession =
         AppEndpointConfigurator.getObject (config, HttpSession.class);

      if (httpSession == null) {
         LOG.severe
            ("Session " + wsSession.getId () + " " +
             "has no associated HTTP-Session, closing.");
         wsSession.close
            (new CloseReason
                (UNEXPECTED_CONDITION, "No HTTP-Session"));
         return;
      }

      final StateKeeperBean stateKeeper =
         (StateKeeperBean) httpSession.getAttribute (StateKeeperBean.NAME);

      if (stateKeeper == null) {
         LOG.severe
            ("Session " + wsSession.getId () + " " +
             "has no associated StateKeeperBean, closing.");
         wsSession.close
            (new CloseReason
                (UNEXPECTED_CONDITION, "No properties found."));
         return;
      }

      wsSession
         .getBasicRemote ()
         .sendText (JSONSpecGenerator.generate ().toString ());

      final String workflowId = httpSession.getId ();

      workflow = workflowService.detach (workflowId);

      if (workflow == null) {
         LOG.info
            ("Created new " + workflowId + " workflow.");
         workflow = Workflow
            .newBuilder ()
            .setId (workflowId)
            .setHttpSession (httpSession)
            .setWsSession (wsSession)
            .setStateKeeper (stateKeeper)
            .build ();
         threadFactory
            .newThread (workflow.getWorker ())
            .start ();
      }
      else if (!workflow.getWorker ().isRun ()) {
         LOG.severe
            ("Worker for " + workflowId + " workflow is not running.");
         wsSession.close
            (new CloseReason
                (UNEXPECTED_CONDITION, "No worker found."));
         return;
      }
      else {
         LOG.info
            ("Restored " + workflowId + " workflow.");
         workflow
            .suspend ()
            .resume (wsSession);
      }

      workflowService.attach (workflow);
   }

   @OnMessage
   public void onMessage (final Command[] commands) {
      workflow.getIncomings ().putAll (commands);
   }

   @OnClose
   public void onClose (final Session session,
                        final CloseReason reason)
   {
      LOG.info
         ("Session " + session.getId () + " is closed for " +
          "reason " + reason.toString ());

      if (workflow == null || workflow.isSuspended ()) {
         return;
      }

      if (workflow.getWorker ().isRun ()) {
         if (reason.getCloseCode () == CLOSED_ABNORMALLY) {
            workflow.suspend ();
         }
         else {
            workflowService.destroy (workflow);
         }
      }
   }

   @OnError
   public void onError (final Session session,
                        final Throwable cause)
   {
      LOG.log
         (Level.WARNING,
          "An error in session " + session.getId () + " occured: ",
          cause);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Resource (name = JNDI_THREAD_FACTORY)
   private ManagedThreadFactory threadFactory;

   @EJB
   private WorkflowServiceBean workflowService;

   private Workflow workflow;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String JNDI_THREAD_FACTORY =
      "java:comp/DefaultManagedThreadFactory";

   private static final Logger LOG =
      Logger.getLogger (AppEndpoint.class.getSimpleName ());
}
