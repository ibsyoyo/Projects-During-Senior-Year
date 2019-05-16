import java.time.*;
import java.util.*;

public class StatusReportCreator {
	
	private StatusReportCreator() {
		
	}
	
	static final String[] distname = new String[]{"ASHLAND","BEAVERDAM","EASTERN_HANOVER","DOSWELL","COURTHOUSE","HENRY",
			"MECHANICSVILLE","MONTPELIER","ROCKVILLE","CHICKAHOMINY","FARRINGTON","BLACK_CREEK","ASHCAKE_VRS",
			"EAST_HANOVER_VRS","WEST_HANOVER_VRS","ASHLAND_VRS"};
	
	static final String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", 
			"August", "September", "October", "November", "December"};
	

	public static void complianceReportTest(List<Row> in, int month, int prio, ServiceType type) {

		LocalDateTime start = LocalDateTime.of(2018, month, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2018, month+1, 1, 0, 0);

		List<Row> list = Methods.filterDate(Methods.filterPriority(Methods.filterServiceType(in, type), prio), start, end);

		int[] ctArray = new int[16];
		double[] pcArray = new double[16];
		String[] artArray = new String[16];

		for(int i = 1; i < 17; i++) {
			ctArray[i-1] = Methods.filterDistrict(list, i).size();
			pcArray[i-1] = Methods.percentCompliance(Methods.filterDistrict(list, i), LocalTime.of(0, 9, 0));
			artArray[i-1] = Methods.averageResponseTime(Methods.filterDistrict(list, i));
		}

		System.out.println("Compliance Report for "+months[month-1]);
		
		for(int j = 0; j < 16; j++) {
			System.out.println("Station #"+(j+1)+" District: " + distname[j] + " There are " +ctArray[j]+
					" Priority "+prio+" "+type+" incidents, "+pcArray[j]+"% compliance, and an average response time of "
					+artArray[j].substring(3, artArray[j].length()));
		}

	}
	

	public static void complianceReportBoth(List<Row> in, LocalDateTime begin, LocalDateTime end) {
		
	}
	
	public static void complianceReportEMS(List<Row> in, LocalDateTime begin, LocalDateTime end) {
		
	}
	
	public static void complianceReportFire(List<Row> in, LocalDateTime begin, LocalDateTime end) {
		
	}
	
}
