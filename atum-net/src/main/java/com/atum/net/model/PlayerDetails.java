package com.atum.net.model;

public class PlayerDetails {

	private enum Rights {
		NORMAL(0),
		MOD(1),
		ADMIN(2),
		OWNER(3);
		
		private int rights;
		
		Rights(int rights){
			this.rights = rights;
		}
		
	}
	
	private Rights rights = Rights.NORMAL;
}
