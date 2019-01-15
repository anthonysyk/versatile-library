package versatile.memory

import akka.actor.Actor
import versatile.memory.RuntimeMemoryActor.MemoryTick

class RuntimeMemoryActor extends Actor {
  override def receive: Receive = {
    case MemoryTick =>
      // memory info in MB
      val mb = 1024 * 1024
      val runtime = Runtime.getRuntime
      println("** Used Memory:  " + (runtime.totalMemory - runtime.freeMemory) / mb)
      println("** Free Memory:  " + runtime.freeMemory / mb)
      println("** Total Memory: " + runtime.totalMemory / mb)
      println("** Max Memory:   " + runtime.maxMemory / mb)
      println("\n")
  }
}

object RuntimeMemoryActor {

  case object MemoryTick

}
