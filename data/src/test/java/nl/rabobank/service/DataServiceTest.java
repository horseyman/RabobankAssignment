package nl.rabobank.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import nl.rabobank.mongo.AccessPermission;
import nl.rabobank.mongo.AccountDocument;
import nl.rabobank.mongo.AccountRepository;
import nl.rabobank.mongo.AccountType;
import nl.rabobank.mongo.AttorneyDocument;
import nl.rabobank.mongo.AttorneyRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SEE:
 * https://jschmitz.dev/2021/07/14/how_to_test_the_data_layer_of_your_spring_boot_application_with_dataMongotest/
 * http://www.zakariaamine.com/2018-11-11/testing-spring-custom-mongo-port
 *
 *  This is not really a unit-test on the DataService, since the repositories are not mocked.
 *  Instead, we want to use the Embedded Mongo Database, which is why we annotate the
 *  class using @DataMongoTest.
 *
 *  The use of @SpyBean on the mongo-reposities may fail due to:
 *  https://github.com/spring-projects/spring-boot/issues/7033
 *
 *  I circumvented that using the "initializeDataService" method.
 */

@RunWith(SpringRunner.class)
@DataMongoTest
public class DataServiceTest
{
    private static Logger   logger  = LoggerFactory.getLogger(DataServiceTest.class);

    @Autowired
    MongoTemplate   mongoTemplate;

    //@SpyBean
    @Autowired
    AccountRepository   accountRepository;

    //@SpyBean
    @Autowired
    AttorneyRepository  attorneyRepository;

    @InjectMocks
    DataService dataService;

    public DataServiceTest() {

    }

    @Test
    public void testAccountStorage() {
        AccountDocument account = new AccountDocument();
        account.setBalance(9844.50);
        account.setNumber("9349459340");
        account.setType(AccountType.SAVINGS);
        account.setHolder("Wally Whale");

        AttorneyDocument attorney    = new AttorneyDocument();
        attorney.setGrantor("DonaldDuck");
        attorney.setGrantee("MickyMouse");
        attorney.setAccount(account);
        attorney.setAccessPermission(AccessPermission.WRITE);

        this.mongoTemplate.save(account);
        this.mongoTemplate.save(attorney);
        logger.warn("Account was saved, with ID " + account.getId());
        logger.warn("Attorney was saved, with ID " + attorney.getId());

        AccountDocument foundAccount = this.accountRepository.findByNumber("9349459340");
        assertNotNull(foundAccount);
        assertEquals(foundAccount.getHolder(), account.getHolder());

        List<AttorneyDocument> attorneys = this.attorneyRepository.findAttorneysByGrantee("MickyMouse");
        assertEquals(attorneys.size(), 1);
        assertEquals(attorneys.get(0).getAccount().getId(), foundAccount.getId());
        assertEquals(attorneys.get(0).getAccount().getHolder(), foundAccount.getHolder());
    }

    /**
     * Initialized the DataService, since its repositories are not mocked
     * but autowired.
     * Can they be both??
     */

    public void initializeDataService() {
        this.dataService.setAccountRepository(this.accountRepository);
        this.dataService.setAttorneyRepository(this.attorneyRepository);
    }

    @Test
    public void testDataService() {
        this.initializeDataService();

        AccountDocument account = new AccountDocument();
        account.setBalance(9844.50);
        account.setNumber("9349459340");
        account.setType(AccountType.SAVINGS);
        account.setHolder("Wally Whale");

        AttorneyDocument attorney    = new AttorneyDocument();
        attorney.setGrantor("DonaldDuck");
        attorney.setGrantee("MickyMouse");
        attorney.setAccount(account);
        attorney.setAccessPermission(AccessPermission.WRITE);

        this.mongoTemplate.save(account);
        this.mongoTemplate.save(attorney);
        logger.warn("Account was saved, with ID " + account.getId());
        logger.warn("Attorney was saved, with ID " + attorney.getId());

        AccountDocument foundAccount = this.dataService.findByNumber("9349459340");
        assertNotNull(foundAccount);
        assertEquals(foundAccount.getHolder(), account.getHolder());

        List<AttorneyDocument> attorneys = this.dataService.findAttorneysByGrantee("MickyMouse");
        assertEquals(attorneys.size(), 1);
        assertEquals(attorneys.get(0).getAccount().getId(), foundAccount.getId());
        assertEquals(attorneys.get(0).getAccount().getHolder(), foundAccount.getHolder());
    }

    /**
     * Clean up the database after each test.
     */

    @After
    public void cleanUpDatabase() {
        mongoTemplate.getDb().drop();
    }
}

