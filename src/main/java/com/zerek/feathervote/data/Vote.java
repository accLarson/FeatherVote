package com.zerek.feathervote.data;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;

@CompositePK({ "mojang_uuid", "year_month" })
public class Vote extends Model {
    static {
        validatePresenceOf("mojang_uuid", "year_month").message(
                "one or more composite PK's missing.");
    }
}
