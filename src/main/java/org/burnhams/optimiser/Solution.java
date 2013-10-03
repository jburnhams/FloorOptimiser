package org.burnhams.optimiser;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Solution<T> implements Cloneable {

    private final T[] fixedPrefix;
    private final int fixedLength;
    private final T[] solution;

    protected boolean hasChanged = true;

    public Solution(Solution<T> other) {
        this(other.fixedPrefix, Arrays.copyOf(other.solution, other.solution.length));
    }

    public Solution(Collection<T> items) {
        this(null, getArray(items));
    }

    public Solution(Collection<T> fixedPrefix, Collection<T> items) {
        this(getArray(fixedPrefix), getArray(items));
    }

    @SafeVarargs
    public Solution(T... items) {
        this(null, items);
    }

    public Solution(T[] fixedPrefix, T[] items) {
        this.fixedPrefix = fixedPrefix;
        fixedLength = fixedPrefix == null ? 0 : fixedPrefix.length;
        solution = items;
    }

    private static <T> T[] getArray(Collection<T> items) {
        return items==null||items.isEmpty()?null:items.toArray((T[])Array.newInstance(items.iterator().next().getClass(), items.size()));
    }

    public void shuffle() {
        Random rnd = new Random();
        for (int i = solution.length - 1; i >= 0; i--)
        {
            swap(rnd.nextInt(i + 1), i);
        }
    }

    public int totalSize() {
        return fixedLength + solution.length;
    }

    public int swappableSize() {
        return solution.length;
    }

    public T get(int index) {
        return index < fixedLength ? fixedPrefix[index] : solution[index-fixedLength];
    }

    public List<T> getList() {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        if (fixedLength > 0) {
            builder.add(fixedPrefix);
        }
        return builder.add(solution).build();
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
