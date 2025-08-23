package  org.model;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * NOTE: THIS PIECE OF THE CODE IS BASED ON ONE OF THE TASKS GIVEN OUT ON THE COURSE WITH THE CODE OF BMEVIIIAB00 BY Budapest University of Technology and Economics
 * A thread-safe stack implementation with additional features for game-specific use cases.
 * The stack supports synchronized push and pop operations, maintains a historical list of
 * elements, and reloads the stack from the list when needed.
 *
 * @param <T> the type of elements held in this stack
 */
public class ConcurrentStack<T> {
    private Deque<T> stack = new ArrayDeque<>();
    public ArrayList<T> list = new ArrayList<>();
    private int t;
    /**
     * Reloads the stack from the historical list, pushing all elements
     * from the list back onto the stack in reverse order.
     */
    public void reload() {
        for (int i = list.size() - 1; i >= 0; i--) {
            stack.push(list.get(i));
        }
    }
    /**
     * Clears the historical list of elements.
     */
    public void clear() {
        list.clear();
    }

    /**
     * Pushes an element onto the stack and adds it to the historical list.
     * Notifies all waiting threads.
     *
     * @param t the element to be pushed onto the stack
     */
    public synchronized void push(T t) {
        stack.push(t);
        list.add(t);
        notifyAll();
    }

    /**
     * Pops an element from the stack. If the stack is empty, waits until an
     * element is available. If the popped element has special values
     * (-69 or -420), an {@link InterruptedException} is thrown with a specific message.
     *
     * @return the popped element as an integer
     * @throws InterruptedException if the element is a special control value
     */
    public synchronized int pop() throws InterruptedException {
        while (stack.isEmpty()) {
            wait();
        }
        notifyAll();
        t = (int) stack.pop();
        if (t == -69) {
            throw new InterruptedException("GAME STOPPED");
        } else if (t == -420) {
            throw new InterruptedException("GAME RESTARTED");
        } else {
            return t;
        }
    }
    /**
     * Gets the latest element popped from the stack.
     *
     * @return the most recently popped element as an integer
     */
    public int getLatest() {
        return t;
    }
}