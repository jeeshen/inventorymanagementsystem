import java.io.*;
import java.util.InputMismatchException;

public class Product {
    private String productID;
    private String name;
    private double unitPrice;
    private Supplier supplier;
    private Category category;
    private String status;

    public static int productCount = 0;

    public Product (String name, double unitPrice, Supplier supplier, Category category, String status) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.supplier = supplier;
        this.category = category;
        this.status = status;
        generateProductID();
    }

    public Product () {
        this("",0,null,null,"");
    }

    //getter
    public String getProductID() {
        return this.productID;
    }

    public String getName() {
        return this.name;
    }

    public double getUnitPrice() {
        return this.unitPrice;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public Category getCategory() {
        return this.category;
    }

    public String getStatus() {
        return this.status;
    }

    //setter
    public void setName(String name) {
        this.name = name;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setSupplier (Supplier supplier) {
        this.supplier = supplier;
    }

    public void setCategory (Category category) {
        this.category = category;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    //other methods
    public void generateProductID() {
        String prefix = "P";
        this.productID = String.format("%s%04d",prefix,productCount+1);
    }

    public void loadProduct() {
        Supplier tempSupplier = new Supplier();
        Category tempCategory = new Category();
        try (BufferedReader reader = new BufferedReader(new FileReader("product.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] productData = line.split("\\|");
                int i = 0;
                int found = 0;
                tempSupplier = new Supplier();
                tempCategory = new Category();
                for (Supplier x: Supplier.supplierList) {
                    if (x != null) {
                        if (x.getName().equals(productData[2])) {
                            tempSupplier = Supplier.supplierList[i];
                            found++;
                        }
                    }
                    i++;
                }
                i = 0;
                for (Category x: Main.categoryList) {
                    if (x != null) {
                        if (x.getName().equals(productData[3])) {
                            tempCategory = Main.categoryList[i];
                            found++;
                        }
                    }
                    i++;
                }
                if (found == 2) {
                    Main.productList[productCount] = new Product (productData[0],Double.parseDouble(productData[1]),tempSupplier,tempCategory,productData[4]);
                    productCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("    Failed to load product data!");
        }
    }

    public void saveProduct() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("product.txt"))) {
            for (Product x:Main.productList) {
                if (x != null && !x.getName().isEmpty()) {
                    writer.write(String.join("|", x.getName(),String.valueOf(x.getUnitPrice()),x.getSupplier().getName(),x.getCategory().getName(),x.getStatus()));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("    Failed to save product data!");
        }
    }

    public void addProduct() {
        System.out.println("\n    ** Adding New Product **");
        System.out.print("    Enter new product name: ");
        String productName = Main.sc.nextLine();
        double unitPrice = 0;
        boolean valid;
        do {
            valid = true;
            try {
                System.out.print("    Enter unit price of product: ");
                unitPrice = Main.sc.nextDouble();
                Main.sc.nextLine();

                if (unitPrice == 0) {
                    valid = false;
                    System.out.println("\n    You cannot enter zero value as unit price!");
                }
            }
            catch (InputMismatchException e){
                valid = false;
                System.out.println("\n    Enter only digits in here!");
                Main.sc.next();
            }
        } while (!valid);

        Supplier productSupplier = new Supplier();
        Category productCategory = new Category();
        System.out.println("\n    Select product supplier: ");
        int i = 1;
        for (Supplier x: Supplier.supplierList) {
            if(x != null && !x.getName().isEmpty()) {
                System.out.println("    "+i+". "+x.getName());
                i++;
            }
        }
        if (i == 1) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Product Menu\n");
        }
        else
            System.out.println("    "+i+". Back to Product Menu\n");
        do {
            valid = true;
            try {
                System.out.print("    Enter your selection: ");
                int selection = Main.sc.nextInt();
                Main.sc.nextLine();
                if (i == 1) {
                    if (selection == 1 || selection == 2) {
                        System.out.println("    Add new supplier to proceed! Press enter to continue!");
                        Main.sc.nextLine();
                    }
                    else {
                        valid = false;
                        System.out.println("\n    Invalid selection! Please select again!");
                    }
                }
                else {
                    if (selection == i) {
                        Main.productMenu();
                    }
                    else if (selection > 0 && selection < i) {
                        productSupplier = Supplier.supplierList[selection-1];
                    }
                    else {
                        valid = false;
                        System.out.println("\n    Invalid selection! Please select again!");
                    }
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    Invalid selection! Please select again!");
                Main.sc.nextLine();
                valid = false;
            }
        } while (!valid);
        System.out.println("    You have selected "+productSupplier.getName()+" as product supplier of "+productName+"!");
        System.out.println("\n    Select product category: ");
        i = 0;
        int[] availableOption = new int[Main.categoryList.length];
        int j = 0;
        for (Category x:Main.categoryList) {
            if(x != null && !x.getName().isEmpty() && x.getStatus().equals("available")) {
                System.out.println("    "+(i+1)+". "+x.getName());
                availableOption[i] = j;
                i++;
            }
            j++;
        }
        if (i == 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Product Menu\n");
        }
        else
            System.out.println("    "+(i+1)+". Back to Product Menu\n");
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = Main.sc.nextInt();
                Main.sc.nextLine();
                if (i == 1) {
                    if (selection == 1 || selection == 2) {
                        Main.productMenu();
                    }
                    else {
                        valid = false;
                        System.out.println("\n    Invalid selection! Please select again!");
                    }
                }
                else {
                    if (selection == i+1) {
                        Main.productMenu();
                    }
                    else if (selection > 0 && selection <= i) {
                        productCategory = Main.categoryList[availableOption[selection-1]];
                    }
                    else {
                        valid = false;
                        System.out.println("\n    Invalid selection! Please select again!");
                    }
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please select again!");
                valid = false;
                Main.sc.nextLine();
            }
        } while (!valid);
        System.out.println("    You have selected "+productCategory.getName()+" as product category!");
        Main.productList[productCount] = new Product(productName,unitPrice,productSupplier,productCategory,"available");

        System.out.println("\n    You have successfully added new product!");
        System.out.println("    Product ID: "+Main.productList[productCount].getProductID());
        System.out.println("    Product Name: "+Main.productList[productCount].getName());
        System.out.println("    Unit Price: RM"+Main.productList[productCount].getUnitPrice());
        System.out.println("    Product Supplier: "+Main.productList[productCount].getSupplier().getName());
        System.out.println("    Product Category: "+Main.productList[productCount].getCategory().getName());
        productCount++;
        System.out.print("    Press enter to continue!");
        Main.sc.nextLine();
        saveProduct();
        Main.productMenu();
    }

    public void editProduct() {
        System.out.println("\n    ** Editing Existing Product **");
        System.out.println("    Select product to edit: ");
        int i = 0;
        int[] availableOption = new int[Main.categoryList.length];
        int j = 0;
        for (Product x:Main.productList) {
            if(x != null && x.getCategory() != null && x.getStatus().equals("available")) {
                System.out.println("    "+(i+1)+". "+x.getName());
                availableOption[i] = j;
                i++;
            }
            j++;
        }
        if (i == 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Product Menu\n");
        }
        System.out.println("    "+(i+1)+". Back to Product Menu\n");
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = Main.sc.nextInt();
                Main.sc.nextLine();
                if (i == 0) {
                    switch (selection) {
                        case 1:
                        case 2:
                            Main.productMenu();
                            break;
                        default:
                            valid = false;
                            System.out.println("\n    Invalid selection! Please select again!");
                            break;
                    }
                }
                else if (selection == i+1) {
                    Main.productMenu();
                }
                else {
                    if (selection > 0 && selection <= i) {
                        int selection2 = 0;
                        do {
                            System.out.println("\n    Select information to edit:");
                            System.out.println("    1. Product Name - "+Main.productList[availableOption[selection-1]].getName());
                            System.out.println("    2. Product Unit Price - RM"+Main.productList[availableOption[selection-1]].getUnitPrice());
                            System.out.println("    3. Product Supplier - "+Main.productList[availableOption[selection-1]].getSupplier().getName());
                            System.out.println("    4. Product Category - "+Main.productList[availableOption[selection-1]].getCategory().getName());
                            System.out.println("    5. Back to Product Menu\n");
                            do {
                                System.out.print("    Enter your selection: ");
                                try {
                                    selection2 = Main.sc.nextInt();
                                    Main.sc.nextLine();
                                    boolean valid2;
                                    switch (selection2) {
                                        case 1:
                                            System.out.print("    Enter new product name (Current: "+ Main.productList[availableOption[selection-1]].getName() +"): ");
                                            Main.productList[availableOption[selection-1]].setName(Main.sc.nextLine());
                                            Main.product.saveProduct();
                                            Main.stock.saveStock();
                                            break;
                                        case 2:
                                            double unitPrice = 0;
                                            do {
                                                valid2 = true;
                                                try {
                                                    System.out.print("    Enter new product unit price (Current: RM"+Main.productList[availableOption[selection-1]].getUnitPrice()+"): ");
                                                    unitPrice = Main.sc.nextDouble();
                                                    Main.sc.nextLine();

                                                    if (unitPrice == 0) {
                                                        valid2 = false;
                                                        System.out.println("\n    You cannot enter zero value as unit price!");
                                                    }
                                                }
                                                catch (InputMismatchException e){
                                                    valid2 = false;
                                                    System.out.println("\n    Enter only digits in here!");
                                                    Main.sc.next();
                                                }
                                            } while (!valid2);
                                            Main.productList[availableOption[selection-1]].setUnitPrice(unitPrice);
                                            saveProduct();
                                            break;
                                        case 3:
                                            j = 1;
                                            System.out.println("\n    Select new product supplier: ");
                                            for (Supplier x : Supplier.supplierList) {
                                                if (x != null && Main.productList[availableOption[selection - 1]].getSupplier().getName().equals(x.getName())) {
                                                    System.out.println("    "+j + ". " + x.getName() + " (Current)");
                                                    j++;
                                                } else if (x != null) {
                                                    System.out.println("    "+j + ". " + x.getName());
                                                    j++;
                                                }
                                            }
                                            if (j == 1) {
                                                System.out.println("    1. No Data Found");
                                                System.out.println("    2. Back to Product Menu\n");
                                            }
                                            else
                                                System.out.println("    " + j + ". Back to Product Menu\n");
                                            do {
                                                valid2 = true;
                                                System.out.print("    Enter your selection: ");
                                                try {
                                                    int selection3 = Main.sc.nextInt();
                                                    Main.sc.nextLine();

                                                    if (selection3 == j) {
                                                        if (selection3 == 1 || selection3 == 2)
                                                            Main.productMenu();
                                                    }
                                                    else if (selection3 > 0 && selection3 < j) {
                                                        Main.productList[availableOption[selection - 1]].setSupplier(Supplier.supplierList[selection3 - 1]);
                                                    }
                                                    else {
                                                        System.out.println("\n    No such selection! Please select again!");
                                                        valid2 = false;
                                                    }
                                                }
                                                catch (InputMismatchException e) {
                                                    System.out.println("\n    No such selection! Please select again!");
                                                    Main.sc.nextLine();
                                                    valid2 = false;
                                                }
                                            } while (!valid2);
                                            Main.product.saveProduct();
                                            Main.stock.saveStock();
                                            break;
                                        case 4:
                                            j = 0;
                                            int[] availableOption2 = new int[Main.categoryList.length];
                                            int p = 0;
                                            System.out.println("\n    Select new product category: ");
                                            for (Category x : Main.categoryList) {
                                                if (x != null && Main.productList[availableOption[selection - 1]].getCategory().getName().equals(x.getName())) {
                                                    System.out.println("    "+(j+1) + ". " + x.getName() + " (Current)");
                                                    availableOption2[j] = p;
                                                    j++;
                                                } else if (x != null && x.getStatus().equals("available")) {
                                                    System.out.println("    "+(j+1) + ". " + x.getName());
                                                    availableOption2[j] = p;
                                                    j++;
                                                }
                                                p++;
                                            }
                                            if (j == 0) {
                                                System.out.println("    1. No Data Found");
                                                System.out.println("    2. Back to Product Menu\n");
                                            }
                                            else
                                                System.out.println("    " + (j+1) + ". Back to Product Menu\n");
                                            do {
                                                valid2 = true;
                                                System.out.print("    Enter your selection: ");
                                                try {
                                                    int selection3 = Main.sc.nextInt();
                                                    Main.sc.nextLine();

                                                    if (selection3 == j+1) {
                                                        Main.productMenu();
                                                    }
                                                    else if (selection3 > 0 && selection3 <= j) {
                                                        Main.productList[availableOption[selection - 1]].setCategory(Main.categoryList[availableOption2[selection3 - 1]]);
                                                    } else {
                                                        System.out.println("\n    No such selection! Please select again!");
                                                        valid2 = false;
                                                    }
                                                }
                                                catch (InputMismatchException e) {
                                                    System.out.println("\n    No such selection! Please enter again!");
                                                    valid2 = false;
                                                    Main.sc.nextLine();
                                                }
                                            } while (!valid2);
                                            saveProduct();
                                            Main.stock.saveStock();
                                            break;
                                        case 5:
                                            Main.productMenu();
                                            break;
                                        default:
                                            System.out.println("\n    No such selection! Please select again!");
                                    }
                                }
                                catch (InputMismatchException e) {
                                    System.out.println("\n    No such selection! Please enter again!");
                                    valid = false;
                                    Main.sc.nextLine();
                                }
                            } while (selection2 < 1 || selection2 > 5);
                        } while(selection2 != 5);
                    }
                    else {
                        valid = false;
                        System.out.println("\n    Invalid selection! Please select again!");
                    }
                }
            }
            catch (InputMismatchException e){
                valid = false;
                System.out.println("\n    Invalid selection! Please select again!");
                Main.sc.next();
            }
        } while(!valid);
    }

    public void deleteProduct() {
        System.out.println("\n    ** Deleting Existing Product **");
        System.out.println("    Select product to delete: ");
        int i = 0;
        int[] availableOption = new int[Main.categoryList.length];
        int j = 0;
        for (Product x:Main.productList) {
            if (x != null && x.getCategory() != null && x.getStatus().equals("available")) {
                System.out.println("    "+(i+1)+". "+x.getName());
                availableOption[i] = j;
                i++;
            }
            j++;
        }
        if (i == 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Product Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Product Menu\n");
        }
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = Main.sc.nextInt();
                Main.sc.nextLine();
                if (i == 0 || selection == i+1) {
                    Main.productMenu();
                }
                else if (selection > 0 && selection <= i) {
                    System.out.println("\n    ** Product Info **");
                    System.out.println("    Product Name: "+Main.productList[availableOption[selection-1]].getName());
                    System.out.println("    Unit Price: RM"+Main.productList[availableOption[selection-1]].getUnitPrice());
                    System.out.println("    Product Supplier: "+Main.productList[availableOption[selection-1]].getSupplier().getName());
                    System.out.println("    Product Category: "+Main.productList[availableOption[selection-1]].getCategory().getName());
                    boolean valid2;
                    do {
                        valid2 = true;
                        System.out.print("    Are you sure you want to delete following (Y/N)? ");
                        String confirmation = Main.sc.nextLine();
                        confirmation = confirmation.toUpperCase();
                        if (confirmation.equals("Y")) {
                            Main.productList[availableOption[selection-1]].setStatus("deleted");
                            System.out.print("    You have successfully deleted the product! Press enter to proceed!");
                            Main.sc.nextLine();
                            saveProduct();
                            deleteProduct();
                        }
                        else if (confirmation.equals("N")) {
                            deleteProduct();
                        }
                        else {
                            valid2 = false;
                            System.out.println("\n    No such selection! Please enter again!");
                        }
                    } while (!valid2);
                }
                else {
                    valid = false;
                    System.out.println("\n    No such selection! Please select again!");
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please enter again!");
                Main.sc.nextLine();
                valid = false;
            }
        } while (!valid);
    }

    public void viewProduct() {
        boolean valid2;
        do {
            valid2 = true;
            System.out.println("\n    ** View All Product **");
            System.out.println("    Select product to view: ");
            int i = 0;
            int[] availableOption = new int[Main.categoryList.length];
            int j = 0;
            for (Product x:Main.productList) {
                if (x != null && x.getCategory() != null &&x.getStatus().equals("available")) {
                    System.out.println("    "+(i+1)+". "+x.getName());
                    availableOption[i] = j;
                    i++;
                }
                j++;
            }
            if (i == 0) {
                System.out.println("    1. No Data Found");
                System.out.println("    2. Back to Product Menu\n");
            }
            else {
                System.out.println("    "+(i+1)+". Back to Product Menu\n");
            }
            boolean valid;
            do {
                valid = true;
                System.out.print("    Enter your selection: ");
                try {
                    int selection = Main.sc.nextInt();
                    Main.sc.nextLine();
                    if (i == 0 || selection == i+1) {
                        Main.productMenu();
                    }
                    else if (selection > 0 && selection <= i) {
                        System.out.println("\n    ** Product Info **");
                        System.out.println("    Product Name: "+Main.productList[availableOption[selection-1]].getName());
                        System.out.println("    Unit Price: RM"+Main.productList[availableOption[selection-1]].getUnitPrice());
                        System.out.println("    Product Supplier: "+Main.productList[availableOption[selection-1]].getSupplier().getName());
                        System.out.println("    Product Category: "+Main.productList[availableOption[selection-1]].getCategory().getName());
                        System.out.print("    Press enter to return!");
                        Main.sc.nextLine();
                        valid2 = false;
                    }
                    else {
                        valid = false;
                        System.out.println("\n    No such selection! Please select again!");
                    }
                }
                catch (InputMismatchException e) {
                    System.out.println("\n    No such selection! Please enter again!");
                    Main.sc.nextLine();
                    valid = false;
                }
            } while (!valid);
        } while (!valid2);
    }

    @Override
    public String toString() {
        return "Product{"+
                "Product ID: "+this.productID+
                ",Unit Price: "+this.unitPrice+","+
                this.supplier.toString()+","+
                this.category.toString()+
                "}";
    }
}
