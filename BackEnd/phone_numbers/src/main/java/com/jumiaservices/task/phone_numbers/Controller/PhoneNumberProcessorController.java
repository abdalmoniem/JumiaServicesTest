package com.jumiaservices.task.phone_numbers.Controller;

import com.jumiaservices.task.phone_numbers.Controller.DBInterface.Filter;
import com.jumiaservices.task.phone_numbers.Model.Customer;
import com.jumiaservices.task.phone_numbers.Model.Response;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * The PhoneNumberProcessorController class represents the main REST Controller of the business
 * logic
 *
 * @author AbdAlMoniem AlHifnawy
 * @version 1.0
 * @since Jan 27th 2022
 */
@RestController
public class PhoneNumberProcessorController {

  /**
   * database interface object for sending queries to the database
   */
  DBInterface dbi = new DBInterface();

  /**
   * This method is used to return all customers stored in the database without filtering or
   * pagination
   *
   * @return Response - a response object with the result of the query
   *     database
   * @see Response
   */
  @RequestMapping(
      path = "getNumbers",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Response getNumbers() {
    Response response = new Response();
    ArrayList<Customer> customers = dbi.getCustomers();

    return getResponse(response, customers);
  }

  /**
   * This method is used to return all customers stored in the database with state valid or invalid
   * still no pagination
   *
   * @param state - the path variable to parse as input valid values are {@link Filter#VALID} and
   * {@link Filter#INVALID}
   * @return Response - a response object with the result of the query
   *     database
   * @see Response
   */
  @RequestMapping(
      path = "getNumbers/byState/{state}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Response getStateNumbers(@PathVariable String state) {
    Response response = new Response();
    ArrayList<Customer> customers;

    switch (state) {
      case "valid":
        customers = dbi.getStateCustomers(Filter.VALID);
        break;
      case "invalid":
        customers = dbi.getStateCustomers(Filter.INVALID);
        break;
      default:
        customers = null;
        break;
    }

    return getResponse(response, customers);
  }

  /**
   * This method is used to return all customers stored in the database with state valid or invalid
   * still no pagination
   *
   * @param country - the path variable to parse as input valid values are {@link Filter#CAMEROON},
   * {@link Filter#ETHIOPIA}, {@link Filter#MOROCCO}, {@link Filter#MOZAMBIQUE}, {@link Filter#UGANDA}
   * @return Response - a response object with the result of the query
   *     database
   * @see Response
   */
  @RequestMapping(
      path = "getNumbers/byCountry/{country}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Response getCountryNumbers(@PathVariable String country) {
    Response response = new Response();
    ArrayList<Customer> customers;

    switch (country) {
      case "cameroon":
        customers = dbi.getCountryCustomers(Filter.CAMEROON);
        break;
      case "ethiopia":
        customers = dbi.getCountryCustomers(Filter.ETHIOPIA);
        break;
      case "morocco":
        customers = dbi.getCountryCustomers(Filter.MOROCCO);
        break;
      case "mozambique":
        customers = dbi.getCountryCustomers(Filter.MOZAMBIQUE);
        break;
      case "uganda":
        customers = dbi.getCountryCustomers(Filter.UGANDA);
        break;
      default:
        customers = null;
        break;
    }

    return getResponse(response, customers);
  }

  /**
   * Constructs a response object as follows:
   * <br>
   * if {@code customers} is {@code null}, then the response code is 204
   * <br>
   * if {@code customers} is not {@code null}, then the response code is 200
   * @param response
   * @param customers
   * @return
   */
  private Response getResponse(Response response, ArrayList<Customer> customers) {
    if (customers != null) {
      response.setCode(200);
      response.setCustomers(customers);
      response.setMessage("OK");
    } else {
      response.setCode(204);
      response.setCustomers(null);
      response.setMessage("No Customers found in Database");
    }

    return response;
  }
}
