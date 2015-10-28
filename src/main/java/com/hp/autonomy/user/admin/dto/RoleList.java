/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user.admin.dto;

import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading a list of roles out of Community
 */
@IdolDocument(value = "responsedata")
public class RoleList {

    /**
     * @return The list of role names
     */
    @Getter
    private final List<String> roles = new ArrayList<>();

    @IdolField("autn:role")
    void setRolesProducts(final String role){
        this.roles.add(role);
    }
}