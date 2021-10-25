package nl.rabobank.mongo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttorneyRepository extends MongoRepository<AttorneyDocument, String> {
    List<AttorneyDocument> findAttorneysByGrantee(String grantee);
}
