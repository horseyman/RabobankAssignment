package nl.rabobank.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends MongoRepository<AccountDocument, String> {
    AccountDocument findByNumber(String number);
}
