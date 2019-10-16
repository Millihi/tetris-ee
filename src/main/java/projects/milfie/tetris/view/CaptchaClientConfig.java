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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CaptchaClientConfig {

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public CaptchaClientConfig () {
      this (DEFAULT_CONFIG_FILE);
   }

   public CaptchaClientConfig (final String configFile) {
      if (configFile == null || configFile.isEmpty ()) {
         throw new IllegalArgumentException ("Given config file is empty.");
      }

      this.configFile = configFile;
      this.loadFromFile ();
   }

   public String getConfigFile () {
      return configFile;
   }

   public boolean isClientSecured () {
      return clientSecured;
   }

   public String getClientHost () {
      return clientHost;
   }

   public String getClientPort () {
      return clientPort;
   }

   public String getClientEndpoint () {
      return clientEndpoint;
   }

   public String getClientUsername () {
      return clientUsername;
   }

   public String getClientPassword () {
      return clientPassword;
   }

   public boolean isConsumerSecured () {
      return consumerSecured;
   }

   public String getConsumerHost () {
      return consumerHost;
   }

   public String getConsumerPort () {
      return consumerPort;
   }

   public String getConsumerEndpoint () {
      return consumerEndpoint;
   }

   public String getClientServiceUrl () {
      return
         (clientSecured ? "https" : "http") + "://" +
         clientHost + ':' + clientPort + clientEndpoint;
   }

   public String getConsumerServiceUrl () {
      return
         (consumerSecured ? "https" : "http") + "://" +
         consumerHost + ':' + consumerPort + consumerEndpoint;
   }

   public void reset () {
      loadDefaults ();
   }

   public void reload () {
      loadFromFile ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final String configFile;

   private boolean clientSecured;
   private String  clientHost;
   private String  clientPort;
   private String  clientEndpoint;
   private String  clientUsername;
   private String  clientPassword;
   private boolean consumerSecured;
   private String  consumerHost;
   private String  consumerPort;
   private String  consumerEndpoint;

   private void loadDefaults () {
      for (final Schema key : Schema.values ()) {
         final Object value = key.converter.convert (key.defaultValue);
         try {
            key.field.set (this, value);
         }
         catch (final IllegalAccessException e) {
            throw (IllegalStateException)
               new IllegalStateException ().initCause (e);
         }
      }
   }

   private void loadFromFile () {
      final Properties p = loadProperties (configFile);

      for (final Schema key : Schema.values ()) {
         final Object value = key.converter.convert
            (System.getProperty
               (key.property, p.getProperty
                  (key.property, key.defaultValue)));
         try {
            key.field.set (this, value);
         }
         catch (final IllegalAccessException e) {
            throw (IllegalStateException)
               new IllegalStateException ().initCause (e);
         }
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String
      DEFAULT_CONFIG_FILE = "captcha-client-config.xml";

   private enum Schema {
      CLIENT_SECURED
         ("projects.milfie.captcha.client.secured",
          Converter.BOOLEAN, "false"),
      CLIENT_HOST
         ("projects.milfie.captcha.client.host",
          Converter.STRING, "127.0.0.1"),
      CLIENT_PORT
         ("projects.milfie.captcha.client.port",
          Converter.STRING, "8080"),
      CLIENT_ENDPOINT
         ("projects.milfie.captcha.client.endpoint",
          Converter.STRING, "/captcha/CaptchaClientService"),
      CLIENT_USERNAME
         ("projects.milfie.captcha.client.username",
          Converter.STRING, ""),
      CLIENT_PASSWORD
         ("projects.milfie.captcha.client.password",
          Converter.STRING, ""),
      CONSUMER_SECURED
         ("projects.milfie.captcha.consumer.secured",
          Converter.BOOLEAN, "false"),
      CONSUMER_HOST
         ("projects.milfie.captcha.consumer.host",
          Converter.STRING, "127.0.0.1"),
      CONSUMER_PORT
         ("projects.milfie.captcha.consumer.port",
          Converter.STRING, "8080"),
      CONSUMER_ENDPOINT
         ("projects.milfie.captcha.consumer.endpoint",
          Converter.STRING, "/captcha/puzzle");

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      private final String    property;
      private final Field     field;
      private final Converter converter;
      private final String    defaultValue;

      private Schema (final String property,
                      final Converter converter,
                      final String defaultValue)
      {
         this.property = property;
         this.converter = converter;
         this.defaultValue = defaultValue;
         try {
            this.field = CaptchaClientConfig.class
               .getDeclaredField (Schema.getFieldName (this));
         }
         catch (final NoSuchFieldException e) {
            throw new IllegalStateException (e.getMessage (), e);
         }
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private static section                                             //
      /////////////////////////////////////////////////////////////////////////

      private static String getFieldName (final Schema schema) {
         if (schema == null) {
            throw new IllegalArgumentException ("Given schema is null.");
         }

         final String name = schema.name ();
         final int length = name.length ();
         final StringBuilder canonName = new StringBuilder ();

         int idx = 0;
         boolean meetUnderscore = false;

         if (name.charAt (idx) == '_') {
            canonName.append (name.charAt (idx++));
         }

         while (idx < length) {
            final char ch = name.charAt (idx);

            if (ch == '_') {
               meetUnderscore = true;
            }
            else if (Character.isLetter (ch)) {
               if (meetUnderscore) {
                  meetUnderscore = false;
                  canonName.append (Character.toUpperCase (ch));
               }
               else {
                  canonName.append (Character.toLowerCase (ch));
               }
            }
            ++idx;
         }

         return canonName.toString ();
      }
   }

   private enum Converter {
      BOOLEAN
         {
            @Override
            public Boolean convert (final String value) {
               return Boolean.parseBoolean (value);
            }
         },
      INTEGER
         {
            @Override
            public Integer convert (final String value) {
               return Integer.parseInt (value);
            }
         },
      STRING
         {
            @Override
            public String convert (final String value) {
               return value;
            }
         },
      STRING_ARRAY
         {
            @Override
            public String[] convert (final String value) {
               if (value == null || value.isEmpty ()) {
                  return EMPTY_STRING_ARRAY;
               }
               return STRING_ARRAY_PATTERN.split (value);
            }
         };

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public abstract Object convert (final String value);

      /////////////////////////////////////////////////////////////////////////
      //  Private static section                                             //
      /////////////////////////////////////////////////////////////////////////

      private static final Pattern
         STRING_ARRAY_PATTERN = Pattern.compile ("\\s*,+\\s*");
      private static final String[]
         EMPTY_STRING_ARRAY   = new String[0];
   }

   private static Properties loadProperties (final String configFile) {
      final Properties properties = new Properties ();

      final ClassLoader classLoader = Thread
         .currentThread ()
         .getContextClassLoader ();

      if (classLoader == null) {
         return properties;
      }

      final InputStream configStream =
         classLoader.getResourceAsStream (configFile);

      if (configStream == null) {
         return properties;
      }

      try {
         properties.loadFromXML (configStream);
      }
      catch (final IOException ignored1) {
         try {
            configStream.close ();
         }
         catch (final IOException ignored2) {
            return properties;
         }
         return properties;
      }

      return properties;
   }
}
