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

package projects.milfie.tetris.websocket.rpc;

import java.util.Collections;
import javax.json.*;

public class JSONSpecGenerator {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final String APP_NAME = "WS_RPC_EE";

   public static JsonObject generate () {
      final JsonBuilderFactory jFactory = Json
         .createBuilderFactory (Collections.<String, Object>emptyMap ());
      final JsonObjectBuilder jSpec = jFactory.createObjectBuilder ();

      jSpec.add (APP_KEY, APP_NAME);
      jSpec.add (VERSION_KEY, EnvelopeSpec.VERSION);
      jSpec.add (PACKET_ID_KEY, EnvelopeSpec.PACKET_ID);
      jSpec.add (DIRECTION_KEY, generateDirectionSpec (jFactory));
      jSpec.add (TYPE_KEY, generateTypeSpec (jFactory));
      jSpec.add (COMMAND_KEY, generateCommandSpec (jFactory));

      return jSpec.build ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String APP_KEY        = "App";
   private static final String VERSION_KEY    = "Version";
   private static final String DIRECTION_KEY  = "DIRECTION";
   private static final String TYPE_KEY       = "TYPE";
   private static final String COMMAND_KEY    = "COMMAND";
   private static final String ID_KEY         = "ID";
   private static final String NAME_KEY       = "NAME";
   private static final String PARAMETERS_KEY = "PARAMETERS";
   private static final String PACKET_ID_KEY  = "PACKET_ID";

   private static JsonObjectBuilder generateDirectionSpec
      (final JsonBuilderFactory jFactory)
   {
      final JsonObjectBuilder jSpec = jFactory.createObjectBuilder ();

      for (final Direction value : Direction.values ()) {
         final String name = value.name ();
         final Integer ordinal = value.ordinal ();

         final JsonObjectBuilder jItem = jFactory.createObjectBuilder ();
         final JsonObjectBuilder jSelfRef = generateReference
            (jFactory, DIRECTION_KEY, name);

         jItem.add (ID_KEY, ordinal);
         jItem.add (NAME_KEY, name);

         jSpec.add (ordinal.toString (), jSelfRef);
         jSpec.add (name, jItem);
      }

      return jSpec;
   }

   private static JsonObjectBuilder generateCommandSpec
      (final JsonBuilderFactory jFactory)
   {
      final JsonObjectBuilder jSpec = jFactory.createObjectBuilder ();

      jSpec.add (PACKET_ID_KEY, CommandSpec.PACKET_ID);

      for (final CommandSpec command : CommandSpec.values ()) {
         final String name = command.name ();
         final Integer ordinal = command.ordinal ();

         final JsonObjectBuilder jItem = jFactory.createObjectBuilder ();
         final JsonArrayBuilder jParamArray = jFactory.createArrayBuilder ();

         final JsonObjectBuilder jSelfRef = generateReference
            (jFactory, COMMAND_KEY, name);
         final JsonObjectBuilder jDirectionRef = generateReference
            (jFactory, DIRECTION_KEY,
             command.getDirection ().opposite ().name ());

         for (final TypeSpec param : command.getTypeSpecs ()) {
            final JsonObjectBuilder jParamRef = generateReference
               (jFactory, TYPE_KEY, param.name ());
            jParamArray.add (jParamRef);
         }

         jItem.add (ID_KEY, ordinal);
         jItem.add (NAME_KEY, name);
         jItem.add (DIRECTION_KEY, jDirectionRef);
         jItem.add (PARAMETERS_KEY, jParamArray);

         jSpec.add (ordinal.toString (), jSelfRef);
         jSpec.add (name, jItem);
      }

      return jSpec;
   }

   private static JsonObjectBuilder generateTypeSpec
      (final JsonBuilderFactory jFactory)
   {
      final JsonObjectBuilder jSpec = jFactory.createObjectBuilder ();

      jSpec.add (PACKET_ID_KEY, TypeSpec.PACKET_ID);

      for (final TypeSpec typeSpec : TypeSpec.values ()) {
         final String name = typeSpec.name ();
         final Integer ordinal = typeSpec.ordinal ();

         final JsonObjectBuilder jItem = jFactory.createObjectBuilder ();
         final JsonObjectBuilder jSelfRef = generateReference
            (jFactory, TYPE_KEY, name);

         jItem.add (ID_KEY, ordinal);
         jItem.add (NAME_KEY, name);

         jSpec.add (ordinal.toString (), jSelfRef);
         jSpec.add (name, jItem);
      }

      return jSpec;
   }

   private static JsonObjectBuilder generateReference
      (final JsonBuilderFactory jFactory,
       final String... path)
   {
      final JsonObjectBuilder jSpec = jFactory.createObjectBuilder ();
      final JsonArrayBuilder jPathArray = jFactory.createArrayBuilder ();

      for (final String elem : path) {
         jPathArray.add (elem);
      }

      jSpec.add ("type", "REFERENCE");
      jSpec.add ("path", jPathArray);

      return jSpec;
   }
}
