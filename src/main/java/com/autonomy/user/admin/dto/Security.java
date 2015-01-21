package com.autonomy.user.admin.dto;

import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import lombok.Getter;

@IdolDocument("responsedata")
public class Security {

    @Getter
    private boolean authenticated;

    @IdolField("autn:authenticate")
    public void setAuthenticated(final boolean authenticated) {
        this.authenticated = authenticated;
    }
}