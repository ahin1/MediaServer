import java.rmi.*;
import java.io.*;
import personnel.*;

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
 * Purpose: demonstrate using the RMI API with a command line client
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * @see <a href="http://pooh.poly.asu.edu/Ser321">Ser321 Home Page</a>
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @date    February, 2020
 * @license See above
 */
public class RMIClient {
   public static void main(String args[]) {
      try {
         String hostId="localhost";
         String regPort="1099";
         if (args.length >= 2){
	    hostId=args[0];
            regPort=args[1];
         }
         Emp employee;
         //Note client uses the remote interface (EmpServer) not the
         //implementation class (EmpServerImpl).
         EmpServer server;
         server=(EmpServer)Naming.lookup(
                             "rmi://"+hostId+":"+regPort+"/EmployeeServer");
         System.out.println("Client obtained remote object reference to" +
                            " the EmployeeServer");
         BufferedReader stdin = new BufferedReader(
                                          new InputStreamReader(System.in));
         String inStr;
         do {
            System.out.print("Employee number or end>");
            inStr = stdin.readLine();
            if (inStr.equalsIgnoreCase("End")) break;
            // get the (serializable) employee object from the emp server
            employee = server.getEmp(Integer.parseInt(inStr));
            //Note that getEmp() runs on server; getName() runs in client
            System.out.println(employee.getName());
         } while (true);
      }catch (Exception e) {
         e.printStackTrace();
      }
   }
}



