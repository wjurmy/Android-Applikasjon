package com.tigot.rozgar.utils.bean;

import java.io.Serializable;

public class Holder<A> implements Serializable {
	private A value;
	public Holder(A value) {
		this.value = value;
	}
	public A getValue() {
		return value;
	}
	public void setValue(A value) {
		this.value = value;
	}
}
