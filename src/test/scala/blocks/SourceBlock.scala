package blocks

import pipeline.{Block, BlockResult}

import scala.reflect.ClassTag

class SourceBlock[T](value: T)(implicit tag: ClassTag[T]) extends Block[Null, T]{
  override protected def blockProcess(args: BlockResult[Null]*): T = {
    value
  }
}
