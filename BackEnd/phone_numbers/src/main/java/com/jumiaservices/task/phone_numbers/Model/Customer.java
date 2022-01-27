package com.jumiaservices.task.phone_numbers.Model;

/**
 * The Customer class represents the model of a customer
 * in the database
 *
 * @author  AbdAlMoniem AlHifnawy
 * @version 1.0
 * @since   Jan 22nd 2022
 */
public class Customer {
  /**
   * the ID of a customer
   */
  private int ID;

  /**
   * the name of a customer
   */
  private String name;

  /**
   * the phone number of a customer
   */
  private String phone;

  /**
   * Creates a new Customer with no data associated
   */
  public Customer() {}

  /**
   * Creates a new customer with the following data:
   *
   * @param ID the customer's ID
   * @param name the customer's name
   * @param phone the customer's phone
   */
  public Customer(int ID, String name, String phone) {
    this.ID = ID;
    this.name = name;
    this.phone = phone;
  }

  /**
   *
   * @return int - the ID of this customer
   */
  public int getID() {
    return ID;
  }

  /**
   *
   * @param ID the ID to set to this customer
   */
  public void setID(int ID) {
    this.ID = ID;
  }

  /**
   *
   * @return String - the name of this customer
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @param name the name to set to this customer
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   *
   * @return String - the phone number of this customer
   */
  public String getPhone() {
    return phone;
  }

  /**
   *
   * @param phone the phone number to set to this customer
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }
}
