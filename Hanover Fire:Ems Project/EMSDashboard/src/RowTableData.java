import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import javafx.beans.property.*;

public class RowTableData {

	private SimpleIntegerProperty INPRTY;

	private SimpleStringProperty INOSTR;

	private SimpleStringProperty INOXSTR;	

	private SimpleStringProperty DISPATCH; 

	private SimpleStringProperty ARRIVE;

	private SimpleStringProperty Staffing;

	private SimpleStringProperty DayOfWk;

	private SimpleStringProperty SSA;

	private SimpleIntegerProperty District;

	private SimpleStringProperty INORI;

	private SimpleStringProperty ININC;

	private SimpleStringProperty CAD_INC;

	private SimpleStringProperty INUNIT1;

	private SimpleStringProperty INBDG1; 

	private SimpleStringProperty INITYPE;

	private SimpleStringProperty INSECT;

	private SimpleStringProperty SERVICE_TYPE;

	private SimpleStringProperty FRSTUNITARRV;

	private SimpleStringProperty ResponseTime;

	private SimpleIntegerProperty RowNumber;

	private SimpleStringProperty isValid;

	private Row linkedRow;

	private DateTimeFormatter dateAndTimeFormatter;

	public RowTableData(Row r) {
		this.linkedRow = r;

		this.ININC = new SimpleStringProperty(r.getIninc());
		this.RowNumber = new SimpleIntegerProperty(r.getRowNumber());
		this.INPRTY = new SimpleIntegerProperty(r.getPriorityNumber());
		this.INOSTR = new SimpleStringProperty(r.getAddress());
		this.INOXSTR = new SimpleStringProperty(r.getCross());

		dateAndTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss");
		if(r.getDispatchTime() != null && r.getArriveTime() != null) {
			this.DISPATCH = new SimpleStringProperty(dateAndTimeFormatter.format(r.getDispatchTime()));
			this.ARRIVE = new SimpleStringProperty(dateAndTimeFormatter.format(r.getArriveTime()));
		}

		this.ResponseTime = new SimpleStringProperty(r.getResponse());

		this.Staffing = new SimpleStringProperty(r.getStaffing());
		this.SSA = new SimpleStringProperty(r.getSsa());
		this.DayOfWk = new SimpleStringProperty(r.getDay());
		this.INORI = new SimpleStringProperty(r.getInori());

		this.District = new SimpleIntegerProperty(r.getDistrictNumber());
		this.INSECT = new SimpleStringProperty(fixInsect(r.getInsectNumber()));

		this.CAD_INC = new SimpleStringProperty(r.getCadinc());

		this.INUNIT1 = new SimpleStringProperty(r.getInunit());
		this.INBDG1 = new SimpleStringProperty(r.getInbdg());
		this.INITYPE = new SimpleStringProperty(r.getInitype());
		this.FRSTUNITARRV = new SimpleStringProperty(r.getFua());
		this.SERVICE_TYPE = new SimpleStringProperty(r.getServtype());

		this.isValid = new SimpleStringProperty(r.isValid()+"");
	}

	public Row getRow() {
		return this.linkedRow;
	}

	public int getRownum() {
		if(this.RowNumber != null) {
			return this.RowNumber.get();
		}
		else {
			return 0;
		}
	}

	public String getValid() {
		if(this.isValid != null) {
			return this.isValid.get();
		}
		else {
			return "false";
		}
	}

	public void setValid(boolean b) {
		//need to add validity check on all RowTableData here
		this.isValid.set(b+"");
	}

	public int getPriority() {
		if(this.INPRTY != null) {
			return this.INPRTY.get();
		}
		else {
			return 0;
		}
	}

	public void setPriority(int i) {
		if(i > 0 && i < 4) {
			if(i != this.getPriority()) {
				GUI.sendInfo("Updated Priority from "+this.getPriority()+" to "+i+" for row "+this.getRownum()+".");
				this.INPRTY.set(i);
				GUI.setEdited();
			}
		}
		else {
			GUI.sendInfo("Warning: tried to change priority to an invalid value in row "+this.getRownum()+". Priority should be 1, 2, or 3.");
		}
	}

	public String getAddress() {
		if(this.INOSTR != null) {
			return this.INOSTR.get();
		}
		else {
			return "";
		}
	}

