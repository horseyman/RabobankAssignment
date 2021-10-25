package nl.rabobank.converter;


import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.AccountDocument;
import nl.rabobank.mongo.AccountType;
import nl.rabobank.mongo.AttorneyDocument;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert MongoDB Attorneys to Domain PowerOfAttorney
 *
 * This converter is registered in the WebConfiguration,
 * so it can be invoked by means of the ConversionService.
 */

public class AttorneyConverter implements Converter<AttorneyDocument, PowerOfAttorney> {

    @Override
    public PowerOfAttorney convert(AttorneyDocument attorney) {
        PowerOfAttorney.PowerOfAttorneyBuilder builder = PowerOfAttorney.builder();
        builder.granteeName(attorney.getGrantee());
        builder.grantorName(attorney.getGrantor());
        builder.authorization(Authorization.valueOf(attorney.getAccessPermission().name()));
        builder.account(convertAccount(attorney.getAccount()));
        return builder.build();
    }

    public Account convertAccount(AccountDocument account) {
        AccountType accountType = account.getType();
        switch (accountType) {
            case SAVINGS: return new SavingsAccount(account.getNumber(), account.getHolder(), account.getBalance());
            case PAYMENT: return new PaymentAccount(account.getNumber(), account.getHolder(), account.getBalance());
            default: throw new IllegalArgumentException("unexpected account type");
        }
    }
}
