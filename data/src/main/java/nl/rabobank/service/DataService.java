package nl.rabobank.service;


import java.util.List;
import nl.rabobank.mongo.AccountDocument;
import nl.rabobank.mongo.AccountRepository;
import nl.rabobank.mongo.AttorneyDocument;
import nl.rabobank.mongo.AttorneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService
{
    @Autowired
    AttorneyRepository attorneyRepository;

    @Autowired
    AccountRepository accountRepository;

    public DataService() {

    }

    public AccountRepository getAccountRepository() {
        return accountRepository;
    }


    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AttorneyRepository getAttorneyRepository() {
        return attorneyRepository;
    }

    public void setAttorneyRepository(AttorneyRepository attorneyRepository) {
        this.attorneyRepository = attorneyRepository;
    }

    public List<AttorneyDocument> findAttorneysByGrantee(String grantee){
        return this.attorneyRepository.findAttorneysByGrantee(grantee);
    }

    public AccountDocument findByNumber(String accountNumber) {
        return this.accountRepository.findByNumber(accountNumber);
    }

    public void saveAttorney(AttorneyDocument attorney) {
        this.attorneyRepository.save(attorney);
    }
}