	public void setAddress(String i) {
		String s = i.trim();

		if(s.length() >= 1) {
			if(this.getAddress() == null || this.getAddress().isEmpty() || s.compareTo(this.getAddress()) != 0) {
				GUI.sendInfo("Updated Address from "+this.getAddress()+" to "+s+" for row "+this.getRownum()+".");
				this.INOSTR.set(s);
				GUI.setEdited();
			}
		}
		else {
			GUI.sendInfo("Warning: Address field cannot be blank in row "+this.getRownum()+". Value not updated.");
		}
	}

	public String getCross() {
		if(this.INOXSTR != null) {
			return this.INOXSTR.get();
		}
		else {
			return "";
		}
	}

	public void setCross(String i) {
		String s = i.trim();

		if(this.getCross() == null || this.getCross().isEmpty() || s.compareTo(this.getCross()) != 0) {
			if(s.length() >= 1) {
				GUI.sendInfo("Updated Cross Street from "+this.getCross()+" to "+s+" for row "+this.getRownum()+".");
				this.INOXSTR.set(s);
				GUI.setEdited();
			}
			else {
				GUI.sendInfo("Removed Cross Street for row "+this.getRownum()+".");
				this.INOXSTR.set(s);
				GUI.setEdited();
			}
		}
	}

	public String getDispatch() {
		if(this.DISPATCH != null) {
			return this.DISPATCH.get();
		}
		else {
			return "";
		}
	}

	public void setDispatch(String i) {
		boolean valid = false;
		String d = i.trim();

		if(d.compareTo(this.getDispatch()) == 0) return;

		try {
			LocalDateTime.parse(d, dateAndTimeFormatter);
			valid = true;
		} catch(DateTimeParseException e) {
			valid = false;
			GUI.sendError("Error in changing Dispatch value for row "+this.getRownum()+
					": invalid date/time format. Ensure the date/time matches the format 'M/d/yyyy H:mm:ss'.");
		}

		if(valid) {
			GUI.sendInfo("Updated Dispatch time from "+this.getDispatch()+" to "+d+" for row "+this.getRownum()+".");
			this.DISPATCH.set(d);
			GUI.setEdited();
		}

		updateResponse();
	}

	public String getArrive() {
		if(this.ARRIVE != null) {
			return this.ARRIVE.get();
		}
		else {
			return "";
		}
	}

	public void setArrive(String i) {
		boolean valid = false;
		String d = i.trim();

		if(d.compareTo(this.getArrive()) == 0) return;

		try {
			LocalDateTime.parse(d, dateAndTimeFormatter);
			valid = true;
		} catch(DateTimeParseException e) {
			valid = false;
			GUI.sendError("Error in changing Arrival value for row "+this.getRownum()+
					": invalid date/time format. Ensure the date/time matches the format 'M/d/yyyy H:mm:ss'.");
		}

		if(valid && LocalDateTime.parse(this.getDispatch(), dateAndTimeFormatter).isBefore(LocalDateTime.parse(d, dateAndTimeFormatter))) {
			GUI.sendInfo("Updated Arrival time from "+this.getArrive()+" to "+d+" for row "+this.getRownum()+".");
			this.ARRIVE.set(d);
			GUI.setEdited();
		}

		updateResponse();
	}

