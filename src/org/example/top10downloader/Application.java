package org.example.top10downloader;

public class Application {

	private String name;
	private String artist;
	private String releaseDate;
	
	public Application(String name, String artist, String releaseDate) {
		this.name = name;
		this.artist = artist;
		this.releaseDate = releaseDate;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public String getReleaseDate() {
		return releaseDate;
	}
	
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Override
	public String toString() {
		return name + " by " + artist + "\nRelease date " + releaseDate;
	}
	
}
