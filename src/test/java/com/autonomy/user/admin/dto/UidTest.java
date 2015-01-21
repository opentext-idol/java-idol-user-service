package com.autonomy.user.admin.dto;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactoryImpl;
import com.autonomy.aci.client.services.StAXProcessor;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.List;

import static com.autonomy.test.unit.TestUtils.getResourceAsXMLStreamReader;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class UidTest {

    private XMLStreamReader uidXmlStreamReader;
    private IdolAnnotationsProcessorFactory processorFactory;

    @Before
    public void setUp() throws XMLStreamException {
        this.uidXmlStreamReader = getResourceAsXMLStreamReader("/userAdd.xml");
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
