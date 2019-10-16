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

package projects.milfie.tetris.service;

import projects.milfie.tetris.domain.Result;

import java.util.Collection;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;

@Local
public interface ResultServiceLocal {

   public Collection<Result> findResults ();

   public Result find
      (final long id);

   public Result create
      (@NotNull final Result result);

   public void remove
      (@NotNull final Result result);
}
