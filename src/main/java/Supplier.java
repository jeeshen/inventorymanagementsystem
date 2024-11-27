import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Supplier extends Account{
    private String supplierID;
    private String name;
    private String address;
    private String phoneNumber;

    public static int supplierCount = 0;
    public static Supplier[] supplierList = new Supplier[100];

    Scanner sc = new Scanner(System.in);

    //constructor
    public Supplier (String username, String email, String password, String name, String address, String phoneNumber) {
        super(username,email,password);
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        generateSupplierID();
    }

    public Supplier() {
        this("","","","","","");
    }

    //getter
    public String getSupplierID() {
        return this.supplierID;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    //setter
    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    //other methods
    public void generateSupplierID() {
        String prefix = "U";
        this.supplierID = String.format("%s%04d",prefix,supplierCount+1);
    }

    public void saveAccount() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("supplier.txt"))) {
            for (Supplier x:supplierList) {
                if (x != null) {
                    writer.write(String.join("|",x.getUsername(),x.getEmail(),x.getPassword(),x.getName(),x.getAddress(),x.getPhoneNumber()));
                    writer.newLine();
                }
            }
        }
        catch (IOException e) {
            System.out.println("    Failed to save supplier account data!");
        }
    }

    public void loadAccount() {
        try (BufferedReader reader = new BufferedReader(new FileReader("supplier.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] supplierData = line.split("\\|");
                supplierList[supplierCount] = new Supplier(supplierData[0],supplierData[1],supplierData[2],supplierData[3],supplierData[4],supplierData[5]);
                supplierCount++;
            }
        }
        catch (IOException e) {
            System.out.println("    Failed to read supplier account data!");
        }
    }

    public void register() {
        String inputUsername;
        String inputEmail;
        String inputPassword;
        String inputName;
        String inputAddress;
        String inputPhoneNumber;
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your username: ");
            inputUsername = sc.nextLine();
            String nameFormat = "^[a-zA-Z\\s]+$";

            if (!inputUsername.matches(nameFormat)) {
                valid = false;
                System.out.println("\n    You can only use letters or space in your name!");
            }
            else if (isNameDuplicate(inputUsername)) {
                valid = false;
                System.out.println("\n    The username is taken! Please use another username!");
            }
        } while(!valid);
        do {
            valid = true;
            System.out.print("    Enter your email: ");
            inputEmail = sc.nextLine();
            String emailFormat = "^(.+)@(\\S+)$";

            if (!inputEmail.matches(emailFormat)) {
                valid = false;
                System.out.println("\n    Invalid email format!");
            }
            else if (isEmailDuplicate(inputEmail)) {
                valid = false;
                System.out.println("\n    The email is taken! Please use another email!");
            }
        } while (!valid);

        System.out.print("    Enter your password: ");
        inputPassword = sc.nextLine();

        do {
            valid = true;
            System.out.print("    Enter your company name: ");
            inputName = sc.nextLine();
            String nameFormat = "^[a-zA-Z0-9\\s]+$";

            if (!inputName.matches(nameFormat)) {
                valid = false;
                System.out.println("\n    You can only use letters, numbers and space in your name!");
            }
        } while (!valid);

        System.out.print("    Enter your company address: ");
        inputAddress = sc.nextLine();

        do {
            valid = true;
            System.out.print("    Enter your company phone number: ");
            inputPhoneNumber = sc.nextLine();
            String phoneFormat = "01\\d{8}";

            if (!inputPhoneNumber.matches(phoneFormat)) {
                valid = false;
                System.out.println("    Invalid phone number format! Please enter again!");
            }
        } while(!valid);
        supplierList[supplierCount] = new Supplier(inputUsername,inputEmail,inputPassword,inputName,inputAddress,inputPhoneNumber);
        supplierCount++;
        saveAccount();
    }

    public void viewPurchaseOrderRequest() {
        System.out.println("\n    ** View Purchase Order Request From Kampung Grocery **");
        System.out.println("    Select purchase order to view: ");
        int i = 0;
        int[] availableOption = new int[Order.orderList.length];
        int j = 0;
        for (Order x:Order.orderList) {
            if (x != null && x.getStatus().equals("Pending to accept by supplier") && x.getSupplier().getUsername().equals(Main.sessionName)) {
                System.out.println("    "+(i+1)+". "+x.getProduct().getName()+" x "+x.getQuantity());
                availableOption[i] = j;
                i++;
            }
            j++;
        }
        if (i == 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Purchase Order Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Purchase Order Menu\n");
        }
        boolean valid2;
        do {
            valid2 = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                if (i == 0 || i+1 == selection) {
                    Main.supplierPurchaseOrderMenu();
                }
                else if (selection > 0 && selection <= i) {
                    System.out.println("\n    ** Purchase Order Detail ** ");
                    System.out.println("    Product: "+Order.orderList[availableOption[selection-1]].getProduct().getName());
                    System.out.println("    Quantity: "+Order.orderList[availableOption[selection-1]].getQuantity());
                    System.out.println("    Supplier: "+Order.orderList[availableOption[selection-1]].getSupplier().getName());
                    System.out.println("    Unit Price: RM"+Order.orderList[availableOption[selection-1]].getProduct().getUnitPrice());
                    System.out.println("    Total Payment: RM"+(Order.orderList[availableOption[selection-1]].getProduct().getUnitPrice() * Order.orderList[availableOption[selection-1]].getQuantity()));
                    boolean valid;
                    do {
                        valid = true;
                        System.out.print("    Are you sure you want to accept the purchase order from Kampung Grocery (Y/N)? ");
                        String confirmation = sc.nextLine();
                        confirmation = confirmation.toUpperCase();
                        if (confirmation.equals("Y")) {
                            Order.orderList[availableOption[selection-1]].setStatus("Pending to pay");
                            System.out.print("    You have successfully accepted the purchase order! Press enter to proceed!");
                            sc.nextLine();
                            Main.order.saveOrder();
                            viewPurchaseOrderRequest();
                        }
                        else if (confirmation.equals("N"))
                            Main.supplierPurchaseOrderMenu();
                        else {
                            System.out.println("\n    No such selection! Please select again!");
                            valid = false;
                        }
                    } while (!valid);
                }
                else {
                    System.out.println("\n    No such selection! Please enter again!");
                    valid2 = false;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please enter again!");
                valid2 = false;
                sc.nextLine();
            }
        } while (!valid2);
    }

    public void updatePurchaseOrder() {
        System.out.println("\n    ** Update Purchase Order Status **");
        System.out.println("    Select purchase order to update: ");
        int i = 0;
        int[] availableOption = new int[Order.orderList.length];
        int j = 0;
        for (Order x:Order.orderList) {
            if (x != null && (x.getStatus().equals("Pending to ship by supplier") || x.getStatus().equals("Packing") || x.getStatus().equals("Shipping")) && x.getSupplier().getUsername().equals(Main.sessionName)) {
                System.out.println("    "+(i+1)+". "+x.getProduct().getName()+" x "+x.getQuantity()+" ("+x.getStatus()+")");
                availableOption[i] = j;
                i++;
            }
            j++;
        }
        if (i == 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Purchase Order Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Purchase Order Menu\n");
        }
        boolean valid2;
        do {
            valid2 = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                if (i == 0 || i+1 == selection) {
                    Main.supplierPurchaseOrderMenu();
                }
                else if (selection > 0 && selection <= i) {
                    System.out.println("\n    ** Purchase Order Detail ** ");
                    System.out.println("    Product: "+Order.orderList[availableOption[selection-1]].getProduct().getName());
                    System.out.println("    Quantity: "+Order.orderList[availableOption[selection-1]].getQuantity());
                    System.out.println("    Supplier: "+Order.orderList[availableOption[selection-1]].getSupplier().getName());
                    System.out.println("    Unit Price: RM"+Order.orderList[availableOption[selection-1]].getProduct().getUnitPrice());
                    System.out.println("    Total Payment: RM"+(Order.orderList[availableOption[selection-1]].getProduct().getUnitPrice() * Order.orderList[availableOption[selection-1]].getQuantity()));
                    System.out.println("\n    Current Status: "+Order.orderList[availableOption[selection-1]].getStatus());
                    boolean valid;
                    do {
                        valid = true;
                        System.out.println("    Status option to select:");
                        boolean valid3;
                        boolean valid4;
                        if (Order.orderList[availableOption[selection-1]].getStatus().equals("Pending to ship by supplier")) {
                            System.out.println("    1. Packing");
                            System.out.println("    2. Shipping");
                            System.out.println("    3. Shipped");
                            System.out.println("    4. Back to Purchase Order Menu");
                            do {
                                valid3 = true;
                                System.out.print("    Enter your selection: ");
                                try {
                                    int selection2 = sc.nextInt();
                                    sc.nextLine();
                                    if (selection2 > 0 && selection2 < 5) {
                                        do {
                                            valid4 = true;
                                            System.out.print("    Are you sure (Y/N)? ");
                                            String confirmation = sc.nextLine();
                                            confirmation = confirmation.toUpperCase();
                                            if (confirmation.equals("Y")) {
                                                switch (selection2) {
                                                    case 1:
                                                        Order.orderList[availableOption[selection-1]].setStatus("Packing");
                                                        Calendar calendar = Calendar.getInstance();
                                                        calendar.add(Calendar.DAY_OF_MONTH, 10);
                                                        Date expectedDate = calendar.getTime();

                                                        Shipment.shipmentList[Shipment.shipmentCount] = new Shipment(Order.orderList[availableOption[selection-1]],expectedDate,new Date());
                                                        Shipment.shipmentCount++;
                                                        Main.shipment.saveShipment();

                                                        ShipmentTracking.shipmentTrackingList[ShipmentTracking.shipmentTrackingCount] = new ShipmentTracking(new Date(),Shipment.shipmentList[Shipment.shipmentCount-1],"Packing");
                                                        ShipmentTracking.shipmentTrackingCount++;
                                                        Main.shipmentTracking.saveShipmentTracking();

                                                        System.out.print("    You have successfully set the status to packing! Press enter to proceed!");
                                                        sc.nextLine();
                                                        Main.order.saveOrder();
                                                        updatePurchaseOrder();
                                                        break;
                                                    case 2:
                                                        Order.orderList[availableOption[selection-1]].setStatus("Shipping");
                                                        calendar = Calendar.getInstance();
                                                        calendar.add(Calendar.DAY_OF_MONTH, 10);
                                                        expectedDate = calendar.getTime();

                                                        Shipment.shipmentList[Shipment.shipmentCount] = new Shipment(Order.orderList[availableOption[selection-1]],expectedDate,new Date());
                                                        Shipment.shipmentCount++;
                                                        Main.shipment.saveShipment();

                                                        ShipmentTracking.shipmentTrackingList[ShipmentTracking.shipmentTrackingCount] = new ShipmentTracking(new Date(),Shipment.shipmentList[Shipment.shipmentCount-1],"Packing");
                                                        ShipmentTracking.shipmentTrackingCount++;
                                                        ShipmentTracking.shipmentTrackingList[ShipmentTracking.shipmentTrackingCount] = new ShipmentTracking(new Date(),Shipment.shipmentList[Shipment.shipmentCount-1],"Shipping");
                                                        ShipmentTracking.shipmentTrackingCount++;
                                                        Main.shipmentTracking.saveShipmentTracking();

                                                        System.out.print("    You have successfully set the status to shipping! Press enter to proceed!");
                                                        sc.nextLine();
                                                        Main.order.saveOrder();
                                                        updatePurchaseOrder();
                                                        break;
                                                    case 3:
                                                        Order.orderList[availableOption[selection-1]].setStatus("Shipped");
                                                        calendar = Calendar.getInstance();
                                                        calendar.add(Calendar.DAY_OF_MONTH, 10);
                                                        expectedDate = calendar.getTime();

                                                        Shipment.shipmentList[Shipment.shipmentCount] = new Shipment(Order.orderList[availableOption[selection-1]],expectedDate,new Date());
                                                        Shipment.shipmentCount++;
                                                        Main.shipment.saveShipment();

                                                        ShipmentTracking.shipmentTrackingList[ShipmentTracking.shipmentTrackingCount] = new ShipmentTracking(new Date(),Shipment.shipmentList[Shipment.shipmentCount-1],"Packing");
                                                        ShipmentTracking.shipmentTrackingCount++;
                                                        ShipmentTracking.shipmentTrackingList[ShipmentTracking.shipmentTrackingCount] = new ShipmentTracking(new Date(),Shipment.shipmentList[Shipment.shipmentCount-1],"Shipping");
                                                        ShipmentTracking.shipmentTrackingCount++;
                                                        ShipmentTracking.shipmentTrackingList[ShipmentTracking.shipmentTrackingCount] = new ShipmentTracking(new Date(),Shipment.shipmentList[Shipment.shipmentCount-1],"Shipped");
                                                        ShipmentTracking.shipmentTrackingCount++;
                                                        Main.shipmentTracking.saveShipmentTracking();
                                                        StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(Order.orderList[availableOption[selection-1]].getQuantity(),"Supplier","Warehouse Inbound","Purchase order items",new Date(),Main.sessionName,Order.orderList[availableOption[selection-1]].getProduct());
                                                        StockRecord.recordCount++;
                                                        Main.stockRecord.saveStockRecord();
                                                        Main.stockList[Stock.stockCount] = new Stock(Order.orderList[availableOption[selection-1]].getQuantity(),"inbound","from supplier",false,Order.orderList[availableOption[selection-1]].getProduct(),Main.locationList[50]);
                                                        Stock.stockCount++;
                                                        System.out.print("    You have successfully set the status to shipped! Press enter to proceed!");
                                                        sc.nextLine();
                                                        Main.order.saveOrder();
                                                        Main.stock.saveStock();
                                                        updatePurchaseOrder();
                                                        break;
                                                    case 4:
                                                        Main.purchaseOrderMenu();
                                                        break;
                                                }
                                            }
                                            else if (confirmation.equals("N")) {
                                                updatePurchaseOrder();
                                            }
                                            else {
                                                System.out.println("\n    No such selection! Please select again!");
                                                valid4 = false;
                                            }
                                        } while(!valid4);
                                    }
                                    else {
                                        valid3 = false;
                                        System.out.println("\n    No such selection! Please select again!");
                                    }
                                }
                                catch (InputMismatchException e) {
                                    System.out.println("\n    You can only enter digits!");
                                    sc.nextLine();
                                    valid3 = false;
                                }
                            } while(!valid3);
                        }
                        else if (Order.orderList[availableOption[selection-1]].getStatus().equals("Packing")) {
                            System.out.println("    1. Shipping");
                            System.out.println("    2. Shipped");
                            System.out.println("    3. Back to Purchase Order Menu");
                            do {
                                valid3 = true;
                                System.out.print("    Enter your selection: ");
                                try {
                                    int selection2 = sc.nextInt();
                                    sc.nextLine();
                                    if (selection2 > 0 && selection2 < 5) {
                                        do {
                                            valid4 = true;
                                            System.out.print("    Are you sure (Y/N)? ");
                                            String confirmation = sc.nextLine();
                                            confirmation = confirmation.toUpperCase();
                                            if (confirmation.equals("Y")) {
                                                switch (selection2) {
                                                    case 1:
                                                        Order.orderList[availableOption[selection-1]].setStatus("Shipping");
                                                        for (Shipment x:Shipment.shipmentList) {
                                                            if (x != null && x.getOrder().getOrderID().equals(Order.orderList[availableOption[selection-1]].getOrderID())) {
                                                                x.setLastUpdate(new Date());
                                                                ShipmentTracking.shipmentTrackingList[ShipmentTracking.shipmentTrackingCount] = new ShipmentTracking(new Date(),Shipment.shipmentList[Shipment.shipmentCount-1],"Shipping");
                                                                ShipmentTracking.shipmentTrackingCount++;
                                                            }
                                                        }
                                                        Main.shipment.saveShipment();
                                                        Main.shipmentTracking.saveShipmentTracking();

                                                        System.out.print("    You have successfully set the status to shipping! Press enter to proceed!");
                                                        sc.nextLine();
                                                        Main.order.saveOrder();
                                                        updatePurchaseOrder();
                                                        break;
                                                    case 2:
                                                        Order.orderList[availableOption[selection-1]].setStatus("Shipped");
                                                        for (Shipment x:Shipment.shipmentList) {
                                                            if (x != null && x.getOrder().getOrderID().equals(Order.orderList[availableOption[selection-1]].getOrderID())) {
                                                                x.setLastUpdate(new Date());
                                                                ShipmentTracking.shipmentTrackingList[ShipmentTracking.shipmentTrackingCount] = new ShipmentTracking(new Date(),Shipment.shipmentList[Shipment.shipmentCount-1],"Shipping");
                                                                ShipmentTracking.shipmentTrackingCount++;
                                                                ShipmentTracking.shipmentTrackingList[ShipmentTracking.shipmentTrackingCount] = new ShipmentTracking(new Date(),Shipment.shipmentList[Shipment.shipmentCount-1],"Shipped");
                                                                ShipmentTracking.shipmentTrackingCount++;
                                                            }
                                                        }
                                                        Main.shipment.saveShipment();
                                                        Main.shipmentTracking.saveShipmentTracking();
                                                        Main.stockList[Stock.stockCount] = new Stock(Order.orderList[availableOption[selection-1]].getQuantity(),"inbound","from supplier",false,Order.orderList[availableOption[selection-1]].getProduct(),Main.locationList[50]);
                                                        Stock.stockCount++;
                                                        StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(Order.orderList[availableOption[selection-1]].getQuantity(),"Supplier","Warehouse Inbound","Purchase order items",new Date(),Main.sessionName,Order.orderList[availableOption[selection-1]].getProduct());
                                                        StockRecord.recordCount++;
                                                        Main.stockRecord.saveStockRecord();
                                                        System.out.print("    You have successfully set the status to shipped! Press enter to proceed!");
                                                        sc.nextLine();
                                                        Main.order.saveOrder();
                                                        Main.stock.saveStock();
                                                        updatePurchaseOrder();
                                                        break;
                                                    case 3:
                                                        Main.purchaseOrderMenu();
                                                        break;
                                                }
                                            }
                                            else if (confirmation.equals("N")) {
                                                updatePurchaseOrder();
                                            }
                                            else {
                                                System.out.println("\n    No such selection! Please select again!");
                                                valid4 = false;
                                            }
                                        } while(!valid4);
                                    }
                                    else {
                                        valid3 = false;
                                        System.out.print("\n    No such selection! Please select again!");
                                    }
                                }
                                catch (InputMismatchException e) {
                                    System.out.println("\n    You can only enter digits!");
                                    sc.nextLine();
                                    valid3 = false;
                                }
                            } while(!valid3);
                        }
                        else if (Order.orderList[availableOption[selection-1]].getStatus().equals("Shipping")) {
                            System.out.println("    1. Shipped");
                            System.out.println("    2. Back to Purchase Order Menu");
                            do {
                                valid3 = true;
                                System.out.print("    Enter your selection: ");
                                try {
                                    int selection2 = sc.nextInt();
                                    sc.nextLine();
                                    if (selection2 > 0 && selection2 < 5) {
                                        do {
                                            valid4 = true;
                                            System.out.print("    Are you sure (Y/N)? ");
                                            String confirmation = sc.nextLine();
                                            confirmation = confirmation.toUpperCase();
                                            if (confirmation.equals("Y")) {
                                                switch (selection2) {
                                                    case 1:
                                                        Order.orderList[availableOption[selection-1]].setStatus("Shipped");
                                                        for (Shipment x:Shipment.shipmentList) {
                                                            if (x != null && x.getOrder().getOrderID().equals(Order.orderList[availableOption[selection-1]].getOrderID())) {
                                                                x.setLastUpdate(new Date());
                                                                ShipmentTracking.shipmentTrackingList[ShipmentTracking.shipmentTrackingCount] = new ShipmentTracking(new Date(),Shipment.shipmentList[Shipment.shipmentCount-1],"Shipped");
                                                                ShipmentTracking.shipmentTrackingCount++;
                                                            }
                                                        }
                                                        Main.shipment.saveShipment();
                                                        Main.shipmentTracking.saveShipmentTracking();
                                                        Main.stockList[Stock.stockCount] = new Stock(Order.orderList[availableOption[selection-1]].getQuantity(),"inbound",Order.orderList[availableOption[selection-1]].getOrderID(),false,Order.orderList[availableOption[selection-1]].getProduct(),Main.locationList[50]);
                                                        Stock.stockCount++;
                                                        StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(Order.orderList[availableOption[selection-1]].getQuantity(),"Supplier","Warehouse Inbound","Purchase order items",new Date(),Main.sessionName,Order.orderList[availableOption[selection-1]].getProduct());
                                                        StockRecord.recordCount++;
                                                        Main.stockRecord.saveStockRecord();
                                                        System.out.print("    You have successfully set the status to shipped! Press enter to proceed!");
                                                        sc.nextLine();
                                                        Main.order.saveOrder();
                                                        Main.stock.saveStock();
                                                        updatePurchaseOrder();
                                                        break;
                                                    case 2:
                                                        Main.purchaseOrderMenu();
                                                        break;
                                                }
                                            }
                                            else if (confirmation.equals("N")) {
                                                updatePurchaseOrder();
                                            }
                                            else {
                                                System.out.println("\n    No such selection! Please select again!");
                                                valid4 = false;
                                            }
                                        } while(!valid4);
                                    }
                                    else {
                                        valid3 = false;
                                        System.out.println("\n    No such selection! Please select again!");
                                    }
                                }
                                catch (InputMismatchException e) {
                                    System.out.println("\n    You can only enter digits!");
                                    sc.nextLine();
                                    valid3 = false;
                                }
                            } while(!valid3);
                        }
                        else {
                            valid = false;
                            System.out.println("\n    No such selection! Please select again!");
                        }
                    } while (!valid);
                }
                else {
                    System.out.println("\n    No such selection! Please enter again!");
                    valid2 = false;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please enter again!");
                valid2 = false;
                sc.nextLine();
            }
        } while (!valid2);
    }

    @Override
    public String toString() {
        return "Supplier{"+
                "Username: "+super.getUsername()+
                ",Email: "+super.getEmail()+
                ",Password: "+super.getPassword()+
                ",Supplier ID: "+this.supplierID+
                ",Supplier Company Name: "+this.name+
                ",Supplier Address: "+this.address+
                ",Supplier Phone Number: "+this.phoneNumber+
                "}";
    }
}
