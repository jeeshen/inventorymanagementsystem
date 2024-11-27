import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Category[] categoryList = new Category[100];
    static Product[] productList = new Product[100];
    static Stock[] stockList = new Stock[100];
    static Location[] locationList = new Location[100];

    static String sessionName = "";
    static Scanner sc = new Scanner(System.in);

    static Staff staff = new Staff();
    static Supplier supplier = new Supplier();
    static Category category = new Category();
    static Product product = new Product();
    static Location location = new Location();
    static Stock stock = new Stock();
    static StockRecord stockRecord = new StockRecord();
    static Order order = new Order();
    static Shipment shipment = new Shipment();
    static ShipmentTracking shipmentTracking = new ShipmentTracking();
    static Transaction transaction = new Transaction();
    static StockLevelReport stockLevelReport = new StockLevelReport();
    static MovementReport movementReport = new MovementReport();
    static PurchaseReport purchaseReport = new PurchaseReport();
    static ReturnReport returnReport = new ReturnReport();

    public static void main (String[] args) {
        //loading all the data here
        loadAccountData();
        category.loadCategory();
        product.loadProduct(); //must load category & supplier first
        location.loadLocation();
        stock.loadStock();
        stockRecord.loadStockRecord();
        order.loadOrder();
        shipment.loadShipment();
        shipmentTracking.loadShipmentTracking();
        transaction.loadOrder();

        mainMenu();
    }

    public static void supplierViewProduct() {
        System.out.println("\n    ** View Product Under Your Company **");
        System.out.println("    Product List: ");
        int i = 1;
        for (Product x:productList) {
            if (x != null && x.getSupplier().getUsername().equals(sessionName)) {
                System.out.println("    "+i+". "+x.getName());
                i++;
            }
        }
        if (i == 1) {
            System.out.println("    No product found!");
        }
        System.out.print("\n    Press enter to return!");
        sc.nextLine();
        supplierMenu();
    }

    public static void returnManage() {
        System.out.println("\n    ** Returns Management **");
        System.out.println("    Select returns to manage: ");
        int i = 0;
        int[] availableOption = new int[stockList.length];
        int j =0;
        for (Stock x:stockList) {
            if (x != null && x.getDescription().equals("return") && x.getQuantity() > 0) {
                System.out.println("    "+(i+1)+". "+x.getQuantity()+" x "+x.getProduct().getName());
                availableOption[i] = j;
                i++;
            }
            j++;
        }
        if (i == 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Staff Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Staff Menu\n");
        }
        boolean valid2;
        do {
            valid2 = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                if (i == 0 || i+1 == selection) {
                    staffMenu();
                }
                else if (selection > 0 && selection <= i) {
                    boolean valid3;
                    do {
                        valid3 = true;
                        System.out.print("    Are you sure you want to return this (Y/N)? ");
                        String confirmation = sc.nextLine();
                        confirmation = confirmation.toUpperCase();
                        if (confirmation.equals("Y")) {
                            StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(stockList[availableOption[selection-1]].getQuantity(),"Warehouse Return Zone","Supplier","Returning product",new Date(),sessionName,stockList[availableOption[selection-1]].getProduct());
                            StockRecord.recordCount++;
                            stockList[availableOption[selection-1]].setQuantity(0);
                            stockList[availableOption[selection-1]].setDescription("Returned");
                            stockList[availableOption[selection-1]].setLocation(locationList[52]);
                            double refundAmount = 0;
                            for (Order x:Order.orderList) {
                                if (x != null && x.getOrderID().equals(stockList[availableOption[selection-1]].getPendingReason())) {
                                    x.setStatus("Returned to supplier");
                                    refundAmount += (x.getQuantity() * x.getProduct().getUnitPrice());
                                }
                            }

                            System.out.println("\n    The product will be arranged and send back to supplier soon!");
                            if (refundAmount > 0)
                                System.out.println("    RM"+refundAmount+" has been refunded to Kampung Grocery!");
                            else
                                System.out.println("    Purchase order payment has been refunded to Kampung Grocery!");
                            System.out.print("    Press enter to proceed!");
                            sc.nextLine();
                            stock.saveStock();
                            stockRecord.saveStockRecord();
                            order.saveOrder();
                            returnManage();
                        }
                        else if (confirmation.equals("N")) {
                            staffMenu();
                        }
                        else {
                            valid3 = false;
                            System.out.println("\n    No such selection! Please select again!");
                        }
                    } while(!valid3);
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
    } //no debug yet

    public static void purchaseOrderMenu() {
        logo();
        System.out.println("\n    ** Purchase Order Menu **");
        System.out.println("    Select your option:");
        System.out.println("    1. Place Purchase Order");
        System.out.println("    2. Pay for Purchase Order");
        System.out.println("    3. Cancel Purchase Order");
        System.out.println("    4. Track Purchase Order");
        System.out.println("    5. View All Purchase Order");
        System.out.println("    6. View All Transactions");
        System.out.println("    7. Back to Staff Menu\n");
        boolean valid;
        int selection;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                selection = sc.nextInt();
                sc.nextLine();
                switch (selection) {
                    case 1:
                        order.addPurchaseOrder();
                        break;
                    case 2:
                        order.payPurchaseOrder();
                        break;
                    case 3:
                        order.cancelPurchaseOrder();
                        break;
                    case 4:
                        shipmentTracking.viewShipmentTracking();
                        break;
                    case 5:
                        order.viewPurchaseOrder();
                        break;
                    case 6:
                        transaction.viewTransaction();
                        break;
                    case 7:
                        staffMenu();
                        break;
                    default:
                        System.out.println("\n    No such selection! Please select again!");
                        valid = false;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please select again!");
                valid = false;
                sc.nextLine();
            }
        } while (!valid);
    }

    public static void stockMenu() {
        logo();
        System.out.println("\n    ** Stock Menu **");
        System.out.println("    Select your option:");
        System.out.println("    1. Stock In");
        System.out.println("    2. Stock Out");
        System.out.println("    3. Stock Record");
        System.out.println("    4. Return Stock");
        System.out.println("    5. Stock Loading (From Supplier)");
        System.out.println("    6. View All Stock");
        System.out.println("    7. Back to Staff Menu\n");
        boolean valid;
        int selection;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                selection = sc.nextInt();
                sc.nextLine();
                switch (selection) {
                    case 1:
                        stock.stockIn();
                        break;
                    case 2:
                        stock.stockOut();
                        break;
                    case 3:
                        stock.stockRecord();
                        break;
                    case 4:
                        stock.stockReturn();
                        break;
                    case 5:
                        stock.stockLoading();
                        break;
                    case 6:
                        stock.viewStock();
                        break;
                    case 7:
                        staffMenu();
                        break;
                    default:
                        System.out.println("\n    No such selection! Please select again!");
                        valid = false;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please select again!");
                valid = false;
                sc.nextLine();
            }
        } while (!valid);
    }

    public static void categoryMenu() {
        logo();
        System.out.println("\n    ** Category Menu **");
        System.out.println("    Select your option:");
        System.out.println("    1. Add New Category");
        System.out.println("    2. Edit Existing Category");
        System.out.println("    3. Delete Existing Category");
        System.out.println("    4. View All Category");
        System.out.println("    5. Back to Staff Menu\n");
        boolean valid;
        int selection;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                selection = sc.nextInt();
                sc.nextLine();
                switch (selection) {
                    case 1:
                        category.addCategory();
                        break;
                    case 2:
                        category.editCategory();
                        break;
                    case 3:
                        category.deleteCategory();
                        break;
                    case 4:
                        category.viewCategory();
                        break;
                    case 5:
                        staffMenu();
                        break;
                    default:
                        System.out.println("\n    No such selection! Please select again!");
                        valid = false;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please select again!");
                valid = false;
                sc.nextLine();
            }
        } while (!valid);
    }

    public static void productMenu() {
        logo();
        System.out.println("\n    ** Product Menu **");
        System.out.println("    Select your option:");
        System.out.println("    1. Add New Product");
        System.out.println("    2. Edit Existing Product");
        System.out.println("    3. Delete Existing Product");
        System.out.println("    4. View All Product");
        System.out.println("    5. Back to Staff Menu\n");
        boolean valid;
        int selection;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                selection = sc.nextInt();
                sc.nextLine();
                switch (selection) {
                    case 1:
                        product.addProduct();
                        break;
                    case 2:
                        product.editProduct();
                        break;
                    case 3:
                        product.deleteProduct();
                        break;
                    case 4:
                        product.viewProduct();
                        break;
                    case 5:
                        staffMenu();
                        break;
                    default:
                        System.out.println("\n    No such selection! Please select again!");
                        valid = false;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please select again!");
                valid = false;
                sc.nextLine();
            }
        } while (!valid);
    }

    public static void reportMenu() {
        logo();
        System.out.println("\n    ** Report Menu **");
        System.out.println("    Select your option:");
        System.out.println("    1. View Stock Level Report");
        System.out.println("    2. View Stock Movement Report");
        System.out.println("    3. View Purchase Order Report");
        System.out.println("    4. View Return Report");
        System.out.println("    5. Back to Staff Menu\n");
        boolean valid;
        int selection;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                selection = sc.nextInt();
                sc.nextLine();
                switch (selection) {
                    case 1:
                        stockLevelReport.loadProductToReport();
                        stockLevelReport.displayReport();
                        break;
                    case 2:
                        movementReport.loadProductToReport();
                        movementReport.displayReport();
                        break;
                    case 3:
                        purchaseReport.loadProductToReport();
                        purchaseReport.displayReport();
                        break;
                    case 4:
                        returnReport.loadProductToReport();
                        returnReport.displayReport();
                        break;
                    case 5:
                        staffMenu();
                        break;
                    default:
                        System.out.println("\n    No such selection! Please select again!");
                        valid = false;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please select again!");
                valid = false;
                sc.nextLine();
            }
        } while (!valid);
    }

    public static void staffMenu() {
        logo();
        System.out.println("\n    ** Staff Menu **");
        System.out.println("    Select your option:");
        System.out.println("    1. Product Management");
        System.out.println("    2. Category Management");
        System.out.println("    3. Stock Management");
        System.out.println("    4. Purchase Order Management");
        System.out.println("    5. Returns Management");
        System.out.println("    6. Reporting and Analytics");
        System.out.println("    7. Change Password");
        System.out.println("    8. Sign Out");
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                switch (selection) {
                    case 1:
                        productMenu();
                        break;
                    case 2:
                        categoryMenu();
                        break;
                    case 3:
                        stockMenu();
                        break;
                    case 4:
                        purchaseOrderMenu();
                        break;
                    case 5:
                        returnManage();
                        break;
                    case 6:
                        reportMenu();
                        break;
                    case 7:
                        Account.changePassword(sessionName);
                        saveAccountData();
                        staffMenu();
                        break;
                    case 8:
                        mainMenu();
                        break;
                    default:
                        System.out.println("\n    No such selection! Please select again!");
                        valid = false;
                        break;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please select again!");
                sc.nextLine();
                valid = false;
            }
        } while (!valid);
    }

    public static void supplierPurchaseOrderMenu() {
        logo();
        System.out.println("\n    ** Supplier Purchase Order Menu **");
        System.out.println("    Select your option:");
        System.out.println("    1. View Purchase Order Request");
        System.out.println("    2. Update Purchase Order Status");
        System.out.println("    3. Back to Supplier Menu");
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                switch (selection) {
                    case 1:
                        supplier.viewPurchaseOrderRequest();
                        break;
                    case 2:
                        supplier.updatePurchaseOrder();
                        break;
                    case 3:
                        mainMenu();
                        break;
                    default:
                        System.out.println("\n    No such selection! Please select again!");
                        valid = false;
                        break;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please select again!");
                sc.nextLine();
                valid = false;
            }
        } while (!valid);
    }

    public static void supplierMenu() {
        logo();
        System.out.println("\n    ** Supplier Menu **");
        System.out.println("    Select your option:");
        System.out.println("    1. Purchase Order Management");
        System.out.println("    2. View Product Under Your Company");
        System.out.println("    3. Change Password");
        System.out.println("    4. Sign Out");
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                switch (selection) {
                    case 1:
                        supplierPurchaseOrderMenu();
                        break;
                    case 2:
                        supplierViewProduct();
                        break;
                    case 3:
                        Account.changePassword(sessionName);
                        saveAccountData();
                        supplierMenu();
                        break;
                    case 4:
                        mainMenu();
                        break;
                    default:
                        System.out.println("\n    No such selection! Please select again!");
                        valid = false;
                        break;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please select again!");
                sc.nextLine();
                valid = false;
            }
        } while (!valid);
    }

    public static void logo() {
        System.out.println("      _  __                                          ____                         ");
        System.out.println("     | |/ /__ _ _ __ ___  _ __  _   _ _ __   __ _   / ___|_ __ ___   ___ ___ _ __ ");
        System.out.println("     | ' // _` | '_ ` _ \\| '_ \\| | | | '_ \\ / _` | | |  _| '__/ _ \\ / __/ _ | '__|");
        System.out.println("     | . | (_| | | | | | | |_) | |_| | | | | (_| | | |_| | | | (_) | (_|  __| |   ");
        System.out.println("     |_|\\_\\__,_|_| |_| |_| .__/ \\__,_|_| |_|\\__, |  \\____|_|  \\___/ \\___\\___|_|   ");
        System.out.println("                         |_|                |___/                                 ");
        System.out.println("\n    Welcome to Kampung Grocer's Inventory Management System!");
    }

    public static int registerNewAccount() {
        logo();
        System.out.println("\n    ** Register Menu **");
        System.out.println("    Select your option:");
        System.out.println("    1. Register as Staff");
        System.out.println("    2. Register as Supplier");
        System.out.println("    3. Back to Menu\n");
        int selection = 0;
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                selection = sc.nextInt();
                sc.nextLine();
                if (selection == 1) {
                    System.out.println("\n    You are now register as Staff:");
                    staff.register();
                    System.out.println("    You have successfully registered a Staff account as "+ Staff.staffList[Staff.staffCount-1].getUsername() + " ("+ Staff.staffList[Staff.staffCount-1].getStaffID()+")");
                }
                else if (selection == 2) {
                    System.out.println("\n    You are now register as Supplier:");
                    supplier.register();
                    System.out.println("    You have successfully register a Supplier account as "+ Supplier.supplierList[Supplier.supplierCount-1].getUsername() + " ("+ Supplier.supplierList[Supplier.supplierCount-1].getSupplierID()+")");
                }
                else if (selection == 3 )
                    mainMenu();
                else {
                    System.out.println("\n    No such selection! Please select again!");
                    valid = false;
                }
            }
            catch (InputMismatchException e) {
                valid = false;
                System.out.println("\n    Invalid selection! Please select again!");
                sc.nextLine();
            }
        } while(!valid);
        System.out.print("    Press enter to continue!");
        sc.nextLine();
        return selection;
    }

    public static String loginAccount() {
        logo();

        System.out.println("\n    Enter your credentials to login:");
        String nameCheck;
        do {
            nameCheck = Account.login();
            if (nameCheck.equals("noSuchName"))
                System.out.println("\n    No such username in the system! Please enter again!");
        } while (nameCheck.equals("reset") || nameCheck.equals("noSuchName"));
        return nameCheck;
    }

    public static void loadAccountData() {
        staff.loadAccount();
        supplier.loadAccount();
    }

    public static void saveAccountData() {
        staff.saveAccount();
        supplier.saveAccount();
    }

    public static void mainMenu() {
        int selection = 1;
        String loginInfo = "";
        do {
            logo();
            System.out.println("\n    Select your option:");
            System.out.println("    1. Register Account");
            System.out.println("    2. Login Account");
            System.out.println("    3. Exit Program\n");
            int selection2 = 0;
            do {
                System.out.print("    Enter your selection: ");
                try {
                    selection2 = sc.nextInt();
                    sc.nextLine();
                    if (selection2 < 1 || selection2 > 3) {
                        System.out.println("\n    Invalid selection! Please select again!");
                    }
                }
                catch (InputMismatchException e) {
                    System.out.println("\n    Please only enter digits!");
                    sc.nextLine();
                }
            } while (selection2 < 1 || selection2 > 3);
            if (selection2 == 1)
                selection = registerNewAccount();
            else if (selection2 == 2) {
                loginInfo = loginAccount();
                selection = 0;
                String[] splitLoginInfo = loginInfo.split(",");
                loginInfo = splitLoginInfo[1];
                sessionName = splitLoginInfo[0];
                System.out.println("    You logged on a " + loginInfo + " account as " + sessionName + "!");
                System.out.print("    Press enter to continue!");
                sc.nextLine();
            } else
                System.exit(0);
        } while (selection != 0);


        if (loginInfo.equals("staff"))
            staffMenu();
        else if(loginInfo.equals("supplier"))
            supplierMenu();
    }
}