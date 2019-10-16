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
//  Endpoint
function Endpoint (di_model, di_rpc) {

   var ENDPOINT_PATH = "/tetris/gameplay";

   var
      self = this,
      pv_ws = null,
      pv_timeout = null,
      pv_state = Endpoint.STOP,
      pv_outcomings = [],
      pv_sendAction = null;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      if (typeof di_model !== "object" || di_model.name !== "Model") {
         throw new Error ("Incorrect model given.");
      }
      if (typeof di_rpc !== "object" || di_rpc.name !== "Rpc") {
         throw new Error ("Incorrect RPC given.");
      }

      pv_timeout = new Timeout ();
      pv_sendAction = new IntervalAction (pm_onSendAction, 50);

      return self;
   }

   self.name = "Endpoint";

   self.getState = function () {
      return pv_state;
   };

   self.isRunned = function () {
      return pv_state.active;
   };

   self.run = function () {
      if (!pv_state.active) {
         pm_connect ();
      }
      return self;
   };

   self.stop = function () {
      if (pv_state.active) {
         pm_disconnect ();
      }
      return self;
   };

   self.send = function (command) {
      if (!pv_state.active) {
         throw new Error ("Endpoint not running.");
      }
      pv_outcomings.push (command);
   };

   return im_initialize ();

   ////////////////////////////////////////////////////////////////////////////
   //  Private                                                               //
   ////////////////////////////////////////////////////////////////////////////

   function pm_connect () {
      pv_timeout.reset ();
      pm_reconnect ();
   }

   function pm_reconnect () {
      var message;

      di_model.lock (MESSAGES['ui.endpoint.connect']);

      if (!window.WebSocket) {
         message = MESSAGES['ui.endpoint.error.noWebSocketSuppot'];
         console.error (message);

         pv_sendAction.abort ();
         pv_state = Endpoint.ERROR;
         setTimeout (pm_errorBuilder (message), 1);

         return;
      }

      var remoteUrl;

      if (window.location.protocol === "https:") {
         remoteUrl = "wss://";
      }
      else if (window.location.protocol === "http:") {
         remoteUrl = "ws://";
      }
      else {
         message = MESSAGES['ui.endpoint.error.unsupportedProtocol'];
         message += " " + window.location.protocol;
         console.error (message);

         pv_sendAction.abort ();
         pv_state = Endpoint.ERROR;
         setTimeout (pm_errorBuilder (message), 1);

         return;
      }

      remoteUrl += window.location.host + ENDPOINT_PATH;

      var jSessIdKey = ";jsessionid=";
      var jSessIdPos = window.location.pathname.lastIndexOf (jSessIdKey);

      if (jSessIdPos >= 0) {
         remoteUrl += window.location.pathname.substring (jSessIdPos);
      }

      try {
         pv_ws = new WebSocket (remoteUrl);
      }
      catch (e) {
         console.error
            (e.name + ": " + e.message + "\n\n" + e.stacktrace);

         pv_sendAction.abort ();
         pv_state = Endpoint.ERROR;
         setTimeout (
            pm_errorBuilder (MESSAGES['ui.endpoint.error.noConnection']), 1);

         return;
      }

      pv_ws.onopen = pm_onOpen;
      pv_ws.onmessage = pm_onMessage;
      pv_ws.onerror = pm_onError;
      pv_ws.onclose = pm_onClose;

      pv_state = Endpoint.START;
   }

   function pm_disconnect () {
      di_model.lock (MESSAGES['ui.endpoint.disconnected']);

      pv_ws = null;
      pv_timeout.reset ();
      pv_state = Endpoint.STOP;
   }

   function pm_errorBuilder (message) {
      var lv_message = MESSAGES['ui.endpoint.error.unknown'];

      if (typeof message === "string" && message.length > 0) {
         lv_message = message;
      }

      return lm_errorClojure;

      function lm_errorClojure () {
         di_model.lock (lv_message);

         pv_ws = null;
         pv_timeout.reset ();
         pv_state = Endpoint.ERROR;
      }
   }

   function pm_onOpen (ev) {
      di_model.lock (MESSAGES['ui.endpoint.setup']);

      pv_timeout.reset ();
      pv_state = Endpoint.SETUP;
   }

   function pm_onMessage (ev) {
      var data = ev.data;

      if (typeof data !== "string") {
         console.warn ("Non string data received, ignore it.");
      }
      else if (data.length <= 0) {
         console.warn ("An empty string received, ignore it.");
      }
      else if (data === "PING") {
         // Nothing to do here.
      }
      else {
         var isCommand = di_rpc.Codec.willDecode (data);

         if (pv_state === Endpoint.SETUP && !isCommand) {
            try {
               di_rpc.loadSpec (JSON.parse (data));

               pv_timeout.reset ();
               pv_sendAction.run ();
               pv_state = Endpoint.READY;

               di_model.acceptCommand ("START"); // TODO: Make control
            }
            catch (e) {
               console.error
                  (e.name + ": " + e.message + "\n\n" + e.stacktrace);

               pv_sendAction.abort ();
               pv_state = Endpoint.ERROR;
               pv_ws.close ();
            }
         }
         else if (pv_state === Endpoint.READY && isCommand) {
            var commands = di_rpc.Codec.decode (data);

            for (var i = 0, il = commands.length; i < il; ++i) {
               di_model.dispatchCommand (commands[i]);
            }
         }
         else {
            console.warn ("Unexpected data received, ignore it.");
         }
      }
   }

   function pm_onError (ev) {
      console.error ("An error occured.");

      di_model.lock (MESSAGES['ui.endpoint.error']);
      pv_state = Endpoint.RESTART;
      pv_ws.close ();
   }

   function pm_onClose (ev) {
      console.log ("Closing: code: " + ev.code + ", reason " + ev.reason);

      if (ev.code === 1002) {
         setTimeout (
            pm_errorBuilder (
               MESSAGES['ui.endpoint.error.unsupportedWsVersion']), 1);
      }
      else if (ev.code === 1005) {
         setTimeout (
            pm_errorBuilder (
               MESSAGES['ui.endpoint.error.closedSilently']), 1);
      }
      else if (ev.code === 1011) {
         setTimeout (
            pm_errorBuilder (
               MESSAGES['ui.endpoint.error.internal']), 1);
      }
      else if (pv_state === Endpoint.READY) {
         setTimeout (
            pm_disconnect, 1);
      }
      else if (pv_state === Endpoint.ERROR) {
         setTimeout (
            pm_errorBuilder (
               MESSAGES['ui.endpoint.error.internal']), 1);
      }
      else if (pv_timeout.isMaxTimeout ()) {
         setTimeout (
            pm_errorBuilder (
               MESSAGES['ui.endpoint.error.maxReconnects']), 1);
      }
      else {
         setTimeout (
            pm_reconnect, pv_timeout.getTimeout ());
      }

      pv_sendAction.abort ();
   }

   function pm_onSendAction () {
      if (pv_state === Endpoint.READY && pv_outcomings.length > 0) {
         try {
            pv_ws.send (di_rpc.Codec.encode (pv_outcomings));
            pv_outcomings = [];
         }
         catch (e) {
            console.error (e.name + ": " + e.message + "\n\n" + e.stacktrace);
         }
      }
   }
}
Endpoint.START = { id: 0, active: true, name: "START" };
Endpoint.SETUP = { id: 1, active: true, name: "SETUP" };
Endpoint.READY = { id: 2, active: true, name: "READY" };
Endpoint.RESTART = { id: 3, active: true, name: "RESTART"};
Endpoint.STOP = { id: 4, active: false, name: "STOP" };
Endpoint.ERROR = { id: 5, active: false, name: "ERROR" };

///////////////////////////////////////////////////////////////////////////////
//  Timeout
function Timeout () {

   var MAX_TIMEOUT = 10 * 60; // s

   var
      self = this,
      pv_timeout = 0,
      pv_attempts = 0;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      self.reset ();
      return self;
   }

   self.name = "Timeout";

   self.reset = function () {
      pv_attempts = (pv_timeout = 1);
      return self;
   };

   self.getTimeout = function () {
      var timeMillis = pv_timeout * 1000;

      --pv_attempts;

      if (pv_attempts <= 0) {
         pv_attempts =
            (pv_timeout +=
               (pv_timeout > MAX_TIMEOUT - pv_timeout ? 0 : pv_timeout));
      }

      return timeMillis;
   };

   self.isMaxTimeout = function () {
      return (pv_timeout > MAX_TIMEOUT - pv_timeout);
   };

   return im_initialize ();
}
