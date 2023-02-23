package net.peakgames.ginrummyplu;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ADBEntity {

    @Id
    private String adbValue;

    @Generated(hash = 844035572)
    public ADBEntity(String adbValue) {
        this.adbValue = adbValue;
    }

    @Generated(hash = 441914235)
    public ADBEntity() {
    }

    public String getAdbValue() {
        return this.adbValue;
    }

    public void setAdbValue(String adbValue) {
        this.adbValue = adbValue;
    }
}
