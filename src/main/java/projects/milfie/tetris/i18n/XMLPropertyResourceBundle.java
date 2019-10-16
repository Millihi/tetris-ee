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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * As {@link java.util.PropertyResourceBundle} but supports XML properties.
 *
 * @see java.util.PropertyResourceBundle
 */
public class XMLPropertyResourceBundle
   extends ResourceBundle
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   /**
    * Creates a property resource bundle from an {@link java.io.InputStream
    * InputStream}. The property file read with this constructor must be
    * a valid XML document and encoded in UTF-8 or UTF-16.
    *
    * @param stream
    *    an InputStream that represents a property file to read
    *    from.
    *
    * @throws IOException
    *    if an I/O error occurs
    * @throws NullPointerException
    *    if <code>stream</code> is null
    * @throws InvalidPropertiesFormatException
    *    if {@code stream} contains a malformed XML document.
    * @throws java.io.UnsupportedEncodingException
    *    if the document's encoding declaration contains
    *    an unsupported encoding.
    * @see java.util.Properties#loadFromXML(java.io.InputStream)
    */
   @SuppressWarnings ({"unchecked", "rawtypes"})
   public XMLPropertyResourceBundle (final InputStream stream)
      throws IOException
   {
      if (stream == null) {
         throw new IllegalArgumentException ("Given stream is null");
      }
      this.properties = new Properties ();
      this.properties.loadFromXML (stream);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object handleGetObject (final String key) {
      if (key == null) {
         throw new NullPointerException ("Given key is null.");
      }
      return properties.getProperty (key);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Enumeration<String> getKeys () {
      return Collections.enumeration (properties.stringPropertyNames ());
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Properties properties;
}
