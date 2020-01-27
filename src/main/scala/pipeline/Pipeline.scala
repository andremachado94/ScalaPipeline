package pipeline

import pipeline.exceptions.InvalidBlockResultException

import scala.reflect.ClassTag

class Pipeline {
  val controller = new PipelineController

  def addBlock[I,O](blockUid: Long, block: Block[I,O])(implicit tagI: ClassTag[I], tagO: ClassTag[O]): Unit ={
    controller.addBlock[I,O](blockUid, block)
  }

  def addConnection(source: Long, target: Long): Unit = {
    controller.connect(source, target)
  }

  def execute = controller.execute()

  def getBlockResult[O](blockUid: Long) = {
    controller.blockResults.getOrElse(blockUid, throw new InvalidBlockResultException(s"Block '$blockUid' wasn't executed yet or has an output of type 'None'")).asInstanceOf[BlockResult[O]]
  }
}
