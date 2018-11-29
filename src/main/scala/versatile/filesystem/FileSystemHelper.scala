package versatile.filesystem

import java.io.File

trait FileSystemHelper {

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def getPathFromRoot(relativePath: String): String = new java.io.File(s"./$relativePath").getCanonicalPath

  def getRootPath: String =   new java.io.File(".").getCanonicalPath

}
