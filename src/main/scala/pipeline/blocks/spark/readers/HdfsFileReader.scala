package pipeline.blocks.spark.readers

import org.apache.spark.sql.{DataFrame, SparkSession}
import pipeline.{Block, BlockResult}

class HdfsFileReader(spark: SparkSession, fileFormat: String, filePath: String, options: Map[String, String] = Map.empty[String, String]) extends Block[Nothing, DataFrame](numberOfInputs = 0){
  override protected def blockProcess(args: BlockResult[Nothing]*): DataFrame = {
    spark.read
      .format(fileFormat)
      .options(options)
      .load(filePath)
  }
}