package com.github.robertberry.scalatra_blog.models

import org.squeryl.PrimitiveTypeMode._
import java.sql.Timestamp
import java.util.Date

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
}