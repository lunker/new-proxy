package org.lunker.new_proxy.sip.util;

import org.junit.Test;
import org.lunker.new_proxy.model.Transport;

import javax.sip.header.RecordRouteHeader;

import java.text.ParseException;

import static org.junit.Assert.*;

public class SipMessageFactoryTest {

    public SipMessageFactory sipMessageFactory;

    public SipMessageFactoryTest () {
        sipMessageFactory = SipMessageFactory.getInstance();
    }
    @Test
    public void generateRecordRouteHeader() {
        try {
            RecordRouteHeader recordRouteHeader = sipMessageFactory.generateRecordRouteHeader("test", "127.0.0.1", 5060, "tcp");
            assertNotEquals(recordRouteHeader, null);
            System.out.println(recordRouteHeader);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}