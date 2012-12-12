package com.github.robertberry.scalatra_blog.models

import org.squeryl.PrimitiveTypeMode._
import java.sql.Timestamp
import java.util.{Calendar, GregorianCalendar, Date}
import org.squeryl.Query
import com.github.robertberry.utils.{SquerylUtilities, DateUtilities}
import com.github.robertberry.utils.DateUtilities.createTimestamp
import org.squeryl.dsl.{Measures, DateExpression, GroupWithMeasures}
import org.squeryl.dsl.ast.FunctionNode
import org.joda.time.DateTime

/**
 * Blog post
 */
class Post(val id: Long, val title: String, val body: String,
           val created: Timestamp = new Timestamp(new Date().getTime),
           val modified: Option[Timestamp] = None) extends ScalatraRecord {

  def timeCreated: String = {
    new DateTime(created).toString("hh:mma")
  }

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
      where(post.created >= start and post.created <= end)
        select(post)
        orderBy(post.created desc)
    )
  }

  /**
   * Posts for the given year, ordered by creation date
   *
   * @param year The year
   * @return The posts
   */
  def forYear(year: Int): Query[Post] = {
    val (start, end) = DateUtilities.yearTimestampRange(year)

    createdBetween(start, end)
  }

  /**
   * Number of posts in the given year
   *
   * @param year The year
   * @return Query returning number of posts
   */
  def countForYear(year: Int): Query[Measures[Long]] = {
    val (start, end) = DateUtilities.yearTimestampRange(year)

    from(BlogDatabase.posts)((post) =>
      where(post.created >= start and post.created <= end)
      compute(count())
    )
  }

  /**
   * Number of posts per month in the given year
   *
   * @param year The year
   * @return Query returning tuples of the index of the month and the number
   *         of posts
   */
  def countPerMonth(year: Int): Query[GroupWithMeasures[(Int), (Long)]] = {
    val (start, end) = DateUtilities.yearTimestampRange(year)

    from(BlogDatabase.posts)((post) =>
      where(post.created >= start and post.created <= end)
      groupBy(SquerylUtilities.month(post.created))
      compute(count())
    )
  }

  /**
   * Number of posts per year in the blog
   *
   * @return Query returning tuples of the year and the number of posts made
   *         in that year
   */
  def countPerYear: Query[GroupWithMeasures[(Int), (Long)]] = {
    from(BlogDatabase.posts)((post) =>
      groupBy(SquerylUtilities.year(post.created))
      compute(count())
    )
  }

  /**
   * Posts for the given year and month, ordered by creation date
   *
   * @param year The year
   * @param month The month
   * @return The posts
   */
  def forMonth(year: Int, month: Int): Query[Post] = {
    val zeroIndexedMonth = month - 1

    createdBetween(createTimestamp(year, zeroIndexedMonth, 1, 0, 0, 0),
      createTimestamp(year, month, DateUtilities.daysInMonth(year,
        zeroIndexedMonth), 0, 0, 0))
  }
}