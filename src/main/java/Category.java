import java.io.*;
import java.util.InputMismatchException;

public class Category {
    private String categoryID;
    private String name;
    private String description;
    private String status;

    public static int categoryCount = 0;

    public Category(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
        generateCategoryID();
    }

    public Category() {
        this("","","");
    }

    //getter
    public String getCategoryID() {
        return this.categoryID;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStatus() {
        return this.status;
    }

    //setter
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //other methods
    public void generateCategoryID() {
        String prefix = "C";
        this.categoryID = String.format("%s%04d",prefix,categoryCount+1);
    }

    public void loadCategory() {
        try (BufferedReader reader = new BufferedReader(new FileReader("category.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] categoryData = line.split("\\|");
                Main.categoryList[categoryCount] = new Category(categoryData[0],categoryData[1],categoryData[2]);
                categoryCount++;
            }
        } catch (IOException e) {
            System.out.println("    Failed to load product data!");
        }
    }

    public void saveCategory () {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("category.txt"))) {
            for (Category x:Main.categoryList) {
                if (x != null && !x.getName().isEmpty()) {
                    writer.write(String.join("|", x.getName(), x.getDescription(),x.getStatus()));
                    writer.newLine();
                }
            }
        }
        catch (IOException e) {
            System.out.println("    Failed to save category data!");
        }
    }

    public void addCategory() {
        System.out.println("\n    ** Add New Category **");
        boolean valid;
        String categoryName;
        do {
            valid = true;
            System.out.print("    Enter category name: ");
            categoryName = Main.sc.nextLine();
            for (Category x:Main.categoryList) {
                if (x != null && !x.getName().isEmpty()) {
                    if (x.getName().equalsIgnoreCase(categoryName)) {
                        valid = false;
                        System.out.println("\n    You cannot have same category at the same time!");
                    }
                }
            }
            if (categoryName.isEmpty()) {
                valid = false;
                System.out.println("\n    You must enter a value!");
            }
        } while (!valid);
        System.out.print("    Enter category description: ");
        String categoryDescription = Main.sc.nextLine();
        Main.categoryList[categoryCount] = new Category(categoryName, categoryDescription,"available");
        System.out.print("    You have successfully added a new category ("+Main.categoryList[categoryCount].getName()+") ! Press enter to continue!");
        Main.sc.nextLine();
        categoryCount++;
        saveCategory();
        Main.categoryMenu();
    }

