package roundRobin
import akka.actor._

/* In this exercise, we implement an actor called RoundRobin declared as  
class RoundRobin[A <: Actor : scala.reflect.ClassTag](numChildren: Int) extends Actor. 
This actor should be able to create a number of children (numChildren) and enqueue them 
in an internal queue called children. For each message it receives, the RoundRobin actor 
should forward the message to the child found at the head of the children queue and 
move (enqueue) that child to the tail of the queue. The exercise teaches about 
creating children and forwarding messages.
*/

// A is a subtype of type Actor
class RoundRobin[A <: Actor: scala.reflect.ClassTag](numChildren: Int) extends Actor {
  if (numChildren <= 0)
    throw new IllegalArgumentException("numChildren must be positive")
  
  var children = collection.immutable.Queue[ActorRef]()

  override def preStart = {
    for (c <- 1 to numChildren) {
      // create number of children (numChildren)
      // enqueue them to children queue
      children = children.enqueue(context.actorOf(Props[A], s"$c"))
    }
  }

  def receive = {
    case msg: Object => 
      // send message to head of the children queue
      val dq = children.dequeue
      val headChild = dq._1
      headChild ! msg
      sender ! msg

      // then move child to tail of the children queue
      children = dq._2.enqueue(headChild)
  }
}
