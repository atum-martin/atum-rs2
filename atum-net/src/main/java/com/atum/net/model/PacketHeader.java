package com.atum.net.model;

public enum PacketHeader {
	VARIABLE_BYTE(1), VARIABLE_SHORT(2), FIXED(0), EMPTY(0);
	private int size;
	
	PacketHeader(int size){
		this.size = size;
	}
	
	public int size(){
		return size;
	}
}