import java.io.*;
import java.util.Scanner;

public class Staff extends Account{
    private String staffID;
    private String position;
    private double salary;

    public static int staffCount = 0;
    public static Staff[] staffList = new Staff[100];

    Scanner sc = new Scanner(System.in);

    //constructor
    public Staff(String username, String email, String password, String position, double salary) {
        super(username,email,password);
        this.position = position;
        this.salary = salary;
        generateStaffID();
    }

    public Staff() {
        this("", "", "", "", 0);
    }

    //getter
    public String getStaffID() {
        return this.staffID;
    }

    public String getPosition() {
        return this.position;
    }

    public double getSalary() {
        return this.salary;
    }

    //setter
    public void setPosition(String position) {
        this.position = position;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    //other methods
    public void generateStaffID() {
        String prefix = "S";
        this.staffID = String.format("%s%04d",prefix,staffCount+1);
    }

    public void saveAccount() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("staff.txt"))) {
            for (Staff x:staffList) {
                if (x != null) {
                    writer.write(String.join("|",x.getUsername(),x.getEmail(),x.getPassword(),x.getPosition(),String.valueOf(x.getSalary())));
                    writer.newLine();
                }
            }
        }
        catch (IOException e) {
            System.out.println("    Failed to save staff account data!");
        }
    }

    public void loadAccount() {
        try (BufferedReader reader = new BufferedReader(new FileReader("staff.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] staffData = line.split("\\|");
                staffList[staffCount] = new Staff(staffData[0],staffData[1],staffData[2],staffData[3],Double.parseDouble(staffData[4]));
                staffCount++;
            }
        }
        catch (IOException e) {
            System.out.println("    Failed to read staff account data!");
        }
    }

    public void register() {
        String inputUsername;
        String inputEmail;
        String inputPassword;
        String inputPosition = "";
        double inputSalary = 0;
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
            System.out.println("\n    Position List:");
            String[] positionList = {"Warehouse Manager","Receiving Clerk","Shipping Clerk","Order Packer","Warehouse Associate"};
            double[] salaryList = {25000,10000,10000,3000,3500};
            int i = 1;
            for (String x:positionList) {
                System.out.println("    "+i+". "+x);
                i++;
            }
            System.out.print("    Enter your selection: ");
            int inputPositionNumber = sc.nextInt();
            sc.nextLine();
            switch (inputPositionNumber) {
                case 1:
                    inputPosition = positionList[0];
                    inputSalary = salaryList[0];
                    break;
                case 2:
                    inputPosition = positionList[1];
                    inputSalary = salaryList[1];
                    break;
                case 3:
                    inputPosition = positionList[2];
                    inputSalary = salaryList[2];
                    break;
                case 4:
                    inputPosition = positionList[3];
                    inputSalary = salaryList[3];
                    break;
                case 5:
                    inputPosition = positionList[4];
                    inputSalary = salaryList[4];
                    break;
                default:
                    System.out.println("\n    Invalid selection! Please select again!");
                    valid = false;
            }
        } while (!valid);
        staffList[staffCount] = new Staff(inputUsername,inputEmail,inputPassword,inputPosition,inputSalary);
        staffCount++;
        saveAccount();
    }

    @Override
    public String toString() {
        return "Staff{"+
                "Username: "+super.getUsername()+
                ",Email: "+super.getEmail()+
                ",Password: "+super.getPassword()+
                ",Staff ID: "+this.staffID+
                ",Position: "+this.position+
                ",Salary: "+String.valueOf(this.salary)+
                "}";
    }
}
