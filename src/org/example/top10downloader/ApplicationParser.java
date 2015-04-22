package org.example.top10downloader;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class ApplicationParser {

	private enum ParseTags { 
		NO_TAG, ENTRY_TAG, NAME_TAG, ARTIST_TAG, RELEASE_DATE;
		
		public static String tagName(ParseTags tag) {
			switch (tag) {
			case ENTRY_TAG:
				return "entry";
			case NAME_TAG:
				return "name";
			case ARTIST_TAG:
				return "artist";
			case RELEASE_DATE:
				return "releaseDate";
			default:
				return "noTag";
			}
		}
		
		public static ParseTags identifyTag(String tag)  {
			if (tag.equals("entry"))
				return ENTRY_TAG;
			else if (tag.equals("name"))
				return NAME_TAG;
			else if (tag.equals("artist"))
				return ARTIST_TAG;
			else if (tag.equals("releaseDate"))
				return RELEASE_DATE;
			else 
				return NO_TAG;
		}
	};
	
	public static ArrayList<Application> parse(String xmlData) {
		ArrayList<Application> applications = new ArrayList<Application>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			
			parser.setInput(new StringReader(xmlData));
			int eventType = parser.getEventType();
			String name = "";
			String artist = "";
			String releaseDate = "";
			ParseTags currentTag = ParseTags.NO_TAG;
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					currentTag = ParseTags.identifyTag(parser.getName());
				} else if (eventType == XmlPullParser.TEXT) {
					if (!parser.getText().trim().isEmpty()) {
						if (currentTag == ParseTags.NAME_TAG) {
							name = parser.getText();
						} else if (currentTag == ParseTags.ARTIST_TAG) {
							artist = parser.getText();
						} else if (currentTag == ParseTags.RELEASE_DATE) {
							releaseDate = parser.getText();
						} else {
							/* nothing */
						}
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					currentTag = ParseTags.identifyTag(parser.getName());
					
					if (currentTag == ParseTags.ENTRY_TAG) {
						applications.add(new Application(name, artist, releaseDate));
					}
				} else {
					/* nothing */
				}
				parser.next();
				eventType = parser.getEventType();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return applications;
	}
}
