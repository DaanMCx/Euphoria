package nl.daanmc.euphoria.util;

import net.minecraft.tileentity.TileEntity;

public interface ICannabis {
    float getSativa();
    void setSativa(float v);

    float getIndica();
    void setIndica(float v);

    boolean getAutoFlower();
    void setAutoFlower(boolean v);

    default ICannabis set(float sativa, float indica, boolean autoFlower) {
        this.setSativa(sativa);
        this.setIndica(indica);
        this.setAutoFlower(autoFlower);
        return this;
    }

    default ICannabis copyFromStrain(ICannabis old) {
        this.setSativa(old.getSativa());
        this.setIndica(old.getIndica());
        this.setAutoFlower(old.getAutoFlower());
        return this;
    }

    default ICannabis mutateFromStrain(ICannabis old, float maxMutation) {
        this.setSativa(old.getSativa()+(float)(2*Math.random()-1)*maxMutation);
        this.setIndica(old.getIndica()+(float)(2*Math.random()-1)*maxMutation);
        this.setAutoFlower(old.getAutoFlower());
        return this;
    }

    default ICannabis crossStrains(ICannabis strain1, ICannabis strain2) {
        this.setSativa((strain1.getSativa()+strain2.getSativa())/2);
        this.setIndica((strain1.getIndica()+strain2.getIndica())/2);
        this.setAutoFlower(strain1.getAutoFlower() || strain2.getAutoFlower());
        return this;
    }

    default float getSativaPerc() {
        return (getSativa()/(getSativa()+getIndica()))/100;
    }

    default float getIndicaPerc() {
        return 100-getSativaPerc();
    }

    class Strain extends TileEntity {
        public float sativa;
        public float indica;
        public boolean autoFlower;
        public Strain(float sativa, float indica, boolean autoFlower) {
            this.sativa=sativa;
            this.indica=indica;
            this.autoFlower=autoFlower;
        }
    }
}