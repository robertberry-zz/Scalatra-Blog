package com.github.robertberry.utils

import org.squeryl.dsl.{NumericalExpression, DateExpression}
import java.sql.Timestamp
import org.squeryl.dsl.ast.FunctionNode
import org.squeryl.internals.OutMapper

class Month(exp: DateExpression[Timestamp], mapper: OutMapper[Long])
  extends FunctionNode[Long]("month", Some(mapper),
    Seq(exp)) with NumericalExpression[Long]

/**
 * Utility functions for use with Squeryl.
 */
object SquerylUtilities {
  /**
   * Month of the given timestamp
   *
   * @param exp The expression that resolves to a timestamp
   * @param m Outmapper (implicit parameter used by Squeryl's DSL)
   * @return The month expression
   */
  def month(exp: DateExpression[Timestamp])(implicit m: OutMapper[Long]) =
    new Month(exp, m)
}
