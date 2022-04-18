package com.company;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    // Global Variables.
    public static Scanner scanner = new Scanner(System.in);
    public static String path = "C:\\Users\\tonny\\IdeaProjects\\Bank\\src\\com\\company\\Bank.json";
    public static double oBalance = 0;

    public static void main(String[] args) throws IOException {
        // Input Account Number & Pin (For testing use 11918273645 as Account No & PIN).
        System.out.print("Enter Account Number (For Testing Use: 11918273645): ");
        double accountNo = scanner.nextDouble();
        System.out.print("Enter PIN (For Testing Use: 11918273645): ");
        double PIN = scanner.nextDouble();
        // Authorization
        JSONObject object = GetAccountInfo(accountNo);
        if (object.getDouble("AccountNumber") == accountNo && (object.getDouble("PIN") == PIN)) Option(accountNo);
        else System.out.println("Account Number and PIN does not match.");
    }

    private static void Option(double accountNo) throws IOException {
        // Display the main menu option
        for (String mainMenu : Arrays.asList("Main Menu:", "1. Deposit.", "2. Withdraw.", "3. Balance.",
                "4. Currency Exchange.", "5. Exit."))
            System.out.println(mainMenu);
        System.out.print("Option (Choose one of the above): ");
        int mOption = scanner.nextInt();
        switch (mOption) {
            case 1:
                Deposit(accountNo);
                break;
            case 2:
                Withdraw(accountNo);
                break;
            case 3:
                Balance(accountNo);
                break;
            case 4:
                CurrencyExchange(accountNo);
                break;
            case 5:
                System.out.close();
                break;
            default:
                System.out.println("You entered a wrong value (Please enter integer value between 1 and 5).");
                Option(accountNo);
                break;
        }
    }

    private static void Deposit(double accountNo) throws IOException {
        // Amount to deposit
        System.out.print("Amount To Deposit: ");
        double dAmount = scanner.nextDouble();
        // Account Details
        JSONObject dObject = GetAccountInfo(accountNo);
        for (String accountInfo : Arrays.asList("--------------Account Details--------------",
                String.format("Amount To Deposit: %s", dAmount), String.format("Account Name: %s", dObject.getString("AccountName")),
                String.format("Account Number: %s", dObject.getString("AccountNumber")), "-------------------------------------------"))
            System.out.println(accountInfo);
        // Confirm before depositing
        for (String dConfirm : Arrays.asList(String.format("Do you wish to deposit %s to the above account details? ", dAmount),
                "1. Yes", "2. No"))
            System.out.println(dConfirm);
        // Confirm Deposit Option.
        CDOption(dObject, dAmount, accountNo);
    }

    private static void Withdraw(double accountNo) throws IOException {
        // Amount to withdraw
        System.out.print("Amount To Withdraw: ");
        double wAmount = scanner.nextDouble();
        // Account Info
        JSONObject wObject = GetAccountInfo(accountNo);
        oBalance = wObject.getDouble("Balance");
        // Check account balance
        if (wAmount > oBalance || oBalance < 1000.00) {
            for (String wErrorMsg : List.of("You have insufficient amount in your account, Do you wish to continue to:",
                    "1. Main Menu","2. Exit."))
                System.out.println(wErrorMsg);
            System.out.print("Please Enter(1 or 2): ");
            int iOption = scanner.nextInt();
            if (iOption == 1) {
                Option(accountNo);
            }else{
                return;
            }
        }
        for (String wAccountDetails : Arrays.asList("--------------Account Details--------------",
                String.format("Amount To Withdraw: %s", wAmount),
                String.format("Account Number: %s", wObject.getString("AccountNumber")), "-------------------------------------------")) {
            System.out.println(wAccountDetails);
        }
        // Confirm Withdraw
        for (String withdraw : Arrays.asList(String.format("Do you wish to withdraw %s from the above account details? ",
                        wAmount), "1. Yes", "2. No"))
            System.out.println(withdraw);
        System.out.print("Please Enter(1 or 2): ");
        int cwOption = scanner.nextInt();
        if (cwOption == 1) {
            // Amount remaining after withdrawal
            double nBalance = oBalance - wAmount;
            // Update balance
            UpdateBalance(String.valueOf(oBalance), String.valueOf(nBalance));
            for (String sWithdraw : Arrays.asList("Withdraw was successfully, Do you wish to continue to:", "1. Main Menu.","2. Exit"))
                System.out.println(sWithdraw);
            System.out.print("Please Enter(1 or 2): ");
            int wOption = scanner.nextInt();
            if (wOption ==1 ){
                Option(accountNo);
            } else {
                System.out.println();
            }
        } else {
            Option(accountNo);
        }
    }

    private static void Balance(double accountNo) throws IOException {
        JSONObject object = GetAccountInfo(accountNo);
        System.out.println("--------------Account Details--------------");
        for (String s : Arrays.asList(String.format("Account Balance: %s", object.getDouble("Balance")),
                String.format("Account Name: %s", object.getString("AccountName")),
                String.format("Account Number: %s", object.getString("AccountNumber")))) {
            System.out.println(s);
        }
        System.out.println("-------------------------------------------");
        for (String successMsg : List.of("Do you wish to continue to:","1. Main Menu.","2. Exit.")) {
            System.out.println(successMsg);
        }
        System.out.print("Please Enter(1 or 2): ");
        int terminateOption = scanner.nextInt();
        switch (terminateOption) {
            case 1:
                Option(accountNo);
                break;
            case 2:
                System.out.close();
                break;
            default:
                System.out.print("You entered wrong value. ");
                Balance(accountNo);
                break;
        }
    }

    private static void CurrencyExchange(double accountNo) throws IOException {
        String[][] converter = new String[][]{
                {"US Dollar", "USD", "Kenyan Shilling", "KES", "1", "115.50"},
                {"US Dollar", "USD", "Ugandan Shilling", "UGX", "1", "115.50"},
                {"US Dollar", "USD", "Tanzanian Shilling", "TZS", "1", "115.50"},
                {"Kenyan Shilling", "KES", "US Dollar", "USD", "1", "0.0087"},
                {"Kenyan Shilling", "KES", "Ugandan Shilling", "UGX", "1", "30.48"},
                {"Kenyan Shilling", "KES", "Tanzanian Shilling", "TZS", "1", "19.91"},
                {"Ugandan Shilling", "UGX", "US Dollar", "USD", "1", "0.0087"},
                {"Ugandan Shilling", "UGX", "Kenyan Shilling", "KES", "1", "30.48"},
                {"Ugandan Shilling", "UGX", "Tanzanian Shilling", "TZS", "1", "19.91"},
                {"Tanzanian Shilling", "TZS", "US Dollar", "USD", "1", "0.0087"},
                {"Tanzanian Shilling", "TZS", "Kenyan Shilling", "KES", "1", "30.48"},
                {"Tanzanian Shilling", "TZS", "Ugandan Shilling", "UGX", "1", "19.91"},
        };
        // List of Available Currencies
        System.out.println("------------Available Currencies-----------");
        for (int i = 0; i < converter.length; i++)
        {
            int j;
            for (j = 0; j < i; j++)
                if (converter[i][0].equals(converter[j][0]))
                    break;
            if (i == j)
                System.out.printf("%s - %s%n", converter[i][1], converter[i][0]);
        }
        System.out.println("-------------------------------------------");
        System.out.print("Convert From: (Enter one of the above (Currency Code)): ");
        String convertFrom = scanner.next();
        System.out.print("Convert To: (Enter one of the above (Currency Code)): ");
        String convertTo = scanner.next();

        for (String[] strings : converter) {
            if (convertFrom.equals(strings[1]) && convertTo.equals(strings[3])) {
                System.out.printf("For every %s %s, you will get %s %s.%n", strings[4], strings[0], strings[5], strings[2]);
                Option(accountNo);
                return;
            }
        }
        System.out.println("Currencies exchange rate not available.");
        CurrencyExchange(accountNo);
    }

    // Get Account information.
    private static JSONObject GetAccountInfo(double accountNo) throws IOException {
        JSONObject object = null;
        String Data = new String(Files.readAllBytes(Paths.get(path)));
        JSONArray array = new JSONArray(Data);
        for (int i = 0; i < array.length(); i++) {
            object = array.getJSONObject(i);
            if (object.getDouble("AccountNumber") == accountNo) {
                return object;
            }
        }
        return object;
    }

    // Confirm Deposit Option.
    private static void CDOption(JSONObject dObject, double dAmount, double accountNo) throws IOException {
        System.out.print("Please Enter(1 or 2): ");
        int cOption = scanner.nextInt();
        switch (cOption) {
            case 1:
                // New balance
                double nBalance = dObject.getDouble("Balance") + dAmount;
                // Update balance
                UpdateBalance(String.valueOf(dObject.getDouble("Balance")), String.valueOf(nBalance));
                for (String dSMessage : Arrays.asList("Deposit was successfully, Do you wish to continue to: ",
                        "1. Main Menu.", "2. Exit."))
                    System.out.println(dSMessage);
                SDOption(accountNo);
                return;
            case 2:
                // Cancel and return to the main menu
                System.out.println("Deposit cancelled successfully.");
                Option(accountNo);
                return;
            default:
                System.out.print("You entered wrong a value. ");
                CDOption(dObject, accountNo, accountNo);
        }
    }

    // Success Deposit Options.
    private static void SDOption(double accountNo) throws IOException {
        System.out.print("Please Enter(1 or 2): ");
        int sDOption = scanner.nextInt();
        if (sDOption == 1) {
            Option(accountNo);
        } else if (sDOption == 2) {
            System.out.close();
        } else {
            System.out.print("You entered a wrong value. ");
            SDOption(accountNo);
        }
    }

    private static void UpdateBalance(String oBalance, String nBalance) throws IOException {
        //Instantiating the Scanner class to read the file
        Scanner sc = new Scanner(new File(path));

        //instantiating the StringBuffer class
        StringBuilder buffer = new StringBuilder();

        //Reading lines of the file and appending them to StringBuffer
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()).append(System.lineSeparator());
        }
        String fileContents = buffer.toString();
        sc.close();

        //Replacing the old line with new line
        fileContents = fileContents.replaceAll(oBalance, nBalance);

        //instantiating the FileWriter class
        FileWriter writer = new FileWriter(path);
        writer.append(fileContents);
        writer.flush();
    }
}
