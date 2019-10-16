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
//  Model
function Model (di_rpc) {

   var
      self = this,
      pv_endpoint = null,
      pv_keyboard = null,
      pv_canvas = null,
      pv_control = null,
      pv_state = null,
      pv_locked = false;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      if (typeof di_rpc !== "object" || di_rpc.name !== "Rpc") {
         throw new Error ("Invalid RPC given.");
      }
      return self;
   }

   self.name = "Model";

   self.setEndpoint = function (endpoint) {
      if (typeof endpoint !== "object" || endpoint.name !== "Endpoint") {
         throw new Error ("Invalid endpoint.");
      }
      pv_endpoint = endpoint;
   };

   self.setKeyboard = function (keyboard) {
      if (typeof keyboard !== "object" || keyboard.name !== "Keyboard") {
         throw new Error ("Invalid keyboard.");
      }
      pv_keyboard = keyboard;
   };

   self.setCanvas = function (canvas) {
      if (typeof canvas !== "object" || canvas.name !== "Canvas") {
         throw new Error ("Invalid canvas.");
      }
      pv_canvas = canvas;
   };

   self.setControl = function (control) {
      if (typeof control !== "object" || control.name !== "Control") {
         throw new Error ("Invalid control.");
      }
      pv_control = control;
   };

   self.setState = function (state) {
      if (typeof state !== "object" || state.name !== "State") {
         throw new Error ("Invalid state.");
      }
      pv_state = state;
   };

   self.run = function () {
      setTimeout (function () {
         pv_endpoint.run ();
      }, 1);
   };

   self.isLocked = function () {
      return pv_locked;
   };

   self.lock = function (message) {
      if (pv_locked) {
         pv_control.setCurtainMessage (message);
      }
      else {
         pv_keyboard.setBlocked (true);
         pv_control.showCurtain (message);
         pv_locked = true;
      }
   };

   self.unlock = function () {
      if (pv_locked) {
         pv_keyboard.setBlocked (false);
         pv_control.hideCurtain ();
         pv_locked = false;
      }
   };

   self.acceptCommand = function (commandName) {
      if (typeof commandName !== "string" || commandName.length <= 0) {
         throw new Error ("Invalid command name given.");
      }

      if (!pv_endpoint.isRunned ()) {
         console.warn ("Accepting command while endpoint is inactive.");
         return;
      }

      var args = [];

      for (var i = 1, il = arguments.length; i < il; ++i) {
         args.push (arguments[i]);
      }

      try {
         pv_endpoint.send (di_rpc.CommandFactory.create (commandName, args));
      }
      catch (e) {
         console.error (e.name + ": " + e.message + "\n\n" + e.stacktrace);
      }
   };

   self.dispatchCommand = function (command) {
      if (typeof command !== "object" || command.name !== "Command") {
         throw new Error ("Invalid Command given.");
      }

      var commandName = command.spec.NAME;

      if (commandName === "ENTER_RUN") {
         if (pv_locked) {
            self.unlock ();
         }
         else {
            pv_control.hideCurtain ();
         }
      }
      else if (commandName === "ENTER_PAUSE") {
         pv_control.showCurtain (MESSAGES['ui.model.paused']);
      }
      else if (commandName === "ENTER_STOP") {
         pv_control.enterWin ();
      }
      else if (commandName === "ENTER_ERROR") {
         pv_control.enterError ();
      }
      else if (commandName === "UPDATE_COMBO") {
         pv_state.setCombo (command.args[0]);
      }
      else if (commandName === "UPDATE_SCORE") {
         pv_state.setScore (command.args[0]);
      }
      else if (commandName === "UPDATE_LEVEL") {
         pv_state.setLevel (command.args[0]);
      }
      else if (commandName === "UPDATE_BRICK") {
         pv_state.setBrick (command.args[0]);
      }
      else if (commandName === "UPDATE_REGION") {
         pv_canvas.updateRegion (
            command.args[0], command.args[1], command.args[2]);
      }
      else if (commandName === "WIN") {
         pv_control.enterWin ();
      }
      else if (commandName === "LOOSE") {
         pv_control.enterLoose ();
      }
      else {
         console.warn (self.name + ": Unhandled command " + commandName);
      }
   };

   return im_initialize ();
}
