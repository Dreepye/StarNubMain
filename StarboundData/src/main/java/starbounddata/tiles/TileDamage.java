package starbounddata.tiles;

import lombok.Getter;
import lombok.Setter;

public class TileDamage {

    public enum TileDamageType {
        NONE,
        PLANTISH,
        BLOCKISH,
        BEAMISH,
        EXPLOSIVE,
        FIRE,
        TILLING,
        CRUSHING,
    }

    @Getter @Setter
    private TileDamageType tileDamageType;

    @Getter @Setter
    private float amount;

    @Getter @Setter
    private int harvestLevel;

    public TileDamage(TileDamageType tileDamageType, float amount) {
        this.tileDamageType = tileDamageType;
        this.amount = amount;
        this.harvestLevel = 1;
    }


}

