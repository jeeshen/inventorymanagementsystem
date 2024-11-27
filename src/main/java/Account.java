import java.util.Scanner;

public abstract class Account {
    private String username;
    private String email;
    private String password;

    static Scanner sc = new Scanner(System.in);

    //constructor
    public Account(String username,String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Account() {
        this("","","");
    }

    //getter
    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    //setter
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //other methods
    abstract public void saveAccount();
    abstract public void loadAccount();
    abstract public void register();

    public boolean isNameDuplicate(String name) {
        for (Staff x : Staff.staffList) {
            if (x != null && x.getUsername().equalsIgnoreCase(name)) {
                return true;
            }
        }
        for (Supplier x : Supplier.supplierList) {
            if (x != null && x.getUsername().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmailDuplicate(String email) {
        for (Staff x : Staff.staffList) {
            if (x != null && x.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        for (Supplier x: Supplier.supplierList) {
            if (x != null && x.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public static String login() {
        System.out.print("    Enter your name: ");
        String nameEntered = sc.nextLine();
        boolean passwordCheck;
        for(Staff x:Staff.staffList) {
            if (x != null && x.getUsername().equalsIgnoreCase(nameEntered)) {
                do {
                    System.out.print("    Enter your password (0 - Enter another name): ");
                    String passwordEntered = sc.nextLine();
                    passwordCheck = x.getPassword().equals(passwordEntered);
                    if (passwordEntered.equals("0"))
                        return "reset";
                    if (!passwordCheck)
                        System.out.println("\n    Wrong password! Please enter again!");
                } while (!passwordCheck);
                return x.getUsername()+",staff";
            }
        }

        for (Supplier x:Supplier.supplierList) {
            if (x != null && x.getUsername().equalsIgnoreCase(nameEntered)) {
                do {
                    System.out.print("    Enter your password (0 - Reenter name): ");
                    String passwordEntered = sc.nextLine();
                    passwordCheck = x.getPassword().equals(passwordEntered);
                    if (passwordEntered.equals("0"))
                        return "reset";
                    if (!passwordCheck) {
                        System.out.println("\n    Wrong password! Please enter again!");
                    }
                } while (!passwordCheck);
                return x.getUsername()+",supplier";
            }
        }
        return "noSuchName";
    }

    public static void changePassword(String sessionName) {
        String confirmation = "";
        do {
            System.out.print("    Are you sure you want to change your password (Y/N)? ");
            confirmation = sc.nextLine().toUpperCase();
            if (!confirmation.equals("Y") && !confirmation.equals("N"))
                System.out.println("\n    Invalid selection! Please enter again!");
        } while (!confirmation.equals("Y") && !confirmation.equals("N"));

        if (confirmation.equals("Y")) {
             boolean valid;
             do {
                 valid = false;
                 System.out.print("    Enter your old password: ");
                 String oldPassword = sc.nextLine();
                 int count = 0;
                 int found = 0;
                 for (Supplier x:Supplier.supplierList) {
                     if (x != null && x.getUsername().equals(sessionName) && x.getPassword().equals(oldPassword)) {
                         boolean valid2;
                         do {
                             valid2 = true;
                             System.out.print("    Enter new password: ");
                             String newPassword = sc.nextLine();
                             System.out.print("    Enter new password again: ");
                             String newPasswordRepeat = sc.nextLine();
                             if (newPassword.equals(newPasswordRepeat)) {
                                 Supplier.supplierList[count].setPassword(newPassword);
                                 valid = true;
                                 found++;
                             }
                             else {
                                 System.out.println("\n    Password do not match! Please enter again!");
                                 valid2 = false;
                             }
                         } while (!valid2);
                     }
                     count++;
                 }
                 count = 0;
                 for (Staff x:Staff.staffList) {
                     if (x != null && x.getUsername().equals(sessionName) && x.getPassword().equals(oldPassword)) {
                         boolean valid2;
                         do {
                             valid2 = true;
                             System.out.print("    Enter new password: ");
                             String newPassword = sc.nextLine();
                             System.out.print("    Enter new password again: ");
                             String newPasswordRepeat = sc.nextLine();
                             if (newPassword.equals(newPasswordRepeat)) {
                                 Staff.staffList[count].setPassword(newPassword);
                                 valid = true;
                                 found++;
                             }
                             else {
                                 System.out.println("\n    Password do not match! Please enter again!");
                                 valid2 = false;
                             }
                         } while (!valid2);
                     }
                     count++;
                 }
                 if (found == 0) {
                     System.out.println("\n    Wrong password! Please enter again!");
                 }
             } while (!valid);
            System.out.print("    You have successfully changed your password! Press enter to proceed!");
            sc.nextLine();
        }
    }
}
