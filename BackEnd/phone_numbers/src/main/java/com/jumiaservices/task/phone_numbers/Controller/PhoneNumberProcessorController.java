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

  /** database interface object for sending queries to the database */
  DBInterface dbi = new DBInterface();

  /**
   * This method is used to return all customers stored in the database without filtering or
   * pagination
   *
   * @return Response - a response object with the result of the query database
   * @see Response
   */
  @RequestMapping(
      value = {
        "getNumbers",
        "getNumbers/{page}",
        "getNumbers/byFilter/{filter}",
        "getNumbers/byFilter/{filter}/{page}"
      },
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Response getNumbers(
      @PathVariable(required = false) Integer page, @PathVariable(required = false) String filter) {
    Response response;
    // Response response = dbi.getCustomers(page);

    if (filter != null) {
      switch (filter) {
        case "valid":
          response = dbi.getCustomersByFilter(page, Filter.VALID);
          break;
        case "invalid":
          response = dbi.getCustomersByFilter(page, Filter.INVALID);
          break;
        case "cameroon":
          response = dbi.getCustomersByFilter(page, Filter.CAMEROON);
          break;
        case "ethiopia":
          response = dbi.getCustomersByFilter(page, Filter.ETHIOPIA);
          break;
        case "morocco":
          response = dbi.getCustomersByFilter(page, Filter.MOROCCO);
          break;
        case "mozambique":
          response = dbi.getCustomersByFilter(page, Filter.MOZAMBIQUE);
          break;
        case "uganda":
          response = dbi.getCustomersByFilter(page, Filter.UGANDA);
          break;
        default:
          response = null;
          break;
      }
    } else {
      response = dbi.getCustomers(page);
    }

    if (response.getData() != null) {
      response.setCode(200);
      response.setMessage("OK");
    } else {
      response.setCode(204);
      response.setMessage("No Customers found in Database");
    }

    return response;
  }
}
