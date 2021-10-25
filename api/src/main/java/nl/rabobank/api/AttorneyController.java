package nl.rabobank.api;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.AccessPermission;
import nl.rabobank.mongo.AccountDocument;
import nl.rabobank.mongo.AttorneyDocument;
import nl.rabobank.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example to make a Controller using OpenId authentication
 * <p>
 * https://hellokoding.com/spring-security-oauth2-and-openid-connect-in-spring-boot/
 */

@RestController()
@RequestMapping("/attorney")
public class AttorneyController {

    private static Logger logger = LoggerFactory.getLogger(AttorneyController.class);

    @Autowired
    DataService dataService;

    @Autowired
    ConversionService conversionService;

    /**
     * Find the PowerOfAttorneys that are granted to the authenticated user,
     * i.e. for which the authenticated user is the grantee.
     *
     * @param principal
     * @return
     */

    @GetMapping
    public List<PowerOfAttorney> grantedPowerOfAttorneys(Principal principal) {
        String username = principal.getName();
        List<AttorneyDocument> attorneys = this.dataService.findAttorneysByGrantee(username);
        return attorneys.stream()
                .map(it -> conversionService.convert(it, PowerOfAttorney.class))
                .collect(Collectors.toList());
    }

    /**
     * Check that the grantor is the accountHolder of the account.
     * Also validate the AttorneyRequest
     *
     * @param principal
     * @param request
     * @return
     */

    @PostMapping
    public ResponseEntity grantTo(Principal principal, @Valid @RequestBody AttorneyRequest request) {
        String username = principal.getName();

        String granteeName = request.getGrantee();
        String accountNumber = request.getAccount();
        Authorization authorization = request.getAuthorization();

        logger.debug("Find account number " + accountNumber);

        AccountDocument mongoAccount = this.dataService.findByNumber(accountNumber);
        if (mongoAccount != null) {
            if (mongoAccount.getHolder().equals(username)) {
                AttorneyDocument attorney = new AttorneyDocument();
                attorney.setGrantor(username);
                attorney.setGrantee(granteeName);
                attorney.setAccount(mongoAccount);
                attorney.setAccessPermission(AccessPermission.valueOf(authorization.name()));
                this.dataService.saveAttorney(attorney);
                PowerOfAttorney powerOfAttorney = this.conversionService.convert(attorney, PowerOfAttorney.class);
                return ResponseEntity.status(200).body(powerOfAttorney);
            } else {
                // return a 401: unauthorized
                logger.error("=> Account doesn't belong to accountHolder");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("account doesn't belong to grantor");
            }
        } else {
            // return a 400: bad request (?)
            logger.error("=> Account not found!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("account not found");
        }
    }

}

