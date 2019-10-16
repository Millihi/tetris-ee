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

import javax.faces.application.Application;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

abstract class AbstractController {

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   protected static final String FACES_REDIRECT = "faces-redirect=true";
   protected static final String FACES_PARAMS   = "includeViewParams=true";

   protected static Flash getFlash () {
      return
         FacesContext
            .getCurrentInstance ()
            .getExternalContext ()
            .getFlash ();
   }

   protected static void setErrorMessage (final String summary) {
      setErrorMessage (null, summary);
   }

   protected static void setErrorMessage (final String clientId,
                                          final String summary)
   {
      FacesContext.getCurrentInstance ().addMessage
         (clientId, new FacesMessage
            (FacesMessage.SEVERITY_ERROR, summary, null));
   }

   protected static void performFacesRedirect (final String target) {
      final FacesContext fctx = FacesContext.getCurrentInstance ();
      final Application app = fctx.getApplication ();
      final ConfigurableNavigationHandler handler =
         (ConfigurableNavigationHandler) app.getNavigationHandler ();

      handler.performNavigation (target);
   }
}
