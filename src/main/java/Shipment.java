import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Shipment {
    public String shipmentID;
    private Order order;
    private Date expectedReceiveDate;
    private Date lastUpdate;

    public static int shipmentCount = 0;
    public static Shipment[] shipmentList = new Shipment[100];

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public Shipment (Order order,String date1,String date2) {
        this.order = order;
        try {
            this.expectedReceiveDate = formatter.parse(date1);
            this.lastUpdate = formatter.parse(date2);
        }
        catch (ParseException e) {
            System.out.println("    Failed to read order date!");
        }
        generateReceiveID();
    }

    public Shipment(Order order,Date date1, Date date2) {
        this.order = order;
        this.expectedReceiveDate = date1;
        this.lastUpdate = date2;
        generateReceiveID();
    }

    public Shipment() {
        this(null,new Date(),new Date());
    }

    public String getShipmentID() {
        return this.shipmentID;
    }

    public Order getOrder() {
        return this.order;
    }

    public String getExpectedReceiveDate() {
        return formatter.format(this.expectedReceiveDate);
    }

    public String getLastUpdate() {
        return formatter.format(this.lastUpdate);
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setExpectedReceiveDate(Date expectedReceiveDate) {
        this.expectedReceiveDate = expectedReceiveDate;
    }

    public void setLastUpdate (Date lastUpdate){
        this.lastUpdate = lastUpdate;
    }

    public void generateReceiveID() {
        String prefix = "E";
        this.shipmentID = String.format("%s%04d",prefix,shipmentCount+1);
    }

    public void saveShipment() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("shipment.txt"))) {
            for (Shipment x:shipmentList) {
                if (x != null && x.getOrder() != null) {
                    writer.write(String.join("|", x.getOrder().getOrderID(),x.getExpectedReceiveDate(),x.getLastUpdate()));
                    writer.newLine();
                }
            }
        }
        catch (IOException e) {
            System.out.println("    Failed to save shipment data!");
        }
    }

    public void loadShipment() {
        try (BufferedReader reader = new BufferedReader(new FileReader("shipment.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] receiveData = line.split("\\|");
                Order tempOrder = new Order();
                for (Order x:Order.orderList) {
                    if (x != null && x.getOrderID().equals(receiveData[0]))
                        tempOrder = x;
                }
                try {
                    Date parsedDate = formatter.parse(receiveData[1]);
                    Date parsedDate2 = formatter.parse(receiveData[2]);
                    shipmentList[shipmentCount] = new Shipment(tempOrder,parsedDate,parsedDate2);
                    shipmentCount++;
                } catch (ParseException e) {
                    System.out.println("   Failed to read shipment date!");
                }

            }
        } catch (IOException e) {
            System.out.println("    Failed to load shipment data!");
        }
    }
}
