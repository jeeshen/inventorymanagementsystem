import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    private String transactionID;
    private Date transactionDate;
    private double payAmount;

    public static int transactionCount = 0;
    public static Transaction[] transactionList = new Transaction[100];

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public Transaction(Date transactionDate, double payAmount) {
        this.transactionDate = transactionDate;
        this.payAmount = payAmount;
        generateTransactionID();
    }

    public Transaction(String date, double payAmount) {
        try {
            this.transactionDate = formatter.parse(date);
        }
        catch (ParseException e) {
            System.out.println("    Failed to read transaction date!");
        }
        this.payAmount = payAmount;
        generateTransactionID();
    }

    public Transaction() {
        this(new Date(),0);
    }

    public String getTransactionID() {
        return this.transactionID;
    }

    public String getTransactionDate() {
        return formatter.format(this.transactionDate);
    }

    public double getPayAmount() {
        return this.payAmount;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public void generateTransactionID() {
        String prefix = "R";
        this.transactionID = String.format("%s%04d",prefix,transactionCount+1);
    }

    public void saveTransaction() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transaction.txt"))) {
            for (Transaction x:transactionList) {
                if (x != null && x.getPayAmount() != 0) {
                    writer.write(String.join("|", x.getTransactionDate(),String.valueOf(x.getPayAmount())));
                    writer.newLine();
                }
            }
        }
        catch (IOException e) {
            System.out.println("    Failed to save transaction data!");
        }
    }

    public void loadOrder() {
        try (BufferedReader reader = new BufferedReader(new FileReader("transaction.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] transactionData = line.split("\\|");
                try {
                    Date parsedDate = formatter.parse(transactionData[0]);
                    transactionList[transactionCount] = new Transaction(parsedDate,Double.parseDouble(transactionData[1]));
                    transactionCount++;
                } catch (ParseException e) {
                    System.out.println("   Failed to read transaction date!");
                }

            }
        } catch (IOException e) {
            System.out.println("    Failed to load transaction data!");
        }
    }

    public void viewTransaction() {
        System.out.println("\n    ** View All Transactions **");
        for(Transaction x:transactionList) {
            if (x != null && x.getPayAmount() > 0) {
                System.out.println("    "+x.getTransactionID()+" - "+x.getTransactionDate()+" = RM"+x.getPayAmount());
            }
        }
        System.out.print("\n    Press enter to return!");
        Main.sc.nextLine();
        Main.purchaseOrderMenu();
    }
}
