/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.autonomy.user.admin.dto;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactoryImpl;
import com.autonomy.aci.client.services.StAXProcessor;
import com.hp.autonomy.test.xml.XmlTestUtils;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class UidTest {

    private XMLStreamReader uidXmlStreamReader;
    private IdolAnnotationsProcessorFactory processorFactory;

    @Before
    public void setUp() throws XMLStreamException {
        this.uidXmlStreamReader = XmlTestUtils.getResourceAsXMLStreamReader("/userAdd.xml");
        this.processorFactory = new IdolAnnotationsProcessorFactoryImpl();
    }

    @Test
    public void testUserAddXml(){
        final StAXProcessor<List<Uid>> listProcessor = processorFactory.listProcessorForClass(Uid.class);
        final List<Uid> uids = listProcessor.process(this.uidXmlStreamReader);

        assertThat(uids, hasSize(1));
        final Uid uid = uids.get(0);

        assertThat(uid.getUid(), is(12L));
    }

}
