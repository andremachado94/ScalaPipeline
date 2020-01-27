package pipeline

import java.time.LocalDateTime

import scala.reflect.ClassTag

abstract class Block[I,O](numberOfInputs: Int) {
  def this(){
    this(-1)
  }
  protected def blockProcess(args: BlockResult[I]*): O
  def run(args: BlockResult[I]*): BlockResult[O] = {
    val name = this.getClass.getSimpleName
    val st = LocalDateTime.now()
    if (numberOfInputs < -1) {
      new BlockResult[O](None, name, st, Option(new Exception("")))
    }
    else {
      if (!numberOfInputs.equals(-1) && !numberOfInputs.equals(args.length))
        new BlockResult[O](None, name, st, Option(new Exception("")))

      try {
        new BlockResult[O](Option(blockProcess(args: _*)), name, st)
      }
      catch {
        case t: Throwable => new BlockResult[O](None, name, st, Option(t))
      }
    }
  }
}
