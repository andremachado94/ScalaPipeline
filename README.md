# ScalaPipeline

### Implementing a custom Execution Block
#### SourceBlock
    import pipeline.{Block, BlockResult}
    import scala.reflect.ClassTag
    
    //SourceBlock Class
    class SourceBlock[T](value: T)(implicit tag: ClassTag[T]) extends Block[Any, T]{
      override protected def blockProcess(args: BlockResult[Any]*): T = {
        value
      }
    }
### Defining the pipeline
    val pipeline = new Pipeline

    val source = new SourceBlock[Int](3)
    val sink = new Multiply(2)

    pipeline.addBlock(1L, source)
    pipeline.addBlock(2L, sink)

    pipeline.addConnection(1L, 2L)

    pipeline.execute