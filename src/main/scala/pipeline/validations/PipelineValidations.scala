package pipeline.validations

import pipeline.TaggedBlock
import pipeline.exceptions._

trait PipelineValidations extends GraphValidations {

  @throws(classOf[BlockDoesNotExistException])
  def failIfBlocksDontExist(blocks: Map[Long, Any], b: Long*): Unit = {
    b.foreach(bl => if (!blockExists(blocks, bl)) throw new BlockDoesNotExistException(s"Block with uid $bl does not exist."))
  }

  private def blockExists(blocks: Map[Long, Any], blockUid: Long): Boolean = {
    !blocks.getOrElse(blockUid, None).equals(None)
  }

  @throws(classOf[BlockAlreadyExistsException])
  def failIfBlocksExist(blocks: Map[Long, Any], b: Long*): Unit = {
    b.foreach(bl => if (blockExists(blocks, bl)) throw new BlockAlreadyExistsException(s"Block with uid $bl already exists."))
  }

  @throws(classOf[BlockInputAndOutputDontMatchException])
  def failIfConnectionTypesDontMatch(blocks: Map[Long, TaggedBlock[_, _]], fromBlock: Long, toBlock: Long): Unit = {
    if (!connectionTypesMatch(blocks, fromBlock, toBlock))
      throw new BlockInputAndOutputDontMatchException(s"A connection between blocks $fromBlock and $toBlock. Block $fromBlock has a return value of type ${blocks(fromBlock).tagO.toString} and $toBlock is expecting an input of type ${blocks(fromBlock).tagI.toString} ")
  }

  private def connectionTypesMatch(blocks: Map[Long, TaggedBlock[_, _]], fromBlock: Long, toBlock: Long): Boolean = {
    println(s"${blocks(toBlock).tagI} -- ${blocks(toBlock).tagO}")
    blocks(toBlock).tagI.equals(blocks(fromBlock).tagO)
  }

  @throws(classOf[NotAcyclicPipelineException])
  def failIfLoopExits(node: Long, connections: Map[Long, Set[Long]]): Unit ={
    if(hasLoop[Long](node, connections))
      throw new NotAcyclicPipelineException(s"The insertion of Block '$node' created a non acyclic pipeline.")
  }

  @throws(classOf[DisconnectedPipelineException])
  def failIfNotFullyConnected(connections: Map[Long, Set[Long]]): Unit ={
    if(!isFullyConnected[Long](connections)){
      throw new DisconnectedPipelineException(s"The pipeline is not fully connected")
    }
  }
}
