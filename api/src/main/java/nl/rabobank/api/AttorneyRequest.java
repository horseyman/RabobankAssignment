package nl.rabobank.api;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.authorizations.Authorization;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AttorneyRequest {

    @NotEmpty(message = "specify a non-empty grantee")
    private String grantee;

    @NotEmpty(message = "specify an account number")
    private String account;

    @NotNull(message = "specify the authorization")
    private Authorization authorization;

}