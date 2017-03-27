package com.wohuizhong.client.app.util;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectionUtil {

	public static boolean isEmpty(Collection<?> list) {
		return list == null || list.size() == 0;
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.size() == 0;
	}

	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length <= 0;
	}

	public static boolean isEmpty(long[] array) {
		return array == null || array.length <= 0;
	}

	public static List<Integer> getSparseArrayKeys(SparseArray<?> sparseArray) {
		List<Integer> keys = new ArrayList<Integer>();
		for (int i=0; i < sparseArray.size(); i++) {
			keys.add(sparseArray.keyAt(i));
		}
		return keys;
	}

	public static <E> boolean isLastItem(List<E> list, E item) {
		return list.indexOf(item) == list.size() - 1;
	}

	public static <T> T[] arrayAppend(T[] array, T item) {
        List<T> arrayList = new ArrayList<>(Arrays.asList(array));
        arrayList.add(item);
        return arrayList.toArray(array);
    }

	public static <T> List<T> arrayToLinkedList(T[] array) {
		List<T> list = new LinkedList<>();
		for (T element : array) {
			list.add(element);
		}
		return list;
	}

	public static List<Long> arrayToList(long[] array) {
		List<Long> list = new ArrayList<>(array.length);
		for (long element: array) {
			list.add(element);
		}
		return list;
	}

	public static <T> ArrayList<T> arrayToList(T[] array) {
		ArrayList<T> list = new ArrayList<>();
		for (T element : array) {
			list.add(element);
		}
		return list;
	}
}
