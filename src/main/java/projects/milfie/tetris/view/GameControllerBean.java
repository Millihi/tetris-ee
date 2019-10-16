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

import projects.milfie.captcha.wsdl.ClientNotFoundException_Exception;
import projects.milfie.captcha.wsdl.ProfileNotFoundException_Exception;
import projects.milfie.tetris.domain.Result;
import projects.milfie.tetris.service.ResultServiceLocal;
import projects.milfie.tetris.service.StateKeeperBean;
import projects.milfie.tetris.service.WorkflowServiceBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ViewScoped
@ManagedBean (name = "gameController")
public class GameControllerBean
   extends AbstractPageController
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   @PostConstruct
   public void postConstructInit () {
      reset ();
      resourceInit ();

      final FacesContext fctx = FacesContext.getCurrentInstance ();
      final ExternalContext ectx = fctx.getExternalContext ();
      final HttpServletRequest request =
         (HttpServletRequest) ectx.getRequest ();
      final HttpSession session = request.getSession (true);

      captcha.setName (session.getId ());

      state = (StateKeeperBean) session.getAttribute (StateKeeperBean.NAME);

      if (state == null) {
         state = StateKeeperBean.obtainFromJNDI ();

         if (state == null) {
            LOG.severe ("Could not obtain state bean from JNDI.");
            enterError (getGlobalMessage ("ui.app.error.internal"));
            return;
         }

         session.setAttribute (StateKeeperBean.NAME, state);
      }

      enterHighScores ();
   }

   public Collection<Result> getResults () {
      return results;
   }

   public StateKeeperBean getState () {
      return state;
   }

   public String getViewName () {
      return view.name ().toLowerCase ();
   }

   public String getTemplatePath () {
      return view.templatePath;
   }

   public CaptchaBean getCaptcha () {
      return captcha;
   }

   public void setCaptcha (final CaptchaBean captcha) {
      this.captcha = captcha;
   }

   public String enterHighScores () {
      view = View.HIGH_SCORES;
      results = resultService
         .findResults ()
         .stream ()
         .sorted ((left, right) -> {
            if (left.getScore () == right.getScore ()) {
               if (left.getLevel () == right.getLevel ()) {
                  return 0;
               }
               if (left.getLevel () > right.getLevel ()) {
                  return -1;
               }
               return 1;
            }
            if (left.getScore () > right.getScore ()) {
               return -1;
            }
            return 1;
         })
         .collect (Collectors.toList ());

      return null;
   }

   public String enterSettings () {
      view = View.SETTINGS;

      return null;
   }

   public String enterPlay ()
      throws ClientNotFoundException_Exception,
             ProfileNotFoundException_Exception
   {
      if (captcha.isLastPuzzleSolved ()) {
         view = View.PLAY;
         captcha.deactivateSession ();
      }
      else {
         view = View.ASK_CAPTCHA;
         captcha.activateSession ();
      }

      return null;
   }

   public String enterFinish () {
      destroyWorkflow ();

      if (state.getScore () == Result.MIN_SCORE) {
         state.resetTemporal ();

         return enterHighScores ();
      }

      final String name = state.getName ();

      if (name == null || name.isEmpty () ||
          StateKeeperBean.DEFAULT_NAME.equals (name))
      {
         view = View.ASK_NAME;

         return null;
      }

      return saveResult ();
   }

   public String enterError (final String message) {
      view = View.ERROR;

      if (message == null || message.isEmpty ()) {
         setErrorMessage (getGlobalMessage ("ui.app.error.internal"));
      }
      else {
         setErrorMessage (message);
      }
      return null;
   }

   public String submit () {
      return view.submit (this);
   }

   public String cancel () {
      return view.cancel (this);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @EJB
   private WorkflowServiceBean workflowService;

   @EJB
   private ResultServiceLocal resultService;

   @ManagedProperty (value = "#{captcha}")
   private CaptchaBean captcha;

   private Collection<Result> results;
   private StateKeeperBean    state;
   private View               view;

   private void reset () {
      results = Collections.emptyList ();
      view = View.HIGH_SCORES;
   }

   private String saveResult () {
      try {
         resultService.create
            (Result
                .newBuilder ()
                .setName (state.getName ())
                .setDate (new Date ())
                .setScore (state.getScore ())
                .setLevel (state.getLevel ())
                .build ());
      }
      catch (final Throwable e) {
         LOG.log (Level.SEVERE, e.getMessage (), e);

         return enterError (e.getMessage ());
      }

      state.resetTemporal ();

      return enterHighScores ();
   }

   private void destroyWorkflow () {
      final FacesContext fctx = FacesContext.getCurrentInstance ();
      final ExternalContext ectx = fctx.getExternalContext ();
      final HttpSession session = (HttpSession) ectx.getSession (false);

      workflowService.destroy (session.getId ());
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Logger LOG =
      Logger.getLogger (GameControllerBean.class.getSimpleName ());

   private static final String VIEW_ID = "index";

   private enum View {
      /////////////////////////////////////////////////////////////////////////
      //  Public static section                                              //
      /////////////////////////////////////////////////////////////////////////

      HIGH_SCORES ("/WEB-INF/view/high_scores.xhtml")
         {
            @Override
            public String submit (final GameControllerBean bean) {
               return null;
            }

            @Override
            public String cancel (final GameControllerBean bean) {
               return null;
            }
         },
      SETTINGS ("/WEB-INF/view/settings.xhtml")
         {
            @Override
            public String submit (final GameControllerBean bean) {
               return null;
            }

            @Override
            public String cancel (final GameControllerBean bean) {
               return null;
            }
         },
      ASK_CAPTCHA ("/WEB-INF/view/ask_captcha.xhtml")
         {
            @Override
            public String submit (final GameControllerBean bean) {
               if (bean.captcha.commitAnswer ()) {
                  bean.view = View.PLAY;
               }
               else {
                  bean.view = View.ASK_CAPTCHA;
                  setErrorMessage
                     (bean.getGlobalMessage ("ui.ask_captcha.wrong"));
               }
               return null;
            }

            @Override
            public String cancel (final GameControllerBean bean) {
               return null;
            }
         },
      PLAY ("/WEB-INF/view/play.xhtml")
         {
            @Override
            public String submit (final GameControllerBean bean) {
               return null;
            }

            @Override
            public String cancel (final GameControllerBean bean) {
               return null;
            }
         },
      ASK_NAME ("/WEB-INF/view/ask_name.xhtml")
         {
            @Override
            public String submit (final GameControllerBean bean) {
               return bean.saveResult ();
            }

            @Override
            public String cancel (final GameControllerBean bean) {
               bean.state.setName (StateKeeperBean.DEFAULT_NAME);
               return bean.saveResult ();
            }
         },
      ERROR ("/WEB-INF/view/error.xhtml")
         {
            @Override
            public String submit (final GameControllerBean bean) {
               return this.cancel (bean);
            }

            @Override
            public String cancel (final GameControllerBean bean) {
               return VIEW_ID + '?' + FACES_REDIRECT + '&' + FACES_PARAMS;
            }
         };

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public abstract String submit (final GameControllerBean bean);

      public abstract String cancel (final GameControllerBean bean);

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      private View (final String templatePath) {
         this.templatePath = templatePath;
      }

      private final String templatePath;
   }

   private static final long serialVersionUID = 201908220831L;
}
