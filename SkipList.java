import java.lang.Comparable;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public class SkipList<T extends Comparable<? super T>> {

	/**
	 * SkipList node having its own element, an array of references to the next
	 * elements and a reference to the previous element.
	 */
	class SLEntry<T> {
		T element;
		SLEntry<T>[] next;
		SLEntry<T> previous;

		SLEntry(T x) {
			element = x;
			next = new SLEntry[maxLevel + 1];
			previous = null;
		}

		/**
		 * Constructor -
		 * 
		 * @param x:
		 *            T - element to be added
		 * @param noOfLevels:
		 *            Number of levels of next pointers it will store
		 */
		SLEntry(T x, int noOfLevels) {
			element = x;
			next = new SLEntry[noOfLevels + 1];
			previous = null;
			for (int i = 0; i < noOfLevels; i++) {
				next[i] = null;
			}
		}
	}

	SLEntry<T> head;
	SLEntry<T> tail;
	int size;
	int maxLevel;
	int threshold;

	/**
	 * Constructor
	 * 
	 * @param s
	 *            : size of the list
	 * @param headValue:
	 *            header sentinel element
	 * @param tailValue:
	 *            tail sentinel element
	 */
	SkipList(int s, T headValue, T tailValue) {
		size = 0;
		maxLevel = (int) Math.ceil(Math.log10(s) / Math.log10(2));
		threshold = s;
		head = new SLEntry<>(headValue);
		tail = new SLEntry<>(tailValue);
		for (int i = 0; i <= maxLevel; i++) {
			head.next[i] = tail;
			tail.next[i] = null;
		}
		tail.previous = head;
	}

	/**
	 * To find the previous position references in each level of referencing for
	 * a given element
	 * 
	 * @param x:
	 *            T
	 * @return: array of references/pointers previous to the given element
	 */
	SLEntry<T>[] find(T x) {
		SLEntry<T>[] prev = new SLEntry[maxLevel + 1];
		SLEntry<T> p;
		p = head;
		for (int i = maxLevel; i >= 0; i--) {
			while (p.next[i].element.compareTo(x) < 0) {
				p = p.next[i];
			}
			prev[i] = p;
		}
		return prev;
	}

	// Add an element x to the list. Returns true if x was a new element.
	/**
	 * To add an element to the skip list
	 * 
	 * @param x:
	 *            T
	 * @return: true - if it is new element, false - if element already exists
	 */
	boolean add(T x) {
		SLEntry<T>[] prev;
		prev = find(x);
		// element already exist
		if (prev[0].next[0].element.compareTo(x) == 0) {
			prev[0].next[0].element = x;
			return false;
		}
		// element doesn't exist - will be added at appropriate position
		else {
			int level = choice(maxLevel);// number of references being stored
			SLEntry<T> n = new SLEntry<>(x, level);// new node
			// storing the references into the node
			for (int i = 0; i <= level; i++) {
				n.next[i] = prev[i].next[i];
				prev[i].next[i] = n;
			}
			n.previous = n.next[0].previous;
			n.next[0].previous = n;
			size++;
			// if the size of the skiplist goes beyond the threshold rebuild the
			// skiplist using new maxLevel(i.e number of references/pointers
			// being stored)
			if (size >= threshold) {
				rebuild();
			}
			return true;
		}
	}

	/**
	 * To determine, probabilistically, the number of pointers to be stored for
	 * a node
	 * 
	 * @param level:
	 *            int - maximum possible levels/pointers
	 * @return: the probabilistically determined number between 0 and maxLevelF
	 */
	int choice(int level) {
		int i = 0;
		Random random = new Random();
		while (i < maxLevel) {
			if (random.nextBoolean())
				i++;
			else
				break;
		}
		return i;
	}

	/**
	 * To find least element that is >= x, or null if no such element
	 * 
	 * @param x:
	 *            T
	 * @return: T - element >= x if exist, else return null
	 */
	T ceiling(T x) {
		if (size == 0)
			return null;
		SLEntry<T>[] prev;
		prev = find(x);
		if (prev[0].next[0].element.compareTo(x) == 0)
			return x;
		else {
			if (prev[0].next[0] != tail)
				return prev[0].next[0].element;
			else
				return null;
		}
	}

	/**
	 * To find if the given element exist in the skip list
	 * 
	 * @param x:
	 *            T
	 * @return: true if found, else falseF
	 */
	boolean contains(T x) {
		if (size == 0)
			return false;
		SLEntry<T>[] prev;
		prev = find(x);
		return prev[0].next[0].element.compareTo(x) == 0;
	}

	/**
	 * To find an element based on the index
	 * 
	 * @param index:
	 *            int
	 * @return: T - element at that index if exist, else null
	 */
	T findIndex(int index) {
		if (index >= size || index < 0)
			return null;
		SLEntry<T> p = head.next[0];
		for (int i = 0; i < index; i++)
			p = p.next[0];
		return p.element;
	}

	/**
	 * Returns the first element of the skip lisst
	 * 
	 * @return: T
	 */
	T first() {
		return head.next[0] != tail ? head.next[0].element : null;
	}

	/**
	 * To find the greatest element that is <= x, or null if no such element
	 * 
	 * @param x:T
	 * @return: T
	 */
	T floor(T x) {
		if (size == 0)
			return null;
		SLEntry<T>[] prev;
		prev = find(x);
		if (prev[0].next[0].element.compareTo(x) == 0)
			return x;
		else {
			// no element < x exists
			if (prev[0] == head)
				return null;
			// element < x
			else
				return prev[0].element;
		}
	}

	/**
	 * TO check if the skip list is empty
	 * 
	 * @return: boolean - true - if it is empty, else false
	 */
	boolean isEmpty() { // Is the list empty?
		return size == 0;
	}

	/**
	 * Iterator for the skip list
	 * 
	 * @return: Iterator<T>
	 */
	Iterator<T> iterator() {
		return new skipListIterator();
	}

	/**
	 * Iterator for Skip List
	 *
	 */
	public class skipListIterator implements Iterator<T> {
		private SLEntry<T> currentNode;

		skipListIterator() {
			currentNode = head;
		}

		@Override
		public boolean hasNext() {
			return currentNode.next[0] != tail;
		}

		@Override
		public T next() {
			T element = currentNode.next[0].element;
			currentNode = currentNode.next[0];
			return element;
		}
	}

	/**
	 * Return the last element of the list
	 * 
	 * @return: T
	 */
	T last() {
		return tail.previous != head ? tail.previous.element : null;
	}

	/**
	 * Rebuild the list into a perfect skip list
	 */
	void rebuild() {
		// doubled the maxLevel - can be changed
		maxLevel = maxLevel * 2;
		threshold = (int) Math.pow(2, maxLevel);

		int level = 0;
		SLEntry<T>[] prev = new SLEntry[maxLevel + 1];
		SLEntry<T> newHead = head;
		SLEntry<T> newTail = tail;
		SLEntry<T> p = newHead.next[0];

		// head and tail with new maxLevels(i.e. number of pointers to the next
		// elements)
		head = new SLEntry<>(newHead.element, maxLevel);
		tail = new SLEntry<>(newTail.element, maxLevel);

		// Initialization of head and tail.
		// Storing prev array with the head, for further insertions from the
		// current skip list in order
		for (int i = 0; i <= maxLevel; i++) {
			head.next[i] = tail;
			tail.next[i] = null;
			prev[i] = head;
		}
		tail.previous = head;

		for (int i = 0; i < size; i++) {
			SLEntry<T> n = new SLEntry<>(p.element, level);
			for (int j = 0; j <= level; j++) {
				n.next[j] = prev[j].next[j];
				prev[j].next[j] = n;
				prev[j] = n;
			}
			// each pointer at each level points to the 2^level element away
			// from it
			level = (level + 1) % maxLevel;
			n.next[0].previous = n;
			p = p.next[0];
		}
	}

	/**
	 * Remove x from list; returns false if x was not in list
	 * 
	 * @param x:T
	 * @return: boolean
	 */
	boolean remove(T x) {
		if (size == 0)
			return false;
		SLEntry<T>[] prev;
		SLEntry<T> n;
		prev = find(x);
		n = prev[0].next[0];
		// element doesn't exist
		if (n.element.compareTo(x) != 0)
			return false;
		// element exists
		else {
			// change its next and previous pointers
			for (int i = 0; i <= maxLevel; i++) {
				if (prev[i].next[i] == n)
					prev[i].next[i] = n.next[i];
				else
					break;
			}
			n.next[0].previous = prev[0];
			size--;
			return true;
		}
	}

	/**
	 * returns the number of elements in the list
	 * 
	 * @return: int
	 */
	int size() {
		return size;
	}

	/**
	 * To print the elements in the list
	 */
	void printSList() {
		Iterator<T> itr = iterator();
		while (itr.hasNext())
			System.out.print(itr.next() + " ");
		System.out.println();
	}

	public static void main(String args[]) {
		SkipList<Integer> sk = new SkipList<>(8, Integer.MIN_VALUE, Integer.MAX_VALUE);
		TreeSet<Integer> ts = new TreeSet<>();
		Timer t = new Timer();
		Random random = new Random();
		int num;
		System.out.print("Input: ");
		for (int i = 0; i < 15; i++) {
			num = random.nextInt(100);
			System.out.print(num+" ");
			sk.add(num);
		}
		System.out.println();
		sk.printSList();
		
		sk.remove(sk.first());
		sk.remove(sk.last());
		sk.printSList();
		System.out.println("Floor 70 = "+sk.floor(70));
		System.out.println("Ceiling 37 = "+sk.ceiling(37));		
	}
}


/*
Input: 93 46 32 36 6 61 46 52 56 64 78 55 56 79 7 
6 7 32 36 46 52 55 56 61 64 78 79 93 
7 32 36 46 52 55 56 61 64 78 79 
Floor 70 = 64
Ceiling 37 = 46
*/