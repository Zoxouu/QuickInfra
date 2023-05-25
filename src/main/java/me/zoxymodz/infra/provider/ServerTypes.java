package me.zoxymodz.infra.provider;

import com.google.gson.annotations.SerializedName;

public class ServerTypes{

	@SerializedName("min_ram")
	private final int minRam;

	@SerializedName("max_ram")
	private final int maxRam;

	@SerializedName("name")
	private final String name;

	public ServerTypes(int minRam, int maxRam, String name) {
		this.minRam = minRam;
		this.maxRam = maxRam;
		this.name = name;
	}

	public int getMinRam(){
		return minRam;
	}

	public int getMaxRam(){
		return maxRam;
	}

	public String getName(){
		return name;
	}

	public String getRam() {
		return String.format("-Xms%dM -Xmx%dM", minRam,maxRam);
	}
}