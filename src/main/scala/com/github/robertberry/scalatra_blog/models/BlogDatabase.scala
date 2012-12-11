package com.github.robertberry.scalatra_blog.models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema

/**
 * Blog database schema
 */
object BlogDatabase extends Schema {
  val posts = table[Post]("posts")

  on(posts)(a => declare(
    a.id is(autoIncremented)))
}
