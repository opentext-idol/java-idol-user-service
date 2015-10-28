/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user.admin.dto;

import com.autonomy.aci.client.annotations.IdolBuilder;
import com.autonomy.aci.client.annotations.IdolBuilderBuild;
import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@IdolDocument("autn:uid")
@Data
public class Uid {

    private final long uid;

    private Uid(final long uid) {
        this.uid = uid;
    }

    @IdolBuilder
    @IdolDocument("autn:uid")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Builder {

        private long uid;

        @IdolField("autn:uid")
        public Builder setUid(final long uid) {
            this.uid = uid;
            return this;
        }

        @IdolBuilderBuild
        public Uid build() {
            return new Uid(uid);
        }

    }
}
