package nl.rabobank.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.AccountDocument;
import nl.rabobank.mongo.AccountType;
import nl.rabobank.service.DataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AttorneyController.class)
public class AttorneyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DataService dataService;

    /**
     * Can we really get an Access-token within the scope of the Unit-tests?
     * SEE: https://www.baeldung.com/oauth-api-testing-with-spring-mvc
     *
     * I'm not using these ideas, yet.
     */

    private String obtainAccessToken() {
        return "aapnootmies";
    }

    /**
     * Invoke the Controller grantedPowerOfAttorneys method.
     * @return
     * @throws JsonProcessingException
     */

    private MockHttpServletRequestBuilder findGrantedPermissions() throws JsonProcessingException {
        return get("/attorney")
                //.header("Authorization", "Bearer " + this.obtainAccessToken())
                .contentType("application/json")
                .with(csrf());
    }

    /**
     * Invokes the controller grantTo method, to create a PowerOfAttorney
     * @param attorneyRequest
     * @return
     * @throws JsonProcessingException
     */

    private MockHttpServletRequestBuilder grantAttorney(AttorneyRequest attorneyRequest) throws JsonProcessingException {
        return post("/attorney")
                //.header("Authorization", "Bearer " + this.obtainAccessToken())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(attorneyRequest))
                .with(csrf());
    }

    @WithMockUser(value = "marchorstman")
    @Test
    public void testValidInput_accountNotFound() throws Exception {
        when(dataService.findByNumber("account")).thenReturn(null);

        AttorneyRequest attorneyRequest = new AttorneyRequest("grantee", "account", Authorization.READ);

        mockMvc.perform(grantAttorney(attorneyRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("account not found"));
    }


    @WithMockUser(value = "marchorstman")
    @Test
    public void testValidInput_accountFound() throws Exception {
        AccountDocument account = mock(AccountDocument.class);
        when(account.getHolder()).thenReturn("marchorstman");
        when(account.getBalance()).thenReturn(9876.54);
        when(account.getNumber()).thenReturn("RABO21AAP904343");
        when(account.getType()).thenReturn(AccountType.PAYMENT);

        PaymentAccount paymentAccount = new PaymentAccount("RABO21AAP904343", "marchorstman", 9876.54);

        when(dataService.findByNumber("account")).thenReturn(account);

        AttorneyRequest attorneyRequest = new AttorneyRequest("grantee", "account", Authorization.READ);

        // Create the expected outcome!
        PowerOfAttorney.PowerOfAttorneyBuilder builder = PowerOfAttorney.builder();
        builder.granteeName("grantee");
        builder.grantorName("marchorstman");
        builder.authorization(Authorization.READ);
        builder.account(paymentAccount);
        PowerOfAttorney powerOfAttorney = builder.build();

        mockMvc.perform(grantAttorney(attorneyRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(powerOfAttorney)));
    }

    @WithMockUser(value = "marchorstman")
    @Test
    public void testAttorneyRequestWithOtherAccountHolder() throws Exception {
        AccountDocument account = mock(AccountDocument.class);
        when(account.getHolder()).thenReturn("pietPiraat");
        when(account.getBalance()).thenReturn(9876.54);
        when(account.getNumber()).thenReturn("RABO21AAP904343");
        when(account.getType()).thenReturn(AccountType.PAYMENT);

        PaymentAccount paymentAccount = new PaymentAccount("RABO21AAP904343", "marchorstman", 9876.54);

        when(dataService.findByNumber("account")).thenReturn(account);

        AttorneyRequest attorneyRequest = new AttorneyRequest("grantee", "account", Authorization.READ);

        mockMvc.perform(grantAttorney(attorneyRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("account doesn't belong to grantor"));
    }

    /**
     * The RabobankExceptionHandler is used, that
     * handles the MethodArgumentNotValidException
     */

    @Test
    public void testInvalidInput() throws Exception {
        AttorneyRequest attorneyRequest = new AttorneyRequest("grantee", null, Authorization.READ);

        mockMvc.perform(grantAttorney(attorneyRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("specify an account number"));
    }
}
