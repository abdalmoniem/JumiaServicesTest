package com.jumiaservices.task.phone_numbers.Controller;

import com.jumiaservices.task.phone_numbers.Model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The DBInterface class is the class responsible for any operations done to/from the database such
 * as connection management and query executions
 *
 * @author AbdAlMoniem AlHifnawy
 * @version 1.0
 * @since Jan 27th 2022
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
      // load the sqlite-JDBC driver using the current class loader
      Class.forName("org.sqlite.JDBC");

      // create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:SQLiteDB/customers.db");
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
   * @return ArrayList&lt;{@link Customer}&gt; - an ArrayList containing all customers in the
   *     database
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

  /**
   * return all valid phone numbers
   *
   * @param filter - filter value for countries, valid values are {@link Filter#CAMEROON},
   * {@link Filter#ETHIOPIA}, {@link Filter#MOROCCO}, {@link Filter#MOZAMBIQUE}, {@link Filter#UGANDA}
   * @return ArrayList&lt;{@link Customer}&gt; - an ArrayList containing all valid phone numbers
   */
  public ArrayList<Customer> getStateCustomers(Filter filter) {
    ArrayList<Customer> customers = null;

    try {
      if (!dbOpenConnection()) {
        return null;
      } else {
        customers = new ArrayList<>();

        String sqlQuery = "";

        switch (filter) {
          case VALID:
            // use (like) in query to extract numbers that are valid based on country code, since sqlite does not support
            // regex matching
            sqlQuery =
                    "select * from customer where phone like '(237)_%' or phone like '(251)_%' or phone like '(212)_%' or "
                            + "phone like '(258)_%' or phone like '(256)_%'";
            break;
          case INVALID:
            sqlQuery =
                    "select * from customer";
          default:
            break;
        }

        // compile a regex to test against for all valid phone numbers returned from the database
        Pattern pattern =
                Pattern.compile(
                        "\\((237\\)\\s?[2368]\\d{7,8}$)|(\\(251\\)\\s?[1-59]\\d{8}$)|(\\(212\\)\\s?[5-9]\\d{8}$)"
                                + "|(\\(258\\)\\s?[28]\\d{7,8}$)|(\\(256\\)\\s?\\d{9}$)");

        sqlStatement = connection.prepareStatement(sqlQuery);
        ResultSet resultSet = sqlStatement.executeQuery();

        while (resultSet.next()) {
          Customer customer = new Customer();

          int id = resultSet.getInt("id");
          String name = resultSet.getString("name");
          String phone = resultSet.getString("phone");

          Matcher matcher = pattern.matcher(phone);

          if (matcher.matches() && (filter == Filter.VALID)) {
            customer.setID(id);
            customer.setName(name);
            customer.setPhone(phone);

            customers.add(customer);
          } else if (!matcher.matches() && filter == Filter.INVALID) {
            customer.setID(id);
            customer.setName(name);
            customer.setPhone(phone);

            customers.add(customer);
          }
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

  /**
   * return all valid phone numbers for a given country
   *
   * @param filter - filter value for countries, valid values are {@link Filter#CAMEROON},
   * {@link Filter#ETHIOPIA}, {@link Filter#MOROCCO}, {@link Filter#MOZAMBIQUE}, {@link Filter#UGANDA}
   * @return ArrayList&lt;{@link Customer}&gt; - an ArrayList containing all valid phone numbers for a given country
   */
  public ArrayList<Customer> getCountryCustomers(Filter filter) {
    ArrayList<Customer> customers = null;

    try {
      if (!dbOpenConnection()) {
        return null;
      } else {
        customers = new ArrayList<>();

        String sqlQuery = "";
        Pattern pattern = null;

        // use (like) in query to extract numbers that are valid based on country code, since sqlite does not support
        // regex matching and compile a regex to test against for all valid phone numbers returned from the database
        switch (filter) {
          case CAMEROON:
            sqlQuery = "select * from customer where phone like '(237)_%'";
            pattern = Pattern.compile("\\(237\\)\\s?[2368]\\d{7,8}$");
            break;
          case ETHIOPIA:
            sqlQuery = "select * from customer where phone like '(251)_%'";
            pattern = Pattern.compile("\\(251\\)\\s?[1-59]\\d{8}$");
            break;
          case MOROCCO:
            sqlQuery = "select * from customer where phone like '(212)_%'";
            pattern = Pattern.compile("\\(212\\)\\s?[5-9]\\d{8}$");
            break;
          case MOZAMBIQUE:
            sqlQuery = "select * from customer where phone like '(258)_%'";
            pattern = Pattern.compile("\\(258\\)\\s?[28]\\d{7,8}$");
            break;
          case UGANDA:
            sqlQuery = "select * from customer where phone like '(256)_%'";
            pattern = Pattern.compile("\\(256\\)\\s?\\d{9}$");
            break;
          default:
            break;
        }

        sqlStatement = connection.prepareStatement(sqlQuery);
        ResultSet resultSet = sqlStatement.executeQuery();

        while (resultSet.next()) {
          Customer customer = new Customer();

          int id = resultSet.getInt("id");
          String name = resultSet.getString("name");
          String phone = resultSet.getString("phone");

          if (pattern != null) {
            Matcher matcher = pattern.matcher(phone);

            if (matcher.matches()) {
              customer.setID(id);
              customer.setName(name);
              customer.setPhone(phone);

              customers.add(customer);
            } else {
              // do nothing
            }
          } else {
            // do nothing
          }
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

  /**
   * Filter values
   * <br>
   * {@link #VALID} filter for all valid phone numbers from all countries
   * <br>
   * {@link #INVALID} filter for all invalid phone numbers from all countries
   * <br>
   * {@link #CAMEROON} filter for phone numbers from Cameroon
   * <br>
   * {@link #ETHIOPIA} filter for phone numbers from Ethiopia
   * <br>
   * {@link #MOROCCO} filter for phone numbers from Morocco
   * <br>
   * {@link #MOZAMBIQUE} filter for phone numbers from Mozambique
   * <br>
   * {@link #UGANDA} filter for phone numbers from Uganda
   */
  public enum Filter {
    /**
     * filter for all valid phone numbers from all countries
     */
    VALID,

    /**
     * filter for all invalid phone numbers from all countries
     */
    INVALID,

    /**
     * filter for phone numbers from Cameroon
     */
    CAMEROON,

    /**
     * filter for phone numbers from Ethiopia
     */
    ETHIOPIA,

    /**
     * filter for phone numbers from Morocco
     */
    MOROCCO,

    /**
     * filter for phone numbers from Mozambique
     */
    MOZAMBIQUE,

    /**
     * filter for phone numbers from Uganda
     */
    UGANDA
  }
}
