package test.rvp.muzenly;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class Contacts extends RealmObject  implements RealmModel {


    private String name;
    private String number;


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
