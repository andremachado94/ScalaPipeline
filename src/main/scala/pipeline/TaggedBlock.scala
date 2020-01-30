package pipeline

import scala.reflect.ClassTag

/** An auxiliary class to store a Block and it's Input and Output Class Tags
  *
  * @param block An instance of [[Block]]
  * @param tagI Class Tag of the block's input type
  * @param tagO Class Tag of the block's output type
  * @tparam I Block's input type
  * @tparam O Block's output type
  */
private [pipeline] case class TaggedBlock[I,O](block: Any, tagI: ClassTag[I], tagO: ClassTag[O])
