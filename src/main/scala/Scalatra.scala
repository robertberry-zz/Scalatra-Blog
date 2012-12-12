import com.github.robertberry.scalatra_blog._
import database.ConnectionManager
import org.scalatra._
import javax.servlet.ServletContext

/**
 * This is the Scalatra bootstrap file. You can use it to mount servlets or
 * filters. It's also a good place to put initialization code which needs to
 * run at application start (e.g. database configurations), and init params.
 */
class Scalatra extends LifeCycle with ConnectionManager {
  override def init(context: ServletContext) {
    setupDatabase()
    // Mount one or more servlets
    context.mount(new ScalatraBlog, "/*")
  }

  override def destroy(context: ServletContext) {
    closeDatabase()
  }
}
