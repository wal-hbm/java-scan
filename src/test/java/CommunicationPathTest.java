/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Stephan Gatzka
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.messages.CommunicationPath;
import com.hbm.devices.scan.messages.AnnounceParser;

public class CommunicationPathTest {

    private CommunicationPath cp;
    private FakeMessageReceiver fsmmr;

    @Before
    public void setUp() {
        fsmmr = new FakeMessageReceiver();
        AnnounceParser parser = new AnnounceParser();
        fsmmr.addObserver(parser);
        parser.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof CommunicationPath) {
                    cp = (CommunicationPath) arg;
                }
            }
        });
    }

    @Test
    public void checkCookie() {
        fsmmr.emitSingleCorrectMessage();
        assertNotNull("Go not CommunicationPath object", cp);
        Object object = new Object();
        cp.setCookie(object);
        Object checkObject = cp.getCookie();
        assertSame("Cookie object not same", object, checkObject);
    }

    @Test
    public void testEquals() {
        fsmmr.emitSingleCorrectMessage();
        assertNotNull("Go not CommunicationPath object", cp);
        Object object = new Object();
        assertFalse("equals returns true for object !instanceof CommuncitionPath", cp.equals(object));
        assertEquals("equals with same object returns false", cp, cp);

        CommunicationPath oldCp = cp;
        fsmmr.emitCorrectMessageManual();
        assertNotSame("New object is same as old", oldCp, cp);
        assertEquals("Both CommunicationPath objects represent the same communication path, but return not equal", oldCp, cp);
        fsmmr.emitSingleCorrectMessageDevice2();
        assertNotSame("New object is same as old", oldCp, cp);
        assertNotEquals("Both CommunicationPath objects represent the different communication path, but return equal", oldCp, cp);
    }
}
