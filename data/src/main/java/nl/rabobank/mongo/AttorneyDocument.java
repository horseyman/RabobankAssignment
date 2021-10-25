package nl.rabobank.mongo;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "attorney")
public class AttorneyDocument {
    @Id
    @Field("_id")
    private ObjectId id = null;


    private String grantor;

    /**
     * We will search Attorneys based on grantee.
     */

    @Indexed
    private String grantee;

    @DBRef
    private AccountDocument account;

    private AccessPermission accessPermission;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getGrantor() {
        return grantor;
    }

    public void setGrantor(String grantor) {
        this.grantor = grantor;
    }

    public String getGrantee() {
        return grantee;
    }

    public void setGrantee(String grantee) {
        this.grantee = grantee;
    }

    public AccountDocument getAccount() {
        return account;
    }

    public void setAccount(AccountDocument account) {
        this.account = account;
    }

    public AccessPermission getAccessPermission() {
        return accessPermission;
    }

    public void setAccessPermission(AccessPermission accessPermission) {
        this.accessPermission = accessPermission;
    }
}