    public void editCategory() {
        System.out.println("\n    ** Editing Existing Category **");
        System.out.println("    Select category to edit:");
        int i = 0;
        int[] availableOption = new int[Main.categoryList.length];
        int count = 0;
        for (Category x:Main.categoryList) {
            if (x != null && x.getStatus().equals("available")) {
                System.out.println("    "+(i+1)+". "+x.getName());
                availableOption[i] = count;
                i++;
            }
            count++;
        }
        if (i== 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Category Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Category Menu\n");
        }
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = Main.sc.nextInt();
                Main.sc.nextLine();
                if (i == 0 || selection == i+1) {
                    Main.categoryMenu();
                }
                else if (selection > 0 && selection <= i) {
                    System.out.println("\n    Select data to edit:");
                    System.out.println("    1. Category Name (Current: "+Main.categoryList[availableOption[selection-1]].getName()+")");
                    System.out.println("    2. Category Description (Current: "+Main.categoryList[availableOption[selection-1]].getDescription()+")");
                    System.out.println("    3. Back to Edit Category List\n");
                    boolean valid2;
                    do {
                        valid2 = true;
                        System.out.print("    Enter your selection: ");
                        try {
                            int selection2 = Main.sc.nextInt();
                            Main.sc.nextLine();
                            switch (selection2) {
                                case 1:
                                    boolean valid3;
                                    String categoryName;
                                    do {
                                        valid3 = true;
                                        System.out.print("    Enter new category name: ");
                                        categoryName = Main.sc.nextLine();
                                        for (Category x:Main.categoryList) {
                                            if (x != null) {
                                                if (x.getName().equalsIgnoreCase(categoryName)) {
                                                    valid3= false;
                                                    System.out.println("\n    You cannot have same category at the same time");
                                                }
                                            }
                                        }
                                        if (categoryName.isEmpty()) {
                                            valid3 = false;
                                            System.out.println("\n    You must a value!");
                                        }
                                    } while (!valid3);
                                    Category tempCategory = new Category(categoryName,null,"available");
                                    int j = 0;
                                    for (Product x:Main.productList) {
                                        if (x != null && !x.getName().isEmpty()) {
                                            if (x.getCategory().getName().equals(Main.categoryList[availableOption[selection-1]].getName())) {
                                                Main.productList[j].setCategory(tempCategory);
                                                j++;
                                            }
                                        }
                                    }
                                    Main.categoryList[availableOption[selection-1]].setName(categoryName);
                                    System.out.println("    You have successfully changed category name to "+Main.categoryList[availableOption[selection-1]].getName()+"! Press enter to proceed!");
                                    Main.sc.nextLine();
                                    saveCategory();
                                    Main.product.saveProduct();
                                    editCategory();
                                    break;
                                case 2:
                                    System.out.print("    Enter new category description: ");
                                    String categoryDescription = Main.sc.nextLine();
                                    Main.categoryList[availableOption[selection-1]].setDescription(categoryDescription);
                                    System.out.print("    You have successfully changed category description to "+Main.categoryList[availableOption[selection-1]].getDescription()+"! Press enter to proceed!");
                                    saveCategory();
                                    editCategory();
                                    break;
                                case 3:
                                    editCategory();
                                    break;
                                default:
                                    valid2 = false;
                                    System.out.println("\n    No such selection! Please enter again!");
                            }
                        }
                        catch (InputMismatchException e) {
                            System.out.println("\n    No such selection! Please enter again!");
                            Main.sc.nextLine();
                            valid2 = false;
                        }
                    } while (!valid2);
                }
                else {
                    valid = false;
                    System.out.println("\n    No such selection! Please enter again!");
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please enter again!");
                Main.sc.nextLine();
                valid = false;
            }
        } while (!valid);
    }

    public void deleteCategory() {
        System.out.println("\n    ** Deleting Existing Category **");
        System.out.println("    Select category to delete:");
        int i = 0;
        int[] availableOption = new int[Main.categoryList.length];
        int count = 0;
        for (Category x:Main.categoryList) {
            if (x != null && !x.getName().isEmpty() && x.getStatus().equals("available")) {
                System.out.println("    "+(i+1)+". "+x.getName());
                availableOption[i] = count;
                i++;
            }
            count++;
        }
        if (i == 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Category Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Category Menu\n");
        }
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = Main.sc.nextInt();
                Main.sc.nextLine();
                if (i == 0 || selection == i+1) {
                    Main.categoryMenu();
                }
                else if (selection > 0 && selection <= i) {
                    System.out.println("\n    ** Category Info **");
                    System.out.println("    Category Name: "+Main.categoryList[availableOption[selection-1]].getName());
                    System.out.println("    Category Description: "+Main.categoryList[availableOption[selection-1]].getDescription());
                    boolean valid2;
                    do {
                        valid2 = true;
                        System.out.print("    Are you sure you want to delete following (Y/N)? ");
                        String confirmation = Main.sc.nextLine();
                        confirmation = confirmation.toUpperCase();
                        if (confirmation.equals("Y")) {
                            Main.categoryList[availableOption[selection-1]].setStatus("deleted");
                            saveCategory();
                            System.out.print("    You have successfully deleted the category! Press enter to proceed!");
                            Main.sc.nextLine();
                            deleteCategory();
                        }
                        else if (confirmation.equals("N")) {
                            deleteCategory();
                        }
                        else {
                            System.out.println("\n    No such selection! Please enter again!");
                            valid2 = false;
                        }
                    } while (!valid2);
                }
                else {
                    System.out.println("\n    No such selection! Please enter again!");
                    valid = false;
                }
            }
            catch (InputMismatchException e){
                System.out.println("\n    No such selection! Please enter again!");
                valid = false;
                Main.sc.nextLine();
            }
        } while (!valid);
    }

    public void viewCategory() {
        System.out.println("\n    ** View All Category **");
        System.out.println("    Select category to view:");
        int i = 0;
        int[] availableOption = new int[Main.categoryList.length];
        int count = 0;
        for (Category x:Main.categoryList) {
            if (x != null && !x.getName().isEmpty() && x.getStatus().equals("available")) {
                System.out.println("    "+(i+1)+". "+x.getName());
                availableOption[i] = count;
                i++;
            }
            count++;
        }
        if (i == 0) {
            System.out.println("    1. No Data Found");
            System.out.println("    2. Back to Category Menu\n");
        }
        else {
            System.out.println("    "+(i+1)+". Back to Category Menu\n");
        }
        boolean valid;
        do {
            valid = true;
            System.out.print("    Enter your selection: ");
            try {
                int selection = Main.sc.nextInt();
                Main.sc.nextLine();
                if (i == 1 || selection == i+1) {
                    Main.categoryMenu();
                }
                else if (selection > 0 && selection <= i) {
                    System.out.println("\n    ** Category Info **");
                    System.out.println("    Category Name: "+Main.categoryList[availableOption[selection-1]].getName());
                    System.out.println("    Category Description: "+Main.categoryList[availableOption[selection-1]].getDescription());
                    System.out.print("    Press enter to proceed!");
                    Main.sc.nextLine();
                    viewCategory();
                }
                else {
                    valid = false;
                    System.out.println("\n    No such selection! Please enter again!");
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n    No such selection! Please enter again!");
                valid = false;
                Main.sc.nextLine();
            }
        } while(!valid);
    }

    @Override
    public String toString() {
        return "Category{"+
                "Category ID: "+this.categoryID+
                ",Name: "+this.name+
                ",Description: "+this.description+
                ",Status: "+this.status+
                "}";
    }
}
