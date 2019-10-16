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

package projects.milfie.tetris.i18n;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public abstract class AbstractUTF8MessageBundle
   extends ResourceBundle
{
   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   protected AbstractUTF8MessageBundle (final String bundleName) {
      if (bundleName == null || bundleName.isEmpty ()) {
         throw new IllegalArgumentException ("Given bundle name is empty.");
      }

      final Locale locale = getFacesLocale ();
      final ResourceBundle bundle =
         ResourceBundle.getBundle (bundleName, locale, UTF8_CONTROL);

      this.setParent (bundle);
   }

   protected AbstractUTF8MessageBundle (final String bundleName,
                                        final Locale locale)
   {
      if (bundleName == null || bundleName.isEmpty ()) {
         throw new IllegalArgumentException ("Given bundle name is empty.");
      }
      if (locale == null) {
         throw new IllegalArgumentException ("Requested locale is null.");
      }

      final ResourceBundle bundle =
         ResourceBundle.getBundle (bundleName, locale, UTF8_CONTROL);

      this.setParent (bundle);
   }

   @Override
   protected Object handleGetObject (final String key) {
      return parent.getObject (key);
   }

   @Override
   public Enumeration<String> getKeys () {
      return parent.getKeys ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   protected static final String  CHARSET      = "UTF-8";
   protected static final Control UTF8_CONTROL = new UTF8Control ();

   private static final Logger LOG =
      Logger.getLogger (AbstractUTF8MessageBundle.class.getSimpleName ());

   protected static class UTF8Control
      extends Control
   {
      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      @Override
      public List<String> getFormats (final String baseName) {
         if (baseName == null) {
            throw new NullPointerException ("Given base name is null.");
         }
         return BUNDLE_FORMAT_LIST;
      }

      @Override
      public ResourceBundle newBundle (final String baseName,
                                       final Locale locale,
                                       final String format,
                                       final ClassLoader loader,
                                       final boolean reload)
         throws IOException
      {
         if (baseName == null || locale == null ||
             format == null || loader == null)
         {
            throw new NullPointerException ("One or more arguments are null");
         }

         final BundleFormat bundleFormat = BundleFormat.getInstance (format);

         if (bundleFormat == null) {
            throw new IllegalArgumentException ("Unknown format: " + format);
         }

         final String resourceName =
            bundleFormat.getResourceBundleName (this, baseName, locale);

         if (resourceName == null) {
            return null;
         }

         return createBundle (resourceName, bundleFormat, loader, reload);
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private static section                                             //
      /////////////////////////////////////////////////////////////////////////

      private enum BundleFormat {
         //////////////////////////////////////////////////////////////////////
         //  Public static section                                           //
         //////////////////////////////////////////////////////////////////////

         PROPERTY ("properties")
            {
               @Override
               public String getResourceBundleName (final Control control,
                                                    final String baseName,
                                                    final Locale locale)
               {
                  return
                     control.toResourceName
                        (control.toBundleName (baseName, locale), extension);
               }

               @Override
               public ResourceBundle createBundle (final InputStream stream,
                                                   final String charset)
                  throws IOException
               {
                  return
                     new PropertyResourceBundle
                        (new InputStreamReader (stream, charset));
               }
            },
         XML ("xml")
            {
               @Override
               public String getResourceBundleName (final Control control,
                                                    final String baseName,
                                                    final Locale locale)
               {
                  return
                     control.toResourceName
                        (control.toBundleName (baseName, locale), extension);
               }

               @Override
               public ResourceBundle createBundle (final InputStream stream,
                                                   final String charset)
                  throws IOException
               {
                  return new XMLPropertyResourceBundle (stream);
               }
            };

         public static BundleFormat getInstance (final String extension) {
            return EXTENSION_TYPE_MAP.get (extension);
         }

         //////////////////////////////////////////////////////////////////////
         //  Public section                                                  //
         //////////////////////////////////////////////////////////////////////

         public final String extension;

         public abstract String getResourceBundleName (final Control control,
                                                       final String baseName,
                                                       final Locale locale);

         public abstract ResourceBundle createBundle (final InputStream stream,
                                                      final String charset)
            throws IOException;

         public String getExtension () {
            return extension;
         }

         //////////////////////////////////////////////////////////////////////
         //  Private section                                                 //
         //////////////////////////////////////////////////////////////////////

         private BundleFormat (final String extension) {
            this.extension = extension;
         }

         //////////////////////////////////////////////////////////////////////
         //  Private static section                                          //
         //////////////////////////////////////////////////////////////////////

         private static final Map<String, BundleFormat>
            EXTENSION_TYPE_MAP = new HashMap<> ();

         static {
            for (final BundleFormat type : BundleFormat.values ()) {
               EXTENSION_TYPE_MAP.put (type.extension, type);
            }
         }
      }

      public static final List<String> BUNDLE_FORMAT_LIST = Collections
         .unmodifiableList
            (Arrays
                .stream (BundleFormat.values ())
                .map (BundleFormat::getExtension)
                .collect (Collectors.toList ()));

      private static ResourceBundle createBundle (final String resourceName,
                                                  final BundleFormat format,
                                                  final ClassLoader loader,
                                                  final boolean reload)
         throws IOException
      {
         final InputStream stream = getStream (resourceName, loader, reload);

         if (stream == null) {
            return null;
         }

         try {
            return
               format.createBundle
                  (new BufferedInputStream (stream), CHARSET);
         }
         finally {
            stream.close ();
         }
      }

      private static InputStream getStream (final String resourceName,
                                            final ClassLoader loader,
                                            final boolean reload)
         throws IOException
      {
         if (!reload) {
            return loader.getResourceAsStream (resourceName);
         }

         final URL url = loader.getResource (resourceName);

         if (url == null) {
            return null;
         }

         final URLConnection connection = url.openConnection ();

         if (connection == null) {
            return null;
         }

         connection.setUseCaches (false);
         return connection.getInputStream ();
      }
   }

   protected static Locale getFacesLocale () {
      final FacesContext fctx = FacesContext.getCurrentInstance ();

      if (fctx == null) {
         LOG.warning
            ("No faces instance found, falling back to system locale.");
         return Locale.getDefault ();
      }

      final UIViewRoot viewRoot = fctx.getViewRoot ();

      if (viewRoot == null) {
         LOG.warning
            ("No view root found, falling back to system locale.");
         return Locale.getDefault ();
      }

      return viewRoot.getLocale ();
   }
}
