package bigdata

import org.apache.hadoop.fs.FileStatus
import org.apache.spark.sql.SparkSession

object HDFSUtils {

  /**
    * Get most recent file path at the specified path
    *
    * @param src: HDFS path
    * @return
    */
  def getLastSourceDateRef(src: String): String = {
    val hadoopConf = new org.apache.hadoop.conf.Configuration()
    val fs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)
    val it = fs.listStatus(new org.apache.hadoop.fs.Path(src))
    val srcCleaned = if (src.last == '/') src.dropRight(1) else src
    srcCleaned + "/" + it
      .filter(dir ⇒ fs.getContentSummary(dir.getPath).getSpaceConsumed > 0)
      .maxBy(_.getPath.getName).getPath.getName
  }

  /**
    * Get Files from the specified path on HDFS
    *
    * @param src: HDFS path
    * @return
    */
  def getAvailableFiles(src: String): Array[FileStatus] = {
    val hadoopConf = new org.apache.hadoop.conf.Configuration()
    val fs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)
    val it = fs.listStatus(new org.apache.hadoop.fs.Path(src))
    it.sortBy(_.getPath.getName)
  }

  /**
    * Find the last valid source (checks if the input RDD is not empty)
    * if it is empty, then it takes the previous valid source
    *
    * @param ss: SparkSession
    * @param srcPath: HDFS path
    * @return
    */
  def findLastValidSourceDate(implicit ss: SparkSession, srcPath: String): String = {

    val lastSourceDate = getLastSourceDateRef(srcPath)

    val allDumps = HDFSUtils.getAvailableFiles(lastSourceDate).zipWithIndex

    val totalFiles = allDumps.length

    val maybePath: Option[(FileStatus, Int)] = allDumps.reverse.find {
      case (filestatus: FileStatus, index: Int) =>
        val rdd = ss.read.parquet(filestatus.getPath.toString).rdd
        !rdd.isEmpty
    }

    if (maybePath.isEmpty) {
      println("Error, cannot find a non empty kafka dump")
      System.exit(0)
    }

    val kafkaDumpValidAfter = totalFiles - (maybePath.get._2 + 1)

    val lastValidSourceDate = maybePath.get._1.getPath.toString

    println(s"reading $lastSourceDate kafka dump after trying $kafkaDumpValidAfter files")

    lastValidSourceDate
  }

}
