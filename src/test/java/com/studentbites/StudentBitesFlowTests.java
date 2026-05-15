package com.studentbites;

import com.studentbites.model.OrderStatus;
import com.studentbites.model.StudentOrder;
import com.studentbites.repository.StudentOrderRepository;
import com.studentbites.repository.AppUserRepository;
import com.studentbites.service.AuthService;
import com.studentbites.service.CartService;
import com.studentbites.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
@org.springframework.test.context.TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:studentbites-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class StudentBitesFlowTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentOrderRepository orders;

    @Autowired
    private AppUserRepository users;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Test
    void cartCanAddItemAndOpenCart() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/cart/add/1").session(session).header("Referer", "http://localhost/menu"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/menu"));

        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void homePageRendersWithoutTableLinks() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("StudentBites")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/tables"))));
    }

    @Test
    void guestCannotCheckoutWithoutLogin() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/cart/add/1").session(session).header("Referer", "http://localhost/menu"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/checkout")
                        .session(session)
                        .param("studentName", "Guest Student")
                        .param("email", "guest@example.com")
                        .param("phone", "5555555555")
                        .param("hostelOrClass", "Hostel A")
                        .param("orderMode", "Pickup")
                        .param("paymentMode", "UPI"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertThat(orders.findTop8ByEmailIgnoreCaseOrderByCreatedAtDesc("guest@example.com")).isEmpty();
    }

    @Test
    void signupKeepsEmailUniqueAndLoginWorks() throws Exception {
        MockHttpSession signupSession = new MockHttpSession();

        mockMvc.perform(post("/signup")
                        .session(signupSession)
                        .param("fullName", "Unique Student")
                        .param("email", "Unique.Student@Example.com ")
                        .param("phone", "5555555555")
                        .param("hostelOrClass", "Hostel C")
                .param("password", "secret123"))
        .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertThat(users.findByEmailIgnoreCase("unique.student@example.com")).isPresent();
        assertThat(signupSession.getAttribute(AuthService.USER_EMAIL)).isEqualTo("unique.student@example.com");

        mockMvc.perform(post("/signup")
                        .param("fullName", "Duplicate Student")
                        .param("email", "UNIQUE.STUDENT@example.com")
                        .param("phone", "6666666666")
                        .param("hostelOrClass", "Hostel D")
                        .param("password", "secret123"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("An account already exists for this email")));

        assertThat(users.findAll().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase("unique.student@example.com"))
                .count()).isEqualTo(1);

        MockHttpSession loginSession = new MockHttpSession();
        mockMvc.perform(post("/login")
                        .session(loginSession)
                        .param("email", " UNIQUE.STUDENT@example.com ")
                .param("password", " secret123 "))
        .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertThat(loginSession.getAttribute(AuthService.USER_NAME)).isEqualTo("Unique Student");
        assertThat(loginSession.getAttribute(AuthService.USER_EMAIL)).isEqualTo("unique.student@example.com");
    }

    @Test
    void loggedInStudentOrderAppearsInTheirTracker() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUserName", "Food Student");
        session.setAttribute("currentUserEmail", "food@example.com");
        session.setAttribute("currentUserPhone", "5555555555");
        session.setAttribute("currentUserHostel", "Hostel B");

        mockMvc.perform(post("/cart/add/1").session(session).header("Referer", "http://localhost/menu"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/checkout")
                        .session(session)
                        .param("studentName", "Wrong Name")
                        .param("email", "wrong@example.com")
                        .param("phone", "1111111111")
                        .param("hostelOrClass", "")
                        .param("orderMode", "Pickup")
                        .param("paymentMode", "UPI"))
                .andExpect(status().is3xxRedirection());

        var studentOrders = orders.findTop8ByEmailIgnoreCaseOrderByCreatedAtDesc("food@example.com");
        assertThat(studentOrders).isNotEmpty();
        assertThat(studentOrders.get(0).getStudentName()).isEqualTo("Food Student");

        mockMvc.perform(get("/track").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void orderStatusProgressesAutomaticallyFromCreatedTime() {
        StudentOrder order = new StudentOrder();
        order.setStudentName("Progress Student");
        order.setEmail("progress@example.com");
        order.setPhone("5555555555");
        order.setOrderMode("Pickup");
        order.setPaymentMode("UPI");
        order.setPaymentReference("SIM-TEST");
        order.setTotal(BigDecimal.valueOf(99));
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now().minusMinutes(4));

        StudentOrder saved = orders.save(order);
        StudentOrder refreshed = orderService.refreshStatus(saved);

        assertThat(refreshed.getStatus()).isEqualTo(OrderStatus.READY);

        refreshed.setCreatedAt(LocalDateTime.now().minusMinutes(7));
        refreshed.setStatus(OrderStatus.PREPARING);
        StudentOrder delivered = orderService.refreshStatus(orders.save(refreshed));

        assertThat(delivered.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void cartSeparatesGuestAndLoggedInStudentData() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/cart/add/1").session(session).header("Referer", "http://localhost/menu"))
                .andExpect(status().is3xxRedirection());
        assertThat(cartService.count(session)).isEqualTo(1);

        cartService.clearGuestCart(session);
        session.setAttribute(AuthService.USER_NAME, "Cart Student");
        session.setAttribute(AuthService.USER_EMAIL, "cart@example.com");
        session.setAttribute(AuthService.USER_PHONE, "5555555555");
        assertThat(cartService.count(session)).isZero();

        mockMvc.perform(post("/cart/add/2").session(session).header("Referer", "http://localhost/menu"))
                .andExpect(status().is3xxRedirection());
        assertThat(cartService.count(session)).isEqualTo(1);

        session.removeAttribute(AuthService.USER_NAME);
        session.removeAttribute(AuthService.USER_EMAIL);
        session.removeAttribute(AuthService.USER_PHONE);
        assertThat(cartService.count(session)).isZero();
    }
}
