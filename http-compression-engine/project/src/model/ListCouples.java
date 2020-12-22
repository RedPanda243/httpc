package model;

import java.util.*;

public class ListCouples implements Map<String,String> {

	private List<String> names = new ArrayList<>();
	private List<String> values = new ArrayList<>();

	@Override
	public int size() {
		return names.size();
	}

	@Override
	public boolean isEmpty() {
		return names.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return names.contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values.contains(value);
	}

	@Override
	public String get(Object key)
	{
		return values.get(names.indexOf(key));
	}

	@Override
	public String put(String key, String value) {
		int index = names.indexOf(key);
		if (index == -1)
		{
			names.add(key);
			values.add(value);
			return null;
		}
		return values.set(index,value);
	}

	@Override
	public String remove(Object key) {
		int index = names.indexOf(key);
		if (index == -1)
			return null;
		names.remove(index);
		return values.remove(index);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m)
	{
		for(String s:m.keySet())
			put(s,m.get(s));
	}

	@Override
	public void clear() {
		names.clear();
		values.clear();
	}

	@Override
	public Set<String> keySet() {
		return new HashSet<>(names);
	}

	public List<String> names()
	{
		return new ArrayList<>(names);
	}

	@Override
	public List<String> values() {
		return new ArrayList<>(values);
	}

	@Override
	public Set<Entry<String, String>> entrySet()
	{
		HashSet<Entry<String,String>> set = new HashSet<>();
		for (String n:names)
			set.add(new Couple(n,get(n)));
		return set;
	}
}
