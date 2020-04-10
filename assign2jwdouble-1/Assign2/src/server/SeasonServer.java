/**

	SeasonLibrary class holds the various season series
	
*/

package ser321.assign2.ahinric1.server;

import java.util.HashMap;
import java.io.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.net.URL;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONString;
import org.json.JSONTokener;

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

public class SeasonServer extends UnicastRemoteObject implements SeasonServerInt{
	private HashMap<String,SeriesSeason> seasons;
	private static final String fileName = "series.json";

	public SeasonServer () {
		seasons = new HashMap<String,SeriesSeason>();
		restoreLibraryFromFile();
	}

	public SeasonServer (SeriesSeason season) {
		seasons = new HashMap<String,SeriesSeason>();
		seasons.put(season.getSeriesTitle(), season);
	}

	public SeriesSeason get (String title) {
		return seasons.get(title);
	}

	public String[] getTitles () {
		return seasons.keySet().toArray(new String[seasons.size()]);
	}

	public boolean add (SeriesSeason season) {
		seasons.put(season.getSeriesTitle(), season);
		return true;
	}

	public boolean remove (String title) {
		try {
			seasons.remove(title);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String toString () {
		String output = "";
		String[] keys = this.getTitles();
		for (int i = 0; i < keys.length; i++) {
			output += seasons.get(keys[i]);
		}
		return output;
	}

	public String toJsonString() {
		String ret = "";
      	try {
      		String[] keys = this.getTitles();
      		for (int i = 0; i < keys.length; i++) {
      			ret += seasons.get(keys[i]).toJson().toString(4);
      		}
        	 System.out.println("returning " + ret);
      	} catch (Exception e) {
        	 System.out.println("Exception in toJsonString: " + e.getMessage());
      	}
      	return ret;
	}

	/**
	Creates a JasonArray stored as series to hold all the various series before serializing
	*/

	public JSONObject toJson () {
		JSONObject jasonObject = new JSONObject();
		String[] keys = this.getTitles();
		JSONArray jasonArray = new JSONArray();
		try {
			for (int i = 0; i < seasons.size(); i++) {
				jasonArray.put(seasons.get(keys[i]).toJson());		
			}
		} catch (Exception e) {
			System.out.println("toJson Library");
		}
		JSONObject tempObject = new JSONObject();
		tempObject.put("series", jasonArray);
		return tempObject;
	}

	public boolean saveLibraryToFile () {
		try{
			FileWriter outFile = new FileWriter(fileName);

			outFile.write(toJson().toString(4));				
			outFile.flush();
			outFile.close();
		} catch (Exception e) {
			System.out.println("Save library to file broke: " + e.getMessage());
			e.printStackTrace();
		}
		return true;
			
	}

	public boolean restoreLibraryFromFile () {
		try {
			

			InputStream input = this.getClass().getClassLoader().getResourceAsStream(this.fileName);
			if (input == null) {
				input = new FileInputStream(new File(this.fileName));
			}
			JSONObject jasonObject = new JSONObject(new JSONTokener(input));
			JSONArray jasonArray = (JSONArray)jasonObject.get("series");
			for (int i = 0; i < jasonArray.length(); i++) {
				System.out.println(jasonArray.get(i));
				String title;
				JSONObject jason = jasonArray.getJSONObject(i);
				SeriesSeason newSeason = new SeriesSeason(jason);
				title = newSeason.getSeriesTitle();
				seasons.put(title, newSeason);
			}
			
	        input.close();

		} catch (Exception e) {
			System.out.println("Restore from library broke: " + e.getMessage());
		}
		return true;
	}
	   public static void main(String args[]) {
	       try {
	          String hostId="localhost";
	          String regPort="1099";
	          if (args.length >= 2){
	             hostId=args[0];
	             regPort=args[1];
	          }
	          //System.setSecurityManager(new RMISecurityManager()); // rmisecmgr deprecated
	          SeasonServerInt obj = new SeasonServer();
	          Naming.rebind("rmi://"+hostId+":"+regPort+"/EmployeeServer", obj);
	          System.out.println("Server bound in registry as: "+
	                             "rmi://"+hostId+":"+regPort+"/EmployeeServer");
	       }catch (Exception e) {
	          e.printStackTrace();
	       }
	    }

}