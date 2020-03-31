/*
 * Websecbench is a suite of web security benchmarks generated by (CL)S.
 * Copyright (C) 2020  Jan Bessai and Malte Mues
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

import org.combinators.cls.types.{Constructor, Kinding, Type, Variable}

object SemanticTypes {
  val JavaString: Type = Constructor("String")
  val Decoded: Type = Constructor("Decoded")
  val JavaFilename: Type = Constructor("FileName")
  def JavaInputStream(status: Type): Type = Constructor("InputStream", status)
  def JavaOutputStream(status: Type): Type = Constructor("OutputStream", status)
  val JavaVoid: Type = Constructor("Void")
  val Used: Type = Constructor("Used")
  val Unused: Type = Constructor("Unused")
  val Encoded: Type = Constructor("Encoded")
  val JavaSQL: Type = Constructor("JavaSQL");

  val UsageStatus: Variable= Variable("streamStatus")
  val kinding = Kinding(UsageStatus).addOption(Used).addOption(Unused)
}

