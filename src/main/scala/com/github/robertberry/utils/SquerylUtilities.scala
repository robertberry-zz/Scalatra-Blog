package com.github.robertberry.utils

import org.squeryl.dsl.{NumericalExpression, DateExpression}
import java.sql.Timestamp
import org.squeryl.dsl.ast.FunctionNode
import org.squeryl.internals.OutMapper

class Month(exp: DateExpression[Timestamp], mapper: OutMapper[Int])
  extends FunctionNode[Int]("month", Some(mapper),
    Seq(exp)) with NumericalExpression[Int]

class Year(exp: DateExpression[Timestamp], mapper: OutMapper[Int])
  extends FunctionNode[Int]("year", Some(mapper), Seq(exp))
  with NumericalExpression[Int]

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
  def month(exp: DateExpression[Timestamp])(implicit m: OutMapper[Int]) =
    new Month(exp, m)

  /**
   * Year of the given timestamp
   *
   * @param exp The expression that resolves to a timestamp
   * @param m Outmapper (implicit param used by Squeryl DSL)
   * @return The year expression
   */
  def year(exp: DateExpression[Timestamp])(implicit m: OutMapper[Int]) =
    new Year(exp, m)
}
