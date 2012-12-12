package com.github.robertberry.scalatra_blog

import models.{Post, BlogDatabase}
import org.scalatra._
import org.squeryl.PrimitiveTypeMode._
import scalate.ScalateSupport
import com.github.robertberry.scalatra_blog.database._
import org.slf4j.LoggerFactory
import com.github.robertberry.utils.DateUtilities
import org.joda.time.DateTime

class ScalatraBlog extends ScalatraServlet with ScalateSupport
    with DatabaseSessionSupport with FlashMapSupport {
  val logger = LoggerFactory.getLogger(getClass)

  /**
   * Front page view - lists most recent posts.
   */
  get("/") {
    contentType = "text/html"

    val posts = from(BlogDatabase.posts)((post) =>
      select(post)
      orderBy(post.created desc)).groupBy(post => {
        new DateTime(post.created).toDateMidnight
      }).map(group => {
        (group._1.toString("dd MMMM, yyyy"), group._2)
      })

    scaml("index", "posts" -> posts)
  }

  /**
   * Archive view - lists the years that the user has written posts for and how
   * many posts have been written per year.
   */
  get("/archive") {
    contentType = "text/html"

    def yearUrl(year: Int) = "/archive/%d".format(year)

    val counts = Post.countPerYear.map((group) => {
      val year = group.key
      val count = group.measures

      (year, yearUrl(year), count)
    })

    scaml("archive", "counts" -> counts)
  }

  /**
   * Year view - shows a list of the months in the year and how many posts were
   * made in each month.
   */
  get("""^/archive/(\d{4})$""".r) {
    contentType = "text/html"

    val year = multiParams("captures").head.toInt

    def monthUrl(month: Int) = "/archive/%d/%d".format(year, month)

    val total = Post.countForYear(year).single.measures

    if (total > 0) {
      val counts = Post.countPerMonth(year).map((group) => {
        // MySQL returns the month 1-indexed.
        // todo: check if this is standard for all SQL
        val monthIndex = group.key - 1
        (DateUtilities.nameOfMonth(monthIndex), monthUrl(monthIndex),
          group.measures)
      })

      scaml("year", "counts" -> counts, "year" -> year, "total" -> total)
    } else {
      notFound()
    }
  }

  /**
   * Month view - lists all posts in the given month.
   */
  get("""/archive/(\d{4})/(\d{2})""".r) {
    contentType = "text/html"

    val captures = multiParams("captures")
    val year = captures(0).toInt
    val month = captures(1).toInt

    val posts = Post.forMonth(year, month).toList

    if (posts.length > 0) {
      scaml("month", "posts" -> posts, "year" -> year,
        "month" -> DateUtilities.nameOfMonth(month - 1))
    } else {
      notFound()
    }
  }

  /**
   * Create new post view
   */
  get("/posts/new") {
    contentType = "text/html"

    scaml("new")
  }

  /**
   * Submits a new post
   */
  post("/posts") {
    val title = params("title")
    val body = params("body")

    val post = new Post(0, title, body)

    if (Post.create(post)) {
      logger.info("New post: %s".format(title))
      flash("notice") = "'%s' successfully published".format(title)
      redirect("/")
    } else {
      logger.info("Could not create post.");
      flash("error") = "Unable to publish your post"
      scaml("new")
    }
  }

  /**
   * View for post with the given ID
   */
  get("/posts/:id") {
    contentType = "text/html"

    val id = params("id").toLong

    val post = from(BlogDatabase.posts)(post =>
      where(post.id === id)
        select (post)).headOption

    if (post.isDefined) {
      scaml("post", "post" -> post.get)
    } else {
      notFound()
    }
  }

  /**
   * Creates the database schema
   */
  get("/create-database") {
    contentType = "text/html"

    BlogDatabase.create
    redirect("/")
  }

  notFound {
    // remove content type in case it was set through an action
    contentType = null
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map {
      path =>
        contentType = "text/html"
        layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }
}
