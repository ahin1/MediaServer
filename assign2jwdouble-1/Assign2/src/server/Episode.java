/**
	Episode class holds the information about each individual episode
**/

package ser321.assign2.jwdouble.client;

import org.json.JSONObject;
import java.io.Serializable;


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

public class Episode implements Serializable {
	private String title;
	private String episodeImdbRating;

	public Episode (String jasonString) {
		this(new JSONObject(jasonString));
	}

	public Episode (JSONObject jasonObject) {
		title = jasonObject.getString("Title");
		episodeImdbRating = jasonObject.getString("imdbRating");
		
	}

	public String getTitle () {
		return title;
	}

	public String getRating () {
		return episodeImdbRating;
	}

	public String toJsonString () {
		String ret = "{}";
		try {
			ret = this.toJson().toString(4);
		} catch (Exception e) {
			System.out.println("Exception in Episode toJsonString" + e.getMessage());
		}
		return ret;
	}

	public JSONObject toJson () {
		JSONObject object = new JSONObject();
		try {
			object.put("Title", title);
			object.put("imdbRating", episodeImdbRating);
		} catch (Exception e) {
			System.out.println("Exception in episode toJSonObject" + e.getMessage());
		}
		return object;
	}
}