/*
	SeriesSeason class holds data for each of the series and holds a list of Episodes
*/

package ser321.assign2.jwdouble.client;

import java.util.ArrayList;
import java.io.Serializable;
import org.json.JSONObject;
import org.json.JSONArray;

/*
* Copyright 2020 Jeremy Doubleday,
*
* This software is the intellectual property of the author, and can not be
distributed, used, copied, or
* reproduced, in whole or in part, for any purpose, commercial or otherwise.
The author grants the ASU
* Software Engineering program the right to copy, execute, and evaluate this
work for the purpose of
* determining performance of the author in coursework, and for Software
Engineering program evaluation,
* so long as this copyright and right-to-use statement is kept in-tact in such
use.
* All other uses are prohibited and reserved to the author.
*
* Purpose: A TV Show class for serializing between client and server.
*
* Ser321 Principles of Distributed Software Systems
* see http://pooh.poly.asu.edu/Ser321
* @author Jeremy Doubleday
* Software Engineering, CIDSE, IAFSE, ASU Poly
* @version 1.0
*/

public class SeriesSeason implements Serializable {
	private String seriesTitle;
	private int seasonNumber;
	private String seriesImdbRating;
	private String genre;
	private String posterLink;
	private String summary;
	private ArrayList<Episode> episodes;

	public SeriesSeason (String jasonString, String episodeString) {
		episodes = new ArrayList<Episode>();
		deSerializeSeason(new JSONObject(jasonString));
		deSerializeEpisode(new JSONObject(episodeString), true);
	}

	public SeriesSeason (JSONObject jason) {
		episodes = new ArrayList<Episode>();
		deSerializeSeason(jason);
		deSerializeEpisode(jason, false);
	}

	public SeriesSeason (String jasonString) {
		deSerializeSeason(new JSONObject(jasonString));
		deSerializeEpisode(new JSONObject(jasonString), false);
	}

	private boolean deSerializeSeason (JSONObject jason) {
		try {
			System.out.println("Herro!!!\n" + jason.toString());	
			seriesTitle = jason.getString("Title");	
			
			seriesImdbRating = jason.getString("imdbRating");
			genre = jason.getString("Genre");
			posterLink = jason.getString("Poster");
			summary = jason.getString("Plot");		
		} catch (Exception e) {
			System.out.println("deSerializeSeason broke: " + e.getMessage());
		}
		return true;
	}

	private boolean deSerializeEpisode (JSONObject jason, boolean flag) {
		try {
			seasonNumber = jason.getInt("Season");
			
			if (flag) {
				seriesTitle += " Season # " + String.valueOf(seasonNumber);
			}

			JSONArray jasonArray = jason.getJSONArray("Episodes");
			for (int i = 0; i < jasonArray.length(); i++) {
				episodes.add(new Episode(jasonArray.getJSONObject(i)));
			}
		} catch (Exception e) {
			System.out.println("deSerializeEpisode broke: " + e.getMessage());
			return false;
		}
		return true;
	}

	public Episode getEpisode (String title) {
		for (int i = 0; i < episodes.size(); i++) {
			if (episodes.get(i).getTitle() == title) {
				return episodes.get(i);
			}
		}
		return null;
	}

	public String toJsonString() {
		String ret = "{}";
      	try {
        	 ret = this.toJson().toString(4);
        	 System.out.println("returning " + ret);
      	} catch (Exception e) {
        	 System.out.println("Exception in toJsonString: " + e.getMessage());
      	}
      	return ret;
	}

	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		try {
				object.put("Title", seriesTitle);
				object.put("imdbRating", seriesImdbRating);
				object.put("Season", seasonNumber);
				object.put("Genre", genre);
				object.put("Poster", posterLink);
				object.put("Plot", summary);
				JSONArray jasonArray = new JSONArray();
				for (int j = 0; j < episodes.size(); j++) {
					jasonArray.put(episodes.get(j).toJson());
				}
				object.put("Episodes", jasonArray);
				
		} catch (Exception e) {
			System.out.println("Exception in toJSONObject: " + e.getMessage());
		}
		return object;
	}

	public ArrayList<Episode> getEpisodes () {
		return episodes;
	}

	
	public String getSeriesTitle () {
		return seriesTitle;
	}

	public String getRating () {
		return seriesImdbRating;
	}

	public int numEpisodes () {
		return episodes.size();
	}

	public int getSeason () {
		return seasonNumber;
	}

	public String getGenre () {
		return genre;
	}

	public String getSummary () {
		return summary;
	}

	public String getPoster () {
		return posterLink;
	}
}