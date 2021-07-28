/*
 * Class: Driver
 * ----------------------------
 * Info: The following program parses an input file that holds info which is then store in a 
 * HashMap data structure. This HashMap has a key that will store the empoyee ids and a
 * value of type HashMap<Integer, Date> that will store the project ids and the dates that an 
 * employee stared and finished a project. The program will then go through each employee and 
 * will comapre it to others checking if they have common projects and then analysing the total
 * time spend together in each one. Finally, the program will print the pair of empoyees that
 * have worked the most time together in one or many projects. 
 * 
 * The file format must be like the one below:
    
         EmpID, ProjectID, DateFrom, DateTo
         
 * Example:
        
        143, 12, 2013-11-01, 2014-01-05
        218, 10, 2012-05-16, NULL    
        143, 10, 2009-01-01, 2011-04-27
        ...
        
 *------------------------------
 * Date: 7/27/2021
 * -----------------------------
 * Author: Aleksandar Odaybat 
 */

package mypackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Driver {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in); 
		
		// Variable that will store the file's records
		HashMap<Integer ,HashMap<Integer, Date[]>> records = new HashMap<>();
		
		System.out.format("Please type the full pathname of the file that you want to explore \n"
				+ "and press ENTER: ");
		
		// While the text file is not parsed repeat the below procedure 
		while(true) {
			try {
			     String path = scan.nextLine();
			     Scanner text = new Scanner(new File(path)); // reading the text file 
			     
			     while(text.hasNext()) {
			    	 // parsing the text file
			         int empID = Integer.parseInt(text.next().replaceAll(",", "")); 
			         int projectID = Integer.parseInt(text.next().replaceAll(",", ""));
			         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			         Date dateFrom = sdf.parse(text.next().replaceAll(",", ""));
			         String to = text.next().replaceAll(",", "");
			         if(to.contains("NULL")) {
			        	 to = sdf.format(new Date()); // today's date
			         }
			         Date dateTo = sdf.parse(to);
			         Date[] arr = {dateFrom, dateTo};
			         if(records.containsKey(empID)) {
			        	 records.get(empID).put(projectID, arr);
			         }
			         else { 
			        	 HashMap<Integer, Date[]> map = new HashMap<>();
			             map.put(projectID, arr);
			             records.put(empID, map);
			         }	         
			         if(text.hasNextLine())
			        	  text.nextLine();
			     }
			     scan.close();
			     text.close();
			     break;
			}  
			catch(FileNotFoundException e) {
				System.out.format("Invalid pathname! Please type a valid one: ");
			}
			catch(Exception e) {
				System.out.format("Invalid file format! Please type a pathname of file with a valid format: ");
			} 
		}
		
		// Following is the algorithm for solving the task 
		Set<Integer> employees = records.keySet(); // getting all the employees
		int[] pair = new int[2];
		long best = 0;
        for(int employee1: employees) {
        	for(int employee2: employees) {
        		if(employee1 == employee2) {
        			continue;
        		}
        		long sum = 0; 
        		Set<Integer> projects = records.get(employee1).keySet(); // getting all of the projects of the current employee 
        		for(int projectID: projects) {
        			if(records.get(employee2).keySet().contains(projectID)) {
        				Date[] time1 = records.get(employee1).get(projectID); // getting the start and end times
        				Date[] time2 = records.get(employee2).get(projectID);
        				sum += calc(time1, time2); // calcolating the common time between two intervals of dates
        			}
        		}
        		if(best < sum) { //compare 
        			best = sum;
        			pair[0] = employee1; pair[1] = employee2;
        		}
        	}
        }
        System.out.format("The pair of empoyees that have worked the most time together \n"
        		+ "in common projects is: (" + pair[0] + ", " +pair[1] + ")");
	}
	
	// Utility method for calcolating the common time between two intervals of dates
	static long calc(Date[] d1, Date[] d2) {
		Date[][] dates = {d1, d2};
		long[] bounds = new long[2]; // variable for storing the start and end times
		for(int i = 0; i < 2; i++) {
			if(d1[i].getTime() >= d2[i].getTime()) {
				bounds[i] = dates[i][i].getTime();
			}
			else {
				bounds[i] = dates[(i+1)%2][i].getTime();
			}
		}
		TimeUnit time = TimeUnit.DAYS; 
		long diffrence = time.convert(bounds[1]-bounds[0], TimeUnit.MILLISECONDS);
        return diffrence;
	}
		
}