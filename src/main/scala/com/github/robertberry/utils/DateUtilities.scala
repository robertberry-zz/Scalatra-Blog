package com.github.robertberry.utils

import java.util.{GregorianCalendar, Calendar}
import java.sql.Timestamp

/**
 * Utility functions for dates and timestamps
 */
object DateUtilities {
  /**
   * Number of days in the given month in the given year
   *
   * @param year The year
   * @param month The month (zero-indexed)
   * @return The number of days
   */
  def daysInMonth(year: Int, month: Int): Int = {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.getActualMaximum(Calendar.DATE)
  }

  /**
   * Helper method for creating timestamps (which is a bizarrely laborious
   * process).
   *
   * @param year Year
   * @param month Month (zero-indexed)
   * @param day Day
   * @param hour Hour
   * @param minute Minute
   * @param second Second
   * @return The timestamp
   */
  def createTimestamp(year: Int, month: Int, day: Int,
                              hour: Int, minute: Int, second: Int): Timestamp =
  {
    new Timestamp(new GregorianCalendar(year, month, day, hour, minute, second)
      .getTime.getTime)
  }

  /**
   * A tuple containing the earliest and latest timestamp belonging to the
   * given year
   *
   * @param year The year
   * @return The tuple
   */
  def yearTimestampRange(year: Int): Tuple2[Timestamp, Timestamp] = {
    new Tuple2(createTimestamp(year, 0, 1, 0, 0, 0),
      createTimestamp(year, 11, 31, 23, 59, 59))
  }

  /**
   * Given the number of a month, returns its name
   *
   * @param month Number of month (zero-indexed)
   * @return Name of month
   */
  def nameOfMonth(month: Int): String = {
    val months = new java.text.DateFormatSymbols().getMonths
    months(month)
  }
}
