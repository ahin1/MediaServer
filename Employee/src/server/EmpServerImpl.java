package personnel;

import java.rmi.server.*;
import java.rmi.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Set;


/**
 * Copyright (c) 2020 Tim Lindquist,
 * Software Engineering,
 * Arizona State University at the Polytechnic campus
 * <p/>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation version 2
 * of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but without any warranty or fitness for a particular purpose.
 * <p/>
 * Please review the GNU General Public License at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 * see also: https://www.gnu.org/licenses/gpl-faq.html
 * so you are aware of the terms and your rights with regard to this software.
 * Or, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,USA
 * <p/>
 * Purpose: demonstrate using the RMI API
 * Implementation of employee server - create a remote server object 
 * (with a couple of employees). Register the remote server object with the
 * rmi registry.
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * @see <a href="http://pooh.poly.asu.edu/Ser321">Ser321 Home Page</a>
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @date    February, 2020
 * @license See above
 */
class EmpServerImpl extends UnicastRemoteObject implements EmpServer {

/*
   protected Emp empList[] = {new Emp("Bob", 100), new Emp("Sue", 200),
                              new Emp("Jen", 300), new Emp("Tom", 400)};
*/
   protected Hashtable<String,Emp> empList;
   protected static int empNo;

   public EmpServerImpl() throws RemoteException {
      empNo = 0;
      try{
         File inFile = new File("employees.ser");
         if(inFile.exists()){
            ObjectInputStream is = 
               new ObjectInputStream(new FileInputStream(inFile));
            empList = (Hashtable)is.readObject();
         }else{
            empList = new Hashtable<String,Emp>();
            empList.put("Bob.Marley",new Emp("Bob.Marley", 100));
            empList.put("Dick.Smothers",new Emp("Dick.Smothers", 200));
            empList.put("Tom.Smothers",new Emp("Tom.Smothers", 300));
            empList.put("Jen.Jenkins",new Emp("Jen.Jenkins", 400));
         }
      }catch(Exception e){
         System.out.println("exception initializing employee store "+e.getMessage());
      }
   }

   public Emp getEmp(int id) throws RemoteException {
      Emp ret = null;
      for (Enumeration<Emp> e = empList.elements(); e.hasMoreElements();){
         ret = (Emp)e.nextElement();
         if(ret.getId()==id){
            System.out.println("Completing request for employee "+id);
            break;
         }
      }
      return ret;
   }

   public Emp addEmp(String name) throws RemoteException {
      Emp emp = new Emp("Already Registered",-1);
      if(!empList.keySet().contains(name) && !name.equals("")){
         emp = new Emp(name,empNo++);
         empList.put(name,emp);
         try{
            File outFile = new File("employees.ser");
            ObjectOutputStream os = 
               new ObjectOutputStream(new FileOutputStream(outFile));
            os.writeObject(empList);
         }catch(Exception e){
            System.out.println("exception initializing employee store "+e.getMessage());
         }
      }
      return emp;
   }

   public String[] getNames() throws RemoteException {
      String[] ret = ((Set<String>)empList.keySet()).toArray(new String[]{});
      return ret;
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
         EmpServer obj = new EmpServerImpl();
         Naming.rebind("rmi://"+hostId+":"+regPort+"/EmployeeServer", obj);
         System.out.println("Server bound in registry as: "+
                            "rmi://"+hostId+":"+regPort+"/EmployeeServer");
      }catch (Exception e) {
         e.printStackTrace();
      }
   }
}
