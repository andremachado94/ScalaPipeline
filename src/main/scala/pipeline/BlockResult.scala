package pipeline

import pipeline.constants.PipelineConstants
import java.time.LocalDateTime


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