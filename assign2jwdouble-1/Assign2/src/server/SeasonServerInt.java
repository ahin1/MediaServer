package ser321.assign2.ahinric1.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.rmi.*;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public interface SeasonServerInt extends Remote{
    public SeriesSeason get(String title) throws RemoteException;
    public String[] getTitles() throws RemoteException;
    public boolean add (SeriesSeason season) throws RemoteException;
    public boolean remove (String title) throws RemoteException;
    public String toString();
    public String toJsonString() throws RemoteException;
    public JSONObject toJson() throws RemoteException;
    public boolean saveLibraryToFile() throws RemoteException;
    public boolean restoreLibraryFromFile() throws RemoteException;
}
