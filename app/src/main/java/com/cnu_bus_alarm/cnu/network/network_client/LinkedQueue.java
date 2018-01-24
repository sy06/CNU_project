package com.cnu_bus_alarm.cnu.network.network_client;


public class LinkedQueue<T> implements Queue<T> {
	
	//private variables
	private int _size;
	private Node _front;
	private Node _rear;
	
	//Getters & Setters
	@Override
	public int size() {
		return this._size;
	}
	private void setSize(int newSize) {
		this._size = newSize;
	}
	private Node front() {
		if(this._front == null) {
			this._front = new Node();
		}
		if(this._front.next()==null) {
			return null;
		}
		return this._front.next();
	}
	
	
	private void setNewList() {
		this._front = new Node();
		this._rear = _front;
	}
	private void setFront(Node newFront) {
		if(this._front == null) {
			this.setNewList();
		}
		
		this._front.setNext(newFront);
	}
	
	
	private Node rear() {
		return this._rear;
	}
	private void setRear(Node newRear) {
		this._rear.setNext(newRear);
		this._rear = newRear;
	}
	//private methods
	private Node nextRear() {
		return this.rear().next();
	}
	private Node nextFront() {
		return this.front().next();
	}
	
	//Constructor
	@SuppressWarnings("unchecked")
	public LinkedQueue() {
		this.reset();
	}
	
	
	@Override
	public void reset() {
		this.setNewList();
		this.setSize(0);
	}

	@Override
	public boolean isEmpty() {
		return this.size()==0;
	}
	
	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public boolean add(T anElement) {
		this.setRear(new Node(anElement));
		this.setSize(this.size()+1);
		return true;
	}

	@Override
	public T pop() {
		if(this.isEmpty()) {
			return null;
		}
		else {
			T tmp = this.front().element();
			if(this.size()==1) {
				this.reset();
			}
			else {
				this.setSize(this.size()-1);
			
			this.setFront(this.nextFront());
			}
			return tmp;
		}
	}
	public void DEBUG_printAll() {
		System.out.print("모든 원소 출력 : ");
		Node n = this._front;
		if(n == null) {
			System.out.println("[err] front is null");
		}
		while(n!=null) {
			System.out.print(" "+n.element());
			n = n._next;
		}
		System.out.println("");
	}
	private class Node{
		private T _element=null;
		private  Node _next=null;
		public Node() {
			
		}
		public Node(T newElement) {
			this.setElement(newElement);
		}
		//public Getter/setter
		public T element() {
			return this._element;
		}
		public void setElement(T newElement) {
			this._element = newElement;
		}
		public Node next() {
			return this._next;
		}
		public void setNext(Node newNext) {
			this._next = newNext;
		}
	}
}
