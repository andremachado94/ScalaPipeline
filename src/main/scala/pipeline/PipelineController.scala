package pipeline

import pipeline.validations.PipelineValidations

import scala.collection.mutable
import scala.reflect.ClassTag

private [pipeline] class PipelineController extends PipelineValidations {
  var blocks: Map[Long, TaggedBlock[_, _]] = Map()
  var connections: Map[Long, Set[Long]] = Map()
  var blockResults: Map[Long, _] = Map()

  def addBlock[I, O](blockUid: Long, block: Block[I, O])(implicit tagI: ClassTag[I], tagO: ClassTag[O]): Unit = {
    failIfBlocksExist(blocks, blockUid)
    blocks += (blockUid -> TaggedBlock(block, tagI, tagO))
    connections += (blockUid -> Set.empty[Long])
  }

  def connect(source: Long, target: Long): Unit = {
    failIfBlocksDontExist(blocks, source, target)
    failIfConnectionTypesDontMatch(blocks, source, target)
    connections += (source -> (connections.getOrElse(source, Set.empty[Long]) + target))
    failIfLoopExits(source, connections)
  }

  def execute(): Unit ={
    failIfNotFullyConnected(connections)
    getFinishNodes().foreach(fn => {
      val res = executeBlock(fn)
      blockResults += (fn -> res)
    })
  }

  private def executeBlock[O](blockUid: Long): BlockResult[O] = {
    val tb = blocks(blockUid)
    val taggedBlock = tb.asInstanceOf[TaggedBlock[tb.tagI.type , tb.tagO.type ]]
    val currentBlock = taggedBlock.block.asInstanceOf[Block[taggedBlock.tagI.type, taggedBlock.tagO.type]]
    if(blockResults.contains(blockUid)) {
      println(s"Block $blockUid was already executed - skipping")
      blockResults(blockUid).asInstanceOf[BlockResult[O]]
    }
    else
      currentBlock.run(
        getSourcesOfNode(blockUid).map(
          sourceUid => {
            val res = executeBlock(sourceUid).asInstanceOf[BlockResult[taggedBlock.tagI.type]]
            blockResults += (sourceUid -> res)
            res

          }): _*).asInstanceOf[BlockResult[O]]
  }

  private def getFinishNodes(): Seq[Long] = {
    connections.filter(_._2.isEmpty).keys.toSeq
  }

  private def getSourcesOfNode(node: Long): Seq[Long] = {
    connections.filter(_._2.contains(node)).keys.toSeq
  }
}
