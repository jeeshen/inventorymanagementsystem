import java.util.Date;
import java.util.Scanner;

public class ReturnReport extends Report{
    private Supplier supplier;
    private int count;

    public static int reportCount = 0;
    public static ReturnReport[] returnReports = new ReturnReport[100];

    Scanner sc = new Scanner(System.in);

    public ReturnReport(Date dateGenerated, String staffInCharge, Supplier supplier, int count) {
        super(dateGenerated,staffInCharge);
        this.supplier = supplier;
        this.count = count;
    }

    public ReturnReport() {
        this(new Date(),"",null,0);
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public int getCount() {
        return this.count;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void loadProductToReport() {
        for (Supplier x:Supplier.supplierList) {
            int returnTimes = 0;
            if (x != null && !x.getUsername().isEmpty()) {
                for (Stock y:Main.stockList) {
                    if (y != null && y.getDescription().equals("Returned") && y.getProduct().getSupplier().getName().equals(x.getName())) {
                        returnTimes++;
                    }
                }
                returnReports[reportCount] = new ReturnReport(new Date(),Main.sessionName,x,returnTimes);
                reportCount++;
            }
        }
    }

    public void displayReport() {
        if (reportCount == 0)
            returnReports[0] = new ReturnReport();
        int i = 0;
        double total = 0;
        System.out.println("\n    Report ID: "+returnReports[0].getReportID());
        System.out.println("    Date Generated: "+returnReports[0].getDateGenerated());
        System.out.println("    Staff In Charged: "+Main.sessionName);
        System.out.printf("\n    %-50s | %-40s\n", "Supplier","Return Times");
        System.out.println("    -----------------------------------------------------------------------------------------------------------------------------------------------");
        for (ReturnReport x:returnReports) {
            if (x != null && x.getCount() > 0) {
                if (x.getCount() >= 5)
                    System.out.printf("\u001B[31m    %-50s | %-40s\u001B[0m\n",x.getSupplier().getName(),x.getCount());
                else
                    System.out.printf("    %-50s | %-40s\n",x.getSupplier().getName(),x.getCount());
                i++;
            }
        }
        if (i == 0)
            System.out.println("    No data found!");
        System.out.println("    -----------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("\n    PS: Red colour text indicates return times more than 5! Please aware!");
        System.out.print("\n    Press enter to return!");
        sc.nextLine();
        Main.reportMenu();
    }
}
