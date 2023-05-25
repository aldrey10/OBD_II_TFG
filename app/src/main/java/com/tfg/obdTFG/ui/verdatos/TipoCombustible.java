package com.tfg.obdTFG.ui.verdatos;

import java.util.HashMap;
import java.util.Map;

public enum TipoCombustible {
    GASOLINE(0x01, "Gasolina"),
    METHANOL(0x02, "Metanol"),
    ETHANOL(0x03, "Etanol"),
    DIESEL(0x04, "Diesel"),
    LPG(0x05, "GPL/LGP"),
    CNG(0x06, "Gas Natural"),
    PROPANE(0x07, "Propano"),
    ELECTRIC(0x08, "Electrico"),
    BIFUEL_GASOLINE(0x09, "Biodiesel + Gasolina"),
    BIFUEL_METHANOL(0x0A, "Biodiesel + Metanol"),
    BIFUEL_ETHANOL(0x0B, "Biodiesel + Etanol"),
    BIFUEL_LPG(0x0C, "Biodiesel + GPL/LGP"),
    BIFUEL_CNG(0x0D, "Biodiesel + Gas Natural"),
    BIFUEL_PROPANE(0x0E, "Biodiesel + Propano"),
    BIFUEL_ELECTRIC(0x0F, "Biodiesel + Electrico"),
    BIFUEL_GASOLINE_ELECTRIC(0x10, "Biodiesel + Gasolina/Electrico"),
    HYBRID_GASOLINE(0x11, "Hibrido Gasolina"),
    HYBRID_ETHANOL(0x12, "Hibrido Etanol"),
    HYBRID_DIESEL(0x13, "Hibrido Diesel"),
    HYBRID_ELECTRIC(0x14, "Hibrido Electrico"),
    HYBRID_MIXED(0x15, "Hibrido Mixto"),
    HYBRID_REGENERATIVE(0x16, "Hibrido Regenerativo");

    private static Map<Integer, TipoCombustible> map = new HashMap<>();

    static {
        for (TipoCombustible error : TipoCombustible.values())
            map.put(error.getValue(), error);
    }

    private final int value;
    private final String description;

    private TipoCombustible(final int value, final String description) {
        this.value = value;
        this.description = description;
    }



    public static TipoCombustible fromValue(final int value) {
        return map.get(value);
    }

    //devuelve el valor
    public int getValue() {
        return value;
    }

    //devuelve el nombre del tipo de combustible
    public String getDescription() {
        return description;
    }
}
