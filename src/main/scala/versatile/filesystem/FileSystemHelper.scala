package versatile.filesystem

import java.io.File

trait FileSystemHelper {

  def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def getPathFromRoot(relativePath: String): String = new java.io.File(s"./$relativePath").getCanonicalPath

  def getRootPath: String = new java.io.File(".").getCanonicalPath

  def getResourcesPath = s"$getRootPath/src/main/resources"

  import java.io.{BufferedWriter, File, FileWriter}

  def writeFile(canonicalFilename: String, text: String): Unit = {
    val file = new File(canonicalFilename)
    file.getParentFile.mkdirs()
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(text)
    bw.close()
  }

}
