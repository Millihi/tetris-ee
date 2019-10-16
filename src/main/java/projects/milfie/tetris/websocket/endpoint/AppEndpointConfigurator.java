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

package projects.milfie.tetris.websocket.endpoint;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class AppEndpointConfigurator
   extends ServerEndpointConfig.Configurator
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static <T> T getObject (final EndpointConfig config,
                                  final Class<T> objectClass)
   {
      return objectClass.cast
         (config.getUserProperties ().get (objectClass.getName ()));
   }

   public static <T> T popObject (final EndpointConfig config,
                                  final Class<T> objectClass)
   {
      return objectClass.cast
         (config.getUserProperties ().remove (objectClass.getName ()));
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public void modifyHandshake (final ServerEndpointConfig config,
                                final HandshakeRequest request,
                                final HandshakeResponse response)
   {
      config.getUserProperties ().put
         (HttpSession.class.getName (), request.getHttpSession ());
   }
}
