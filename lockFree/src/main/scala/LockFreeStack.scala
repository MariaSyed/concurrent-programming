package lockFree
import instrumentation.monitors.LockFreeMonitor
import atomicReference.SimpleAtomicReference

/*
* One of the main uses of a atomic types is to implement syncronization 
* perimitives such as semaphores and mutexes. It is also useul to implement
* lock-free algorithms.
*  
* Task: In this exersise we implement a lock-free stack using an atomic reference.
* Assume that you have a class implementing an atomic refrence called 
* SimpleAtomicReference with an interface as follows:
*
* class SimpleAtomicReference[V](initValue: V) {
*  protected var value:V
*
*  def compareAndSet(expect: V, update: V): Boolean
*  def get: V
*  def set(newValue: V): Unit 
*}
*
* Hint: refer and use compareAndSet(expect: V, update: V): Boolean method from
* past exercises. 
*/

/* Implement a lock free stack. */
class LockFreeStack[E](capacity: Int) extends LockFreeMonitor {
  class Node[E](val value: E) {
    var next: Node[E] = null
  }
  val top = new SimpleAtomicReference[Node[E]](null)
  // Do not add other variables

  def push(e: E): Unit = {
    var oldHead: Node[E] = null
    var newHead: Node[E] = new Node[E](e)

    do {
      oldHead = top.get
      newHead.next = oldHead
    } while (!top.compareAndSet(oldHead, newHead))
  } 

  def pop(): E = {
    var oldHead: Node[E] = null
    var newHead: Node[E] = null

    do {
      oldHead = top.get
      newHead = oldHead.next
    } while (!top.compareAndSet(oldHead, newHead))
    
    return oldHead.value
  }
}
