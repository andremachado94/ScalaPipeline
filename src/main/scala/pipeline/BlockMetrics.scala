package pipeline

import java.time.LocalDateTime

private [pipeline] case class BlockMetrics(executionStatus: String, blockName: String, executionStartTime: LocalDateTime, executionEndTime: LocalDateTime)
