import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Methods {

	private Methods(){}

	//the following array is a quick way for fire compliance method to determine whether a given district is inside or outside SSA
	//for each index 'i' in the array, the boolean value at 'i' represents whether or not the district 'i+1' is inside or outside SSA
	//true = inside, false = outside
	//**Arrays start at 0, so again, index 0 represents district 1, index 1 represents district 2, and so on.**
	private static final boolean[] insideSsaArray = new boolean[]
			{true, //district 1
					false, // 2
					false, // 3
					false, // 4
					false, // 5
					true,  // 6
					true,  // 7
					false, // 8
					false, // 9
					true,  //10
					false, //11
					false, //12
					true,  //13
					true,  //14
					false, //15
					true}; //16

	
	//simple method to get valid rows only from input ArrayList
	public static List<Row> filterValid(List<Row> inputList) {
		List<Row> outputList = new ArrayList<>();

		//iterates through inputList and adds all valid rows to outputList
		for(Row row : inputList) {
			if(row.isValid()) {
				outputList.add(row);
			}
		}
	
		//outputList is returned after being populated
		return outputList;
	}

	
	//simple method to filter an input list of rows by priority level
	//takes input of a List of Rows and an integer representing priority level
	//note that this integer input is not validated, so if the priority level input is, say, 4, it will return an empty list
	//(since no rows with priority level 4 exist)
	public static List<Row> filterPriority(List<Row> inputList, int priorityLevel) {
		List<Row> outputList = new ArrayList<>();

		//iterates through inputList, adding only the rows that match the specified priority level to the outputList
		for(Row row : inputList) {
			if(row.getPriorityNumber() == priorityLevel) {
				outputList.add(row);
			}
		}

		//outputList is returned after being populated
		return outputList;
	}

	
	//simple method to filter an input list of rows by service type
	//takes input of a List of Rows and a service type (ServiceType.FIRE or ServiceType.EMS)
	public static List<Row> filterServiceType(List<Row> inputList, ServiceType serviceType) {
		List<Row> outputList = new ArrayList<>();

		//iterates through inputList, adding only the rows that match the specified service type to the outputList
		for(Row row : inputList) {
			if(row.getServiceType() == serviceType) {
				outputList.add(row);
			}
		}

		//outputList is returned after being populated
		return outputList;
	} 

	
	//simple method to filter an input list of rows by district number
	//takes input of a List of Rows and an integer representing district number
	//note that this integer input is not validated, so if the district # input is 27, it will return an empty list
	//(since no rows with district number 27 exist)
	public static List<Row> filterDistrict(List<Row> inputList, int districtNumber) {
		List<Row> outputList = new ArrayList<>();
		
		//iterate through inputList, adding all rows with district number matching the specified one to the outputList
		for(Row row : inputList) {
			if(row.getDistrictNumber() == districtNumber) {
				outputList.add(row);
			}
		}

		//outputList is returned after being populated
		return outputList;
	}

	
	//simple method to filter the rows in the input list of rows to only those within the specified date range
	//takes in a List of Rows along with two LocalDateTime variables
	//the second LocalDateTime MUST be after the first chronologically or else an error will occur and an empty list will be returned
	//if no incidents occurred within the specified date/time range, an empty list will be returned (but no error will occur)
	public static List<Row> filterDate(List<Row> inputList, LocalDateTime startOfDateRange, LocalDateTime endOfDateRange) {
		//checks to ensure that the start of the range (1st LocalDateTime input) is before the end of the range (2nd LocalDateTime input)
		if(startOfDateRange.compareTo(endOfDateRange) > 0) {
			//if not, then send an error and return empty list
			GUI.sendError("Error: start of date range must be before end of date range.");
			return Collections.emptyList();
		}

		List<Row> outputList = new ArrayList<>();

		//iterate through inputList, adding all rows that fall within the specified date range to the outputList
		for(Row row : inputList) {
			if(row.getDispatchTime().compareTo(startOfDateRange) >= 0 && row.getDispatchTime().compareTo(endOfDateRange) <= 0) {
				outputList.add(row);
			}
		}

		//outputList is returned after being populated
		return outputList;
	}

	//data should be FULLY validated before calling this method
	public static double[][] fireSsaComplianceArrayOutput(List<Row> inputList) {
		//16 rows (1 for each district) + inside + outside + county
		//6 columns (dist#, compliance threshold, compliance pct, # incidents, # comply, avg time)
		double[][] outputArray = new double[19][6];

		for(int districtNumber = 1; districtNumber < 17; districtNumber++) {
			//set district number in 1st column
			outputArray[districtNumber-1][0] = districtNumber;

			//set compliance threshold and whether or not this district is inside SSA
			//2nd column represents max # of minutes required to be compliant
			//draws from hardcoded insideSsaArray defined above in this class
			LocalTime complianceThreshold;
			if(insideSsaArray[districtNumber-1]) {
				//if this district is inside SSA, set the compliance threshold to 9 minutes
				complianceThreshold = LocalTime.of(0, 9);
				//and note this compliance threshold in the output array
				outputArray[districtNumber-1][1] = 9;
			}
			else {
				//otherwise, district must be outside SSA
				//so set compliance threshold to 15 minutes
				complianceThreshold = LocalTime.of(0, 15);
				//and again, note this in the output array
				outputArray[districtNumber-1][1] = 15;
			}

			//get list of all valid incidents in the current district
			List<Row> currentDistrictList = filterValid(filterDistrict(inputList, districtNumber));

			//use this list to calculate percent compliance
			//uses compliance threshold determined by SSA status
			//then round compliance percent to 2 decimal places
			double compliancePercent = percentCompliance(currentDistrictList, complianceThreshold);
			double roundedCompliancePercent = (double) Math.round(compliancePercent*100.0)/100.0;

			//size of list is equal to total incident count
			double totalCount = currentDistrictList.size();

			//now we can just multiply the percentage by the list size
			//and round to nearest integer
			double compliantCount = compliancePercent*totalCount/100;
			Math.round(compliantCount);

			//3rd column is compliance percent
			//4th column is total incident count
			//5th column is number of compliant incidents
			//remember that since arrays start at 0, the 3rd column is represented by outputArray[x][2], the 4th column is represented by outputArray[x][3], etc...
			//the same applies to rows, hence the districtNumber-1
			outputArray[districtNumber-1][2] = roundedCompliancePercent;
			outputArray[districtNumber-1][3] = totalCount;
			outputArray[districtNumber-1][4] = compliantCount;

			//finally, do average response time
			double averageResponse = averageResponseTimeDouble(currentDistrictList);
			//this goes in column 6
			outputArray[districtNumber-1][5] = averageResponse;

			if(insideSsaArray[districtNumber-1]) {
				//if district is inside SSA, then we need to add its values to the totals for inside SSA
				outputArray[16][3] += totalCount;
				outputArray[16][4] += compliantCount;

				//this loop gets the response time from each individual incident and adds it to a total
				//this total will consist of all incidents from inside the SSA and divide it by the total incident count
				for(Row row : currentDistrictList) {
					outputArray[16][5] += row.getResponseDouble();
				}
			}
			else {
				//else if district is outside SSA, then we add its values to totals for outside SSA
				outputArray[17][3] += totalCount;
				outputArray[17][4] += compliantCount;

				//this loop is the same as the one immediately above, but applies to outside SSA instead
				for(Row row : currentDistrictList) {
					outputArray[17][5] += row.getResponseDouble();
				}
			}
		}

		//placeholders
		//16 / 1 represents inside SSA
		//17 / 2 represents outside SSA
		//18 / 3 represents countywide totals
		outputArray[16][0] = 1;
		outputArray[17][0] = 2;
		outputArray[18][0] = 3;

		//represents response time requirement
		//-1 for bottom row as this is the total across all districts, so compliance threshold varies
		outputArray[16][1] = 9;
		outputArray[17][1] = 15;
		outputArray[18][1] = -1;

		//add total response times
		outputArray[18][5] = outputArray[16][5]+outputArray[17][5];

		//check to ensure incident count for inside SSA was not 0, else we risk dividing by zero
		if(outputArray[16][3] != 0) {
			//calculate the overall compliance rate for inside SSA by dividing compliant incidents by total incidents
			//round to two decimal places and place data in output array
			double insideSsaCompliance = outputArray[16][4]*100.0/outputArray[16][3];
			insideSsaCompliance = (double) Math.round(insideSsaCompliance*100.0)/100.0;
			outputArray[16][2] = insideSsaCompliance;

			//now, calculate the average response time across all inside SSA incidents
			//and round to two decimal places
			outputArray[16][5] = outputArray[16][5]/outputArray[16][3];
			outputArray[16][5] = (double) Math.round(outputArray[16][5]*100.0)/100.0;
		}

		//check to ensure incident count for outside SSA was not 0, else we risk dividing by zero
		if(outputArray[17][3] != 0) {
			//calculate overall compliance rate for outside SSA
			//round and place in array
			double outsideSsaCompliance = outputArray[17][4]*100.0/outputArray[17][3];
			outsideSsaCompliance = (double) Math.round(outsideSsaCompliance*100.0)/100.0;
			outputArray[17][2] = outsideSsaCompliance;

			//calculate average response time across all outside SSA incidents
			//round to 2 decimal places
			outputArray[17][5] = outputArray[17][5]/outputArray[17][3];
			outputArray[17][5] = (double) Math.round(outputArray[17][5]*100.0)/100.0;
		}

		//the total incidents and compliant incidents counts for the county-wide totals are obtained by adding inside SSA and outside SSA totals
		//outputArray[18][3] = total incidents
		//outputArray[18][4] = compliant incidents
		outputArray[18][3] = outputArray[16][3]+outputArray[17][3];
		outputArray[18][4] = outputArray[16][4]+outputArray[17][4];

		//check to ensure total incident count is not zero to avoid dividing by zero errors
		if(outputArray[18][3] != 0) {
			//calculate total compliance by dividing compliant count by total count and multiplying by 100
			//round to 2 decimal places and place into output array
			double totalCompliance = outputArray[18][4]*100.0/outputArray[18][3];
			totalCompliance = (double) Math.round(totalCompliance*100.0)/100.0;
			outputArray[18][2] = totalCompliance;

			//calculate average response time by dividing total response time counter by number of incidents
			outputArray[18][5] = outputArray[18][5]/outputArray[18][3];
			outputArray[18][5] = (double) Math.round(outputArray[18][5]*100.0)/100.0;
		}

		//finally return outputArray
		return outputArray;
	}


	
	public static double percentCompliance(List<Row> inputList, LocalTime complianceThreshold) {
		double numberCompliant = 0;
		double totalIncidents = 0;

		if(inputList.isEmpty()) {
			return 0;
		}

		for(Row row : inputList) {
			totalIncidents += 1;

			if(row.getResponseTime().compareTo(complianceThreshold) < 0) {
				numberCompliant += 1;
			}
		}

		if(totalIncidents == 0) {
			GUI.sendError("Error calculating percent compliance: input list is empty.");
			return 0;
		}
		else {
			return numberCompliant*100.0/totalIncidents;
		}
	}


	public static double averageResponseTimeDouble(List<Row> inputList) {
		double totalSeconds = 0;
		double incidentCount = 0;

		if(inputList.isEmpty()) {
			return 0;
		}

		for(Row row : inputList) {
			totalSeconds += ChronoUnit.SECONDS.between(row.getDispatchTime(), row.getArriveTime());
			incidentCount++;
		}

		if(incidentCount == 0) {
			GUI.sendError("Error calculating average response time: no data in input list.");
			return 0;
		}
		else {
			double average = totalSeconds*1.0/incidentCount;

			double seconds = average;
			double minutes = 0;

			while(seconds > 59) {
				seconds -= 60;
				minutes++;
			}

			double secondsDecimal = seconds/60.0;

			double output = minutes+secondsDecimal;
			
			output = (double) Math.round(output*100.0)/100.0;
			
			return output;
		}
	}

	//data should be FULLY validated before calling this method
	//specifically, dispatch time and arrival time
	public static String averageResponseTime(List<Row> inputList) {
		int totalSeconds = 0;
		int incidentCount = 0;

		String zeroTime = "0:00:00";

		if(inputList.isEmpty()) {
			return zeroTime;
		}

		for(Row row : inputList) {
			totalSeconds += ChronoUnit.SECONDS.between(row.getDispatchTime(), row.getArriveTime());
			incidentCount++;
		}

		if(incidentCount == 0) {
			GUI.sendError("Error calculating average response time: no data in input list.");
			return null;
		}
		else {
			int average = totalSeconds/incidentCount;

			int seconds = average;
			int minutes = 0;
			int hours = 0;

			while(seconds > 59) {
				seconds -= 60;
				minutes++;
			}

			while(minutes > 59) {
				minutes -= 60;
				hours++;
			}

			if(hours <= 23) {
				LocalTime outputTime = LocalTime.of(hours, minutes, seconds);
				String outputTimeAsString = outputTime.toString();

				if(seconds == 0) {
					outputTimeAsString = outputTimeAsString+":00";
				}

				return outputTimeAsString;
			}
			else {
				GUI.sendError("Error calculating average response time: avg response time exceeded 24 hours. Check data for validity and accuracy.");
				return null;
			}
		}
	}

	//method to create Row from String array
	//this is used when importing raw data from CSV
	//requires carefully and specifically formatted String array
	//String array format is as follows:
	//index[0] = INPRTY (Priority)
	//index[1] = INOSTR (Address)
	//index[2] = INOXST (Cross Street)
	//index[3] = DISPATCH (Dispatch Time)
	//index[4] = ARRIVE (Arrival Time)
	//index[5] = RESPONSE (Response time as double)
	//index[6] = Staffing (Staffing)
	//index[7] = DayofWk (Day of Week)
	//index[8] = SSA (inside/outside SSA)
	//index[9] = DISTRICT (District Number)
	//index[10] = INORI#
	//index[11] = ININC#
	//index[12] = INUNIT1
	//index[13] = INBDG1
	//index[14] = INITYPE
	//index[15] = INSECT
	//index[16] = CAD_INC#
	//index[17] = SERVICE_TYPE (Service Type: FIRE/EMS)
	//index[18] = FRSTUNITARRV (First Unit Arrived)
	public static Row createRow(String[] rowAsArray, int lineCount) {

		//Row is initialized to null so that we don't get an error if ININC is blank
		//Row object requires ININC number in constructor
		Row outputRow = new Row(lineCount);

		//First check if ININC column is empty for current row data
		if(rowAsArray[11].isEmpty()) {
			GUI.sendError("Error: ININC# does not exist for row "+lineCount+".");
		}
		else {
			//If not, try to parse what is there
			//It should be a long integer
			try {
				outputRow.setIninc(Long.parseLong(rowAsArray[11]));
			}
			catch (NumberFormatException e) {
				//Otherwise we get an error and this row is set to invalid
				//A message is printed informing what the error is and the line in which the error occurred
				GUI.sendError("Error: ININC# is invalid (not a number) for row "+lineCount+".");
				outputRow.setValid(false);
			}
		}


		//try/catch for INPRTY
		//Any numerical values have to be parsed, which allows for an exception if the String is not numerical
		//The try/catch statements handle the NumberFormatException that could potentially be thrown
		try {
			outputRow.setPriority((int) Double.parseDouble(rowAsArray[0]));
		}
		catch (NumberFormatException e) {
			//If there is not a valid number where there should be, print an error and set this row to invalid
			//Data in this row must be corrected in GUI to make it valid again
			GUI.sendError("Error: INPRTY is invalid (not a number) for row "+lineCount+".");
			outputRow.setValid(false);
		}


		//address block
		//error handling tbd
		outputRow.setAddress(rowAsArray[1].trim());

		if(rowAsArray[2] != null) {
			outputRow.setCross(rowAsArray[2].trim());
		}
		else {
			outputRow.setCross("");
		}
		//end address block


		//Date block
		//DateTimeFormatter is created to specify format that we expect the date/time to be in
		//This is consistent with what is usually found in the Excel/CSV
		//If the date/time read from CSV is not able to be read by this formatter, an error will be thrown
		//And this line of data will not be included
		DateTimeFormatter dateAndTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm[:ss]");
		try {
			outputRow.setDispatch(LocalDateTime.parse(rowAsArray[3].trim(), dateAndTimeFormatter));
			outputRow.setArrive(LocalDateTime.parse(rowAsArray[4].trim(), dateAndTimeFormatter));
		}
		catch (DateTimeParseException e) {
			GUI.sendError("Error: could not parse dispatch and/or arrive time for row "+lineCount+".");
			outputRow.setValid(false);
		}

		//See above comments about parsing numbers
		//This block attempts to parse the RESPONSE variable as a double
		try {
			outputRow.setResponseDouble(Double.parseDouble(rowAsArray[5]));
		}
		catch (NumberFormatException e) {
			GUI.sendError("Error: RESPONSE is invalid (not a number) for row "+lineCount+".");
			outputRow.setValid(false);
		}
		//If parsing was successful, we can also check that the number parsed is still valid
		//Response time logically must be at least zero, so any negative response time results in a warning
		//Warning (as opposed to error) means the row will still be valid, but there is possibly something wrong with the data
		if(outputRow.getResponseDouble() < 0) {
			GUI.sendInfo("Warning: RESPONSE is invalid (less than zero) for row "+lineCount+".");
			GUI.sendInfo("This will be overwritten by a manually calculated response time, but still worth noting.");
		}


		outputRow.setStaffing(rowAsArray[6].trim());
		//Quick validation check to ensure Staffing is either Volunteer or Career Staffing
		if(outputRow.getStaffing().toLowerCase().compareTo("volunteer") != 0 && outputRow.getStaffing().toLowerCase().compareTo("career staff") != 0) {
			GUI.sendError("Error: Staffing is unexpected value (neither Volunteer nor Career Staffing) for row "+lineCount+".");
			outputRow.setValid(false);
		}

		//The following code ensures that the day of week is a valid day of week
		//Since is this isn't an entirely essential variable, a warning is thrown if day of week is invalid
		//Again, the data will still be included, but it does suggest there is an issue with it
		outputRow.setDay(rowAsArray[7].trim());
		switch (outputRow.getDay().toLowerCase()) {
		case "monday":
			break;
		case "tuesday":
			break;
		case "wednesday":
			break;
		case "thursday":
			break;
		case "friday":
			break;
		case "saturday":
			break;
		case "sunday":
			break;
		default:
			GUI.sendError("Error: Day of Week is invalid for row "+lineCount+".");
			outputRow.setValid(false);
			break;
		}


		//SSA validation moved to row class
		outputRow.setSsa(rowAsArray[8].trim());

		//Try/catch for district number to ensure data is numerical and avoid error causing crash
		try {
			outputRow.setDistrict((int) Double.parseDouble(rowAsArray[9]));
		}
		catch (NumberFormatException e) {
			GUI.sendError("Error: District number is invalid (not a number) for row "+lineCount+".");
			outputRow.setValid(false);
		}
		//moved district number validation to row class


		//not sure about validation for these
		outputRow.setInori(rowAsArray[10].trim());
		outputRow.setInunit(rowAsArray[12].trim());
		outputRow.setInbdg(rowAsArray[13].trim());
		outputRow.setInitype(rowAsArray[14].trim());


		//INSECT variable is numerical so try/catch to avoid errors
		try {
			outputRow.setInsect((int) Double.parseDouble(rowAsArray[15]));
		}
		catch (NumberFormatException e) {
			GUI.sendError("Error: INSECT is invalid (not a number) for row "+lineCount+".");
			outputRow.setValid(false);
		}


		//see above comments on numerical variables
		try {
			outputRow.setCadinc(Long.parseLong(rowAsArray[16]));
		}
		catch (NumberFormatException e) {
			GUI.sendError("Error: CAD_INC is invalid (not a number) for row "+lineCount+".");
			outputRow.setValid(false);
		}


		//Service_type should be either FIRE or EMS
		//We can check this easily, if it is neither than throw an error and skip this row of data
		//Subject to change 
		if(rowAsArray[17].compareTo("FIRE") == 0) {
			outputRow.setServiceType(ServiceType.FIRE);
		}
		else if (rowAsArray[17].compareTo("EMS") == 0) {
			outputRow.setServiceType(ServiceType.EMS);
		}
		else {
			GUI.sendError("Error: SERVICE_TYPE is invalid for row "+lineCount+".");
			outputRow.setValid(false);
		}

		outputRow.setFua(rowAsArray[18].trim());


		if(outputRow.getDispatchTime() != null && outputRow.getArriveTime() != null) {
			//for calculating response time
			//first calculates total number of minutes between dispatch and arrive
			int responseTimeMinutesValue = (int) ChronoUnit.MINUTES.between(outputRow.getDispatchTime(), outputRow.getArriveTime());
			int responseTimeHoursValue = 0;
			//If minutes is 60 or greater, than response time is over an hour, so we have to account for that
			while(responseTimeMinutesValue > 59) {
				responseTimeMinutesValue -= 60;
				responseTimeHoursValue++;
			}
			//Once hours and minutes are calculated, move on to seconds
			int responseTimeSecondsValue = (int) ChronoUnit.SECONDS.between(outputRow.getDispatchTime(), outputRow.getArriveTime());
			//Here we check to ensure that DISPATCH comes before ARRIVE, otherwise throw an error since we have a time traveler on our hands
			if(responseTimeSecondsValue < 0) {
				GUI.sendError("Error: Response time less than zero (calculated from Dispatch and Arrive) for row "+lineCount+".");	
				outputRow.setValid(false);
			}
			//Assuming that check passes, we can finalize the value of the seconds variable by calculating it modulo 60
			responseTimeSecondsValue = responseTimeSecondsValue%60;

			//now make sure hours is 23 or less
			if(responseTimeHoursValue > 23) {
				responseTimeHoursValue = 23;
				GUI.sendError("Error: Response time exceeds 1 day for row "+lineCount+". Response time value in table may not be accurate; check Dispatch and Arrival times.");
			}
			//Then use the calculated variables for hours, minutes, and seconds to create the responseTime variable
			LocalTime responseTime = LocalTime.of(responseTimeHoursValue, responseTimeMinutesValue, responseTimeSecondsValue);
			outputRow.setResponseTime(responseTime);
		}
		else {
			GUI.sendError("Error: Dispatch and/or Response time are null for row "+lineCount+", so response time could not be calculated.");
			outputRow.setValid(false);
		}
		//Now that all of the data has been imported into the Row object, return the Row object so it can be added to ArrayList
		return outputRow;
	}

}
