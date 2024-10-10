package info.kgeorgiy.ja.podkorytov.arrayset;

import java.util.*;
import java.util.function.UnaryOperator;

public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E>, List<E> {
    private final List<E> data;
    private final Comparator<? super E> comparator;

    /*
    Constructors
     */
    public ArraySet() {
        data = Collections.emptyList();
        comparator = null;
    }

    public ArraySet(Collection<? extends E> col) {
        data = new ArrayList<>(new TreeSet<>(col));
        comparator = null;
    }

    // :NOTE: Обобщить с предыдущим (TreeSet)
    public ArraySet(Collection<? extends E> col, Comparator<? super E> comp) {
        SortedSet<E> orderedElements = new TreeSet<>(comp);
        // Now we can use this set to reorder elements according to comparator
        orderedElements.addAll(col);
        comparator = comp;
        data = new ArrayList<>(orderedElements);
    }

    // Non-copying ctor
    private ArraySet(List<E> data, Comparator<? super E> comparator, boolean nonCopy) {
        this.data = data;
        this.comparator = comparator;
    }

    /*
    size() & empty()
     */

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(data, (E) o, comparator) >= 0;
    }

    /*
    Comparator getter
     */

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    /*
    Helper methods
     */

    // :NOTE: move top
    private ArraySet(Comparator<? super E> cmp) {
        data = Collections.emptyList();
        comparator = cmp;
    }

    private boolean checkIndex(int i) {
        return i >= 0 && i < size();
    }

    private int getIndex(E e, int shiftFound, int shiftNotFound) {
        // :NOTE: Collections.binarySearch can be used once
        int i = Collections.binarySearch(data, e, comparator);
        if (i < 0) {
            i = -(i + 1);
            return checkIndex(i + shiftNotFound) ? (i + shiftNotFound) : -1;
        }
        return checkIndex(i + shiftFound) ? (i + shiftFound) : -1;
    }

    private E bound(E e, int shiftFound, int shiftNotFound) {
        int idx = getIndex(e, shiftFound, shiftNotFound);
        return checkIndex(idx) ? data.get(idx) : null;
    }

    private E lowerBound(E e) {
        return bound(e, 0, 0);
    }

    private E upperBound(E e) {
        return bound(e, 1, 0);
    }

    private E upperBoundDown(E e) {
        return bound(e, 0, -1);
    }

    private E lowerBoundDown(E e) {
        return bound(e, -1, -1);
    }

    private boolean checkComparableElements(E from, E to) {
        return Collections.reverseOrder(comparator).reversed().compare(from, to) > 0;
    }

    /*
    NavigableSet methods
     */

    @Override
    public E lower(E e) {
        return lowerBoundDown(e);
    }

    @Override
    public E floor(E e) {
        return upperBoundDown(e);
    }

    @Override
    public E ceiling(E e) {
        return lowerBound(e);
    }

    @Override
    public E higher(E e) {
        return upperBound(e);
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @Override
    public E pollLast()
    {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }


    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(data).iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(
                data.reversed(),
                Collections.reverseOrder(comparator),
                true
        );
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        if (checkComparableElements(fromElement, toElement)) {
            throw new IllegalArgumentException("Invalid arguments provided"); // :NOTE: more info, a < b...
        }

        int shiftFound = fromInclusive ? 0 : 1;
        int fromIndex = getIndex(fromElement, shiftFound, 0);
        int shiftFount2 = toInclusive ? 0 : -1;
        int toIndex = getIndex(toElement, shiftFount2, -1);

        if (fromIndex > toIndex || fromIndex < 0) {
            return new ArraySet<>(comparator);
        }
        return new ArraySet<>(data.subList(fromIndex, toIndex + 1), comparator, true);
    }

    // :NOTE: headSet/tailSet one-liners
    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        if (isEmpty()) {
            return new ArraySet<>(comparator);
        }
        if (checkComparableElements(first(), toElement)) {
            return new ArraySet<>(comparator);
        }
        return subSet(first(), true, toElement, inclusive);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        if (isEmpty()) {
            return new ArraySet<>(comparator);
        }
        if (checkComparableElements(fromElement, last())) {
            return new ArraySet<>(comparator);
        }
        return subSet(fromElement, inclusive, last(), true);
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        if (checkComparableElements(fromElement, toElement)) {
            throw new IllegalArgumentException("Invalid arguments provided");
        }
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E removeFirst() {
        // :NOTE: ifEmpty -> NoSuchElementException, else:
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @Override
    public E removeLast() {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @Override
    public ArraySet<E> reversed() {
        return new ArraySet<>(
                data.reversed(),
                Collections.reverseOrder(comparator)
        );
    }

    @Override
    public E first() {
        if (data.isEmpty()) {
            throw new NoSuchElementException("Can't use first() on empty collection");
        }
        return data.getFirst();
    }

    @Override
    public E last() {
        if (data.isEmpty()) {
            throw new NoSuchElementException("Can't use last() on empty collection");
        }
        return data.get(size() - 1);
    }

    @Override
    public Spliterator<E> spliterator() {
        return NavigableSet.super.spliterator();
    }

    @Override
    public void addFirst(E e) {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @Override
    public void addLast(E e) {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @Override
    public E getFirst() {
        return data.getFirst();
    }

    @Override
    public E getLast() {
        return data.getLast();
    }

    /*
        List methods
     */

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @Override
    public void sort(Comparator<? super E> c) {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @Override
    public E get(int index) {
        return data.get(index);
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("Can't modify immutable set");
    }

    @SuppressWarnings("unchecked")
    @Override
    public int indexOf(Object e) {
        int idx =  Collections.binarySearch(data, (E) e, comparator);
        if (idx < 0) {
            return -1;
        }
        return idx;
    }

    @Override
    public int lastIndexOf(Object o) {
        return indexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return data.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return data.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return data.subList(fromIndex, toIndex);
    }
}
