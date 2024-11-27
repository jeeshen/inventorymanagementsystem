import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class StockRecord {
    private String stockRecordID;
    private int quantity;
    private String originLocation;
    private String destination;
    private String reason;
    private Date dateTransfer;
    private String staffHandled;
    private Product product;

    public static int recordCount = 0;
    public static StockRecord[] stockRecords = new StockRecord[500];

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public StockRecord(int quantity,String originLocation,String destination,String reason,String dateTransfer,String staffHandled,Product product) {
        this.quantity = quantity;
        this.originLocation = originLocation;
        this.destination = destination;
        this.reason = reason;
        try {
            this.dateTransfer = formatter.parse(dateTransfer);
        } catch (ParseException e) {
            System.out.println("    Failed to read transfer date!");
        }
        this.staffHandled = staffHandled;
        this.product = product;
        generateStockRecordID();
    }

    public StockRecord(int quantity,String originLocation,String destination,String reason,Date dateTransfer,String staffHandled,Product product) {
        this.quantity = quantity;
        this.originLocation = originLocation;
        this.destination = destination;
        this.reason = reason;
        this.dateTransfer = dateTransfer;
        this.staffHandled = staffHandled;
        this.product = product;
        generateStockRecordID();
    }

    public StockRecord() {
        this(0,"","","",new Date(),"",null);
    }

    public String getStockRecordID() {
        return this.stockRecordID;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getOriginLocation() {
        return this.originLocation;
    }

    public String getDestination() {
        return this.destination;
    }

    public String getReason() {
        return this.reason;
    }

    public String getDateTransfer() {
        return formatter.format(dateTransfer);
    }

    public String getStaffHandled() {
        return this.staffHandled;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOriginLocation(String originLocation) {
        this.originLocation = originLocation;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDateTransfer(Date dateTransfer) {
        this.dateTransfer = dateTransfer;
    }

    public void setStaffHandled(String staffHandled) {
        this.staffHandled = staffHandled;
    }

    public void setProduct (Product product) {
        this.product = product;
    }

    public void generateStockRecordID() {
        String prefix = "R";
        this.stockRecordID = String.format("%s%04d",prefix,recordCount+1);
    }

    public void saveStockRecord() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("stockrecord.txt"))) {
            for (StockRecord x:stockRecords) {
                if (x != null && x.getProduct() != null) {
                    writer.write(String.join("|", String.valueOf(x.getQuantity()),x.getOriginLocation(),x.getDestination(),x.getReason(), x.getDateTransfer(),x.getStaffHandled(),x.getProduct().getName()));
                    writer.newLine();
                }
            }
        }
        catch (IOException e) {
            System.out.println("    Failed to save category data!");
        }
    }

    public void loadStockRecord() {
        try (BufferedReader reader = new BufferedReader(new FileReader("stockrecord.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] stockRecordData = line.split("\\|");
                Product tempProduct = new Product();
                for (Product x:Main.productList) {
                    if (x != null && x.getName().equals(stockRecordData[6])) {
                        tempProduct = x;
                    }
                }
                try {
                    Date parsedDate = formatter.parse(stockRecordData[4]);
                    stockRecords[recordCount] = new StockRecord(Integer.parseInt(stockRecordData[0]),stockRecordData[1],stockRecordData[2],stockRecordData[3],parsedDate,stockRecordData[5],tempProduct);
                    recordCount++;
                } catch (ParseException e) {
                    System.out.println("   Failed to read stock record date!");
                }

            }
        } catch (IOException e) {
            System.out.println("    Failed to load stock record data!");
        }
    }
}
