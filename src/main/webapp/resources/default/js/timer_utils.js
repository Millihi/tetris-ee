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
//  FixedTimer
function FixedTimer (timeout) {

   var
      self = this,
      pv_timeout = 0,
      pv_callback = null,
      pv_argument = null,
      pv_timerId = 0,
      pv_lock = false;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function pm_initialize () {
      if (typeof timeout !== "number" || timeout < 0) {
         throw new Error ("Invalid timeout given.");
      }
      pv_timeout = timeout;
      return self;
   }

   self.name = "FixedTimer";
   //  Timeout
   self.getTimeout = function () { return pv_timeout; };
   //  Callback
   self.getCallback = function () { return pv_callback; };
   self.setCallback = function (v) {
      if (typeof v !== "function") {
         throw new Error ("Given callback is not a function.");
      }
      pv_callback = v;
      return self;
   };
   //  Argument
   self.getArgument = function ()  { return pv_argument; };
   self.setArgument = function (v) { pv_argument = v; return self; };
   //  State
   self.isActive = function () { return (pv_timerId !== 0 || pv_lock); };
   //  Action
   self.start = function () {
      self.stop ();
      pv_timerId = setTimeout (pm_callback, pv_timeout);
      return self;
   };
   self.stop = function () {
      if (pv_timerId) {
         clearTimeout (pv_timerId);
         pv_timerId = 0;
      }
      return self;
   };
   self.clear = function () {
      self.stop ();
      pm_reset ();
      return self;
   };
   self.fire = function () {
      if (pv_timerId) {
         self.stop ();
         pm_callback ();
      }
      return self;
   };

   return pm_initialize ();

   ////////////////////////////////////////////////////////////////////////////
   //  Private
   ////////////////////////////////////////////////////////////////////////////

   function pm_callback () {
      pv_lock    = true;
      pv_timerId = 0;
      pv_callback (pv_argument);
      pv_lock    = false;
   }

   function pm_reset () {
      pv_callback = null;
      pv_argument = null;
      pv_timerId  = 0;
      pv_lock     = false;
   }
}

///////////////////////////////////////////////////////////////////////////////
//  DelayedAction
function DelayedAction (callback, delay) {

   var
      self = this,
      pv_timer = null;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      pv_timer = new FixedTimer (delay).setCallback (callback);
      return self;
   }

   self.name = "DelayedAction";
   //  Delay
   self.getDelay = function () { return pv_timer.getTimeout (); };
   //  Callback
   self.getCallback = function () { return pv_timer.getCallback (); };
   //  Argument
   self.getArgument = function () { return pv_timer.getArgument (); };
   self.setArgument = function (v) { pv_timer.setArgument (v); return self; };
   //  State
   self.isRun = function () { return pv_timer.isActive (); };
   //  Action
   self.run = function () { pv_timer.start (); return self; };
   self.abort = function () { pv_timer.stop (); return self; };
   self.fire = function () { pv_timer.fire (); return self; };

   return im_initialize ();
}

///////////////////////////////////////////////////////////////////////////////
//  FixedInterval
function FixedInterval (timeout) {

   var
      self = this,
      pv_timeout = 0,
      pv_callback = null,
      pv_argument = null,
      pv_timerId = 0,
      pv_lock = false;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      if (typeof timeout !== "number" || timeout < 0) {
         throw new Error ("Invalid timeout given.");
      }
      pv_timeout = timeout;
      return self;
   }

   self.name = "FixedInterval";
   //  Timeout
   self.getTimeout = function () { return pv_timeout; };
   //  Callback
   self.getCallback = function () { return pv_callback; };
   self.setCallback = function (v) {
      if (typeof v !== "function") {
         throw new Error ("Given callback is not a function.");
      }
      pv_callback = v;
      return self;
   };
   //  Argument
   self.getArgument = function ()  { return pv_argument; };
   self.setArgument = function (v) { pv_argument = v; return self; };
   //  State
   self.isActive = function () { return (pv_timerId !== 0 || pv_lock); };
   //  Action
   self.start = function () {
      self.stop ();
      pv_timerId = setInterval (pm_callback, pv_timeout);
      return self;
   };
   self.stop = function () {
      if (pv_timerId) {
         clearInterval (pv_timerId);
         pv_timerId = 0;
      }
      return self;
   };
   self.clear = function () {
      self.stop ();
      pm_reset ();
      return self;
   };
   self.fire = function () {
      if (pv_timerId) {
         self.stop ();
         pm_callback ();
      }
      return self;
   };

   return im_initialize ();

   ////////////////////////////////////////////////////////////////////////////
   //  Private
   ////////////////////////////////////////////////////////////////////////////

   function pm_callback () {
      pv_lock = true;
      pv_callback (pv_argument);
      pv_lock = false;
   }

   function pm_reset () {
      pv_callback = null;
      pv_argument = null;
      pv_timerId  = 0;
      pv_lock     = false;
   }
}

///////////////////////////////////////////////////////////////////////////////
//  IntervalAction
function IntervalAction (callback, delay) {

   var
      self = this,
      pv_timer = null;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      pv_timer = new FixedInterval (delay).setCallback (callback);
      return self;
   }

   self.name = "IntervalAction";
   //  Delay
   self.getDelay = function () { return pv_timer.getTimeout (); };
   //  Callback
   self.getCallback = function () { return pv_timer.getCallback (); };
   //  Argument
   self.getArgument = function () { return pv_timer.getArgument (); };
   self.setArgument = function (v) { pv_timer.setArgument (v); return self; };
   //  State
   self.isRun = function () { return pv_timer.isActive (); };
   //  Action
   self.run = function () { pv_timer.start (); return self; };
   self.abort = function () { pv_timer.stop (); return self; };
   self.fire = function () { pv_timer.fire (); return self; };

   return im_initialize ();
}
