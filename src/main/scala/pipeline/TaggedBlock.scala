package pipeline

import scala.reflect.ClassTag

private [pipeline] case class TaggedBlock[I,O](block: Any, tagI: ClassTag[I], tagO: ClassTag[O])
