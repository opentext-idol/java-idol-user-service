/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user.dto;

import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import lombok.Getter;

/**
 * Class for reading a security response out of Community
 */
@IdolDocument("responsedata")
public class Security {

    /**
     * @return true if the user was authenticated; false otherwise
     */
    @Getter
    private boolean authenticated;

    @IdolField("autn:authenticate")
    public void setAuthenticated(final boolean authenticated) {
        this.authenticated = authenticated;
    }
}