package pipeline

import pipeline.exceptions._

import scala.annotation.meta.param
import scala.reflect.ClassTag

/** Class that represents the execution pipeline.
  *
  * Add class instances to the Pipeline that extend the [[Block]] class
  *
  * {{{
  *   val pipeline = new Pipeline()
  *
  *   val b1 = new Block1()
  *   val b2 = new Block2()
  *
  *   pipeline.addBlock(1L, b1)
  *   pipeline.addBlock(2L, b2)
  * }}}
  *
  * Then define the execution flow by connecting the blocks and execute it
  *
  * {{{
  *   pipeline.addConnection(1L, 2L)
  *
  *   pipeline.execute
  *
  *   val result = pipeline.getBlockResult[<Output data type>](2L).result.get
  * }}}
  *
  * In this case, Block1.run will be executed and its output will be passed as an input for the execution of Block2
  */
class Pipeline {
  private val controller = new PipelineController

  /** Adds a [[Block]] instance to the pipeline with a specified unique Id.
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
  def addBlock[I,O](blockUid: Long, block: Block[I,O])(implicit tagI: ClassTag[I], tagO: ClassTag[O]): Unit ={
    controller.addBlock[I,O](blockUid, block)
  }

  /** Adds a connection from block with id "source" to block with id "target"
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
  def addConnection(source: Long, target: Long): Unit = {
    controller.connect(source, target)
  }

  /** Executes the pipeline
    *
    * @throws pipeline.exceptions.DisconnectedPipelineException When the pipeline can be represented as a disconnected graph [[http://mathworld.wolfram.com/DisconnectedGraph.html]]
    */
  @throws(classOf[DisconnectedPipelineException])
  def execute():Unit = controller.execute()

  /**
    *
    * @param blockUid A Long value that represents a unique id for the Block
    * @tparam O Data type of the block output
    * @throws pipeline.exceptions.InvalidBlockResultException If the method is called before the execution of the pipeline or if there is no output
    * @return The result of the block with id "blockUid" as an object of type "O"
    */
  @throws(classOf[InvalidBlockResultException])
  def getBlockResult[O](blockUid: Long): BlockResult[O] = {
    controller.blockResults.getOrElse(blockUid, throw new InvalidBlockResultException(s"Block '$blockUid' wasn't executed yet or has an output of type 'None'")).asInstanceOf[BlockResult[O]]
  }
}
