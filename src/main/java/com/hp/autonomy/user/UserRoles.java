/*
 * (c) Copyright 2013-2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.user;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A username with a uid and a list of roles
 */
@SuppressWarnings({"WeakerAccess", "JavaDoc", "unused"})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoles implements Serializable {

    private static final long serialVersionUID = 7516101375412437377L;

    /**
     * @serial The name of the user
     */
    private String username;

    /**
     * @serial The uid of the user
     */
    private long uid;

    /**
     * @serial The user security string to add to requests (unescaped)
     */
    private String securityInfo;

    /**
     * @serial The list of the user's roles
     */
    @SuppressWarnings("FieldMayBeFinal")
    private List<String> roles = new ArrayList<>();

    /**
     * @serial The map of user properties
     */
    @SuppressWarnings("FieldMayBeFinal")
    private Map<String, String> fields = new LinkedHashMap<>();

    public UserRoles(final String username){
        this.username = username;
    }
}
