package com.jumiaservices.task.phone_numbers.Controller;

import com.jumiaservices.task.phone_numbers.Model.Customer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * The PhoneNumberProcessorController class represents the main
 * REST Controller of the business logic
 *
 * @author AbdAlMoniem AlHifnawy
 * @version 1.0
 * @since Jan 22nd 2022
 */
@RestController
public class PhoneNumberProcessorController {

  // create database interface object to use for queries
  DBInterface dbi = new DBInterface();

  /**
   * This method is used to return all customers stored
   * in the database without filtering or pagination
   *
   * @return ArrayList&lt;{@link Customer}&gt; - an ArrayList
   * containing all customers in the database
   *
   * @see Customer
   */
  @RequestMapping(
      path = "getNumbers",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ArrayList<Customer> getNumbers() {
    return dbi.getCustomers();
  }
}
