package blocks

import pipeline.{Block, BlockResult}

import scala.reflect.ClassTag

class Multiply(value: Int = 1) extends Block[Int, Int] {
  override protected def blockProcess(args: BlockResult[Int]*): Int = {
    value * args.foldLeft(1){(acc, i) => acc * i.getResult}
  }
}