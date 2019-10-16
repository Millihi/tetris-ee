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

var appContext = null;
document.addEventListener ("readystatechange", AppLoader);

function AppLoader () {
   if (document.readyState == "loading") {
      console.log ("AppLoader: Wait until document isn't loaded.");
      return;
   }

   if (!appContext) {
      console.log ("AppLoader: Load application.");
      appContext = new AppContext ();
   }

   if (!appContext) {
      console.log ("AppLoader: Couldn't initialize application.");
      return;
   }

   if (!appContext.isRun ()) {
      console.log ("AppLoader: Run application.");
      appContext.run ();
   }
}

///////////////////////////////////////////////////////////////////////////////
//  AppContext
function AppContext () {

   var
      self = this,
      pv_runned = false,
      //  App
      pv_keyboard = null,
      pv_canvas = null,
      pv_control = null,
      pv_state = null,
      pv_model = null,
      pv_rpc = null,
      pv_endpoint = null;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      if (!document || !jQuery) {
         throw new Error ("Either of document or jQuery not found.");
      }

      pv_rpc = new Rpc ();
      pv_model = new Model (pv_rpc);
      pv_endpoint = new Endpoint (pv_model, pv_rpc);
      pv_keyboard = new Keyboard (pv_model);
      pv_canvas = new Canvas (pv_model);
      pv_control = new Control (pv_model);
      pv_state = new State (pv_model);

      pv_model.setEndpoint (pv_endpoint);
      pv_model.setKeyboard (pv_keyboard);
      pv_model.setCanvas (pv_canvas);
      pv_model.setControl (pv_control);
      pv_model.setState (pv_state);

      return self;
   }

   self.isRun = function m_isRun () {
      return pv_runned;
   };

   self.run = function m_run () {
      if (!pv_runned) {
         try {
            pv_model.run ();
            pv_runned = true;
         }
         catch (e) {
            pv_runned = false;
            console.error (e.name + ": " + e.message + "\n\n" + e.stacktrace);
         }
      }
   };

   return im_initialize ();
}
