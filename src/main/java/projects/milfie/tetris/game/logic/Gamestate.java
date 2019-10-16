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

package projects.milfie.tetris.game.logic;

import projects.milfie.tetris.game.domain.Rect;

/**
 * Purpose: <p>Maintains a state for one quant of game's time.</p>
 */
public final class Gamestate {
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Gamestate () {
      this.updateRect = new Rect ();
      this.combo = new Property.Combo ();
      this.level = new Property.Level ();
      this.score = new Property.Score (this.combo, this.level);
   }

   public boolean isChanged () {
      return
         (updateRect.isValid () ||
          combo.isChanged () ||
          score.isChanged () ||
          level.isChanged () ||
          newBrick ||
          gameover);
   }

   public void accept () {
      updateRect.invalidate ();
      combo.accept ();
      score.accept ();
      level.accept ();
      newBrick = false;
   }

   public Rect getUpdateRect () {
      return updateRect;
   }

   public Property.Combo getCombo () {
      return combo;
   }

   public Property.Level getLevel () {
      return level;
   }

   public Property.Score getScore () {
      return score;
   }

   public boolean hasGameover () {
      return gameover;
   }

   public boolean hasNewBrick () {
      return newBrick;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Rect           updateRect;
   private final Property.Combo combo;
   private final Property.Score score;
   private final Property.Level level;

   private boolean newBrick = false;
   private boolean gameover = false;

   protected void reset () {
      updateRect.invalidate ();
      combo.reset ();
      score.reset ();
      level.reset ();
      newBrick = false;
      gameover = false;
   }

   protected void addRemovedRows (final int rowsCount) {
      score.update (rowsCount);
   }

   protected void setNewBrick (final boolean newBrick) {
      this.newBrick = newBrick;
   }

   protected void setGameover (final boolean gameover) {
      this.gameover |= gameover;
   }
}
