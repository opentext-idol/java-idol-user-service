/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user;

import com.autonomy.aci.client.transport.AciServerDetails;

/**
 * Interface representing a configuration object that contains the location of a community server.
 */
@SuppressWarnings("WeakerAccess")
public interface UserServiceConfig {
    /**
     * @return The details of the community server
     */
    AciServerDetails getCommunityDetails();

    /**
     * @return Details of the Community server's AgentStore
     */
    AciServerDetails getCommunityAgentStoreDetails();
}
