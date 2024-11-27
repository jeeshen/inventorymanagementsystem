import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Report {
    private String reportID;
    private Date dateGenerated;
    private String staffInCharge;
    private static int reportCount = 0;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public Report (Date dateGenerated, String staffInCharge) {
        this.dateGenerated = dateGenerated;
        this.staffInCharge = staffInCharge;
        generateReportID();
    }

    public Report (String date, String staffInCharge) {
        try {
            this.dateGenerated = formatter.parse(date);
        }
        catch (ParseException e) {
            System.out.println("    Failed to read report date");
        }
        this.staffInCharge = staffInCharge;
        generateReportID();
    }

    public String getReportID() {
        return this.reportID;
    }

    public String getDateGenerated() {
        return formatter.format(this.dateGenerated);
    }

    public String getStaffInCharge() {
        return this.staffInCharge;
    }

    public void setDateGenerated(Date dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

    public void setStaffInCharge(String staffInCharge) {
        this.staffInCharge = staffInCharge;
    }

    public void generateReportID() {
        String prefix = "Y";
        this.reportID = String.format("%s%04d",prefix,reportCount+1);
        reportCount++;
    }
}
