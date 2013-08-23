package org.burnhams.optimiser;

import java.lang.reflect.Array;
import java.util.*;

public class Solution<T> implements Iterable<T>, Cloneable {

    private final T[] solution;
    private final List<T> listView;

    protected boolean hasChanged = true;

    public Solution(Solution<T> other) {
        this(Arrays.copyOf(other.solution, other.size()));
    }

    public Solution(Collection<T> items) {
        solution = items.toArray((T[])Array.newInstance(items.iterator().next().getClass(), items.size()));
        listView = Arrays.asList(solution);
    }

    @SafeVarargs
    public Solution(T... items) {
        solution = items;
        listView = Arrays.asList(solution);
    }

    public void shuffle() {
        Random rnd = new Random();
        for (int i = solution.length - 1; i >= 0; i--)
        {
            swap(rnd.nextInt(i + 1), i);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return listView.iterator();
    }

    public int size() {
        return solution.length;
    }

    public T get(int index) {
        return solution[index];
    }

    public List<T> getList() {
        return new ArrayList<>(listView);
    }

    public boolean swap(int index1, int index2) {
        T s = solution[index1];
        if (s.equals(solution[index2])) {
            return false;
        } else {
            solution[index1] = solution[index2];
            solution[index2] = s;
            hasChanged = true;
            return true;
        }
    }

    public void swap(int from1, int to1, int from2, int to2) {
        T[] original = Arrays.copyOf(solution, solution.length);
        int source = 0;
        for (int i = 0; i < solution.length; i++, source++) {
            if (source == to1) {
                source = to2;
            } else if (source == to2) {
                source = to1;
            }

            if (source == from1) {
                source = from2;
            } else if (source == from2) {
                source = from1;
            }
            solution[i] = original[source];
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Solution solution1 = (Solution) o;

        if (!Arrays.equals(solution, solution1.solution)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(solution);
    }

    @Override
    public Solution<T> clone() {
        return new Solution<T>(this);
    }

    @Override
    public String toString() {
        return "Solution{" +
                "solution=" + Arrays.toString(solution) +
                '}';
    }
}
