package com.github.robertberry.scalatra_blog.models

import org.squeryl.PrimitiveTypeMode._
import java.sql.Timestamp
import java.util.{Calendar, GregorianCalendar, Date}
import org.squeryl.Query

/**
 * Blog post
 */
class Post(val id: Long, val title: String, val body: String,
           val created: Timestamp = new Timestamp(new Date().getTime),
           val modified: Option[Timestamp] = None) extends ScalatraRecord {

  /**
   * URL for the post
   */
  def url = {
    "/posts/%d".format(id)
  }
}

object Post {
  /**
   * Adds a new post to the database
   *
   * @param post The post
   * @return Whether successfully persisted
   */
  def create(post: Post): Boolean = {
    inTransaction {
      val result = BlogDatabase.posts.insert(post)
      result.isPersisted
    }
  }

  /**
   * Posts created between the given start and end timestamp
   *
   * @param start The start timestamp
   * @param end The end timestamp
   * @return The posts
   */
  def createdBetween(start: Timestamp, end: Timestamp): Query[Post] = {
    from(BlogDatabase.posts)((post) =>
      where(post.created >= start
        and post.created <= end)
        select(post)
        orderBy(post.created asc)
    )
  }

  /**
   * Helper method for creating timestamps (which is a bizarrely laborious process).
   *
   * @param year Year
   * @param month Month (zero-indexed)
   * @param day Day
   * @param hour Hour
   * @param minute Minute
   * @param second Second
   * @return The timestamp
   */
  private def createTimestamp(year: Int, month: Int, day: Int,
                              hour: Int, minute: Int, second: Int): Timestamp = {
    new Timestamp(new GregorianCalendar(year, month, day, hour, minute, second)
      .getTime.getTime)
  }

  /**
   * Number of days in the given month in the given year
   *
   * @param year The year
   * @param month The month (zero-indexed)
   * @return The number of days
   */
  private def daysInMonth(year: Int, month: Int) = {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.getActualMaximum(Calendar.DATE)
  }

  /**
   * Posts for the given year, ordered by creation date
   *
   * @param year The year
   * @return The posts
   */
  def forYear(year: Int): Query[Post] = {
    createdBetween(createTimestamp(year, 0, 1, 0, 0, 0),
      createTimestamp(year, 11, 31, 23, 59, 59))
  }

  /**
   * Posts for the given year and month, ordered by creation date
   *
   * @param year The year
   * @param month The month
   * @return The posts
   */
  def forMonth(year: Int, month: Int): Query[Post] = {
    createdBetween(createTimestamp(year, month, 1, 0, 0, 0),
      createTimestamp(year, month, daysInMonth(year, month), 0, 0, 0))
  }
}