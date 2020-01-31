package pipeline

import pipeline.constants.PipelineConstants
import java.time.LocalDateTime

/** Class that contains the result of a Block execution (result or exception thrown) and the execution metrics. 
  *
  * @param result Optional result of the block's execution
  * @param blockName Name of the Block class for metrics purposes
  * @param executionStartTime Execution start time of the block for metrics purposes
  * @param exception Optional exception thrown by the block's execution
  * @tparam T Result's data type
  */
case class BlockResult[T](result: Option[T], blockName: String, executionStartTime: LocalDateTime, exception: Option[Throwable] = None){

  private val executionStatus =
    if(result.isDefined && exception.isEmpty) PipelineConstants.METRICS_CODE_SUCCESS
    else if(result.isEmpty && exception.isDefined) PipelineConstants.METRICS_CODE_FAILURE
    else PipelineConstants.METRICS_CODE_IMPLEMENTATION_ERROR

  private val executionEndTime = LocalDateTime.now()

  //val metrics = BlockMetrics(executionStatus, blockName, tag.toString, executionStartTime, executionEndTime)
  val metrics = BlockMetrics(executionStatus, blockName, executionStartTime, executionEndTime)

  def successfulExecution: Boolean = executionStatus.equals(PipelineConstants.METRICS_CODE_SUCCESS)
  def getResult: T = result.getOrElse(throw exception.getOrElse(throw new Exception("You should not be seeing this exception")))
  //def getTag: ClassTag[T] = tag
}