	public void updateResponse() {
		try {
			LocalDateTime dispatchTime = LocalDateTime.parse(this.getDispatch(), dateAndTimeFormatter);
			LocalDateTime arriveTime = LocalDateTime.parse(this.getArrive(), dateAndTimeFormatter);

			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm[:ss]");

			//for calculating response time
			//first calculates total number of minutes between dispatch and arrive
			int respMins = (int) ChronoUnit.MINUTES.between(dispatchTime, arriveTime);
			int respHrs = 0;
			//If minutes is 60 or greater, than response time is over an hour, so we have to account for that
			while(respMins > 59) {
				respMins -= 60;
				respHrs++;
			}
			//Once hours and minutes are calculated, move on to seconds
			int respSecs = (int) ChronoUnit.SECONDS.between(dispatchTime, arriveTime);
			//Here we check to ensure that DISPATCH comes before ARRIVE, otherwise throw an error since we have a time traveler on our hands
			if(respSecs < 0) {
				GUI.sendError("Error: Response time less than zero (calculated from Dispatch and Arrive) for row "+this.getRownum()+".");
			}
			//Assuming that check passes, we can finalize the value of the seconds variable by calculating it modulo 60
			respSecs = respSecs%60;
			//Then use the calculated variables for hours, minutes, and seconds to create the responseTime variable
			if(respHrs > 23) {
				respHrs = 23;
				GUI.sendError("Error: Response time exceeds 1 day for row "+this.getRownum()+". Response time value in table may not be accurate; check Dispatch and Arrival times.");
			}
			LocalTime responseTime = LocalTime.of(respHrs, respMins, respSecs);
			if(LocalTime.parse(this.ResponseTime.get(),timeFormatter).compareTo(responseTime) != 0) {
				GUI.sendInfo("Updated Response time from "+this.getResponse()+" to "+responseTime.toString()+" for row "+this.getRownum()+".");
				this.ResponseTime.set(responseTime.toString());
				GUI.setEdited();
			}
		} catch(DateTimeParseException e) {
			GUI.sendError("Error in updating response time: could not parse Dispatch/Arrive for row "+this.getRownum()+".");
		}

	}

	public String getResponse() {
		if(this.ResponseTime != null) {
			return this.ResponseTime.get();
		}
		else {
			return "";
		}
	}

	public String getStaffing() {
		if(this.Staffing != null) {
			return this.Staffing.get();
		}
		else {
			return "";
		}
	}

	public void setStaffing(String i) {
		String s = i.trim();
		if(s.compareTo(this.getStaffing()) != 0) {
			GUI.sendInfo("Updated Staffing from "+this.getStaffing()+" to "+s+" for row "+this.getRownum()+".");
			this.Staffing.set(s);
			GUI.setEdited();
		}
	}

	public String getDay() {
		if(this.DayOfWk != null) {
			return this.DayOfWk.get();
		}
		else {
			return "";
		}
	}

