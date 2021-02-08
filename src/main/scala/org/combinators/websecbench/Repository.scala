/*
 * Websecbench is a suite of web security benchmarks generated by (CL)S.
 * Copyright (C) 2021  Jan Bessai and Malte Mues
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.combinators.websecbench

import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.{InhabitationResult, ReflectedRepository}
import org.combinators.cls.types.Type


object Repository {
  val components: Seq[TaggedComponent] =
    Seq(
      iointeraction.components,
      request.components,
      processing.components,
      databaseinteraction.components
    ).flatten

  def repository(componentTags: Set[ComponentTag]): ReflectedRepository[Repository.type] = {
    val selectedComponents = components.filter(comp => comp.tags.intersect(componentTags).nonEmpty)

    selectedComponents.foldLeft(ReflectedRepository(this, classLoader = getClass.getClassLoader,substitutionSpace = SemanticTypes.kinding)) { case (repo, component) =>
      component.addToRepository(repo)
    }
  }
}
