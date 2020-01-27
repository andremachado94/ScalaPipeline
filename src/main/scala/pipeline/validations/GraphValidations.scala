package pipeline.validations

protected trait GraphValidations {
  //TODO: Detect the different block groups
  // WHY: Throw a more detailed exception
  def isFullyConnected[T](connections: Map[T, Set[T]]): Boolean = {
    val startingNode = connections.keys.toSeq.head
    checkConnections(startingNode, connections).size.equals(connections.keys.size)
  }

  private def checkConnections[T](node: T, connections: Map[T, Set[T]], visited: Set[T] = Set.empty[T]): Set[T] = {
    if (visited.contains(node))
      visited
    else {
      val nodeConnections = connections(node) ++ connections.keys.map(n => {
        if (connections(n).contains(node)) Set(n) else Set.empty[T]
      }).foldLeft(Set.empty[T]) { (acc, i) => acc ++ i }

      nodeConnections.map(n => checkConnections(n, connections, visited + node)).foldLeft(visited) { (acc, i) => acc ++ i }
    }
  }

  //TODO: Change cycle detection algorithm to something like Tarjan's strongly connected components algorithm
  // WHY: Detect the cycles and throw a more detailed exception
  /*
  def scanForLoops[T](connections: Map[T, Set[T]]): Boolean = {
    connections.keys.map(hasLoop(_, connections)).toSet.contains(true)
  }
  */
  def hasLoop[T](node: T, connections: Map[T, Set[T]], visited: Set[T] = Set.empty[T]): Boolean = {
    if (visited.contains(node))
      true
    else {
      connections(node).exists(n => {
        hasLoop(n, connections, visited + node)
      })
    }
  }
}
