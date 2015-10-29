/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoles implements Serializable {

    private static final long serialVersionUID = 7516101375412437377L;

    /**
     * @return The name of the user
     * @serial The name of the user
     */
    private String username;

    /**
     * @return The uid of the user
     * @serial The uid of the user
     */
    private long uid;

    /**
     * @return The list of roles the user has
     * @serial The list of the user's roles
     */
    private List<String> roles = new ArrayList<>();

    public UserRoles(final String username){
        this.username = username;
    }
}