package com.jumiaservices.task.phone_numbers.Controller;

import com.jumiaservices.task.phone_numbers.Model.Customer;

import java.sql.*;
import java.util.ArrayList;

/**
 * The DBInterface class is the class responsible for any operations done to/from the database such
 * as connection management and query executions
 *
 * @author AbdAlMoniem AlHifnawy
 * @version 1.0
 * @since Jan 22nd 2022
 */
public class DBInterface {

  /** the {@link Connection} object used to connect to the database and execute queries on it */
  private Connection connection;

  /**
   * the {@link PreparedStatement} object which is used to hold the query statement as well as
   * execute it
   */
  private PreparedStatement sqlStatement;

  /**
   * opens a connection to the SQLite database located in SQLiteDB/customers.db the database's path
   * is fixed for now since the application is not complex and does not require switching between
   * different databases
   *
   * @return boolean - <strong>true</strong> if the connection was successful,
   *     <strong>false</strong> otherwise
   */
  private boolean dbOpenConnection() {
    try {
      System.out.println("Connecting to database...");

      // load the sqlite-JDBC driver using the current class loader
      Class.forName("org.sqlite.JDBC");

      // create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:SQLiteDB/customers.db");

      System.out.println("Connection to Server Established");
      return true;
    } catch (Exception e) {
      System.err.println("Connection Failed :" + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  /**
   * closes an open connection to the SQLite database
   *
   * @return boolean - <strong>true</strong> if the connection was terminated successfully,
   *     <strong>false</strong> otherwise
   */
  private boolean dbCloseConnection() {
    try {
      if (connection != null) {
        connection.close();
      } else {
        //  do nothing
      }
    } catch (Exception e) {
      System.err.println("Error in closing connection: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   *
   * @return ArrayList&lt;{@link Customer}&gt; - an ArrayList
   * containing all customers in the database
   *
   * @see Customer
   */
  public ArrayList<Customer> getCustomers() {
    ArrayList<Customer> customers = null;

    try {
      if (!dbOpenConnection()) {
        return null;
      } else {
        customers = new ArrayList<>();

        String sqlQuery = String.format("SELECT * FROM customer");
        System.out.println(sqlQuery);

        sqlStatement = connection.prepareStatement(sqlQuery);
        ResultSet resultSet = sqlStatement.executeQuery();

        while (resultSet.next()) {
          Customer customer = new Customer();

          int id = resultSet.getInt("id");
          String name = resultSet.getString("name");
          String phone = resultSet.getString("phone");

          customer.setID(id);
          customer.setName(name);
          customer.setPhone(phone);

          customers.add(customer);
        }

        dbCloseConnection();
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();

      dbCloseConnection();
    }

    return customers;
  }
}
