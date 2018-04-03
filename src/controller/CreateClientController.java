
package controller;

import model.BankConnection;
import view.WelcomeScreen;

import javax.swing.*;
import java.sql.*;

public class CreateClientController
{
    private String firstName, lastName, social;
    private int accountNumber;
    private Connection bankConnection;


    private WelcomeScreen view;

    public CreateClientController(String firstName, String lastName, String social)
    {
        this.view = new WelcomeScreen();
        this.bankConnection = BankConnection.createConnection();
        this.accountNumber = Utilities.getBiggestAccountNumber(bankConnection);
        this.accountNumber++; // increment account number for next client

        this.firstName = firstName;
        this.lastName = lastName;
        this.social = social;

        addClientToDatabase(accountNumber, bankConnection);
    }

        private void addClientToDatabase(int accountNumber, Connection connection)
        {
           if(clientInfoValid())
           {
            addClientInfo(firstName, lastName, social, connection);
            addCheckingInfo(connection, accountNumber, 0.0, social);

           }
        }

    private boolean clientInfoValid()
    {
        boolean informationCorrect = false;

        social = social.replaceAll("[- ]", "");


        if(firstName.length() == 0)
        {
            JOptionPane.showMessageDialog(null, "First name invalid");
            System.out.println("first name length: " + firstName.length());
        } else if(lastName.length() == 0)
        {
            JOptionPane.showMessageDialog(null, "Last name invalid");
            System.out.println("last name length: " + lastName.length());
        } else if(social.trim().length() != 9)
        {
            JOptionPane.showMessageDialog(null, "Social Security Number invalid");
        } else
        {
            informationCorrect = true;
        }
        return informationCorrect;
    }

    private void addClientInfo(String firstName, String lastName, String social, Connection bankConnection)
    {
        try
        {
            String createClientStatement = "INSERT INTO clients (first_name, last_name, social, account_number) values (?, ?, ?, ?)";

            try (PreparedStatement preparedStatementClient = bankConnection.prepareStatement(createClientStatement))
            {
                preparedStatementClient.setString(1, firstName);
                preparedStatementClient.setString(2, lastName);
                preparedStatementClient.setString(3, social);
                preparedStatementClient.setInt(4, accountNumber);

                preparedStatementClient.execute();
            }
        }
        catch (SQLException e) { e.printStackTrace(); }}

    private void addCheckingInfo(Connection bankConnection, int ACCOUNT_NUMBER, double balance, String social) {
        try {
            String checkingStatement = "INSERT INTO checking_account (account_number, account_balance, social) values(?,?,?)";

            PreparedStatement preparedStatement = bankConnection.prepareStatement(checkingStatement);

            preparedStatement.setInt(1, ACCOUNT_NUMBER);
            preparedStatement.setDouble(2, 0.0);
            preparedStatement.setString(3, social);

            preparedStatement.execute();
        } catch (SQLException e) { e.printStackTrace(); }
    }



    private boolean doesAccountExist()
    {

        boolean accountExists = false;
        try
        {
            Statement query = bankConnection.createStatement();

            String sqlQuery = "SELECT first_name, last_name, account_number FROM clients";

            ResultSet resultSet = query.executeQuery(sqlQuery);

            while(resultSet.next())
            {

                //account number from database
                int accountNumberDatabase = Integer.parseInt(resultSet.getString(3));
                int accountFromInput = view.getAcctNum();

                if(accountNumberDatabase == accountFromInput)
                {
                    accountExists = true;
                    System.out.println("Account found for: " + resultSet.getString(1) + " " + resultSet.getString(2));
                    break;
                }
                else
                    accountExists = false;
            }
        }
        catch(SQLException e) { e.printStackTrace(); }
        return  accountExists;
    }
}
