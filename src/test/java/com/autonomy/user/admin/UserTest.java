/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.autonomy.user.admin;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactoryImpl;
import com.autonomy.aci.client.services.StAXProcessor;
import com.autonomy.user.admin.dto.User;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.util.List;

import static com.autonomy.test.unit.TestUtils.getResourceAsXMLStreamReader;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UserTest {

    private XMLStreamReader xmlReader;
    private IdolAnnotationsProcessorFactory processorFactory;

    @Before
    public void setUp() throws XMLStreamException {
        this.xmlReader = getResourceAsXMLStreamReader("/user.xml");
        this.processorFactory = new IdolAnnotationsProcessorFactoryImpl();
    }

    @Test
    public void userParseTest(){
        final StAXProcessor<List<User>> listProcessor = processorFactory.listProcessorForClass(User.class);
        final List<User> users = listProcessor.process(this.xmlReader);

        assertThat(users, hasSize(1));
        final User user = users.get(0);

        assertEquals(8, user.getUid());
        assertEquals("baz", user.getName());
        assertFalse(user.isLocked());
        assertEquals(0, user.getLockedLastTime());
        assertEquals(15, user.getMaxAgents());
        assertEquals(0, user.getNumAgents());
        assertEquals(1379929725, user.getLastLoggedIn());
        assertEquals(0, user.getNumFields());
    }
}
