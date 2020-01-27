import org.scalatest.FunSuite
import blocks.{Multiply, SourceBlock}
import pipeline.Pipeline

class BasicTypesPipelineTest extends FunSuite {
  test("Pipeline Test - one to one"){
    val pipeline = new Pipeline

    val source = new SourceBlock[Int](3)
    val sink = new Multiply(2)

    pipeline.addBlock(1L, source)
    pipeline.addBlock(2L, sink)

    pipeline.addConnection(1L, 2L)

    pipeline.execute
    assert(pipeline.getBlockResult[Int](2L).result.get.equals(6))
  }

  test("Pipeline Test - two to one"){
    val pipeline = new Pipeline

    val source1 = new SourceBlock[Int](3)
    val source2 = new SourceBlock[Int](2)
    val sink = new Multiply

    pipeline.addBlock(1L, source1)
    pipeline.addBlock(2L, source2)
    pipeline.addBlock(3L, sink)

    pipeline.addConnection(1L, 3L)
    pipeline.addConnection(2L, 3L)

    pipeline.execute
    assert(pipeline.getBlockResult[Int](3L).result.get.equals(6))
  }

  test("Pipeline Test - one to two"){
    val pipeline = new Pipeline

    val source = new SourceBlock[Int](3)
    val sink = new Multiply(2)

    pipeline.addBlock(1L, source)
    pipeline.addBlock(2L, sink)
    pipeline.addBlock(3L, sink)

    pipeline.addConnection(1L, 2L)
    pipeline.addConnection(1L, 3L)

    pipeline.execute
    assert(pipeline.getBlockResult[Int](2L).result.get.equals(6))
    assert(pipeline.getBlockResult[Int](3L).result.get.equals(6))
  }

}
