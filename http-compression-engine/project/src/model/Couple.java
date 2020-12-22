package model;

import java.util.Map;

public class Couple implements Map.Entry<String,String>
{
	private String key;
	private String value;

	public Couple(String n,String v)
	{
		key = n;
		value = v;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String setValue(String value) {
		String box = this.value;
		this.value = value;
		return box;
	}
}
