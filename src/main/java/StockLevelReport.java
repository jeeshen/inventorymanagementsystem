import java.util.Date;
import java.util.Scanner;

public class StockLevelReport extends Report{
    private int stockLevel;
    private int stock;
    private double unitPrice;
    private double worth;
    private Product product;

    public static int reportCount = 0;
    public static StockLevelReport[] stockLevelReports = new StockLevelReport[100];

    Scanner sc = new Scanner(System.in);

    public StockLevelReport(Date dateGenerated, String staffInCharge, int stockLevel, int stock, double unitPrice, double worth, Product product) {
        super(dateGenerated,staffInCharge);
        this.stockLevel = stockLevel;
        this.unitPrice = unitPrice;
        this.worth = worth;
        this.stock = stock;
        this.product = product;
    }

    public StockLevelReport() {
        this(new Date(),"",0,0,0,0,null);
    }

    public int getStockLevel() {
        return this.stockLevel;
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

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
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
                for (Stock y:Main.stockList) {
                    if (y != null && y.getQuantity() > 0 && y.getProduct().getName().equals(x.getName()))
                        stock += y.getQuantity();
                }
                stockLevelReports[reportCount] = new StockLevelReport(new Date(),Main.sessionName,200,stock,x.getUnitPrice(),(stock*x.getUnitPrice()),x);
                reportCount++;
            }
        }
    }

    public void displayReport() {
        if (reportCount == 0)
            stockLevelReports[0] = new StockLevelReport();
        int i = 0;
        double total = 0;
        System.out.println("\n    Report ID: "+stockLevelReports[0].getReportID());
        System.out.println("    Date Generated: "+stockLevelReports[0].getDateGenerated());
        System.out.println("    Staff In Charged: "+Main.sessionName);
        System.out.printf("\n    %-60s | %-40s | %-15s | %-10s | %-15s\n", "Product","Supplier","Unit Price (RM)","Stock","Total Worth (RM)");
        System.out.println("    -----------------------------------------------------------------------------------------------------------------------------------------------");
        for (StockLevelReport x:stockLevelReports) {
            if (x != null) {
                if (x.getStock() <= x.getStockLevel())
                    System.out.printf("\u001B[31m    %-60s | %-50s | %-15.2f | %-10d | %-15.2f\u001B[0m\n",x.getProduct().getName(),x.getProduct().getSupplier().getName(),x.getUnitPrice(),x.getStock(),x.getWorth());
                else
                    System.out.printf("    %-60s | %-50s | %-15.2f | %-10d | %-15.2f\n",x.getProduct().getName(),x.getProduct().getSupplier().getName(),x.getUnitPrice(),x.getStock(),x.getWorth());
                total += x.getWorth();
                i++;
            }
        }
        if (i == 0)
            System.out.println("    No data found!");
        System.out.println("    -----------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("    Stock Total Worth: RM"+total);
        System.out.println("\n    PS: Red colour text indicates the stock is lower than 200! Please restock them quickly!");
        System.out.print("\n    Press enter to return!");
        sc.nextLine();
        Main.reportMenu();
    }
}
