package com.jumiaservices.task.phone_numbers;

import com.jumiaservices.task.phone_numbers.Controller.DBInterface;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PhoneNumberProcessorControllerTest extends PhoneNumbersApplicationTests {

  @Autowired private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @Before
  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  @DisplayName("Test Get All Numbers")
  public void testGetNumbers() throws Exception {
    mockMvc
        .perform(get("/getNumbers"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data", hasSize(41)))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.pagesLeft").value(0));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 4, 5})
  public void testGetNumbersWithPagination(int page) throws Exception {
    mockMvc
        .perform(get("/getNumbers/" + page))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data", hasSize(lessThanOrEqualTo(10))))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(
            jsonPath("$.pagesLeft")
                .value(
                    allOf(
                        greaterThanOrEqualTo(0),
                        lessThanOrEqualTo((int) Math.ceil(41.0 / 10.0) - 1))));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, 7})
  public void testGetNumbersWithPaginationNegative(int page) throws Exception {
    mockMvc
        .perform(get("/getNumbers/" + page))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data", hasSize(lessThanOrEqualTo(10))))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(
            jsonPath("$.pagesLeft")
                .value(
                    anyOf(
                        lessThanOrEqualTo(0),
                        greaterThanOrEqualTo((int) Math.ceil(41.0 / 10.0) - 1))));
  }

  @ParameterizedTest
  @EnumSource(value = DBInterface.Filter.class)
  public void testNumbersWithFilter(DBInterface.Filter filter) throws Exception {
    mockMvc
        .perform(get("/getNumbers/byFilter/" + filter.toString().toLowerCase()))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data", hasSize(lessThanOrEqualTo(41))))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(
            jsonPath("$.pagesLeft")
                .value(
                    allOf(
                        greaterThanOrEqualTo(0),
                        lessThanOrEqualTo((int) Math.ceil(41.0 / 10.0) - 1))));
  }

  @ParameterizedTest
  @EnumSource(value = DBInterface.Filter.class)
  public void testNumbersWithFilterWithPagination(DBInterface.Filter filter) throws Exception {
    int[] pageValues = {0, 1, 4, 5};

    for (int page : pageValues) {
      mockMvc
          .perform(
              get(
                  String.format(
                      "/getNumbers/byFilter/%s/%d", filter.toString().toLowerCase(), page)))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$.data").isArray())
          .andExpect(jsonPath("$.data", hasSize(lessThanOrEqualTo(41))))
          .andExpect(jsonPath("$.message").value("OK"))
          .andExpect(jsonPath("$.code").value(200))
          .andExpect(
              jsonPath("$.pagesLeft")
                  .value(
                      allOf(
                          greaterThanOrEqualTo(0),
                          lessThanOrEqualTo((int) Math.ceil(41.0 / 10.0) - 1))));

      System.out.println("page=" + page);
    }
  }

  @ParameterizedTest
  @EnumSource(value = DBInterface.Filter.class)
  public void testNumbersWithFilterWithPaginationNegative(DBInterface.Filter filter)
      throws Exception {
    int[] pageValues = {-1, 0, 7};

    for (int page : pageValues) {
      mockMvc
          .perform(
              get(
                  String.format(
                      "/getNumbers/byFilter/%s/%d", filter.toString().toLowerCase(), page)))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$.data").doesNotExist())
          .andExpect(jsonPath("$.message").value("OK"))
          .andExpect(jsonPath("$.code").value(200))
          .andExpect(
              jsonPath("$.pagesLeft")
                  .value(
                      allOf(
                          greaterThanOrEqualTo(0),
                          lessThanOrEqualTo((int) Math.ceil(41.0 / 10.0) - 1))));

      System.out.println("page=" + page);
    }
  }
}
