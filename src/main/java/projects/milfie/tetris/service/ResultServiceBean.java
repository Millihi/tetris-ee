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

import projects.milfie.tetris.dao.ResultDAO;
import projects.milfie.tetris.domain.Result;

import java.util.Collection;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@Stateless
public class ResultServiceBean
   implements ResultServiceLocal
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public Collection<Result> findResults () {
      return resultDAO.findAll ();
   }

   @Override
   public Result find (final long id) {
      return resultDAO.find (id);
   }

   @Override
   public Result create (@NotNull final Result result) {
      resultDAO.persist (result);
      return result;
   }

   @Override
   public void remove (@NotNull final Result result) {
      resultDAO.remove (result);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @Inject
   private ResultDAO resultDAO;
}
