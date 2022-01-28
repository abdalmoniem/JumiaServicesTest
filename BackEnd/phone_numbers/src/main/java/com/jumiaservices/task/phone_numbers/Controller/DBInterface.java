package com.jumiaservices.task.phone_numbers.Controller;

import com.jumiaservices.task.phone_numbers.Model.Customer;
import com.jumiaservices.task.phone_numbers.Model.Response;

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

  /** the maximum number of item to fetch in one page */
  private final int PAGE_ITEM_COUNT = 10;

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
   * @param page - the page number to fetch from the database, if null this method will return all
   *     available entries in the database
   * @return {@link Response} - a Response object containing all customers in the database
   * @see Customer
   */
  public Response getCustomers(Integer page) {
    Response response = new Response();

    try {
      if (!dbOpenConnection()) {
        return null;
      } else {
        ArrayList<Customer> customers = new ArrayList<>();

        String sqlQuery;

        if (page == null) {
          sqlQuery = "SELECT * FROM customer";
        } else {
          sqlQuery =
              String.format(
                  "SELECT * FROM customer limit %d offset %d",
                  PAGE_ITEM_COUNT, ((page - 1) * PAGE_ITEM_COUNT));
        }

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

        executePageCountQuery(page, response);

        response.setData(customers);

        dbCloseConnection();
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();

      dbCloseConnection();
    }

    return response;
  }

  /**
   * return all valid phone numbers
   *
   * @param page - the page number to fetch from the database, if null this method will return all
   *             available entries in the database or all filtered entries if a filter is specified
   * @param filter - filter value for valid/invalid numbers, valid values are {@link Filter#VALID},
   *     {@link Filter#INVALID}, {@link Filter#CAMEROON}, {@link Filter#ETHIOPIA}, {@link
   *     Filter#MOROCCO}, {@link Filter#MOZAMBIQUE}, {@link Filter#UGANDA}
   * @return ArrayList&lt;{@link Customer}&gt; - an ArrayList containing all valid phone numbers
   */
  public Response getCustomersByFilter(Integer page, Filter filter) {
    Response response = new Response();

    try {
      if (!dbOpenConnection()) {
        return null;
      } else {
        ArrayList<Customer> customers = new ArrayList<>();
        String sqlQuery;
        // response = new Response();

        // compile a regex to test against for all valid phone numbers returned from the database
        Pattern pattern =
            Pattern.compile(
                "\\((237\\)\\s?[2368]\\d{7,8}$)|(\\(251\\)\\s?[1-59]\\d{8}$)|(\\(212\\)\\s?[5-9]\\d{8}$)"
                    + "|(\\(258\\)\\s?[28]\\d{7,8}$)|(\\(256\\)\\s?\\d{9}$)");

        switch (filter) {
          case VALID:
            // use (like) in query to extract numbers that are valid based on country code, since
            // sqlite does not support regex matching
            sqlQuery =
                "select * from customer where phone like '(237)_%' or phone like '(251)_%' or phone like '(212)_%' or "
                    + "phone like '(258)_%' or phone like '(256)_%'";
            break;
          case INVALID:
            sqlQuery = "select * from customer";
            break;
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
            sqlQuery = "";
            break;
        }

        sqlStatement = connection.prepareStatement(sqlQuery);
        ResultSet resultSet = sqlStatement.executeQuery();

        ArrayList<Customer> invalidCustomers = new ArrayList<>();

        while (resultSet.next()) {
          Customer customer = new Customer();

          int id = resultSet.getInt("id");
          String name = resultSet.getString("name");
          String phone = resultSet.getString("phone");

          Matcher matcher = pattern.matcher(phone);

          if (!matcher.matches() && (filter == Filter.INVALID)) {
            customer.setID(id);
            customer.setName(name);
            customer.setPhone(phone);

            invalidCustomers.add(customer);
          } else if (matcher.matches()) {
            customer.setID(id);
            customer.setName(name);
            customer.setPhone(phone);

            customers.add(customer);
          }
        }

        if (invalidCustomers.size() > 0) {
          customers.clear();
          customers.addAll(invalidCustomers);
        } else {
          // do nothing
        }

        if (page != null) {
          if (customers.size() <= PAGE_ITEM_COUNT) {
            response.setData(customers);
          } else {
            // the start offset is the page index X item count per page, page index is zero index and the service uses
            // a non-zero index so we subtract 1 from the page index to map to a valid index in the array list
            int startOffset = (page - 1) * PAGE_ITEM_COUNT;

            // the end offset is the start offset + item count per page
            int endOffset = startOffset + PAGE_ITEM_COUNT;

            if (endOffset > customers.size()) {
              endOffset = customers.size();
            } else {
              // do nothing
            }

            // total pages is the total size of entries in the database ÷ item count per page, and we ceil the value to
            // account for floating points, so for example if total pages is calculated to be 4.1 or 4.7 it would be
            // raised to 5 in either case
            int totalPages = (int) Math.ceil(customers.size() * 1.0f / PAGE_ITEM_COUNT);
            response.setPagesLeft(totalPages - page);

            if ((startOffset > endOffset) || (startOffset > customers.size()) || (startOffset < 0)) {
              response.setData(null);
            } else {
              response.setData(new ArrayList<>(customers.subList(startOffset, endOffset)));
            }
          }
        } else {
          response.setData(customers);
        }

        dbCloseConnection();
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();

      dbCloseConnection();
    }

    return response;
  }

  /**
   * @param page - the page number to fetch from the database, if null this method will set response.setPagesLeft
   *             with 0, otherwise it will calculate how many pages are left and store it in the response object
   * @param response - the response object to hold the pages left
   * @throws SQLException
   */
  private void executePageCountQuery(Integer page, Response response) throws SQLException {
    String sqlQuery;
    ResultSet resultSet;
    if (page == null) {
      response.setPagesLeft(0);
    } else {
      int rowCount = 0;

      sqlQuery = "SELECT COUNT(*) FROM customer";
      sqlStatement = connection.prepareStatement(sqlQuery);
      resultSet = sqlStatement.executeQuery();

      while (resultSet.next()) {
        rowCount = resultSet.getInt(1);
      }

      int totalPages = (int) Math.ceil(rowCount * 1.0f / PAGE_ITEM_COUNT);

      response.setPagesLeft(totalPages - page);
    }
  }

  /**
   * Filter values <br>
   * {@link #VALID} filter for all valid phone numbers from all countries <br>
   * {@link #INVALID} filter for all invalid phone numbers from all countries <br>
   * {@link #CAMEROON} filter for phone numbers from Cameroon <br>
   * {@link #ETHIOPIA} filter for phone numbers from Ethiopia <br>
   * {@link #MOROCCO} filter for phone numbers from Morocco <br>
   * {@link #MOZAMBIQUE} filter for phone numbers from Mozambique <br>
   * {@link #UGANDA} filter for phone numbers from Uganda
   */
  public enum Filter {
    /** filter for all valid phone numbers from all countries */
    VALID,

    /** filter for all invalid phone numbers from all countries */
    INVALID,

    /** filter for phone numbers from Cameroon */
    CAMEROON,

    /** filter for phone numbers from Ethiopia */
    ETHIOPIA,

    /** filter for phone numbers from Morocco */
    MOROCCO,

    /** filter for phone numbers from Mozambique */
    MOZAMBIQUE,

    /** filter for phone numbers from Uganda */
    UGANDA
  }
}
