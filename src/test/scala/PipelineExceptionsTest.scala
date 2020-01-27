import blocks.{Multiply, SourceBlock}
import org.scalatest.FunSuite
import pipeline.Pipeline
import pipeline.exceptions._

class PipelineExceptionsTest extends FunSuite{
  test("Exceptions Test - Repeated block insertion"){
    val pipeline = new Pipeline

    val source = new SourceBlock[Int](3)
    val sink = new Multiply(2)

    pipeline.addBlock(1L, source)
    intercept[BlockAlreadyExistsException] {
      pipeline.addBlock(1L, sink)
    }
  }

  test("Exceptions Test - Add connection to block that does not exist "){
    val pipeline = new Pipeline

    val source = new SourceBlock[Int](3)

    pipeline.addBlock(1L, source)
    intercept[BlockDoesNotExistException] {
      pipeline.addConnection(1L, 2L)
    }
    intercept[BlockDoesNotExistException] {
      pipeline.addConnection(2L, 1L)
    }
  }

  test("Exceptions Test - Block inputs and outputs don't match"){
    val pipeline = new Pipeline

    val source = new SourceBlock[String]("3")
    val sink = new Multiply(2)

    pipeline.addBlock(1L, source)
    pipeline.addBlock(2L, sink)

    intercept[BlockInputAndOutputDontMatchException] {
      pipeline.addConnection(1L,2L)
    }
  }

  test("Exceptions Test - Disconnected pipeline"){
    val pipeline = new Pipeline

    val source = new SourceBlock[Int](3)
    val sink = new Multiply(2)

    pipeline.addBlock(1L, source)
    pipeline.addBlock(2L, sink)

    intercept[DisconnectedPipelineException] {
      pipeline.execute
    }
  }

  test("Exceptions Test - Cyclic pipeline - Test 1"){
    val pipeline = new Pipeline

    val source = new SourceBlock[Int](3)
    val sink = new Multiply(2)

    pipeline.addBlock(1L, source)
    pipeline.addBlock(2L, sink)
    pipeline.addBlock(3L, sink)

    pipeline.addConnection(1L,2L)

    intercept[NotAcyclicPipelineException] {
      pipeline.addConnection(2L,2L)
    }
    /*
    pipeline.addConnection(2L,3L)
    intercept[NotAcyclicPipelineException] {
      pipeline.addConnection(3L,2L)
    }
    */
  }

  test("Exceptions Test - Cyclic pipeline - Test 2"){
    val pipeline = new Pipeline

    val source = new SourceBlock[Int](3)
    val sink = new Multiply(2)

    pipeline.addBlock(1L, source)
    pipeline.addBlock(2L, sink)
    pipeline.addBlock(3L, sink)

    pipeline.addConnection(1L,2L)
    pipeline.addConnection(2L,3L)
    intercept[NotAcyclicPipelineException] {
      pipeline.addConnection(3L,2L)
    }
  }

}
