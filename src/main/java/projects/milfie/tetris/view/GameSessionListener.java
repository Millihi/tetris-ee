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

package projects.milfie.tetris.view;

import projects.milfie.tetris.service.StateKeeperBean;
import projects.milfie.tetris.service.WorkflowServiceBean;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class GameSessionListener
   implements HttpSessionListener
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public void sessionCreated (final HttpSessionEvent se) {
      LOG.info ("Created session " + se.getSession ().getId ());
   }

   @Override
   public void sessionDestroyed (final HttpSessionEvent se) {
      final HttpSession session = se.getSession ();

      workflowService.destroy (session.getId ());

      final StateKeeperBean stateKeeperBean =
         (StateKeeperBean) session.getAttribute (StateKeeperBean.NAME);

      if (stateKeeperBean != null) {
         stateKeeperBean.remove ();
      }

      LOG.info ("Destroyed session " + session.getId ());
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @EJB
   private WorkflowServiceBean workflowService;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Logger LOG =
      Logger.getLogger (GameSessionListener.class.getSimpleName ());
}
