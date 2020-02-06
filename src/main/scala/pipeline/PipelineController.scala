package pipeline

import pipeline.exceptions._
import pipeline.validations.PipelineValidations

import scala.annotation.tailrec
import scala.reflect.ClassTag

private [pipeline] class PipelineController extends PipelineValidations {
  var blocks: Map[Long, TaggedBlock[_, _]] = Map()
  var connections: Map[Long, Set[Long]] = Map()
  var blockResults: Map[Long, _] = Map()

  /** Adds a [[Block]] instance to the pipeline with a specified unique Id. This instance is stored as a [[TaggedBlock]] in the blocks Map.
    * Adds a key-value pair to the connections map with the "blockUid" as key and a an empty Set[Long] as value. ''i.e.'' "blockUid" is not connected to any other block
    *
    * @param blockUid A Long value that represents a unique id for the Block
    * @param block The Block instance
    * @param tagI Class Tag of the block's input type
    * @param tagO Class Tag of the block's output type
    * @tparam I Block's input type
    * @tparam O Block's output type
    * @throws pipeline.exceptions.BlockAlreadyExistsException If a block with the same id was already added.
    */
  @throws(classOf[BlockAlreadyExistsException])
  def addBlock[I, O](blockUid: Long, block: Block[I, O])(implicit tagI: ClassTag[I], tagO: ClassTag[O]): Unit = {
    failIfBlocksExist(blocks, blockUid)
    blocks += (blockUid -> TaggedBlock(block, tagI, tagO))
    connections += (blockUid -> Set.empty[Long])
  }

  /** Adds a connection from block with id "source" to block with id "target".
    *
    * @param source Id of the source block
    * @param target Id of the target block
    * @throws pipeline.exceptions.BlockDoesNotExistException If one of unique ids is not found
    * @throws pipeline.exceptions.BlockInputAndOutputDontMatchException If the input data type of the target is different from the output data type of the source
    * @throws pipeline.exceptions.NotAcyclicPipelineException If the connection between the blocks "source" and "target" creates a loop in the execution pipeline
    */
  @throws(classOf[BlockDoesNotExistException])
  @throws(classOf[BlockInputAndOutputDontMatchException])
  @throws(classOf[NotAcyclicPipelineException])
  def connect(source: Long, target: Long): Unit = {
    failIfBlocksDontExist(blocks, source, target)
    failIfConnectionTypesDontMatch(blocks, source, target)
    connections += (source -> (connections.getOrElse(source, Set.empty[Long]) + target))
    failIfLoopExits(source, connections)
  }

  /** Executes the pipeline.
    * Searches for the finish node of the pipeline and evokes the executeBlock method for those ids. Appends the execution result to the blockResults Map
    *
    * @throws pipeline.exceptions.DisconnectedPipelineException When the pipeline can be represented as a disconnected graph [[http://mathworld.wolfram.com/DisconnectedGraph.html]]
    */
  @throws(classOf[DisconnectedPipelineException])
  def execute(): Unit ={
    failIfNotFullyConnected(connections)
    getFinishNodes.foreach(executeBlock)
  }

  /** A recursive function that ....
    *
    * @todo This method's scaladoc
    *
    * @param blockUid A Long value that represents a unique id for the Block
    * @tparam O Block's output type
    * @return a [[BlockResult]] that contains the result and metrics of the block execution
    */
  private def executeBlock[O](blockUid: Long): BlockResult[O] = {
    val taggedBlock = blocks(blockUid)
    val currentBlock = taggedBlock.block.asInstanceOf[Block[taggedBlock.tagI.type, taggedBlock.tagO.type]]

    if(blockResults.contains(blockUid)) {
      println(s"Block $blockUid was already executed - skipping")
      blockResults(blockUid).asInstanceOf[BlockResult[O]]
    }
    else {
      val result = currentBlock.run(
        getSourcesOfNode(blockUid).map(executeBlock(_).asInstanceOf[BlockResult[taggedBlock.tagI.type]]): _*
      ).asInstanceOf[BlockResult[O]]

      blockResults += (blockUid -> result)
      result
    }
  }

  /** Get the last nodes of the pipeline
    *
    * @return a Seq[Long] with the uids of the last nodes of the pipeline
    */
  private def getFinishNodes: Seq[Long] = {
    connections.filter(_._2.isEmpty).keys.toSeq
  }

  /** Get the ids of the blocks that are the input for the block with id "blockUid"
    *
    * @param blockUid A Long value that represents a unique id for the Block
    * @return a Seq[Long] with the ids of the blocks that are the input for the block with id "blockUid"
    */
  private def getSourcesOfNode(blockUid: Long): Seq[Long] = {
    connections.filter(_._2.contains(blockUid)).keys.toSeq
  }
}
