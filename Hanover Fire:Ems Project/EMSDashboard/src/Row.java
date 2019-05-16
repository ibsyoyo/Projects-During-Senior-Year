import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class Row {

	private int INPRTY;
	
	private String INOSTR;
	
	private String INOXSTR;	
	
	private LocalDateTime DISPATCH;
	
	private LocalDateTime ARRIVE;
	
	private double RESPONSE;
	
	private String Staffing;
	
	private String DayOfWk;
	
	private String SSA;
	
	private int District;
	
	private String INORI;
	
	private long ININC;
	
	private long CAD_INC;
	
	private String INUNIT1;
	
	private String INBDG1;
	
	private String INITYPE;
	
	private int INSECT;
	 
	private ServiceType SERVICE_TYPE;
	
	private String FRSTUNITARRV;
	
	private LocalTime ResponseTime;
	
	private final int RowNumber;
	
	private boolean isValid;
	
	public Row(int num) {
		this.RowNumber = num;
		this.isValid = true;
	}
	
	public void setValid(boolean b) {
		this.isValid = b;
	}
	
	public boolean isValid() {
		return this.isValid;
	}
	
	public String getRownum() {
		return this.RowNumber+"";
	}
	
	public int getRowNumber() {
		return this.RowNumber;
	}
	
	public String getPriority() {
		return ""+this.INPRTY;
	}
	
	public int getPriorityNumber() {
		return this.INPRTY;
	}
	
	public void setPriority(int i) {
		if(i > 0 && i < 4) {
			this.INPRTY = i;
		}
	}
	
	public String getAddress() {
		if(this.INOSTR != null) {
			return this.INOSTR;
		}
		else {
			return "";
		}
	}
	
	public void setAddress(String s) {
		this.INOSTR = s;
	}
	
	public String getCross() {
		if(this.INOXSTR != null) {
			return this.INOXSTR;
		}
		else {
			return "";
		}
	}
	
	public void setCross(String s) {
		this.INOXSTR = s;
	}
	
	public String getDispatch() {
		if(this.DISPATCH != null) {
			return this.DISPATCH.toString();
		}
		else {
			LocalDateTime temp = LocalDateTime.of(2000, 1, 1, 0, 0);
			return temp.toString();
		}
	}
	
	public LocalDateTime getDispatchTime() {
		if(this.DISPATCH != null) {
			return this.DISPATCH;
		}
		else {
			return LocalDateTime.of(2000, 1, 1, 0, 0);
		}
	}
	
	public void setDispatch(LocalDateTime t) {
		this.DISPATCH = t;
	}
	
	public String getArrive() {
		if(this.ARRIVE != null) {
			return this.ARRIVE.toString();
		}
		else {
			LocalDateTime temp = LocalDateTime.of(2000, 1, 1, 0, 0);
			return temp.toString();
		}
	}
	
	public LocalDateTime getArriveTime() {
		if(this.ARRIVE != null) {
			return this.ARRIVE;
		}
		else {
			return LocalDateTime.of(2000, 1, 1, 0, 0);
		}
	}
	
	public void setArrive(LocalDateTime t) {
		this.ARRIVE = t;
	}
	
	public String getResponse() {
		if(this.ResponseTime != null) {
			if(this.ResponseTime.toString().length() == 5) {
				return this.ResponseTime.toString()+":00";
			}
			else {
				return this.ResponseTime.toString();
			}
		}
		else {
			LocalTime temp = LocalTime.of(0, 0);
			return temp.toString()+":00";
		}
	}
	
	public LocalTime getResponseTime() {
		if(this.ResponseTime != null) {
			return this.ResponseTime;
		}
		else {
			return LocalTime.of(0, 0);
		}
	}
	
	public double getResponseDouble() {
		return this.RESPONSE;
	}
	
	public void setResponseDouble(double d) {
		this.RESPONSE = d;
	}
	
	public void setResponseTime(LocalTime t) {
		this.ResponseTime = t;
		
		int secondCount = t.getSecond() + t.getMinute()*60 + t.getHour()*3600;
		
		double response = secondCount*1.0/60.0;
		
		this.setResponseDouble(response);
	}
	
	public String getStaffing() {
		if(this.Staffing != null) {
			return this.Staffing;
		}
		else {
			return "";
		}
	}
	
	public void setStaffing(String s) {
		this.Staffing = s;
	}
	
	public String getDay() {
		if(this.DayOfWk != null) {
			return this.DayOfWk;
		}
		else {
			return "";
		}
	}
	
	public void setDay(String s) {
		this.DayOfWk = s;
	}
	
	public String getSsa() {
		if(this.SSA != null) {		
			return this.SSA;
		}
		else {
			return "";
		}
	}
	
	public void setSsa(String s) {
		//SSA is always either "Inside" or "Outside", so we can check that quickly to ensure that the SSA column is valid for each row
		//Error will occur if SSA is not valid
		if(s.toLowerCase().compareTo("inside") != 0 && s.toLowerCase().compareTo("outside") != 0) {
			GUI.sendError("Error: SSA is invalid (neither inside nor outside) for row "+this.RowNumber+".");
			this.isValid = false;
		}
		this.SSA = s;
	}
	
	public String getDistrict() {
		return this.District+"";
	}
	
	public int getDistrictNumber() {
		return this.District;
	}
	
	public void setDistrict(int i) {
		//If the district number is a valid integer, we then check that it is a valid district number (1-16)
		//Any number less than or equal to zero is invalud, so that results in an error
		if(i <= 0) {
			GUI.sendError("Error: District number is invalid (less than zero) for row "+this.RowNumber+".");
			this.isValid = false;
		}
		//If the number is 17 or greater, we will allow it but throw a warning
		//This prevents the program from refusing to accept any further districts should they be created or added
		if(i >= 17) {
			GUI.sendInfo("Warning: District number may not be valid ("+i+") for row "+this.RowNumber+".");
		}
		
		this.District = i;
	}
	
	public String getInori() {
		if(this.INORI != null) {
			return this.INORI;
		}
		else {
			return "";
		}
	}
	
	public void setInori(String s) {
		this.INORI = s;
	}
	
	public String getIninc() {
		return this.ININC+"";
	}
	
	public long getInincNumber() {
		return this.ININC;
	}
	
	//NEEDS VALIDATION
	public void setIninc(long l) {
		this.ININC = l;
	}
	
	public String getInunit() {
		if(this.INUNIT1 != null) {
			return this.INUNIT1;
		}
		else {
			return "";
		}
	}
	
	public void setInunit(String s) {
		this.INUNIT1 = s;
	}
	
	public String getInbdg() {
		if(this.INBDG1 != null) {
			return this.INBDG1;
		}
		else {
			return "";
		}
	}
	
	public void setInbdg(String s) {
		this.INBDG1 = s;
	}
	
	public String getInitype() {
		if(this.INITYPE != null) {
			return this.INITYPE;
		}
		else {
			return "";
		}
	}
	
	public void setInitype(String s) {
		this.INITYPE = s;
	}
	
	public String getInsect() {
		return this.INSECT+"";
	}
	
	public int getInsectNumber() {
		return this.INSECT;
	}
	
	public void setInsect(int i) {
		this.INSECT = i;
	}
	
	public String getCadinc() {
		return this.CAD_INC+"";
	}
	
	//NEEDS VALIDATION
	public void setCadinc(long l) {
		this.CAD_INC = l;
	}
	
	public String getServtype() {
		if(this.SERVICE_TYPE != null) {
			return this.SERVICE_TYPE+"";
		}
		else {
			return "NULL";
		}
	}
	
	public ServiceType getServiceType() {
		return this.SERVICE_TYPE;
	}
	
	public void setServiceType(ServiceType t) {
		this.SERVICE_TYPE = t;
	}
	
	public String getFua() {
		if(this.FRSTUNITARRV != null) {
			return this.FRSTUNITARRV;
		}
		else {
			return "";
		}
	}
	
	public void setFua(String s) {
		this.FRSTUNITARRV = s;
	}
	
	public static boolean hasUniqueIninc(Row inputRow, List<Row> listToBeChecked) {
		for(Row row : listToBeChecked) {
			if(row.getInincNumber() == inputRow.getInincNumber() && row.getRowNumber() != inputRow.getRowNumber()) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean hasUniqueInincs(List<Row> listToBeChecked) {
		HashMap<Long, Integer> inincMap = new HashMap<>();
		
		for(Row row : listToBeChecked) {
			if(inincMap.get(row.getInincNumber()) != null) {
				return false;
			}
			else {
				inincMap.put(row.getInincNumber(), 1);
			}
		}
		
		return true;
	}
	
	public static boolean inincCheck(long inputININC, List<Row> listToBeChecked) {
		HashMap<Long, Integer> inincMap = new HashMap<>();
		
		for(Row row : listToBeChecked) {
			if(inincMap.get(row.getInincNumber()) != null) {
				inincMap.put(row.getInincNumber(), inincMap.get(row.getInincNumber())+1);
			}
			else {
				inincMap.put(row.getInincNumber(), 1);
			}
		}
		
		if(inincMap.get(inputININC) == null || inincMap.get(inputININC) == 1) {  
			return true;
		}
		
		return false;
	}
	
	public String toString() {
		DateTimeFormatter dateAndTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss");
		
		String dispatchFormatted = dateAndTimeFormatter.format(this.DISPATCH);
		String arriveFormatted = dateAndTimeFormatter.format(this.ARRIVE);
		
		return this.INPRTY+","+this.INOSTR+","+this.INOXSTR+","+dispatchFormatted+","+arriveFormatted+","+this.RESPONSE+","+this.Staffing+","
		+this.DayOfWk+","+this.SSA+","+this.District+","+this.INORI+","+this.ININC+","+this.INUNIT1+","
		+this.INBDG1+","+this.INITYPE+","+this.INSECT+","+this.CAD_INC+","+this.SERVICE_TYPE+","+this.FRSTUNITARRV;
	}
	
}
