package com.github.robertberry.scalatra_blog.database

import com.mchange.v2.c3p0.ComboPooledDataSource
import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.MySQLAdapter
import org.slf4j.{Logger, LoggerFactory}

/**
 * ConnectionManager trait - adds methods for connecting to the database.
 */
trait ConnectionManager {
  val logger = LoggerFactory.getLogger(getClass)

  val username = "scalatra_blog"
  val password = "password"
  val driverClass = "com.mysql.jdbc.Driver"
  val uri = "jdbc:mysql://localhost:3306/scalatra_blog"

  var cpds = new ComboPooledDataSource

  def setupDatabase() {
    cpds.setDriverClass(driverClass)
    cpds.setJdbcUrl(uri)
    cpds.setUser(username)
    cpds.setPassword(password)
    cpds.setMinPoolSize(1)
    cpds.setAcquireIncrement(1)
    cpds.setMaxPoolSize(50)

    def connection = {
      logger.info("Creating connection through C3P0.")
      Session.create(cpds.getConnection, new MySQLAdapter)
    }

    SessionFactory.concreteFactory = Some(() => connection)
  }

  def closeDatabase() {
    logger.info("Closing C3P0 connection pool.")
    cpds.close()
  }
}
