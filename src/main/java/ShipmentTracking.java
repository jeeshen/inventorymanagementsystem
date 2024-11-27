import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ShipmentTracking {
    private String shipmentTrackingID;
    private Date date;
    private Shipment shipment;
    private String status;

    public static int shipmentTrackingCount = 0;
    public static ShipmentTracking[] shipmentTrackingList = new ShipmentTracking[500];

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    Scanner sc = new Scanner(System.in);

    public ShipmentTracking(Date date, Shipment shipment, String status) {
        this.date = date;
        this.shipment = shipment;
        this.status = status;
        generateShipmentTrackingID();
    }

    public ShipmentTracking(String date,Shipment shipment,String status) {
        try {
            this.date = formatter.parse(date);
        }
        catch (ParseException e) {
            System.out.println("    Failed to read shipment tracking date!");
        }
        this.shipment = shipment;
        this.status = status;
        generateShipmentTrackingID();
    }

    public ShipmentTracking() {
        this(new Date(),null,"");
    }

    public String getShipmentTrackingID() {
        return this.shipmentTrackingID;
    }

    public String getDate() {
        return formatter.format(date);
    }

    public Shipment getShipment() {
        return this.shipment;
    }

    public String getStatus() {
        return this.status;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void generateShipmentTrackingID() {
        String prefix = "ST";
        this.shipmentTrackingID = String.format("%s%04d",prefix,shipmentTrackingCount+1);
    }

    public void saveShipmentTracking() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("shipmenttracking.txt"))) {
            for (ShipmentTracking x:shipmentTrackingList) {
                if (x != null && x.getShipment() != null) {
                    writer.write(String.join("|", x.getDate(),x.getShipment().getShipmentID(),x.getStatus()));
                    writer.newLine();
                }
            }
        }
        catch (IOException e) {
            System.out.println("    Failed to save shipment tracking data!");
        }
    }

    public void loadShipmentTracking() {
        try (BufferedReader reader = new BufferedReader(new FileReader("shipmenttracking.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] shipmentTrackingData = line.split("\\|");
                Shipment tempShipment = new Shipment();
                for (Shipment x:Shipment.shipmentList) {
                    if (x != null && x.getShipmentID().equals(shipmentTrackingData[1]))
                        tempShipment = x;
                }
                try {
                    Date parsedDate = formatter.parse(shipmentTrackingData[0]);
                    shipmentTrackingList[shipmentTrackingCount] = new ShipmentTracking(parsedDate,tempShipment,shipmentTrackingData[2]);
                    shipmentTrackingCount++;
                } catch (ParseException e) {
                    System.out.println("   Failed to read shipment tracking date!");
                }

            }
        } catch (IOException e) {
            System.out.println("    Failed to load shipment tracking data!");
        }
    }

    public void viewShipmentTracking() {
        System.out.println("\n    ** Track Purchase Order **");
        System.out.println("    Select purchase order to track: ");
        int i = 0;
        for (Order x:Order.orderList) {
            if (x != null && x.getProduct() != null) {
                System.out.println("    "+(i+1)+". "+x.getQuantity()+" x "+x.getProduct().getName()+" ("+x.getStatus()+")");
                i++;
            }
        }
        if (i == 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Purchase Order Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Purchase Order Menu\n");
        }
        boolean valid;
        int selection = 0;
        do {
            valid = true;
            try {
                System.out.print("    Enter your selection: ");
                selection = sc.nextInt();
                sc.nextLine();
                if (i == 0 || selection == i+1)
                    Main.purchaseOrderMenu();
                else if (!(selection > 0 && selection <= i)) {
                    valid = false;
                    System.out.println("\n    No such selection! Please enter again!");
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    You can only enter digits!");
                sc.nextLine();
                valid = false;
            }
        } while (!valid);
        System.out.println("\n    ** Purchase Order Tracking **");
        int j = 0;
        for (Order x:Order.orderList) {
            if (x != null && x.getProduct() != null && x.getOrderID().equals(Order.orderList[selection-1].getOrderID())) {
                for (Shipment y: Shipment.shipmentList) {
                    if (y != null && y.getOrder().getOrderID().equals(x.getOrderID())) {
                        for (ShipmentTracking p: ShipmentTracking.shipmentTrackingList) {
                            if (p != null && p.getShipment().getShipmentID().equals(y.getShipmentID())) {
                                System.out.println("    "+p.getDate()+" - "+p.getStatus());
                                j++;
                            }
                        }
                    }
                }
            }
        }

        if (j < 3) {
            System.out.println("    Purchased item will be delivered soon....");
        }
        System.out.print("\n    Press enter to proceed!");
        sc.nextLine();
        viewShipmentTracking();
    }
}
