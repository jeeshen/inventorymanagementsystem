import javax.swing.*;
import java.io.*;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Stock {
    private String stockID;
    private int quantity;
    private String description;
    private String pendingReason;
    private boolean defect = false;
    private Product product;
    private Location location;

    public static int stockCount = 0;

    static Scanner sc = new Scanner(System.in);

    public Stock (int quantity, String description, String pendingReason,boolean defect, Product product, Location location) {
        this.quantity = quantity;
        this.description = description;
        this.pendingReason = pendingReason;
        this.defect = defect;
        this.product = product;
        this.location = location;
        generateStockID();
    }

    public Stock () {
        this(0,"","",false,null,null);
    }

    //getter
    public String getStockID() {
        return this.stockID;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPendingReason() {
        return this.pendingReason;
    }

    public boolean getDefect() {
        return this.defect;
    }

    public Product getProduct() {
        return this.product;
    }

    public Location getLocation() {
        return this.location;
    }

    //setter
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public void setPendingReason(String pendingReason) {
        this.pendingReason = pendingReason;
    }

    public void setDefect(boolean defect) {
        this.defect = defect;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    //other methods
    public void generateStockID() {
        String prefix = "T";
        this.stockID = String.format("%s%04d",prefix,stockCount+1);
    }

    public void saveStock() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("stock.txt"))) {
            for (Stock x:Main.stockList) {
                if (x != null && x.getProduct() != null) {
                    writer.write(String.join("|", String.valueOf(x.getQuantity()),x.getDescription(),x.getPendingReason(),String.valueOf(x.getDefect()),String.join("?",x.getProduct().getName(),x.getProduct().getSupplier().getName()),String.join("?",x.getLocation().getWarehouseID(),x.getLocation().getRowID(),String.valueOf(x.getLocation().getRackID()))));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("    Failed to save stock data!");
        }
    }

    public void loadStock() {
        Product tempProduct = new Product();
        Location tempLocation = new Location();
        try (BufferedReader reader = new BufferedReader(new FileReader("stock.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] stockData = line.split("\\|");
                int i = 0;
                int found = 0;
                tempProduct = new Product();
                tempLocation = new Location();
                String[] productData = stockData[4].split("\\?");
                for (Product x:Main.productList) {
                    if (x != null) {
                        if (x.getName().equals(productData[0]) && x.getSupplier().getName().equals(productData[1])) {
                            tempProduct = Main.productList[i];
                            found++;
                        }
                    }
                    i++;
                }

                String[] locationData = stockData[5].split("\\?");
                i = 0;
                for (Location x: Main.locationList) {
                    if (x != null) {
                        if (x.getWarehouseID().equals(locationData[0]) && x.getRowID().equals(locationData[1]) && String.valueOf(x.getRackID()).equals(locationData[2])) {
                            tempLocation = Main.locationList[i];
                            found++;
                        }
                    }
                    i++;
                }
                if (found == 2) {
                    Main.stockList[stockCount] = new Stock (Integer.parseInt(stockData[0]),stockData[1],stockData[2],Boolean.parseBoolean(stockData[3]),tempProduct,tempLocation);
                    stockCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("    Failed to load stock data!");
        }
    }

    public void stockIn() {
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

        System.out.println("\n    ** Adding Stock **");
        System.out.println("    Select product to stock in (Warehouse capacity: 250,000): ");
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
            System.out.println("    2. Back to Stock Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Stock Menu\n");
        }
        boolean valid2;
        do {
            valid2 = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                if (i == 0 || i+1 == selection) {
                    Main.stockMenu();
                }
                else if (selection > 0 && selection <= i) {
                    boolean valid;
                    int stockAmount = 0;
                    do {
                        valid = true;
                        System.out.print("    How much do you wish to stock in (Available space: "+availableSpace+"): ");
                        try {
                            stockAmount = sc.nextInt();
                            sc.nextLine();
                            if (stockAmount <= 0)
                                stockIn();
                            if (availableSpace == 0) {
                                System.out.println("\n    Warehouse is currently full now!");
                                System.out.print("    Press enter to proceed!");
                                sc.nextLine();
                                stockIn();
                            }
                            else if ((availableSpace - stockAmount) < 0) {
                                valid = false;
                                System.out.println("\n    Warehouse cannot handle too much stock! (Space required: "+(stockAmount-availableSpace)+")");
                            }
                        }
                        catch (InputMismatchException e) {
                            System.out.println("\n    You can only enter number!");
                            valid = false;
                            sc.nextLine();
                        }
                    } while (!valid);

                    int totalStockin = 0;
                    int p = 0;
                    int o = 0;
                    String[] stockInLocation = new String[100];
                    int differentLocation = stockAmount / 5000;
                    int leftover = stockAmount % 5000;

                    for (Location x:Main.locationList) {
                        int totalQuantity = 0;
                        if (x != null) {
                            if (o > 50)
                                break;

                            for (Stock y : Main.stockList) {
                                if (y != null && y.getProduct() != null) {
                                    if (x.getLocationID().equals(y.getLocation().getLocationID())) {
                                        totalQuantity += y.getQuantity();
                                    }
                                }
                            }

                            if (totalQuantity > 5000) {
                                continue;
                            }

                            if (stockAmount > 5000) {
                                if (differentLocation == 0 && leftover == 0) {
                                    stockAmount = 0;
                                    break;
                                }

                                if (differentLocation > 0 && totalQuantity == 0) {
                                    Main.stockList[stockCount] = new Stock(5000, "stockin", "",false, Main.productList[availableOption[selection - 1]], x);
                                    stockCount++;
                                    totalStockin += 5000;
                                    stockInLocation[p] = x.getLocationID();
                                    p++;
                                    differentLocation--;
                                    continue;
                                }

                                if (((5000 - totalQuantity) >= leftover) && leftover > 0) {
                                    Main.stockList[stockCount] = new Stock(leftover, "stockin","", false, Main.productList[availableOption[selection - 1]], x);
                                    totalStockin += leftover;
                                    leftover = 0;
                                    stockCount++;
                                    stockInLocation[p] = x.getLocationID();
                                    p++;
                                }
                            }
                            else {
                                if (((5000 - totalQuantity) >= stockAmount) && stockAmount > 0) {
                                    Main.stockList[stockCount] = new Stock(stockAmount, "stockin","", false, Main.productList[availableOption[selection - 1]], x);
                                    stockCount++;
                                    totalStockin += stockAmount;
                                    stockInLocation[p] = x.getLocationID();
                                    stockAmount = 0;
                                    p++;
                                    break;
                                }
                            }
                        }
                        o++;
                    }
                    if (stockAmount > 0) {
                        System.out.println("\n    The warehouse is currently full (" + (((differentLocation) * 5000) + leftover) + " stock left)!");
                        System.out.print("      Press enter to proceed!");
                        sc.nextLine();
                        stockIn();
                    }
                    else {
                        StringBuilder sb = new StringBuilder();
                        int y = 0;
                        for (String s : stockInLocation) {
                            if (s != null) {
                                sb.append(s);
                                sb.append(",");
                            }
                            y++;
                        }

                        String allLocation = sb.toString();
                        allLocation = allLocation.substring(0,allLocation.length()-1);

                        StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(totalStockin,"Person","Warehouse","Adding stock in",new Date(),Main.sessionName,Main.productList[availableOption[selection-1]]);
                        StockRecord.recordCount++;
                        System.out.println("\n    You have successfully added "+totalStockin+" x "+Main.productList[availableOption[selection-1]].getName()+" to "+allLocation+"!");
                        System.out.print("    Press enter to proceed!");
                        sc.nextLine();
                        saveStock();
                        Main.stockRecord.saveStockRecord();
                    }
                    stockIn();
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

    public void stockOut() {
        System.out.println("\n    ** Removing Stock **");
        System.out.println("    Select product to stock out: ");
        int i = 0;
        int[] availableOption = new int[Main.productList.length];
        int j = 0;
        for (Product x:Main.productList) {
            if (x != null && x.getStatus().equals("available")) {
                int totalStock = 0;
                for (Stock y:Main.stockList) {
                    if (y != null && y.getProduct() != null) {
                        if (y.getProduct().getName().equals(x.getName()) && y.getDescription().equals("stockin")) {
                            totalStock += y.getQuantity();
                        }
                    }
                }
                System.out.println("    "+(i+1)+". "+x.getName() + " (Stock: "+totalStock+")");
                availableOption[i] = j;
                i++;
            }
            j++;
        }
        if (i == 1) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Stock Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Stock Menu\n");
        }
        boolean valid2;
        do {
            valid2 = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                if (i == 0 || i+1 == selection) {
                    Main.stockMenu();
                }
                else if (selection > 0 && selection <= i) {
                    boolean valid;
                    int stockAmount = 0;
                    do {
                        valid = true;
                        System.out.print("    How much do you wish to stock out: ");
                        try {
                            stockAmount = sc.nextInt();
                            sc.nextLine();
                            int enoughStock = 0;
                            for (Stock y:Main.stockList) {
                                if (y != null && y.getProduct() != null) {
                                    if (Main.productList[availableOption[selection-1]].getName().equals(y.getProduct().getName()) && y.getDescription().equals("stockin")) {
                                        enoughStock += y.getQuantity();
                                    }
                                }
                            }

                            if (stockAmount > enoughStock) {
                                System.out.println("\n    Not enough stock! Please enter again!");
                                valid = false;
                            }
                            if (stockAmount <= 0) {
                                stockOut();
                            }
                        }
                        catch (InputMismatchException e) {
                            System.out.println("\n    You can only enter number!");
                            valid = false;
                            sc.nextLine();
                        }
                    } while (!valid);

                    int stockOut = stockAmount;
                    for (Stock y:Main.stockList) {
                        if (y != null && y.getProduct() != null) {
                            if (Main.productList[availableOption[selection-1]].getName().equals(y.getProduct().getName()) && y.getDescription().equals("stockin")) {
                                if (stockAmount > 0) {
                                    if ((stockAmount - y.getQuantity()) > 0) {
                                        stockAmount -= y.getQuantity();
                                        y.setQuantity(0);
                                    }
                                    else if ((stockAmount - y.getQuantity()) < 0) {
                                        y.setQuantity(y.getQuantity()-stockAmount);
                                        stockAmount = 0;
                                    }
                                    else if ((stockAmount - y.getQuantity()) == 0) {
                                        stockAmount = 0;
                                        y.setQuantity(0);
                                    }
                                }
                            }
                        }
                    }

                    StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(stockOut,"Warehouse","Person","Removing stock",new Date(),Main.sessionName,Main.productList[availableOption[selection-1]]);
                    StockRecord.recordCount++;
                    System.out.println("\n    You have successfully removed "+stockOut+" x "+Main.productList[availableOption[selection-1]].getName()+" from warehouse!");
                    System.out.print("    Press enter to proceed!");
                    sc.nextLine();
                    Main.stockRecord.saveStockRecord();
                    saveStock();
                    stockOut();
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

    public void stockReturn() {
        System.out.println("\n    ** Returning Stock **");
        System.out.println("    Select product to return: ");
        int i = 0;
        int[] availableOption = new int[Main.productList.length];
        int j = 0;
        for (Product x:Main.productList) {
            if (x != null && x.getStatus().equals("available")) {
                int totalStock = 0;
                for (Stock y:Main.stockList) {
                    if (y != null && y.getProduct() != null) {
                        if (y.getProduct().getName().equals(x.getName()) && y.getDescription().equals("stockin")) {
                            totalStock += y.getQuantity();
                        }
                    }
                }
                System.out.println("    "+(i+1)+". "+x.getName() + " (Stock: "+totalStock+")");
                availableOption[i] = j;
                i++;
            }
            j++;
        }
        if (i == 1) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Stock Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Stock Menu\n");
        }
        boolean valid2;
        do {
            valid2 = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                if (i == 0 || i+1 == selection) {
                    Main.stockMenu();
                }
                else if (selection > 0 && selection <= i) {
                    boolean valid;
                    int stockAmount = 0;
                    do {
                        valid = true;
                        System.out.print("    How much do you wish to return: ");
                        try {
                            stockAmount = sc.nextInt();
                            sc.nextLine();
                        }
                        catch (InputMismatchException e) {
                            System.out.println("\n    You can only enter number!");
                            valid = false;
                            sc.nextLine();
                        }
                        boolean valid3;
                        String confirmation;
                        if (stockAmount <= 0) {
                            valid3 = false;
                            System.out.println("\n    You must enter a value!");
                        }
                        do {
                            valid3 = true;
                            System.out.print("    Do you want to return them because of defect item (Y/N)? ");
                            confirmation = sc.nextLine();
                            confirmation = confirmation.toUpperCase();
                            if (confirmation.equals("N")) {
                                confirmation = "false";
                            }
                            else if (confirmation.equals("Y")) {
                                confirmation = "true";
                            }
                            else {
                                valid3 = false;
                                System.out.println("\n    No such selection! Please enter again!");
                            }
                        } while(!valid3);
                        int p = 0;
                        int stockReturn = stockAmount;
                        for (Stock x: Main.stockList) {
                            if (x != null && x.getProduct().getName().equals(Main.productList[selection-1].getName()) && x.getDescription().equals("stockin")) {
                                if (stockReturn > 0) {
                                    if ((x.getQuantity() - stockReturn) > 0) {
                                        x.setQuantity(x.getQuantity()-stockReturn);
                                        stockReturn = 0;
                                    }
                                    else if ((x.getQuantity() - stockReturn) < 0) {
                                        stockReturn -= x.getQuantity();
                                        x.setQuantity(0);
                                    }
                                    else {
                                        x.setQuantity(0);
                                        stockReturn = 0;
                                    }
                                }
                            }
                            p++;
                        }
                        if (stockReturn == 0) {
                            Main.stockList[stockCount] = new Stock(stockAmount,"return","",Boolean.parseBoolean(confirmation),Main.productList[selection-1],Main.locationList[52]);
                            stockCount++;
                            StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(stockAmount,"Warehouse","Warehouse Return Zone","Returning stock",new Date(),Main.sessionName,Main.productList[selection-1]);
                            StockRecord.recordCount++;
                            System.out.println("\n    You have successfully transferred "+stockAmount+" x "+Main.productList[selection-1].getName()+" to return zone!");
                            System.out.print("    Press enter to proceed!");
                            sc.nextLine();
                            saveStock();
                            Main.stockRecord.saveStockRecord();
                            stockReturn();
                        }
                        else {
                            Main.stockList[stockCount] = new Stock((stockAmount-stockReturn),"return","",Boolean.parseBoolean(confirmation),Main.productList[selection-1],Main.locationList[52]);
                            stockCount++;
                            StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord((stockAmount-stockReturn),"Warehouse","Warehouse Return Zone","Returning stock",new Date(),Main.sessionName,Main.productList[selection-1]);
                            StockRecord.recordCount++;
                            System.out.println("\n    You do not have enough stock to return! Please enter again! (Left to return: "+stockReturn+")");
                            System.out.print("    Press enter to proceed!");
                            sc.nextLine();
                            saveStock();
                            Main.stockRecord.saveStockRecord();
                            stockReturn();
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

    public void stockRecord() {
        System.out.println("\n    ** Stock Record Menu **");
        System.out.println("    Select your option:");
        System.out.println("    1. View All");
        System.out.println("    2. View Stock In");
        System.out.println("    3. View Stock Out");
        System.out.println("    4. Back to Stock Menu\n");
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                int i = 0;
                switch (selection) {
                    case 1:
                        System.out.println("\n    ** Displaying All Stock Record **");
                        for(StockRecord x:StockRecord.stockRecords) {
                            if (x != null) {
                                System.out.println("    "+x.getStockRecordID()+" - "+x.getProduct().getName()+" -- "+x.getOriginLocation()+" (-"+x.getQuantity()+") -----> "+x.getDestination()+" (+"+x.getQuantity()+") by "+x.getStaffHandled()+" on "+x.getDateTransfer());
                                i++;
                            }
                        }
                        if(i == 0)
                            System.out.println("    No Data Found!");
                        System.out.print("\n    Press enter to proceed!");
                        sc.nextLine();
                        stockRecord();
                        break;
                    case 2:
                        System.out.println("\n    ** Displaying Stock In Record **");
                        for(StockRecord x:StockRecord.stockRecords) {
                            if (x != null && x.getReason().equals("Adding stock in")) {
                                System.out.println("    "+x.getStockRecordID()+" - "+x.getProduct().getName()+" -- "+x.getOriginLocation()+" (-"+x.getQuantity()+") -----> "+x.getDestination()+" (+"+x.getQuantity()+") by "+x.getStaffHandled()+" on "+x.getDateTransfer());
                                i++;
                            }
                        }
                        if(i == 0)
                            System.out.println("    No Data Found!");
                        System.out.print("\n    Press enter to proceed!");
                        sc.nextLine();
                        stockRecord();
                        break;
                    case 3:
                        System.out.println("\n    ** Displaying Stock Out Record **");
                        for(StockRecord x:StockRecord.stockRecords) {
                            if (x != null && (x.getReason().equals("Removing stock") || x.getReason().equals("Returning stock"))) {
                                System.out.println("    "+x.getStockRecordID()+" - "+x.getProduct().getName()+" -- "+x.getOriginLocation()+" (-"+x.getQuantity()+") -----> "+x.getDestination()+" (+"+x.getQuantity()+") by "+x.getStaffHandled()+" on "+x.getDateTransfer());
                                i++;
                            }
                        }
                        if(i == 0)
                            System.out.println("    No Data Found!");
                        System.out.print("\n    Press enter to proceed!");
                        sc.nextLine();
                        stockRecord();
                        break;
                    case 4:
                        Main.stockMenu();
                        break;
                    default:
                        System.out.println("\n    No such selection! Please select again!");
                        valid = false;
                }
            }
            catch (InputMismatchException e){
                System.out.println("\n    You can only enter digit!");
                valid = false;
                sc.nextLine();
            }
        } while (!valid);
    }

    public void stockLoading() {
        System.out.println("\n    ** Stock Loading **");
        System.out.println("    Select stock to load into warehouse:");
        int i = 0;
        int[] availableOption = new int[Main.stockList.length];
        int j = 0;
        for (Stock x:Main.stockList) {
            if (x != null && x.getDescription().equals("inbound") && x.getQuantity() > 0) {
                System.out.println("    "+(i+1)+". "+x.getQuantity()+" x "+x.getProduct().getName());
                availableOption[i] = j;
                i++;
            }
            j++;
        }
        if (i== 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Stock Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Stock Menu\n");
        }
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                if (i == 0 || selection == i+1) {
                    Main.stockMenu();
                }
                else if (selection > 0 && selection <= i) {
                    boolean valid2;
                    System.out.println("\n    ** Stock Checking **");
                    System.out.println("    Product Name: "+Main.stockList[availableOption[selection-1]].getProduct().getName()+"\n");
                    do {
                        valid2 = true;
                        System.out.print("    Is the product same as following (Y/N)? ");
                        String confirmation = sc.nextLine();
                        confirmation = confirmation.toUpperCase();
                        if (confirmation.equals("N")) {
                            Main.stockList[availableOption[selection-1]].setDescription("return");
                            Main.stockList[availableOption[selection-1]].setLocation(Main.locationList[52]);
                            StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(Main.stockList[availableOption[selection-1]].getQuantity(),"Warehouse Inbound","Warehouse Return Zone","Returning wrong product",new Date(),Main.sessionName,Main.stockList[availableOption[selection-1]].getProduct());
                            StockRecord.recordCount++;

                            System.out.println("    Stock will be arranged to return back to supplier! Press enter to proceed!");
                            sc.nextLine();
                            saveStock();
                            Main.stockRecord.saveStockRecord();
                            stockLoading();
                        }
                        else if (confirmation.equals("Y"))
                            break;
                        else {
                            System.out.println("\n    No such selection! Please select again!");
                            valid2 = false;
                        }
                    } while (!valid2);
                    System.out.println("\n    ** Stock Checking **");
                    System.out.println("    Quantity: "+Main.stockList[availableOption[selection-1]].getQuantity()+"\n");
                    do {
                        valid2 = true;
                        System.out.print("    Is the quantity same as following (Y/N)? ");
                        String confirmation = sc.nextLine();
                        confirmation = confirmation.toUpperCase();
                        if (confirmation.equals("N")) {
                            Main.stockList[availableOption[selection-1]].setDescription("return");
                            Main.stockList[availableOption[selection-1]].setLocation(Main.locationList[52]);
                            StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(Main.stockList[availableOption[selection-1]].getQuantity(),"Warehouse Inbound","Warehouse Return Zone","Returning wrong quantity",new Date(),Main.sessionName,Main.stockList[availableOption[selection-1]].getProduct());
                            StockRecord.recordCount++;

                            System.out.println("    Stock will be arranged to return back to supplier! Press enter to proceed!");
                            sc.nextLine();
                            saveStock();
                            Main.stockRecord.saveStockRecord();
                            stockLoading();
                        }
                        else if (confirmation.equals("Y"))
                            break;
                        else {
                            System.out.println("\n    No such selection! Please select again!");
                            valid2 = false;
                        }
                    } while (!valid2);
                    System.out.println("\n    ** Stock Checking **");
                    System.out.println("    Quality: No defect, still in expired date\n");
                    do {
                        valid2 = true;
                        System.out.print("    Is the quality same as following (Y/N)? ");
                        String confirmation = sc.nextLine();
                        confirmation = confirmation.toUpperCase();
                        if (confirmation.equals("N")) {
                            boolean valid3;
                            do {
                                valid3 = true;
                                System.out.print("    What how many of them are defected?(Total Stock: "+Main.stockList[availableOption[selection-1]].getQuantity()+") ");
                                try {
                                    int defectQuantity = sc.nextInt();
                                    sc.nextLine();
                                    if (defectQuantity <= Main.stockList[availableOption[selection-1]].getQuantity()) {
                                        Main.stockList[availableOption[selection-1]].setQuantity(Main.stockList[availableOption[selection-1]].getQuantity()-defectQuantity);
                                        StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(defectQuantity,"Warehouse Inbound","Warehouse Return Zone","Returning bad quality",new Date(),Main.sessionName,Main.stockList[availableOption[selection-1]].getProduct());
                                        StockRecord.recordCount++;
                                        Main.stockList[stockCount] = new Stock(defectQuantity,"return","",true,Main.stockList[availableOption[selection-1]].getProduct(),Main.locationList[52]);
                                        stockCount++;
                                        System.out.println("    Defected product will be arranged to return back to supplier!");
                                        saveStock();
                                        Main.stockRecord.saveStockRecord();
                                    }
                                    else {
                                        System.out.println("\n    You have have more defected item than the stock amount!");
                                        valid3 = false;
                                    }
                                } catch(InputMismatchException e) {
                                    System.out.println("\n    Please only enter digits!");
                                    sc.nextLine();
                                    valid3 = false;
                                }
                            } while(!valid3);
                        }
                        else if (confirmation.equals("Y"))
                            break;
                        else {
                            System.out.println("\n    No such selection! Please select again!");
                            valid2 = false;
                        }
                    } while (!valid2);

                    if (Main.stockList[availableOption[selection-1]].getQuantity() == 0) {
                        System.out.println("    Press enter to continue!");
                        Main.stockMenu();
                    }

                    int stockAmount = Main.stockList[availableOption[selection-1]].getQuantity();
                    int totalStockin = 0;
                    int p = 0;
                    int o = 0;
                    String[] stockInLocation = new String[100];
                    int differentLocation = stockAmount / 5000;
                    int leftover = stockAmount % 5000;

                    for (Location x:Main.locationList) {
                        int totalQuantity = 0;
                        if (x != null) {
                            if (o > 50)
                                break;

                            for (Stock y : Main.stockList) {
                                if (y != null && y.getProduct() != null) {
                                    if (x.getLocationID().equals(y.getLocation().getLocationID())) {
                                        totalQuantity += y.getQuantity();
                                    }
                                }
                            }

                            if (totalQuantity > 5000) {
                                continue;
                            }

                            if (stockAmount > 5000) {
                                if (differentLocation == 0 && leftover == 0) {
                                    stockAmount = 0;
                                    break;
                                }

                                if (differentLocation > 0 && totalQuantity == 0) {
                                    Main.stockList[stockCount] = new Stock(5000, "stockin", "",false, Main.stockList[availableOption[selection-1]].getProduct(), x);
                                    stockCount++;
                                    totalStockin += 5000;
                                    stockInLocation[p] = x.getLocationID();
                                    p++;
                                    differentLocation--;
                                    continue;
                                }

                                if (((5000 - totalQuantity) >= leftover) && leftover > 0) {
                                    Main.stockList[stockCount] = new Stock(leftover, "stockin","", false, Main.stockList[availableOption[selection-1]].getProduct(), x);
                                    totalStockin += leftover;
                                    leftover = 0;
                                    stockCount++;
                                    stockInLocation[p] = x.getLocationID();
                                    p++;
                                }
                            }
                            else {
                                if (((5000 - totalQuantity) >= stockAmount) && stockAmount > 0) {
                                    Main.stockList[stockCount] = new Stock(stockAmount, "stockin","", false, Main.stockList[availableOption[selection-1]].getProduct(), x);
                                    stockCount++;
                                    totalStockin += stockAmount;
                                    stockInLocation[p] = x.getLocationID();
                                    stockAmount = 0;
                                    p++;
                                    break;
                                }
                            }
                        }
                        o++;
                    }
                    if (stockAmount > 0) {
                        Main.stockList[availableOption[selection-1]].setQuantity(((differentLocation) * 5000) + leftover);
                        System.out.println("\n    The warehouse is currently full (" + (((differentLocation) * 5000) + leftover) + " stock left)!");
                        System.out.print("      Press enter to proceed!");
                        sc.nextLine();
                        saveStock();
                        stockLoading();
                    }
                    else {
                        StringBuilder sb = new StringBuilder();
                        int y = 0;
                        for (String s : stockInLocation) {
                            if (s != null) {
                                sb.append(s);
                                sb.append(",");
                            }
                            y++;
                        }

                        String allLocation = sb.toString();
                        allLocation = allLocation.substring(0,allLocation.length()-1);

                        Main.stockList[availableOption[selection-1]].setQuantity(0);
                        StockRecord.stockRecords[StockRecord.recordCount] = new StockRecord(totalStockin,"Warehouse Inbound","Warehouse Rack","Purchase order items",new Date(),Main.sessionName,Main.stockList[availableOption[selection-1]].getProduct());
                        StockRecord.recordCount++;
                        System.out.println("\n    You have successfully load "+totalStockin+" x "+Main.stockList[availableOption[selection-1]].getProduct().getName()+" to "+allLocation+"!");
                        System.out.print("    Press enter to proceed!");
                        sc.nextLine();
                        saveStock();
                        Main.stockRecord.saveStockRecord();
                        stockLoading();
                    }
                }
                else {
                    valid = false;
                    System.out.println("\n    No such selection! Please enter again!");
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please enter again!");
                sc.nextLine();
                valid = false;
            }
        } while (!valid);
    } //haven bug test

    public void viewStock() {
        System.out.println("\n    ** View Stock (Including Inbound,Outbound & Returns) **");
        System.out.println("    Select product to view: ");
        int i = 1;
        for (Product x:Main.productList) {
            if (x != null) {
                int totalStock = 0;
                for (Stock y:Main.stockList) {
                    if (y != null && y.getProduct() != null) {
                        if (y.getProduct().getName().equals(x.getName())) {
                            totalStock += y.getQuantity();
                        }
                    }
                }
                System.out.println("    "+i+". "+x.getName() + " (Stock: "+totalStock+")");
                i++;
            }
        }
        if (i == 1) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Stock Menu\n");
        }
        else {
            System.out.println("    "+i+". Back to Stock Menu\n");
        }
        boolean valid2;
        do {
            valid2 = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = sc.nextInt();
                sc.nextLine();
                if (i == 1 || i == selection) {
                    Main.stockMenu();
                }
                else if (selection > 0 && selection < i) {
                    JFrame frame = new JFrame("Stock Map");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setSize(781, 607);

                    frame.setAlwaysOnTop(true);
                    frame.toFront();
                    frame.requestFocus();

                    StockLocation imagePanel = new StockLocation("layout.jpg",selection);
                    frame.add(imagePanel);
                    frame.setVisible(true);
                    System.out.print("\n    Press enter once you have finish viewing! (DO NOT CLOSE THE PANEL BEFORE PRESSING ENTER!)");
                    sc.nextLine();
                    viewStock();
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
        return "Stock{"+
                "Stock ID: "+this.stockID+
                ",Stock Quantity: "+this.quantity+
                ",Stock Description: "+this.description+
                ",Pending Reason: "+this.pendingReason+
                ",Defect: "+this.defect+
                ","+this.product.toString()+
                "."+this.location+toString()+
                "}";
    }
}
