/**
 * This module acts as the View layer for your application.
 * The 'MediaLibraryGui' class actually builds the Gui with all
 * the components - buttons, text fields, text areas, panels etc.
 * This class should be used to write the logic to add functionality
 * to the Gui components.
 * You are free add more files and further modularize this class's
 * functionality.
 */
package ser321.assign2.lindquis;

import javax.swing.*;
import ser321.assign2.ahinric1.client.*;
import ser321.assign2.lindquis.MediaLibraryGui;

import java.io.*;
import java.nio.file.Paths;
import java.rmi.*;
import java.nio.charset.Charset;
import javax.sound.sampled.*;
import java.beans.*;
import java.net.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.lang.Runtime;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URLConnection;
import java.time.Duration;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

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

public class SeriesSeasonRMIClient extends MediaLibraryGui implements
TreeWillExpandListener,
ActionListener,
TreeSelectionListener {

	private static final boolean debugOn = true;
    private static final String pre = "https://www.omdbapi.com/?apikey=";
	private static String urlOMBD;
	private String url;
	private SeasonServerInt library;
	private String omdbKey;
	private SeriesSeason searchSeason;
	private	String imageURL = "https://i.ibb.co/5kvMPkp/rsz-breakfastclub.png";


	public SeriesSeasonRMIClient(String author, String authorKey, SeasonServerInt library) {
        // sets the value of 'author' on the title window of the GUI.
		super(author);
		this.omdbKey = authorKey;
		urlOMBD = pre + authorKey + "&t=";
		this.library = library; // TODO: this would need to be your  SeriesLibraryImpl

		// register this object as an action listener for menu item clicks. This will cause
		// my actionPerformed method to be called every time the user selects a menuitem.
		for(int i=0; i<userMenuItems.length; i++){
			for(int j=0; j<userMenuItems[i].length; j++){
				userMenuItems[i][j].addActionListener(this);
			}
		}
		// register this object as an action listener for the Search button. This will cause
		// my actionPerformed method to be called every time the user clicks the Search button
		searchJButt.addActionListener(this);
		try{
			//tree.addTreeWillExpandListener(this);  // add if you want to get called with expansion/contract
			tree.addTreeSelectionListener(this);
			rebuildTreeFromLibrary();
		}catch (Exception ex){
			JOptionPane.showMessageDialog(this,"Handling "+
					" constructor exception: " + ex.getMessage());
		}
		try{
			/*
			 * display an image just to show how the album or artist image can be displayed in the
			 * app's window. setAlbumImage is implemented by MediaLibraryGui class. Call it with a
			 * string url to a png file as obtained from an album search.
			 */

			// TODO: set album image here
		}catch(Exception ex){
			System.out.println("unable to open image");
		}
		setVisible(true);
	}

	/**
	 * A method to facilitate printing debugging messages during development, but which can be
	 * turned off as desired.
     * @param message Is the message that should be printed.
     * @return void
	 */
	private void debug(String message) {
		if (debugOn)
			System.out.println("debug: "+message);
	}

	/**
	 * Create and initialize nodes in the JTree of the left pane.
	 * buildInitialTree is called by MediaLibraryGui to initialize the JTree.
	 * Classes that extend MediaLibraryGui should override this method to 
	 * perform initialization actions specific to the extended class.
	 * The default functionality is to set base as the label of root.
	 * In your solution, you will probably want to initialize by deserializing
	 * your library and displaying the categories and subcategories in the
	 * tree.
	 * @param root Is the root node of the tree to be initialized.
	 * @param base Is the string that is the root node of the tree.
	 */
	public void buildInitialTree(DefaultMutableTreeNode root, String base){
		//set up the context and base name
		try{
			root.setUserObject(base);

			}catch (Exception ex){
			JOptionPane.showMessageDialog(this,"exception initial tree:"+ex);
			ex.printStackTrace();
		}
		
	}

	/**
	 * TODO
	 * method to build the JTree of media shown in the left panel of the UI. The
	 * field tree is a JTree as defined and initialized by MediaLibraryGui class.
	 * It is defined to be protected so it can be accessed by extending classes.
	 * This version of the method uses the music library to get the names of
	 * tracks. Your solutions will need to replace this structure with one that
	 * you need for the series/season and Episode. These two classes should store your information. 
	 * Your library (so a changes - or newly implemented MediaLibraryImpl) will store 
	 * and provide access to Series/Seasons and Episodes.
	 * This method is provided to demonstrate one way to add nodes to a JTree based
	 * on an underlying storage structure.
	 * See also the methods clearTree, valueChanged defined in this class, and
	 * getSubLabelled which is defined in the GUI/view class.
	 **/
	public void rebuildTreeFromLibrary (){
		try {
			tree.removeTreeSelectionListener(this);
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
			clearTree(root, model);
			
			// put nodes in the tree for all registered with the library

			String[] titleList = library.getTitles();
			
			for (int i = 0; i<titleList.length; i++){

				int placement;
				SeriesSeason season = library.get(titleList[i]);
				String title = season.getSeriesTitle();
				debug("Adding series with title:" + title);

				DefaultMutableTreeNode seasonNode = getSubLabelled(root, title);

				if (seasonNode == null) {
					seasonNode = new DefaultMutableTreeNode(title);
				} 

				model.insertNodeInto(seasonNode, root, i);

				ArrayList<Episode> episodes = new ArrayList<Episode>();
				episodes = season.getEpisodes();

				for (int j = 0; j < episodes.size(); j++) {
					String episodeTitle = episodes.get(j).getTitle();
					DefaultMutableTreeNode episodeNode = getSubLabelled(seasonNode, episodeTitle);	
					
					if(episodeNode != null) { // if seriesSeason subnode already exists
					
						debug("Episode exists: " + episodeTitle);
					
						model.insertNodeInto(episodeNode, seasonNode, j);
					} else { // album node does not exist
						
						episodeNode = new DefaultMutableTreeNode(episodeTitle);
						
						debug("no episode, so adding one with name: " + episodeTitle);
						
						model.insertNodeInto(episodeNode, seasonNode, j);
						
					}
				}
			}
			// expand all the nodes in the JTree
			for(int r =0; r < tree.getRowCount(); r++){
				tree.expandRow(r);
			}
			setAlbumImage(imageURL);
			tree.addTreeSelectionListener(this);	
		} catch (Exception e) {
			System.out.println("rebuild tree broke: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void rebuildTreeFromSearch (SeriesSeason newSeason) {
		tree.removeTreeSelectionListener(this);
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		clearTree(root, model);

		ArrayList<Episode> episodes = new ArrayList<Episode>();
		DefaultMutableTreeNode seasonNode = new DefaultMutableTreeNode(newSeason.getSeriesTitle());
		episodes = newSeason.getEpisodes();
		model.insertNodeInto(seasonNode, root, root.getChildCount());

		for (int i = 0; i < episodes.size(); i++) {
			String episodeTitle = episodes.get(i).getTitle();
			DefaultMutableTreeNode episodeNode = new DefaultMutableTreeNode(episodeTitle);
			model.insertNodeInto(episodeNode, seasonNode, i);
			System.out.println(episodeTitle);
		}
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		tree.addTreeSelectionListener(this);
	}

    /**
     * Remove all nodes in the left pane tree view.
     *
     * @param root Is the root node of the tree.
     * @param model Is a model that uses TreeNodes.
     * @return void
     */
	private void clearTree(DefaultMutableTreeNode root, DefaultTreeModel model){
		try{
			DefaultMutableTreeNode next = null;
			int subs = model.getChildCount(root);
			for(int k=subs-1; k>=0; k--){
				next = (DefaultMutableTreeNode)model.getChild(root,k);
				debug("removing node labelled:"+(String)next.getUserObject());
				model.removeNodeFromParent(next);
			}
		}catch (Exception ex) {
			System.out.println("Exception while trying to clear tree:");
			ex.printStackTrace();
		}
	}

	public void treeWillCollapse(TreeExpansionEvent tee) {
		debug("In treeWillCollapse with path: "+tee.getPath());
		tree.setSelectionPath(tee.getPath());
	}

	public void treeWillExpand(TreeExpansionEvent tee) {
		debug("In treeWillExpand with path: "+tee.getPath());
	}

	// TODO: 
	// this will be called when you click on a node. 
	// It will update the node based on the information stored in the library
	// this will need to change since your library will be of course totally different
	// extremely simplified! E.g. make sure that you display sensible content when the root,
	// the My Series, the Series/Season, and Episode nodes are selected
	public void valueChanged(TreeSelectionEvent e) {
		try{
			tree.removeTreeSelectionListener(this);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tree.getLastSelectedPathComponent();
			if(node!=null){
				String nodeLabel = (String)node.getUserObject();
				debug("In valueChanged. Selected node labelled: "+ nodeLabel);
				// is this a terminal node?

				// All fields empty to start with
				seriesSeasonJTF.setText(""); 
				genreJTF.setText(""); 
				ratingJTF.setText(""); 
				episodeJTF.setText("");
				summaryJTA.setText("");

				debug("Text has been set");

				DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot(); // get the root
                // First (and only) child of the root (username) node is 'My Series' node.
				DefaultMutableTreeNode mySeries = (DefaultMutableTreeNode)root.getChildAt(0); // mySeries node
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();

				debug("root parent all set");

				// TODO when it is an episode change the episode to something and set the rating to the episode rating
				if (node.getChildCount() == 0) {
					debug("enterint if block");
					Episode episode;
					SeriesSeason season;
					if (searchSeason == null) {
						episode = library.get((String)parent.getUserObject()).getEpisode(nodeLabel);
						season = library.get((String)parent.getUserObject());	
					} else {
						season = searchSeason;
						episode = season.getEpisode((String)node.getUserObject());
						
					}
					
					setAlbumImage(season.getPoster());

					// TODO just setting some values so you see how it can be done, they do not fit the fields!
					episodeJTF.setText(nodeLabel); // name of the episode
					ratingJTF.setText(episode.getRating()); // change to rating of the episode 
					String parentLabel = (String)parent.getUserObject();
					genreJTF.setText(season.getGenre()); // change to genre of the series from library
					summaryJTA.setText(season.getSummary()); // change to Plot of library for season
					seriesSeasonJTF.setText(parentLabel); // Change to season name
					
				} else if (parent == root) { // should be the series/season
					debug("entering else block");
					SeriesSeason season;
					if (searchSeason == null) {
						debug("in try block");
						season = library.get(nodeLabel);
						debug("leaving try block");
					} else {
						season = searchSeason;
					}
					setAlbumImage(season.getPoster());
					seriesSeasonJTF.setText(nodeLabel); // season name
					genreJTF.setText(season.getGenre()); // genre of the series from library
					ratingJTF.setText(season.getRating()); // rating of the season get from library
					episodeJTF.setText(""); // nothing in here since not an episode
					summaryJTA.setText(season.getSummary());
				}
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
		tree.addTreeSelectionListener(this);
	}

	// TODO: this is where you will need to implement a bunch. So when some action is called the correct thing happens
	public void actionPerformed(ActionEvent e) {
		tree.removeTreeSelectionListener(this);
		if(e.getActionCommand().equals("Exit")) {
			System.exit(0);
		}else if(e.getActionCommand().equals("Save")) {
			boolean savRes = true;
			library.saveLibraryToFile();
			System.out.println("Save "+((savRes)?"successful":"not implemented")); //TODO implement that current library is saved to JSON file
		}else if(e.getActionCommand().equals("Restore")) {
			boolean resRes = true;
			rebuildTreeFromLibrary();
			tree.removeTreeSelectionListener(this);
			System.out.println("Restore "+((resRes)?"successful":"not implemented")); // TODO: implement that tree is restored to library
		}else if(e.getActionCommand().equals("Series-SeasonAdd")) {
			if (searchSeason != null) {
				library.add(searchSeason);			
				System.out.println("Season added to library: " + library.toString());	
			} else {
				System.out.println("Nothing searched to add");
			}
 			
		}else if(e.getActionCommand().equals("Search")) { 
			// TODO: implement that the search result is used to create new series/season object
            /*
             * In the below API(s) the error response should be appropriately handled
             */

			// with all episodes only display this new series/season with the episodes in tree

			// Doing a fetch two times so that we only get the full series info (with poster, summary, rating) once
			// fetch series info
			String searchReqURL = urlOMBD+seriesSearchJTF.getText().replace(" ", "%20");
			System.out.println("calling fetch with url: "+searchReqURL);
			String json = fetchURL(searchReqURL);
			System.out.println("Fetch result just season: " + json);

			// fetch season info
			String searchReqURL2 = urlOMBD+seriesSearchJTF.getText().replace(" ", "%20")+"&season="+seasonSearchJTF.getText();
			String jsonEpisodes = fetchURL(searchReqURL2);
			System.out.println("Fetch result episodes: " + jsonEpisodes);

			searchSeason = new SeriesSeason(json, jsonEpisodes);
			rebuildTreeFromSearch(searchSeason);
			System.out.println("**********\n" + searchSeason.toJsonString());

			/* TODO: implement here that this json will be used to create a Season object with the episodes included
			 * This should also then build the tree and display the info in the left side bar (so the new tree with its episodes)
			 * right hand should display the Series information
			 */

		}else if(e.getActionCommand().equals("Tree Refresh")) {
			searchSeason = null;
			rebuildTreeFromLibrary();
		}else if(e.getActionCommand().equals("Series-SeasonRemove")) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					tree.getLastSelectedPathComponent();
			if (node.getChildCount() > 1 && node.getDepth() == 1) {
				library.remove((String) node.getUserObject());
				rebuildTreeFromLibrary();
			} else {
				System.out.println("Must select a series to delete");
			}
			
		}
		tree.addTreeSelectionListener(this);
	}

	/**
	 *
	 * A method to do asynchronous url request printing the result to System.out
	 * @param aUrl the String indicating the query url for the OMDb api search
	 *
	 **/
	public void fetchAsyncURL(String aUrl){
		try{
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(aUrl))
					.timeout(Duration.ofMinutes(1))
					.build();
			client.sendAsync(request, BodyHandlers.ofString())
			.thenApply(HttpResponse::body)
			.thenAccept(System.out::println)
			.join();
		}catch(Exception ex){
			System.out.println("Exception in fetchAsyncUrl request: "+ex.getMessage());
		}
	}

	/**
	 *
	 * a method to make a web request. Note that this method will block execution
	 * for up to 20 seconds while the request is being satisfied. Better to use a
	 * non-blocking request.
	 * @param aUrl the String indicating the query url for the OMDb api search
	 * @return the String result of the http request.
	 *
	 **/
	public String fetchURL(String aUrl) {
		StringBuilder sb = new StringBuilder();
		URLConnection conn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(aUrl);
			conn = url.openConnection();
			if (conn != null)
				conn.setReadTimeout(20 * 1000); // timeout in 20 seconds
			if (conn != null && conn.getInputStream() != null) {
				in = new InputStreamReader(conn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader br = new BufferedReader(in);
				if (br != null) {
					int ch;
					// read the next character until end of reader
					while ((ch = br.read()) != -1) {
						sb.append((char)ch);
					}
					br.close();
				}
			}
			in.close();
		} catch (Exception ex) {
			System.out.println("Exception in url request:"+ ex.getMessage());
		} 
		return sb.toString();
	}

	public static void main(String args[]) {
	    try {
		String name = "first.last";
		String key = "use-your-last.ombd-key";
		String hostId="localhost";
        String regPort="1099";
        SeasonServerInt server;

		if (args.length >= 4){
			//System.out.println("java -cp classes:lib/json.lib ser321.assign2.lindquist."+
			//                   "MediaLibraryApp \"Lindquist Music Library\" lastFM-Key");
			name = args[0];
			key = args[1];
	        hostId=args[2];
	        regPort=args[3];
	        
	         server=(SeasonServerInt)Naming.lookup(
	                             "rmi://"+hostId+":"+regPort+"/EmployeeServer");
	         System.out.println("Client obtained remote object reference to" +
	                            " the SeriesServer");
	         BufferedReader stdin = new BufferedReader(
	                                          new InputStreamReader(System.in));
		}
			SeriesSeasonRMIClient ssrc = new SeriesSeasonRMIClient(name,key,server);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
