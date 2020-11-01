package semaphoreBlockQueue
import instrumentation.monitors._
import semaphore._
import scala.collection.mutable._

/*
* Another way of implementing a Blocking Queue is using semaphores. As we have encountered in the 
* previous exercises a Blocking Queue is a queue that blocks when you try to take from it and the 
* queue is empty, or if you try to put items to it and the queue is already full. A semaphore, 
* on the other hand, is an important abstract data type used to control access to a common resource 
* required by multiple execution units (threads) in a concurrent system.
*
* Task: In this exercise, we will implement a Blocking Queue based on a Semaphore similar 
* to what we have implemented in the last exercise. You are given Semaphore which has 
* acquire() and release() methods. You will need to base your Blocking Queue implementation on this 
* Semaphore. The Full interface definition of Semaphore is as follows: 
*     
*    class Semaphore(private val capacity: Int) extends Monitor {
*        var permits = capacity
*        def acquire(): Unit
*        def release(): Unit
*        def availablePermits(): Int = permits
*    }
*
* Hint: Take a look at the hints in the previous exercises related to Semaphore and Monitor Blocking Queue.
*
*/

class SemaphoreBlockingQueue[E](capacity: Int) extends Monitor {

  if (capacity <= 0) {
    throw new IllegalArgumentException("capacity must be positive")
  }

  /* Holds the elements of this BlockingQueue. */
  val queue = Queue[E]()

  /* Initialize semaphores. */
  var elementSem = new Semaphore(0)
  var capacitySem = new Semaphore(capacity)

  def put(e: E): Unit = this.synchronized {
    while (this.isFull()) {
      wait()
    }
    notifyAll()
    capacitySem.acquire()
    queue.enqueue(e)
  }

  def take(): E = this.synchronized {
    while(this.isEmpty()) {
      wait()
    }
    notifyAll()
    capacitySem.release()
    return queue.dequeue()
  }
   
  def isEmpty(): Boolean = capacitySem.availablePermits() == capacity
  def isFull(): Boolean = capacitySem.availablePermits() == 0
}
