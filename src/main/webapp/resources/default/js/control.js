///////////////////////////////////////////////////////////////////////////////
//  "THE CAKE-WARE LICENSE" (Revision 41):                                   //
//                                                                           //
//      Milfeulle <mail@milfie.uu.me> wrote this file. As long as you        //
//  retain this notice you can do whatever you want with this stuff. If we   //
//  meet some day, and you think this stuff is worth it, you can buy me      //
//  a cake in return.                                                        //
//                                                                           //
//      Milfie                                                               //
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  Control
function Control (di_model) {

   var INTERNAL_ACTION_FORM_ID = "#beanHiddenActions";
   var INTERNAL_ERROR_ACTION_ID = INTERNAL_ACTION_FORM_ID + "\\:enterError";
   var INTERNAL_FINISH_ACTION_ID = INTERNAL_ACTION_FORM_ID + "\\:enterFinish";

   var USER_ACTION_FORM_ID = "#beanUserActions";
   var USER_STOP_ACTION_ID = INTERNAL_ACTION_FORM_ID + "\\:stopAction";

   var
      self = this,
      pv_stopAction = null,
      pv_enterError = null,
      pv_enterFinish = null,
      pv_curtain = null,
      pv_message = null;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      if (typeof di_model !== "object" || di_model.name !== "Model") {
         throw new Error ("Invalid model given.");
      }
      if (!document || !jQuery) {
         throw new Error ("Either of document or jQuery not found.");
      }

      var beanUserActions = jQuery (USER_ACTION_FORM_ID);
      pv_stopAction = beanUserActions.children (USER_STOP_ACTION_ID);

      var beanHiddenActions = jQuery (INTERNAL_ACTION_FORM_ID);
      pv_enterError = beanHiddenActions.children (INTERNAL_ERROR_ACTION_ID);
      pv_enterFinish = beanHiddenActions.children (INTERNAL_FINISH_ACTION_ID);

      var screen = jQuery (".screen");
      pv_curtain = screen.find (".canvas-curtain");
      pv_message = pv_curtain.children (".canvas-curtain-message");

      pm_installEventHandlers ();

      return self;
   }

   self.name = "Control";

   self.isCurtainHidden = function () {
      return pv_curtain.is (":hidden");
   };

   self.showCurtain = function (message) {
      pv_message.text (message);
      pv_curtain.show ();
      pv_message.stop (true).fadeIn (
         {
            duration: "fast",
            easing: "linear"
         });
   };

   self.hideCurtain = function () {
      pv_message.stop (true).fadeOut (
         {
            duration: "fast",
            easing: "linear",
            complete: function () {
               pv_message.text ("");
               pv_curtain.hide ();
            }
         });
   };
   self.setCurtainMessage = function (message) {
      pv_message.text (message);
   };

   self.enterWin = function () {
      pv_enterFinish.click ();
   };

   self.enterLoose = function () {
      pv_enterFinish.click ();
   };

   self.enterError = function () {
      pv_enterError.click ();
   };

   self.finalize = function () {
      pm_uninstallEventHandlers ();
   };

   return im_initialize ();

   ////////////////////////////////////////////////////////////////////////////
   //  Private                                                               //
   ////////////////////////////////////////////////////////////////////////////

   function pm_installEventHandlers () {
      pv_stopAction.on ("click", pm_onStopAction);
   }

   function pm_uninstallEventHandlers () {
      pv_stopAction.off ("click", pm_onStopAction);
   }

   function pm_onStopAction (ev) {
      di_model.acceptCommand ("STOP");
      ev.preventDefault ();
   }
}
