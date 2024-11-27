import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Order {
    private String orderID;
    private Date dateOrder;
    private Supplier supplier;
    private String staffHandled;
    private Product product;
    private int quantity;
    private String status;

    public static int orderCount = 0;
    public static Order[] orderList = new Order[300];

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    Scanner sc = new Scanner(System.in);

    public Order (String date, Supplier supplier, String staffHandled,Product product, int quantity,String status) {
        try {
            this.dateOrder = formatter.parse(date);
        }
        catch (ParseException e) {
            System.out.println("    Failed to read order date!");
        }
        this.supplier = supplier;
        this.staffHandled = staffHandled;
        this.product = product;
        this.quantity = quantity;
        this.status = status;
    }

    public Order (Date dateOrder, Supplier supplier, String staffHandled,Product product, int quantity,String status) {
        this.dateOrder = dateOrder;
        this.supplier = supplier;
        this.staffHandled = staffHandled;
        this.product = product;
        this.quantity = quantity;
        this.status = status;
        generateOrderID();
    }

    public Order () {
        this(new Date(),null,"",null,0,"");
    }

    public String getOrderID() {
        return this.orderID;
    }

    public String getDateOrder() {
        return formatter.format(dateOrder);
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public String getStaffHandled() {
        return this.staffHandled;
    }

    public Product getProduct() {
        return this.product;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getStatus() {
        return this.status;
    }

    public void setDateOrder(Date dateOrder) {
        this.dateOrder = dateOrder;
    }

    public void setSupplier (Supplier supplier) {
        this.supplier = supplier;
    }

    public void setStaffHandled(String staffHandled) {
        this.staffHandled = staffHandled;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity (int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void generateOrderID() {
        String prefix = "R";
        this.orderID = String.format("%s%04d",prefix,orderCount+1);
    }

    public void saveOrder() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("order.txt"))) {
            for (Order x:orderList) {
                if (x != null && x.getProduct() != null) {
                    writer.write(String.join("|", x.getDateOrder(),x.getSupplier().getName(),x.getStaffHandled(),x.getProduct().getName(),String.valueOf(x.getQuantity()),x.getStatus()));
                    writer.newLine();
                }
            }
        }
        catch (IOException e) {
            System.out.println("    Failed to save order data!");
        }
    }

    public void loadOrder() {
        try (BufferedReader reader = new BufferedReader(new FileReader("order.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split("\\|");
                Product tempProduct = new Product();
                Supplier tempSupplier = new Supplier();
                for (Product x:Main.productList) {
                    if (x != null && x.getName().equals(orderData[3]))
                        tempProduct = x;
                }
                for (Supplier x:Supplier.supplierList) {
                    if (x != null && x.getName().equals(orderData[1]))
                        tempSupplier = x;
                }
                try {
                    Date parsedDate = formatter.parse(orderData[0]);
                    orderList[orderCount] = new Order(parsedDate,tempSupplier,orderData[2],tempProduct,Integer.parseInt(orderData[4]),orderData[5]);
                    orderCount++;
                } catch (ParseException e) {
                    System.out.println("   Failed to read order date!");
                }

            }
        } catch (IOException e) {
            System.out.println("    Failed to load order data!");
        }
    }

    public void addPurchaseOrder() {
        int availableSpace = 0;
        for (int i = 0; i < 50;i++) {
            if (Main.locationList[i] != null) {
                availableSpace += 5000;
            }
        }

        for (Stock y:Main.stockList) {
            if (y != null && y.getDescription().equals("stockin")) {
                if (y.getQuantity() > 0)
                    availableSpace -= y.getQuantity();
            }
        }

        System.out.println("\n    ** Making Purchase Order **");
        System.out.println("    Select product to purchase (Warehouse capacity: 250,000): ");
        int i = 0;
        int[] availableOption = new int[Main.productList.length];
        int j = 0;
        for (Product x:Main.productList) {
            if (x != null && x.getStatus().equals("available")) {
                System.out.println("    "+(i+1)+". "+x.getName());
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
                    Main.purchaseOrderMenu();
                }
                else if (selection > 0 && selection <= i) {
                    boolean valid;
                    int stockAmount = 0;
                    do {
                        valid = true;
                        System.out.print("    How much do you wish to purchase in (Available space (Not including purchase order): " + availableSpace + "): ");
                        try {
                            stockAmount = sc.nextInt();
                            sc.nextLine();
                            if (stockAmount == 0)
                                Main.stock.stockIn();
                            if (availableSpace == 0) {
                                System.out.println("\n    Warehouse is currently full now!");
                                System.out.print("    Press enter to proceed!");
                                sc.nextLine();
                                Main.stock.stockIn();
                            } else if ((availableSpace - stockAmount) < 0) {
                                valid = false;
                                System.out.println("\n    Warehouse cannot handle too much stock! (Space required: " + (stockAmount - availableSpace) + ")");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("\n    You can only enter number!");
                            valid = false;
                            sc.nextLine();
                        }
                    } while (!valid);
                    System.out.println("\n    ** Purchase Order Detail ** ");
                    System.out.println("    Product: "+Main.productList[availableOption[selection-1]].getName());
                    System.out.println("    Quantity: "+stockAmount);
                    System.out.println("    Supplier: "+Main.productList[availableOption[selection-1]].getSupplier().getName());
                    System.out.println("    Unit Price: RM"+Main.productList[availableOption[selection-1]].getUnitPrice());
                    System.out.println("    Total Payment: RM"+(Main.productList[availableOption[selection-1]].getUnitPrice() * stockAmount));
                    do {
                        valid = true;
                        System.out.print("    Are you sure you want to place the purchase order (Y/N)? ");
                        String confirmation = sc.nextLine();
                        confirmation = confirmation.toUpperCase();
                        if (confirmation.equals("Y")) {
                            orderList[orderCount] = new Order(new Date(),Main.productList[availableOption[selection-1]].getSupplier(), Main.sessionName,Main.productList[availableOption[selection-1]],stockAmount,"Pending to accept by supplier");
                            orderCount++;
                            System.out.print("    You have successfully placed a purchase order! Press enter to proceed!");
                            sc.nextLine();
                            saveOrder();
                            Main.purchaseOrderMenu();
                        }
                        else if (confirmation.equals("N"))
                            Main.purchaseOrderMenu();
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

    public void payPurchaseOrder() {
        System.out.println("\n    ** Paying Purchase Order **");
        System.out.println("    Select purchase order to pay: ");
        int i = 0;
        int[] availableOption = new int[orderList.length];
        int j = 0;
        for (Order x:orderList) {
            if (x != null && x.getStatus().equals("Pending to pay")) {
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
                    Main.purchaseOrderMenu();
                }
                else if (selection > 0 && selection <= i) {
                    System.out.println("\n    ** Purchase Order Detail ** ");
                    System.out.println("    Product: "+orderList[availableOption[selection-1]].getProduct().getName());
                    System.out.println("    Quantity: "+orderList[availableOption[selection-1]].getQuantity());
                    System.out.println("    Supplier: "+orderList[availableOption[selection-1]].getSupplier().getName());
                    System.out.println("    Unit Price: RM"+orderList[availableOption[selection-1]].getProduct().getUnitPrice());
                    System.out.println("    Total Payment: RM"+(orderList[availableOption[selection-1]].getProduct().getUnitPrice() * orderList[availableOption[selection-1]].getQuantity()));
                    boolean valid;
                    do {
                        valid = true;
                        System.out.print("    Are you sure you want to pay to supplier to proceed shipping (Y/N)? ");
                        String confirmation = sc.nextLine();
                        confirmation = confirmation.toUpperCase();
                        if (confirmation.equals("Y")) {
                            orderList[availableOption[selection-1]].setStatus("Pending to ship by supplier");
                            Transaction.transactionList[Transaction.transactionCount] = new Transaction(new Date(), orderList[availableOption[selection-1]].getProduct().getUnitPrice() * orderList[availableOption[selection-1]].getQuantity());
                            Transaction.transactionCount++;

                            System.out.print("    You have successfully paid for the purchase order! Press enter to proceed!");
                            sc.nextLine();
                            saveOrder();
                            Main.transaction.saveTransaction();
                            payPurchaseOrder();
                        }
                        else if (confirmation.equals("N"))
                            Main.purchaseOrderMenu();
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

    public void cancelPurchaseOrder() {
        System.out.println("\n    ** Cancel Purchase Order **");
        System.out.println("    Select purchase order to cancel: ");
        int i = 0;
        int[] availableOption = new int[orderList.length];
        int j = 0;
        for (Order x:orderList) {
            if (x != null && x.getStatus().equals("Pending to accept by supplier")) {
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
                    Main.purchaseOrderMenu();
                }
                else if (selection > 0 && selection <= i) {
                    System.out.println("\n    ** Purchase Order Detail ** ");
                    System.out.println("    Product: "+orderList[availableOption[selection-1]].getProduct().getName());
                    System.out.println("    Quantity: "+orderList[availableOption[selection-1]].getQuantity());
                    System.out.println("    Supplier: "+orderList[availableOption[selection-1]].getSupplier().getName());
                    System.out.println("    Unit Price: RM"+orderList[availableOption[selection-1]].getProduct().getUnitPrice());
                    System.out.println("    Total Payment: RM"+(orderList[availableOption[selection-1]].getProduct().getUnitPrice() * orderList[availableOption[selection-1]].getQuantity()));
                    boolean valid;
                    do {
                        valid = true;
                        System.out.print("    Are you sure you want to cancel the purchase order (Y/N)? ");
                        String confirmation = sc.nextLine();
                        confirmation = confirmation.toUpperCase();
                        if (confirmation.equals("Y")) {
                            orderList[availableOption[selection-1]].setStatus("Cancelled by staff");

                            System.out.println("    You have successfully cancelled the purchase order! Press enter to proceed!");
                            sc.nextLine();
                            saveOrder();
                            Main.purchaseOrderMenu();
                        }
                        else if (confirmation.equals("N"))
                            Main.purchaseOrderMenu();
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

    public void viewPurchaseOrder() {
        System.out.println("\n    ** View All Purchase Order **");
        for(Order x:orderList) {
            if (x != null && x.getQuantity() > 0) {
                System.out.println("    "+x.getOrderID()+" - "+x.getDateOrder()+" = "+x.getQuantity()+" x "+x.getProduct().getName()+" (Supplied by "+x.getSupplier().getName()+") - "+x.getStatus());
            }
        }
        System.out.print("\n    Press enter to return!");
        sc.nextLine();
        Main.purchaseOrderMenu();
    }
}