	public void setDay(String input) {
		String inputString = input.trim();
		if(inputString.compareToIgnoreCase(this.getDay()) == 0) return;
		//The following code ensures that the day of week is a valid day of week
		//Since is this isn't an entirely essential variable, a warning is thrown if day of week is invalid
		inputString = inputString.toLowerCase();
		switch (inputString) {
		case "monday":
			GUI.sendInfo("Updated Day of Week from "+this.getDay()+" to Monday for row "+this.getRownum()+".");
			this.DayOfWk.set("Monday");
			GUI.setEdited();
			break;
		case "tuesday":
			GUI.sendInfo("Updated Day of Week from "+this.getDay()+" to Tuesday for row "+this.getRownum()+".");
			this.DayOfWk.set("Tuesday");
			GUI.setEdited();
			break;
		case "wednesday":
			GUI.sendInfo("Updated Day of Week from "+this.getDay()+" to Wednesday for row "+this.getRownum()+".");
			this.DayOfWk.set("Wednesday");
			GUI.setEdited();
			break;
		case "thursday":
			GUI.sendInfo("Updated Day of Week from "+this.getDay()+" to Thursday for row "+this.getRownum()+".");
			this.DayOfWk.set("Thursday");
			GUI.setEdited();
			break;
		case "friday":
			GUI.sendInfo("Updated Day of Week from "+this.getDay()+" to Friday for row "+this.getRownum()+".");
			this.DayOfWk.set("Friday");
			GUI.setEdited();
			break;
		case "saturday":
			GUI.sendInfo("Updated Day of Week from "+this.getDay()+" to Saturday for row "+this.getRownum()+".");
			this.DayOfWk.set("Saturday");
			GUI.setEdited();
			break;
		case "sunday":
			GUI.sendInfo("Updated Day of Week from "+this.getDay()+" to Sunday for row "+this.getRownum()+".");
			this.DayOfWk.set("Sunday");
			GUI.setEdited();
			break;
		default:
			GUI.sendInfo("Warning: tried to change Day of Week to an invalid value for row "+this.getRownum()+".");
			break;
		}

		try {
			String d1 = LocalDateTime.parse(this.getDispatch(), dateAndTimeFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
			String d2 = LocalDateTime.parse(this.getArrive(), dateAndTimeFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

			if(this.DayOfWk.get().compareTo(d1) != 0 && this.DayOfWk.get().compareTo(d2) != 0) {
				if(d1.compareTo(d2) == 0) {
					GUI.sendInfo("Warning: Day of Week is inaccurate for row "+this.getRownum()+". Day of Week should be "+d1+
							". Update Dispatch/Arrival values or change Day of Week value in this row.");
				}
				else {
					GUI.sendInfo("Warning: Day of Week is inaccurate for row "+this.getRownum()+". Day of Week should be "+d1+" or "+d2+
							". Update Dispatch/Arrival values or change Day of Week value in this row.");
				}
			}
		} catch(DateTimeParseException e) {
			GUI.sendError("Error in validating day of week: could not parse Dispatch/Arrive for row "+this.getRownum()+".");
		}
	}

	public String getSsa() {
		if(this.SSA != null) {
			return this.SSA.get();
		}
		else {
			return "";
		}
	}

	public void setSsa(String input) {
		String inputString = input.trim();

		if(inputString.compareToIgnoreCase(this.getSsa()) == 0) return;

		if(inputString.compareToIgnoreCase("inside") == 0) {
			GUI.sendInfo("Updated SSA from "+this.getSsa()+" to Inside for row "+this.getRownum()+".");
			this.SSA.set("Inside");
			GUI.setEdited();
		}
		else if(inputString.compareToIgnoreCase("outside") == 0) {
			GUI.sendInfo("Updated SSA from "+this.getSsa()+" to Outside for row "+this.getRownum()+".");
			this.SSA.set("Outside");
			GUI.setEdited();
		}
		else {
			GUI.sendInfo("Warning: SSA invalid for row "+this.getRownum()+". Should be 'Inside' or 'Outside'");
		}
	}

	public int getDistrict() {
		if(this.District != null) {
			return this.District.get();
		}
		else {
			return 0;
		}
	}

	public void setDistrict(int input) {
		if(input == this.getDistrict()) return;

		if(input <= 0) {
			GUI.sendInfo("Warning: District number should be a positive integer for row "+this.getRownum()+".");
		}
		else if(input > 16 && input < 100) {
			GUI.sendInfo("Warning: District number should be an integer in range 1-16 for row "+this.getRownum()+". Allowing this value but check to ensure validity.");
			GUI.sendInfo("Updated District number from "+this.District.get()+" to "+input+" for row "+this.getRownum()+".");
			this.District.set(input);
			GUI.setEdited();

		}
		else if(input >= 100) {
			GUI.sendInfo("Warning: District number should be an integer in range 1-16 for row "+this.getRownum()+".");
		}
		else {
			GUI.sendInfo("Updated District number from "+this.District.get()+" to "+input+" for row "+this.getRownum()+".");
			this.District.set(input);
			GUI.setEdited();
		}
	}

	public String getInori() {
		if(this.INORI != null) {
			return this.INORI.get();
		}
		else {
			return "";
		}
	}

	public void setInori(String input) {
		String inputString = input.trim();
		if(inputString.compareTo(this.getInori()) != 0) {
			GUI.sendInfo("Updated INORI from "+this.getInori()+" to "+inputString+" for row "+this.getRownum()+".");
			this.INORI.set(inputString);
			GUI.setEdited();
		}
	}

	public String getIninc() {
		if(this.ININC != null) {
			return this.ININC.get();
		}
		else {
			return "";
		}
	}

	public void setIninc(String inputLongString) {
		try {
			//check to ensure valid long
			Long.parseLong(inputLongString);
			this.ININC.set(inputLongString);
			GUI.setEdited();
		} catch(NumberFormatException e) {
			GUI.sendError("Error parsing new ININC for row "+this.getRownum()+".");
		}
	}

	public String getInunit() {
		if(this.INUNIT1 != null) {
			return this.INUNIT1.get();
		}
		else {
			return "";
		}
	}

	public void setInunit(String input) {
		String inputString = input.trim();
		if(inputString.compareTo(this.getInunit()) != 0) {
			GUI.sendInfo("Updated INUNIT1 from "+this.getInunit()+" to "+inputString+" for row "+this.getRownum()+".");
			this.INUNIT1.set(inputString);
			GUI.setEdited();
		}
	}

	public String getInbdg() {
		if(this.INBDG1 != null) {
			return this.INBDG1.get();
		}
		else {
			return "";
		}
	}

	public void setInbdg(String input) {
		String inputString = input.trim();
		if(inputString.compareTo(this.getInbdg()) != 0) {
			GUI.sendInfo("Updated INBDG1 from "+this.getInbdg()+" to "+inputString+" for row "+this.getRownum()+".");
			this.INBDG1.set(inputString);
			GUI.setEdited();
		}
	}

	public String getInitype() {
		if(this.INITYPE != null) {
			return this.INITYPE.get();
		}
		else {
			return "";
		}
	}

	public void setInitype(String input) {
		String inputString = input.trim();
		if(inputString.compareTo(this.getInitype()) != 0) {
			GUI.sendInfo("Updated INITYPE from "+this.getInitype()+" to "+inputString+" for row "+this.getRownum()+".");
			this.INITYPE.set(inputString);
			GUI.setEdited();
		}
	}

	public String getInsect() {
		if(this.INSECT != null) {
			return this.INSECT.get();
		}
		else {
			return "00000";
		}
	}

	public void setInsect(String inputString) {
		if(inputString.compareTo(this.getInsect()) != 0) {
			GUI.sendInfo("Updated INSECT from "+this.getInsect()+" to "+inputString+" for row "+this.getRownum()+".");
			this.INSECT.set(inputString);
			GUI.setEdited();
		}
	}
	
	public void setInsectNumber(int input) {
		if(input != Integer.parseInt(this.getInsect())) {
			String inputAsString = fixInsect(input);
			GUI.sendInfo("Updated INSECT from "+this.getInsect()+" to "+inputAsString+" for row "+this.getRownum()+".");
			this.INSECT.set(inputAsString);
			GUI.setEdited();
		}
	}

	public String getCadinc() {
		if(this.CAD_INC != null) {
			return this.CAD_INC.get();
		}
		else {
			return "";
		}
	}

	public void setCadinc(String inputLongString) {
		try {
			//check to ensure input is valid long
			Long.parseLong(inputLongString);
			this.CAD_INC.set(inputLongString);
			GUI.setEdited();
		} catch(NumberFormatException e) {
			GUI.sendError("Error parsing new CAD_INC for row "+this.getRownum()+".");
		}
	}

	public String getServtype() {
		if(this.SERVICE_TYPE != null) {
			return this.SERVICE_TYPE.get();
		}
		else {
			return "";
		}
	}

	public void setServtype(String input) {
		String inputString = input.trim();
		if(inputString.compareToIgnoreCase("FIRE") == 0) {
			if(inputString.compareToIgnoreCase(this.getServtype()) != 0) {
				GUI.sendInfo("Updated Service Type from "+this.getServtype()+" to FIRE for row "+this.getRownum()+".");
				this.SERVICE_TYPE.set("FIRE");
				GUI.setEdited();
			}
		}
		else if(inputString.compareToIgnoreCase("EMS") == 0) {
			if(inputString.compareToIgnoreCase(this.getServtype()) != 0) {
				GUI.sendInfo("Updated Service Type from "+this.getServtype()+" to EMS for row "+this.getRownum()+".");
				this.SERVICE_TYPE.set("EMS");
				GUI.setEdited();
			}
		}
		else {
			GUI.sendInfo("Warning: Service Type must be 'FIRE' or 'EMS' for row "+this.getRownum()+". Value not changed.");
		}
	}

	public String getFua() {
		if(this.FRSTUNITARRV != null) {
			return this.FRSTUNITARRV.get();
		}
		else {
			return "";
		}
	}

	public void setFua(String input) {
		String inputString = input.trim();
		if(inputString.compareTo(this.getFua()) != 0) {
			GUI.sendInfo("Updated First Unit Arrived from "+this.getFua()+" to "+inputString+" for row "+this.getRownum()+".");
			this.FRSTUNITARRV.set(inputString);
			GUI.setEdited();
		}
	}
	
	public String fixInsect(int input) {
		if(input < 0) {
			return "00000";
		}
		else if(input < 10) {
			return "0000"+input;
		}
		else if(input < 100) {
			return "000"+input;
		}
		else if(input < 1000) {
			return "00"+input;
		}
		else if(input < 10000) {
			return "0"+input;
		}
		else {
			return ""+input;
		}
	}
}
