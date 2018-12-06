import sbt._

/**
  * Used to prevent dependency issue:  javax.ws.rs#javax.ws.rs-api;2.1.1!javax.ws.rs-api.${packaging.type}
  */
object PackagingTypePlugin extends AutoPlugin {
  override val buildSettings = {
    sys.props += "packaging.type" -> "jar"
    Nil
  }
}