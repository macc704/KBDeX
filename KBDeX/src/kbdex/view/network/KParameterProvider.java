package kbdex.view.network;

public class KParameterProvider<T> {
	private T t;
	
	public KParameterProvider(T t){
		this.t = t;
	}

	public T get() {
		return t;
	}

	public void set(T t) {
		this.t = t;
	}
}
