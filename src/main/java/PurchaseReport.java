import java.util.Date;
import java.util.Scanner;

public class PurchaseReport extends Report{
    private int stock;
    private double unitPrice;
    private double worth;
    private Product product;

    public static int reportCount = 0;
    public static PurchaseReport[] purchaseReports = new PurchaseReport[100];

    Scanner sc = new Scanner(System.in);

    public PurchaseReport(Date dateGenerated, String staffInCharge, int stock, double unitPrice, double worth, Product product) {
        super(dateGenerated,staffInCharge);
        this.unitPrice = unitPrice;
        this.worth = worth;
        this.stock = stock;
        this.product = product;
    }

    public PurchaseReport() {
        this(new Date(),"",0,0,0,null);
    }

    public int getStock() {
        return this.stock;
    }

    public double getUnitPrice() {
        return this.unitPrice;
    }

    public double getWorth() {
        return this.worth;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setUnitPrice (double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setWorth(double worth) {
        this.worth = worth;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void loadProductToReport() {
        for (Product x:Main.productList) {
            if (x != null && x.getSupplier() != null) {
                int stock = 0;
                for (Order y:Order.orderList) {
                    if (y != null && y.getQuantity() > 0 && y.getProduct().getName().equals(x.getName()) && y.getStatus().equals("Shipped"))
                        stock += y.getQuantity();
                }
                purchaseReports[reportCount] = new PurchaseReport(new Date(),Main.sessionName,stock,x.getUnitPrice(),(stock*x.getUnitPrice()),x);
                reportCount++;
            }
        }
    }

    public void displayReport() {
        if (reportCount == 0)
            purchaseReports[0] = new PurchaseReport();
        int i = 0;
        double total = 0;
        System.out.println("\n    Report ID: "+purchaseReports[0].getReportID());
        System.out.println("    Date Generated: "+purchaseReports[0].getDateGenerated());
        System.out.println("    Staff In Charged: "+Main.sessionName);
        System.out.printf("\n    %-50s | %-40s | %-15s | %-20s | %-15s\n", "Product","Supplier","Unit Price (RM)","Total Stock Bought","Total Worth (RM)");
        System.out.println("    ----------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (PurchaseReport x:purchaseReports) {
            if (x != null && x.getStock() > 0) {
                System.out.printf("    %-50s | %-40s | %-15.2f | %-20d | %-15.2f\n",x.getProduct().getName(),x.getProduct().getSupplier().getName(),x.getUnitPrice(),x.getStock(),x.getWorth());
                total += x.getWorth();
                i++;
            }
        }
        if (i == 0)
            System.out.println("    No data found!");
        System.out.println("    ----------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("    Total Payment: RM"+total);
        System.out.println("\n    PS: This report will only show purchase order that is shipped by supplier");
        System.out.print("\n    Press enter to return!");
        sc.nextLine();
        Main.reportMenu();
    }
}
