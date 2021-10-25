package nl.rabobank.service;

import nl.rabobank.mongo.MongoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Required to run a the DataServiceTest, which uses SpringRunner.
 */

@SpringBootApplication(scanBasePackages = {"nl.rabobank.mongo"})
@Import(MongoConfiguration.class)
public class TestApplication {
}
