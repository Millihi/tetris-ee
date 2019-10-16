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
//  Keyboard
//
//  Purpose:
//    Dispatches keypress events and gets rid of the default OS keypress
//    behaviour.
function Keyboard (di_model) {

   var POLLING_DELAY = 25; // [ms]

   var KEYS = {
      27: {
         name: "ESC",
         command: "PAUSE",
         state: new KeyState (POLLING_DELAY, 225) },
      32: {
         name: "SPACE",
         command: "PUT_DOWN",
         state: new KeyState (POLLING_DELAY, 225) },
      37: {
         name: "LEFT_ARROW",
         command: "MOVE_LEFT",
         state: new KeyState (POLLING_DELAY, 100) },
      38: {
         name: "UP_ARROW",
         command: "ROTATE_RIGHT",
         state: new KeyState (POLLING_DELAY, 225) },
      39: {
         name: "RIGHT_ARROW",
         command: "MOVE_RIGHT",
         state: new KeyState (POLLING_DELAY, 100) },
      40: {
         name: "DOWN_ARROW",
         command: "MOVE_DOWN",
         state: new KeyState (POLLING_DELAY, 100) }
   };

   var
      self = this,
      pv_blocked = false,
      pv_keypressAction = null;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      if (typeof di_model !== "object" || di_model.name !== "Model") {
         throw new Error ("Incorrect model given.");
      }
      if (!document || !jQuery) {
         throw new Error ("Either of document or jQuery not found.");
      }

      pv_keypressAction =
         new IntervalAction (pm_keypressAction, POLLING_DELAY);

      pm_installEventHandlers ();
      pv_keypressAction.run ();

      return self;
   }

   self.name = "Keyboard";

   self.isBlocked = function () {
      return pv_blocked;
   };

   self.setBlocked = function (v) {
      pv_blocked = !!v;

      if (pv_blocked) {
         pv_keypressAction.abort ();
         pm_resetKeyStates ();
      }
      else {
         pv_keypressAction.run ();
      }
   };

   self.finalize = function () {
      pv_keypressAction.abort ();
      pm_uninstallEventHandlers ();
   };

   return im_initialize ();

   ////////////////////////////////////////////////////////////////////////////
   //  Private                                                               //
   ////////////////////////////////////////////////////////////////////////////

   function pm_installEventHandlers () {
      jQuery (document)
         .on ("keyup", pm_onKeyUpDown)
         .on ("keydown", pm_onKeyUpDown);
   }

   function pm_uninstallEventHandlers () {
      jQuery (document)
         .off ("keyup", pm_onKeyUpDown)
         .off ("keydown", pm_onKeyUpDown);
   }

   function pm_onKeyUpDown (ev) {
      if (pv_blocked) {
         return true;
      }

      var key = KEYS[ev.which];

      if (typeof key !== "object") {
         return true;
      }

      if (ev.type === "keydown") {
         key.state.press ();
      }
      else {
         key.state.release ();
      }

      return false;
   }

   function pm_keypressAction () {
      if (pv_blocked) {
         pv_keypressAction.abort ();
         pm_resetKeyStates ();
         return;
      }

      for (var keyCode in KEYS) {
         if (KEYS.hasOwnProperty (keyCode)) {
            var key = KEYS[keyCode];

            if (key.state.update ()) {
               di_model.acceptCommand (key.command);
            }
         }
      }
   }

   function pm_resetKeyStates () {
      for (var keyCode in KEYS) {
         if (KEYS.hasOwnProperty (keyCode)) {
            KEYS[keyCode].state.reset ();
         }
      }
   }
}

///////////////////////////////////////////////////////////////////////////////
//  KeyState
//
//  Purpose:
//    Performs key's state change logic such as first press and autorepeat.
function KeyState (di_pollingDelay, di_repeatDelay) {

   var
      self = this,
      pv_repeatDelay = 0,
      pv_first = false,
      pv_last = false,
      pv_counter = 0;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      if (typeof di_pollingDelay !== "number" || di_pollingDelay <= 0) {
         throw new Error ("Given polling delay is invalid.");
      }
      if (typeof di_repeatDelay !== "number") {
         throw new Error ("Given repeat delay isn't a number.");
      }
      pv_repeatDelay = Math.max (0, (di_repeatDelay / di_pollingDelay) | 0);
      return self;
   }

   self.name = "KeyState";

   self.getPollingDelay = function () {
      return di_pollingDelay;
   };

   self.getRepeatDelay = function () {
      return di_repeatDelay;
   };

   self.update = function () {
      if (pv_first) {
         pv_first = false;
         pv_counter = 0;
         return true;
      }
      if (pv_repeatDelay <= 0) {
         return false;
      }
      if (pv_last) {
         ++pv_counter;

         if (pv_counter >= pv_repeatDelay) {
            pv_counter = 0;
            return true;
         }
      }
      return false;
   };

   self.isPressed = function () {
      return (pv_first || (pv_last && pv_counter >= pv_repeatDelay));
   };

   self.press = function () {
      if (!pv_last) {
         pv_last = (pv_first = true);
      }
      return self;
   };

   self.release = function () {
      pv_last = false;
      return self;
   };

   self.reset = function () {
      pv_last = (pv_first = false);
      return self;
   };

   return im_initialize ();
}
