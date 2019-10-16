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

import projects.milfie.captcha.wsdl.CaptchaClientService;
import projects.milfie.captcha.wsdl.CaptchaClientService_Service;
import projects.milfie.captcha.wsdl.ClientNotFoundException_Exception;
import projects.milfie.captcha.wsdl.ProfileNotFoundException_Exception;

import java.io.Serializable;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;

@SessionScoped
@ManagedBean (name = "captcha")
public class CaptchaBean
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public long getId () {
      return id;
   }

   public String getImageURL () {
      return config.getConsumerServiceUrl () + '/' + id + ".gif";
   }

   public String getName () {
      return name;
   }

   public void setName (final String name) {
      if (name == null || name.isEmpty ()) {
         throw new IllegalArgumentException ("Given name is empty.");
      }
      this.name = name;
   }

   public String getAnswer () {
      return answer;
   }

   public void setAnswer (final String answer) {
      this.answer = answer;
   }

   public boolean isLastPuzzleSolved () {
      return setUp ().isLastPuzzleSolved (name);
   }

   public boolean commitAnswer () {
      return setUp ().commitConsumerAnswer (name, answer);
   }

   public void activateSession ()
      throws ClientNotFoundException_Exception,
             ProfileNotFoundException_Exception
   {
      id = setUp ().activateConsumer (name, DEFAULT_PROFILE);
   }

   public void deactivateSession () {
      setUp ().deactivateConsumer (name);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @WebServiceRef
      (type = CaptchaClientService.class,
       value = CaptchaClientService_Service.class)
   private transient CaptchaClientService captchaService;

   @Inject
   private transient CaptchaClientConfig config;

   @NotNull
   @Pattern (regexp = "^[a-zA-Z0-9]+$")
   private String answer;

   private String name;
   private long   id;

   private CaptchaClientService setUp () {
      final BindingProvider provider = (BindingProvider) captchaService;
      final Map<String, Object> rctx = provider.getRequestContext ();

      rctx.put
         (BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
          config.getClientServiceUrl ());
      rctx.put
         (BindingProvider.USERNAME_PROPERTY,
          config.getClientUsername ());
      rctx.put
         (BindingProvider.PASSWORD_PROPERTY,
          config.getClientPassword ());

      return captchaService;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String DEFAULT_PROFILE = "Default";

   private static final long serialVersionUID = 201909150224L;
}
