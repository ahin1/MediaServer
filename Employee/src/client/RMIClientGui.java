import java.rmi.*;
import java.io.*;
import javax.swing.*;
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
 * Purpose: demonstrate using the RMI API with gui client
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * @see <a href="http://pooh.poly.asu.edu/Ser321">Ser321 Home Page</a>
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @date    February, 2020
 * @license See above
 */
public class RMIClientGui extends JFrame {

   public RMIClientGui(String hostId, String regPort){
      try {
         Emp employee;
         //Note client uses the remote interface (EmpServer) not the
         //implementation class (EmpServerImpl).
         EmpServer server;
         server = (EmpServer) Naming.lookup(
                             "rmi://"+hostId+":"+regPort+"/EmployeeServer");
         System.out.println("Client obtained remote object reference to" +
                            " the EmployeeServer at:\n"+
                             "rmi://"+hostId+":"+regPort+"/EmployeeServer");
	 String inStr;
         do  {
            inStr = JOptionPane.showInputDialog("Enter employee number such as 100, 200\n"
                                                +"add First.Last,\nor end");
            String[] args = inStr.split(" ");
            if (args.length>=1 && args[0].trim().equalsIgnoreCase("End")){
               break;
            }else if(args.length>=1 && args[0].trim().equalsIgnoreCase("list")){
               String[] names = server.getNames();
               System.out.print("employee names are: ");
               for(int i=0; i<names.length; i++){
                  System.out.print(names[i]+" ");
               }
               System.out.println();
               JOptionPane.showMessageDialog(this,"There are " + names.length +
                                          " employees registered on the sever.",
                                          "Employee Information",
                                          JOptionPane.INFORMATION_MESSAGE);
            }else if(args.length>=1 && !args[0].trim().equalsIgnoreCase("add")){
            // get the (serializable) employee object from the emp server
               System.out.println("You requested information about employee number: "+inStr);
               employee = server.getEmp(Integer.parseInt(inStr));
               //Note that getEmp() runs on server; getName() runs in client
               System.out.println("From server, employee "+inStr+" has name: "+
                                  employee.getName());
               JOptionPane.showMessageDialog(this,"You requested information" +
                                        " about\nemployee "+inStr+" whose name is " +
                                          employee.getName(),
                                          "Employee Information",
                                          JOptionPane.INFORMATION_MESSAGE);
            }else if(args.length>=2 && args[0].trim().equalsIgnoreCase("add")){
               Emp anEmp = server.addEmp(args[1]);
               System.out.println("From server, new employee has name: "+
                                  anEmp.getName()+" with id "+anEmp.getId());
               JOptionPane.showMessageDialog(this,"You added an employe" +
                                          " whose name is " +
                                             anEmp.getName()+" with id "+anEmp.getId(),
                                          "Employee Information",
                                          JOptionPane.INFORMATION_MESSAGE);
            }
         } while (true);
         System.exit(0);
      }catch (Exception e) {
         e.printStackTrace();}
   }

   public static void main(String args[]) {
      String hostId="localhost";
      String regPort="2222";
      if (args.length >= 2){
         hostId=args[0];
         regPort=args[1];
      }
      //System.setSecurityManager(new RMISecurityManager());
      RMIClientGui rmiclient = new RMIClientGui(hostId, regPort);
   }
}
