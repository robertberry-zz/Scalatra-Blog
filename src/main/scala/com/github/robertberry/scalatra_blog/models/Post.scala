package com.github.robertberry.scalatra_blog.models

import org.squeryl.PrimitiveTypeMode._

/**
 * Blog post
 */
class Post(val id: Long, val title: String, val body: String) extends ScalatraRecord {

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