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

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@SessionScoped
@ManagedBean (name = "localeKeeper")
public class LocaleKeeperBean
   extends AbstractController
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @PostConstruct
   public void postConstructInit () {
      this.locale = getFacesLocale ();
   }

   public Collection<Locale> getLocales () {
      return getFacesLocales ();
   }

   public Locale getLocale () {
      return locale;
   }

   public void setLocale (final Locale locale) {
      if (locale == null) {
         throw new IllegalArgumentException ("Given locale is null.");
      }

      setLocalePrivate (locale);
   }

   public String getLanguage () {
      return locale.getLanguage ();
   }

   public void setLanguage (final String language) {
      if (language == null || language.isEmpty ()) {
         throw new IllegalArgumentException ("Given language is empty.");
      }

      setLocalePrivate (new Locale (language));
   }

   public void submit () {
      final String viewId =
         FacesContext.getCurrentInstance ().getViewRoot ().getViewId ();

      performFacesRedirect
         (viewId + '?' + FACES_REDIRECT + '&' + FACES_PARAMS);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private Locale locale;

   private void setLocalePrivate (final Locale locale) {
      this.locale = locale;
      setFacesLocale (locale);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Logger LOG =
      Logger.getLogger (LocaleKeeperBean.class.getSimpleName ());

   private static final long serialVersionUID = 201908220853L;

   private static Collection<Locale> getFacesLocales () {
      final FacesContext fctx = FacesContext.getCurrentInstance ();

      if (fctx == null) {
         LOG.warning
            ("No faces instance found, return empty collection.");
         return Collections.emptyList ();
      }

      final Application application = fctx.getApplication ();

      if (application == null) {
         LOG.warning
            ("No application instance found, return empty collection.");
         return Collections.emptyList ();
      }

      final List<Locale> locales = new ArrayList<> ();
      final Iterator<Locale> supported = application.getSupportedLocales ();

      while (supported.hasNext ()) {
         locales.add (supported.next ());
      }

      return locales;
   }

   private static Locale getFacesLocale () {
      final FacesContext fctx = FacesContext.getCurrentInstance ();

      if (fctx == null) {
         LOG.warning
            ("No faces instance found, fallback to system locale.");
         return Locale.getDefault ();
      }

      final Application application = fctx.getApplication ();

      if (application == null) {
         LOG.warning
            ("No application instance found, fallback to system locale.");
         return Locale.getDefault ();
      }

      Locale defaultLocale = application.getDefaultLocale ();

      if (defaultLocale == null) {
         LOG.warning
            ("No default locale specified in faces-config, " +
             "use system locale as default locale.");
         defaultLocale = Locale.getDefault ();
      }

      final ExternalContext ectx = fctx.getExternalContext ();

      if (ectx == null) {
         LOG.warning
            ("No external context found, fallback to system locale.");
         return defaultLocale;
      }

      final Iterator<Locale> requested = ectx.getRequestLocales ();
      Locale suitableLocale = null;

      while (requested.hasNext ()) {
         final Locale requestedLocale = requested.next ();
         final Iterator<Locale> supported = application.getSupportedLocales ();

         while (supported.hasNext ()) {
            final Locale supportedLocale = supported.next ();

            if (requestedLocale.equals (supportedLocale)) {
               return supportedLocale;
            }
            if (suitableLocale == null &&
                requestedLocale.getLanguage ().equals
                   (supportedLocale.getLanguage ()))
            {
               suitableLocale = supportedLocale;
            }
         }
      }

      if (suitableLocale != null) {
         LOG.warning
            ("No exact locale found, choose the most suitable locale.");
         return suitableLocale;
      }

      LOG.warning
         ("No suitable locale found, fallback to default locale.");
      return defaultLocale;
   }

   private static void setFacesLocale (final Locale locale) {
      final FacesContext fctx = FacesContext.getCurrentInstance ();

      if (fctx == null) {
         LOG.warning ("Unable to set locale: no faces instance found.");
         return;
      }

      final UIViewRoot viewRoot = fctx.getViewRoot ();

      if (viewRoot == null) {
         LOG.warning ("Unable to set locale: no view root found.");
         return;
      }

      viewRoot.setLocale (locale);
   }
}
