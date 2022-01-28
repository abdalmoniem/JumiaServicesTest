package com.hifnawy.subtitle.jumiastaskphonenumberviewer.model;

import java.util.ArrayList;

/**
 * The Response class represents a REST Response with the following data:
 * <br>
 * {@link #code} - response code
 * <br>
 * {@link #message} - response message
 * <br>
 * {@link #customers} - response data
 *
 * @author AbdAlMoniem AlHifnawy
 * @version 1.0
 * @since Jan 28th 2022
 */
public class Response {
  /**
   * response data
   */
  ArrayList<Customer> customers;

  /**
   * response message
   */
  String message;

  /**
   * response code
   */
  int code;

  /**
   *
   * @return ArrayList&lt;{@link Customer}&gt; - get response data
   */
  public ArrayList<Customer> getCustomers() {
    return customers;
  }

  /**
   *
   * @param customers - set response data
   */
  public void setCustomers(ArrayList<Customer> customers) {
    this.customers = customers;
  }

  /**
   *
   * @return String - get response message
   */
  public String getMessage() {
    return message;
  }

  /**
   *
   * @param message - set response message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   *
   * @return int - get response code
   */
  public int getCode() {
    return code;
  }

  /**
   *
   * @param code - set response code
   */
  public void setCode(int code) {
    this.code = code;
  }
}
