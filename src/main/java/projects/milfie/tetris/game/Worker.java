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

package projects.milfie.tetris.game;

import projects.milfie.tetris.game.logic.Computer;
import projects.milfie.tetris.game.logic.Gameplay;
import projects.milfie.tetris.game.logic.Gamestate;
import projects.milfie.tetris.websocket.rpc.Command;
import projects.milfie.tetris.websocket.rpc.CommandAdapter;
import projects.milfie.tetris.websocket.rpc.CommandSpec;
import projects.milfie.tetris.websocket.workflow.Workflow;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Worker
   implements Runnable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Worker (final Workflow workflow) {
      if (workflow == null) {
         throw new IllegalArgumentException ("Given workflow is null.");
      }

      this.id = "@" + Integer.toHexString (this.hashCode ());
      this.state.set (State.STOP);
      this.workflow = workflow;
      this.gameplay = new Gameplay
         (workflow.getStateKeeper ().getWidth (),
          workflow.getStateKeeper ().getHeight ());
      this.computer = new Computer ();

      this.gameplay.setComputer (this.computer);
      this.computer.setGameplay (this.gameplay);

      this.computer.reset ();
      this.gameplay.reset ();
   }

   public void restore () {
      LOG.info ("Worker " + id + " is restored.");

      synchronized (workflow.getOutcomings ()) {
         workflow
            .getOutcomings ()
            .clear ()
            .put (new Command (CommandSpec.ENTER_RUN));

         if (state.get () == State.PAUSE) {
            workflow
               .getOutcomings ()
               .put (new Command (CommandSpec.ENTER_PAUSE));
         }

         gameplay.requestFullUpdate ();
         handleGameplay ();
      }

      wakeup ();
   }

   public boolean isActive () {
      return (state.get ().active && !sleep.get ());
   }

   public boolean isRun () {
      return state.get ().active;
   }

   @Override
   public void run () {
      LOG.info ("Worker " + id + " is running.");

      state.set (State.START);

      do {
         state.get ().dispatch (this);
      }
      while (state.get ().active);

      state.get ().dispatch (this);
   }

   public void stop () {
      LOG.info ("Worker " + id + " is requested to stop.");

      state.set (State.STOP);

      wakeup ();
   }

   public boolean isSleep () {
      return sleep.get ();
   }

   public void sleep () {
      LOG.info ("Worker " + id + " is requested to sleep.");

      sleep.set (true);
   }

   public void wakeup () {
      LOG.info ("Worker " + id + " is requested to wakeup.");

      synchronized (sleep) {
         sleep.set (false);
         sleep.notify ();
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final AtomicBoolean          sleep = new AtomicBoolean (false);
   private final AtomicReference<State> state = new AtomicReference<> ();

   private final String   id;
   private final Workflow workflow;
   private final Computer computer;
   private final Gameplay gameplay;

   private void doStartJob () {
      LOG.info ("Worker " + id + " is run.");

      workflow.getStateKeeper ().resetTemporal ();

      state.set (State.SETUP);
   }

   private void doSetupJob () {
      LOG.info ("Worker " + id + " is waiting for settings.");

      final long optimalTimeNs = State.SETUP.optimalTimeNs;

      while (state.get () == State.SETUP) {
         final long startTimeNs = System.nanoTime ();
         handleCommands ();
         handleSleep (startTimeNs, optimalTimeNs);
      }
   }

   private void doMainJob () {
      LOG.info ("Worker " + id + " is doing its job.");

      workflow.getOutcomings ().put (new Command (CommandSpec.ENTER_RUN));

      handleWorkflow ();

      final long optimalTimeNs = State.RUN.optimalTimeNs;

      while (state.get () == State.RUN) {
         final long startTimeNs = System.nanoTime ();
         handleCommands ();
         handleAI ();
         handleGameplay ();
         handleWorkflow ();
         handleSleep (startTimeNs, optimalTimeNs);
      }
   }

   private void doPauseJob () {
      LOG.info ("Worker " + id + " is paused.");

      workflow.getOutcomings ().put (new Command (CommandSpec.ENTER_PAUSE));

      handleWorkflow ();

      final long optimalTimeNs = State.PAUSE.optimalTimeNs;

      while (state.get () == State.PAUSE) {
         final long startTimeNs = System.nanoTime ();
         handleCommands ();
         handleWorkflow ();
         handleSleep (startTimeNs, optimalTimeNs);
      }
   }

   private void doStopJob () {
      LOG.info ("Worker " + id + " is finished its work.");

      workflow.getOutcomings ().put (new Command (CommandSpec.ENTER_STOP));

      handleWorkflow ();

      workflow.getTransmitter ().closeSession ();
   }

   private void doErrorJob () {
      LOG.info ("Worker " + id + " is met an error.");

      workflow.getOutcomings ().put (new Command (CommandSpec.ENTER_ERROR));

      handleWorkflow ();

      workflow.getTransmitter ().closeSession ();
   }

   private void handleCommands () {
      final State currentState = state.get ();

      for (final Command command : workflow.getIncomings ().popAll ()) {
         final CommandSpec spec = command.getSpec ();

         if (currentState.allowedCommands[spec.ordinal ()] &&
             DISPATCHER.containsKey (spec))
         {
            DISPATCHER.get (spec).dispatch (this, command);
         }
      }
   }

   private void handleAI () {
      computer.update ();
   }

   private void handleGameplay () {
      gameplay.update ();

      final Gamestate state = gameplay.getGamestate ();

      if (state.isChanged ()) {
         if (state.getUpdateRect ().isValid ()) {
            workflow.getOutcomings ().put
               (new Command
                   (CommandSpec.UPDATE_REGION,
                    state.getUpdateRect ().getLeftBound (),
                    state.getUpdateRect ().getBottomBound (),
                    gameplay.getUpdateData ()));
         }

         if (state.hasNewBrick ()) {
            workflow.getOutcomings ().put
               (new Command
                   (CommandSpec.UPDATE_BRICK,
                    (Object) gameplay.getBrickData ()));
         }

         if (state.getScore ().isChanged ()) {
            workflow.getStateKeeper ().setScore (state.getScore ().get ());
            workflow.getOutcomings ().put
               (new Command
                   (CommandSpec.UPDATE_SCORE,
                    state.getScore ().get ().toString ()));
         }

         if (state.getCombo ().isChanged ()) {
            workflow.getOutcomings ().put
               (new Command
                   (CommandSpec.UPDATE_COMBO,
                    state.getCombo ().get ().toString ()));
         }

         if (state.getLevel ().isChanged ()) {
            workflow.getStateKeeper ().setLevel (state.getLevel ().get ());
            workflow.getOutcomings ().put
               (new Command
                   (CommandSpec.UPDATE_LEVEL,
                    state.getLevel ().get ().toString ()));
         }

         if (state.hasGameover ()) {
            this.state.set (State.STOP);
            workflow.getOutcomings ().put (new Command (CommandSpec.LOOSE));
         }

         state.accept ();
      }
   }

   private void handleWorkflow () {
      workflow.flush ();
   }

   private void handleSleep (final long startTimeNs,
                             final long optimalTimeNs)
   {
      try {
         if (sleep.get ()) {
            synchronized (sleep) {
               while (sleep.get ()) {
                  sleep.wait ();
               }
            }
         }
         else {
            Thread.sleep (getUnusedTime (startTimeNs, optimalTimeNs));
         }
      }
      catch (final InterruptedException e) {
         Thread.currentThread ().interrupt ();
         state.set (State.ERROR);

         final String message = "Interrupted while sleeping: ";
         workflow.getStateKeeper ().setError (message + e.getMessage ());
         LOG.log (Level.SEVERE, message, e);
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private enum State {
      /////////////////////////////////////////////////////////////////////////
      //  Public static section                                              //
      /////////////////////////////////////////////////////////////////////////

      START
         (true, -1)
         {
            @Override
            public void dispatch (final Worker worker) {
               worker.doStartJob ();
            }
         },
      SETUP
         (true, 4,
          CommandSpec.START)
         {
            @Override
            public void dispatch (final Worker worker) {
               worker.doSetupJob ();
            }
         },
      RUN
         (true, 20,
          CommandSpec.STOP,
          CommandSpec.PAUSE,
          CommandSpec.ROTATE_LEFT,
          CommandSpec.ROTATE_RIGHT,
          CommandSpec.MOVE_LEFT,
          CommandSpec.MOVE_RIGHT,
          CommandSpec.MOVE_DOWN,
          CommandSpec.PUT_DOWN)
         {
            @Override
            public void dispatch (final Worker worker) {
               worker.doMainJob ();
            }
         },
      PAUSE
         (true, 4,
          CommandSpec.STOP,
          CommandSpec.PAUSE)
         {
            @Override
            public void dispatch (final Worker worker) {
               worker.doPauseJob ();
            }
         },
      STOP
         (false, -1,
          CommandSpec.START)
         {
            @Override
            public void dispatch (final Worker worker) {
               worker.doStopJob ();
            }
         },
      ERROR
         (false, -1)
         {
            @Override
            public void dispatch (final Worker worker) {
               worker.doErrorJob ();
            }
         };

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public abstract void dispatch (final Worker worker);

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      private State (final boolean active,
                     final int targetFps,
                     final CommandSpec... allowedCommands)
      {
         this.active = active;
         this.optimalTimeNs = 1_000_000_000 / targetFps;
         this.allowedCommands = new boolean[CommandSpec.values ().length];

         Arrays.fill (this.allowedCommands, false);

         for (final CommandSpec command : allowedCommands) {
            this.allowedCommands[command.ordinal ()] = true;
         }
      }

      private final boolean   active;
      private final long      optimalTimeNs;
      private final boolean[] allowedCommands;
   }

   private static final Logger LOG =
      Logger.getLogger (Worker.class.getSimpleName ());

   private static final
   EnumMap<CommandSpec, CommandAdapter<Worker>> DISPATCHER =
      new EnumMap<> (CommandSpec.class);

   static {
      DISPATCHER.put (CommandSpec.START, Worker::doStart);
      DISPATCHER.put (CommandSpec.STOP, Worker::doStop);
      DISPATCHER.put (CommandSpec.PAUSE, Worker::doPause);
      DISPATCHER.put (CommandSpec.ROTATE_LEFT, Worker::doRotateCW);
      DISPATCHER.put (CommandSpec.ROTATE_RIGHT, Worker::doRotateCCW);
      DISPATCHER.put (CommandSpec.MOVE_LEFT, Worker::doMoveLeft);
      DISPATCHER.put (CommandSpec.MOVE_RIGHT, Worker::doMoveRight);
      DISPATCHER.put (CommandSpec.MOVE_DOWN, Worker::doMoveDown);
      DISPATCHER.put (CommandSpec.PUT_DOWN, Worker::doPutDown);
   }

   private static void doStart (final Worker worker, final Command c) {
      worker.state.set (State.RUN);
   }

   private static void doStop (final Worker worker, final Command c) {
      worker.state.set (State.STOP);
   }

   private static void doPause (final Worker worker, final Command c) {
      if (worker.state.get () == State.PAUSE) {
         worker.state.set (State.RUN);
      }
      else {
         worker.state.set (State.PAUSE);
      }
   }

   private static void doPutDown (final Worker worker, final Command c) {
      worker.gameplay.putDown ();
   }

   private static void doMoveDown (final Worker worker, final Command c) {
      worker.gameplay.moveDown ();
   }

   private static void doMoveRight (final Worker worker, final Command c) {
      worker.gameplay.moveRight ();
   }

   private static void doMoveLeft (final Worker worker, final Command c) {
      worker.gameplay.moveLeft ();
   }

   private static void doRotateCW (final Worker worker, final Command c) {
      worker.gameplay.rotateClockwise ();
   }

   private static void doRotateCCW (final Worker worker, final Command c) {
      worker.gameplay.rotateCounterClockwise ();
   }

   private static long getUnusedTime (final long startTimeNs,
                                      final long optimalTimeNs)
   {
      final long unusedTimeMs =
         (optimalTimeNs - Math.abs (System.nanoTime () - startTimeNs)) /
         1_000_000;

      if (unusedTimeMs < 0) {
         LOG.warning
            ("Not enough time, " +
             "need at least " + Math.abs (unusedTimeMs) + " more ms.");
         return 0;
      }

      return unusedTimeMs;
   }
}
