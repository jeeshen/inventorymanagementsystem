import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;

public class MovementReport extends Report{
    private int stock;
    private String action;

    public static int reportCount = 0;
    public static MovementReport[] movementReports = new MovementReport[100];

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    Scanner sc = new Scanner(System.in);

    public MovementReport(Date dateGenerated, String staffInCharge, int stock, String action) {
        super(dateGenerated,staffInCharge);
        this.stock = stock;
        this.action = action;
    }

    public MovementReport() {
        this(new Date(),"",0,"");
    }

    public int getStock() {
        return this.stock;
    }

    public String getAction() {
        return this.action;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void loadProductToReport() {
        for (StockRecord x:StockRecord.stockRecords) {
            Date dateNow = new Date();
            try {
                if (x != null )
                    dateNow = formatter.parse(x.getDateTransfer());
            } catch (ParseException e) {
                System.out.println("\n    Failed to convert date");
            }
            YearMonth yearMonthNow = YearMonth.now();
            YearMonth yearMonthDateNow = YearMonth.from(dateNow.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            if (x != null && x.getProduct() != null && yearMonthDateNow.equals(yearMonthNow)) {
                if ((x.getOriginLocation().equals("Person") || x.getOriginLocation().equals("Supplier")) && x.getDestination().equals("Warehouse")) {
                    movementReports[reportCount] = new MovementReport(new Date(),Main.sessionName,x.getQuantity(),"in");
                    reportCount++;
                }
                else if ((x.getOriginLocation().equals("Warehouse Inbound") || x.getOriginLocation().equals("Warehouse")) && (x.getDestination().equals("Warehouse Rack") || x.getDestination().equals("Warehouse Return Zone"))) {
                    movementReports[reportCount] = new MovementReport(new Date(),Main.sessionName,x.getQuantity(),"between");
                    reportCount++;
                }
                else if ((x.getOriginLocation().equals("Warehouse Return Zone") || x.getOriginLocation().equals("Warehouse")) && (x.getDestination().equals("Person") || x.getDestination().equals("Supplier"))){
                    movementReports[reportCount] = new MovementReport(new Date(),Main.sessionName,x.getQuantity(),"out");
                    reportCount++;
                }
            }
        }
    }

    public void displayReport() {
        if (reportCount == 0)
            movementReports[0] = new MovementReport();
        int i = 0;
        int in = 0;
        int out = 0;
        int between = 0;
        System.out.println("\n    Report ID: "+movementReports[0].getReportID());
        System.out.println("    Date Generated: "+movementReports[0].getDateGenerated());
        System.out.println("    Staff In Charged: "+Main.sessionName);
        System.out.printf("\n    %-60s | %-30s\n", "Action Of Current Month","Stock");
        System.out.println("    ---------------------------------------------------------------------------");
        for (MovementReport x:movementReports) {
            if (x != null && x.getStock() > 0) {
                if (x.getAction().equals("in")) {
                    in += x.getStock();
                }
                else if (x.getAction().equals("between")) {
                    between += x.getStock();
                }
                else if (x.getAction().equals("out")) {
                    out += x.getStock();
                }
                i++;
            }
        }

        if (i == 0)
            System.out.println("    No data found!");
        else {
            System.out.printf("    %-60s | %-30d\n","Transfer from External Source to Inventory", in);
            System.out.printf("    %-60s | %-30d\n","Internal Inventory Transfer", between);
            System.out.printf("    %-60s | %-30d\n","Transfer from Inventory to External Destination", out);
        }

        System.out.println("    ---------------------------------------------------------------------------");
        System.out.print("\n    Press enter to return!");
        sc.nextLine();
        Main.reportMenu();
    }
}
