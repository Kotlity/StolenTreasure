package net.peakgames.ginrummyplu;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class FinalUrlEntity {

    @Id
    private String finalUrlValue;

    @Generated(hash = 1134924499)
    public FinalUrlEntity(String finalUrlValue) {
        this.finalUrlValue = finalUrlValue;
    }

    @Generated(hash = 252583676)
    public FinalUrlEntity() {
    }

    public String getFinalUrlValue() {
        return this.finalUrlValue;
    }

    public void setFinalUrlValue(String finalUrlValue) {
        this.finalUrlValue = finalUrlValue;
    }
}
