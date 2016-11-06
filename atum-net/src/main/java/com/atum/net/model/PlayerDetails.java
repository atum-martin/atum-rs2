package com.atum.net.model;

public class PlayerDetails {

	public enum Rights {
		NORMAL(0), MOD(1), ADMIN(2), OWNER(3);

		private int rights;

		Rights(int rights) {
			this.rights = rights;
		}

		public int value() {
			return rights;
		}

	}

	private Rights rights = Rights.NORMAL;
	private String username;
	private String password;
	private String uuid;

	public PlayerDetails(String username, String password, String uuid) {
		this.username = username;
		this.password = password;
		this.uuid = uuid;
	}

	public Rights getRights() {
		return rights;
	}

	public String getName() {
		return username;
	}
}
