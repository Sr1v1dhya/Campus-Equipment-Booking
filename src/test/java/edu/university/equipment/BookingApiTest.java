
package edu.university.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void positiveQuantityReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "borrowerId": "STU1001",
                          "equipmentId": "LAPTOP01",
                          "quantity": 1
                        }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    void zeroQuantityReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "borrowerId": "STU1001",
                          "equipmentId": "LAPTOP01",
                          "quantity": 0
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void negativeQuantityReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "borrowerId": "STU1001",
                          "equipmentId": "LAPTOP01",
                          "quantity": -1
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}
