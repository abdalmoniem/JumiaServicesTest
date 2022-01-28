package com.jumiaservices.task.phone_numbers.Controller;

import com.jumiaservices.task.phone_numbers.Model.Customer;
import com.jumiaservices.task.phone_numbers.Model.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class DBInterfaceTest {

  DBInterface dbInterface;

  @BeforeEach
  void setUp() {
    dbInterface = new DBInterface();
  }

  @Test
  void getCustomers() {
    Response response = dbInterface.getCustomers(null);

    assertEquals(41, response.getData().size());
    assertEquals(0, response.getPagesLeft());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 4, 5})
  void getCustomers(int page) {
    Response response = dbInterface.getCustomers(page);

    assertThat(response.getData().size(), lessThanOrEqualTo(10));
    assertEquals(Math.ceil(41.0 / 10.0) - page, response.getPagesLeft());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, 7})
  void getCustomersNegative(int page) {
    Response response = dbInterface.getCustomers(page);

    assertNull(response.getData());
    assertEquals(0, response.getPagesLeft());
  }

  @ParameterizedTest
  @EnumSource(value = DBInterface.Filter.class)
  void getCustomersByFilter(DBInterface.Filter filter) {
    testGetCustomersByFilter(null, filter);
  }

  @ParameterizedTest
  @EnumSource(value = DBInterface.Filter.class)
  void getCustomersByFilterWithPagination(DBInterface.Filter filter) {
    int[] pageValues = {0, 1, 4, 5};

    for (int page : pageValues) {
      Response response = testGetCustomersByFilter(page, filter);

      assertThat(response.getData().size(), lessThanOrEqualTo(41));
      assertThat(
          response.getPagesLeft(),
          both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo((int) Math.ceil(41.0 / 10.0) - 1)));
    }
  }

  @ParameterizedTest
  @EnumSource(value = DBInterface.Filter.class)
  void getCustomersByFilterWithPaginationNegative(DBInterface.Filter filter) {
    int[] pageValues = {-1, 0, 7};

    for (int page : pageValues) {
      Response response = testGetCustomersByFilter(page, filter);

      assertNull(response.getData());
      assertThat(
          response.getPagesLeft(),
          both(greaterThanOrEqualTo(0)).and(lessThanOrEqualTo((int) Math.ceil(41.0 / 10.0) - 1)));
    }
  }

  private Response testGetCustomersByFilter(Integer page, DBInterface.Filter filter) {
    Response response = dbInterface.getCustomersByFilter(page, filter);

    assertNotNull(response.getData());
    assertThat(response.getData().size(), lessThanOrEqualTo(41));
    assertEquals(0, response.getPagesLeft());

    Pattern pattern;

    for (Customer customer : response.getData()) {
      switch (filter) {
        case VALID:
        case INVALID:
          pattern =
              Pattern.compile(
                  "\\((237\\)\\s?[2368]\\d{7,8}$)|(\\(251\\)\\s?[1-59]\\d{8}$)|(\\(212\\)\\s?[5-9]\\d{8}$)"
                      + "|(\\(258\\)\\s?[28]\\d{7,8}$)|(\\(256\\)\\s?\\d{9}$)");
          break;
        case CAMEROON:
          pattern = Pattern.compile("\\(237\\)\\s?[2368]\\d{7,8}$");
          break;
        case ETHIOPIA:
          pattern = Pattern.compile("\\(251\\)\\s?[1-59]\\d{8}$");
          break;
        case MOROCCO:
          pattern = Pattern.compile("\\(212\\)\\s?[5-9]\\d{8}$");
          break;
        case MOZAMBIQUE:
          pattern = Pattern.compile("\\(258\\)\\s?[28]\\d{7,8}$");
          break;
        case UGANDA:
          pattern = Pattern.compile("\\(256\\)\\s?\\d{9}$");
          break;
        default:
          pattern = null;
          break;
      }

      Matcher matcher = pattern.matcher(customer.getPhone());

      if (filter == DBInterface.Filter.INVALID) {
        assertFalse(matcher.matches());
      } else {
        assertTrue(matcher.matches());
      }
    }

    return response;
  }
}
