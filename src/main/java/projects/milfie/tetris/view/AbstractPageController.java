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

import projects.milfie.tetris.i18n.GlobalMessageBundle;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.faces.bean.ManagedProperty;

abstract class AbstractPageController
   extends AbstractController
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public abstract void postConstructInit ();

   public void resourceInit () {
      globalMessages =
         new GlobalMessageBundle (localeKeeper.getLocale ());
   }

   public ResourceBundle getGlobalMessages () {
      return globalMessages;
   }

   public String getGlobalMessage (final String key, final String... args) {
      return getMessage (globalMessages, key, args);
   }

   public ResourceBundle getModuleMessages () {
      return moduleMessages;
   }

   public String getModuleMessage (final String key, final String... args) {
      return getMessage (moduleMessages, key, args);
   }

   public LocaleKeeperBean getLocaleKeeper () {
      return localeKeeper;
   }

   public void setLocaleKeeper (final LocaleKeeperBean localeKeeper) {
      this.localeKeeper = localeKeeper;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   protected transient ResourceBundle globalMessages;
   protected transient ResourceBundle moduleMessages;

   @ManagedProperty (value = "#{localeKeeper}")
   protected LocaleKeeperBean localeKeeper;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   protected static String getMessage (final ResourceBundle bundle,
                                       final String key,
                                       final String... args)
   {
      final String rawString = bundle.getString (key);

      if (args.length > 0) {
         return MessageFormat.format (rawString, (Object[]) args);
      }

      return rawString;
   }
}
