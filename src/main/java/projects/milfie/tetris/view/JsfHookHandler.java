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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class JsfHookHandler
   extends ViewHandlerWrapper
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public JsfHookHandler (final ViewHandler wrapped) {
      this.wrapped = wrapped;
   }

   @Override
   public ViewHandler getWrapped () {
      return wrapped;
   }

   /**
    * <p>Preserves view parameters in FORM's action URLs.</p>
    */
   @Override
   public String getActionURL (final FacesContext context,
                               final String viewId)
   {
      if (isDelegated (context)) {
         setDelegated (context, false);

         return wrapped.getActionURL (context, viewId);
      }

      if (viewId == null || viewId.isEmpty ()) {
         throw new IllegalArgumentException ("Given viewId is empty.");
      }
      if (viewId.charAt (0) != '/') {
         throw new IllegalArgumentException ("Given viewId is invalid.");
      }

      final HttpServletRequest request = (HttpServletRequest)
         context.getExternalContext ().getRequest ();

      if (hasSameViewId (request, viewId)) {
         setDelegated (context, true);
         return
            wrapped.getRedirectURL
               (context, viewId, Collections.emptyMap (), true);
      }

      return wrapped.getActionURL (context, viewId);
   }

   /**
    * Avoid default delegation to overriden
    * {@link javax.faces.application.ViewHandler#getActionURL}
    */
   @Override
   public String getRedirectURL (
      final FacesContext context,
      final String viewId,
      final Map<String, List<String>> parameters,
      final boolean includeViewParams)
   {
      setDelegated (context, true);
      return
         wrapped.getRedirectURL
            (context, viewId, parameters, includeViewParams);
   }

   /**
    * Avoid default delegation to overriden
    * {@link javax.faces.application.ViewHandler#getActionURL}
    */
   @Override
   public String getBookmarkableURL (
      final FacesContext context,
      final String viewId,
      final Map<String, List<String>> parameters,
      final boolean includeViewParams)
   {
      setDelegated (context, true);
      return
         wrapped.getBookmarkableURL
            (context, viewId, parameters, includeViewParams);
   }

   /**
    * {@link javax.faces.application.ViewExpiredException}. This happens only
    * when we try to logout from timed out pages.
    */
   @Override
   public UIViewRoot restoreView (final FacesContext context,
                                  final String viewId)
   {
      final UIViewRoot viewRoot = wrapped.restoreView (context, viewId);

      if (viewRoot == null) {
         return wrapped.createView (context, viewId);
      }

      return viewRoot;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final ViewHandler wrapped;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String ATTR_KEY_IS_DELEGATE =
      "projects.milfie.jsf.hook.isDelegated";

   private static boolean hasSameViewId (final HttpServletRequest request,
                                         final String viewId)
   {
      return
         request.getRequestURI ().startsWith
            (viewId, request.getContextPath ().length ());
   }

   private static String getViewID (final HttpServletRequest request) {
      return
         request.getRequestURI ().substring
            (request.getContextPath ().length ());
   }

   private static boolean isDelegated (final FacesContext context) {
      return
         Boolean.parseBoolean
            ((String) context.getAttributes ().get (ATTR_KEY_IS_DELEGATE));
   }

   private static void setDelegated (final FacesContext context,
                                     final boolean delegate)
   {
      context.getAttributes ().put
         (ATTR_KEY_IS_DELEGATE, Boolean.toString (delegate));
   }
}
