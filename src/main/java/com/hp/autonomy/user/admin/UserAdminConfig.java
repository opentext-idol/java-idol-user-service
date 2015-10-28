/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user.admin;

import com.autonomy.aci.client.transport.AciServerDetails;

public interface UserAdminConfig {
    AciServerDetails getCommunityDetails();
}
