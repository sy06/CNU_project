package com.cnu_bus_alarm.cnu.network.network_client;

public interface CallbackEvent<T> {
	/* *
	 * 이 클래스를 상속하여 run()함수를 재정의한 후 인자로 넘길 수 있음.
	 * */
	public void run(T input);
}
