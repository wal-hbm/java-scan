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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hbm.devices.scan.configure.ConfigCallback;
import com.hbm.devices.scan.configure.ConfigurationService;
import com.hbm.devices.scan.configure.FakeDeviceEmulator;
import com.hbm.devices.scan.configure.FakeMulticastSender;
import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.messages.ConfigureDevice;
import com.hbm.devices.scan.messages.ConfigureInterface;
import com.hbm.devices.scan.messages.ConfigureNetSettings;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.Interface.Method;
import com.hbm.devices.scan.messages.MissingDataException;
import com.hbm.devices.scan.messages.Response;

public class ConfigurationServiceTest {
/*
    private FakeMulticastSender fakeSender;
    private FakeMessageReceiver fakeReceiver;

    private ConfigurationService service;

    private FakeDeviceEmulator emulator;
    private ConfigurationService service2;

    private JsonParser parser;

    @Before
    public void setup() {
        this.fakeSender = new FakeMulticastSender();
        this.fakeReceiver = new FakeMessageReceiver();

        this.emulator = new FakeDeviceEmulator();

        try {
            // get the private constructor
            Constructor<ConfigurationService> configurationServiceConstructor = ConfigurationService.class
                    .getDeclaredConstructor(Observer.class, Observable.class, String.class);
            configurationServiceConstructor.setAccessible(true);
            this.service = configurationServiceConstructor.newInstance(fakeSender, fakeReceiver,
                    "TEST_UUID");
            this.service2 = configurationServiceConstructor.newInstance(emulator, emulator,
                    "TEST_UUID");
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.parser = new JsonParser();
    }

    public boolean received;
    public boolean timeout;

    @Test
    public void sendingTest() {
        Device device = new Device("0009E5001571");
        NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
        ConfigureParams configParams = new ConfigureParams(device, settings);
        try {
            service.sendConfiguration(configParams, new SimpleCallback(), 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String correctString = "{\"params\":{\"device\":{\"uuid\":\"0009E5001571\"},\"netSettings\":{\"interface\":{\"name\":\"eth0\",\"configurationMethod\":\"dhcp\"}},\"ttl\":1},\"id\":\"TEST_UUID\",\"jsonrpc\":\"2.0\",\"method\":\"configure\"}";
        JsonElement correct = parser.parse(correctString);
        JsonElement sent = parser.parse(fakeSender.getLastSent());
        assertTrue(sent.equals(correct));

        // assertTrue(this.fakeSender.getLastSent().equals(correctString));
    }

    @Test
    public void sendingAndReceivingTest() {
        received = false;

        ConfigCallback cb = new ConfigCallback() {

            @Override
            public void onSuccess(ConfigQuery configQuery, Response response) {
                received = true;
            }

            @Override
            public void onError(ConfigQuery configQuery, Response response) {
                received = true;
            }

            @Override
            public void onTimeout(ConfigQuery configQuery) {
            }

        };

        Device device = new Device("0009E5001571");
        NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
        ConfigureParams configParams = new ConfigureParams(device, settings);

        try {
            service2.sendConfiguration(configParams, cb, 20);

        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(received);
    }

    @Test
    public void RemoveAwaitingEntryOnReceiveTest() {
        // test if the HashMap entry is removed after a successful response
        received = false;
        timeout = false;

        ConfigCallback cb = new ConfigCallback() {

            @Override
            public void onSuccess(ConfigQuery configQuery, Response response) {
                received = true;
            }

            @Override
            public void onError(ConfigQuery configQuery, Response response) {
                received = true;
            }

            @Override
            public void onTimeout(ConfigQuery configQuery) {
                timeout = true;
                this.notifyAll();
            }

        };

        Device device = new Device("0009E5001571");
        NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
        ConfigureParams configParams = new ConfigureParams(device, settings);

        try {
            service2.sendConfiguration(configParams, cb, 20);

        } catch (Exception e) {
            e.printStackTrace();
        }

        assertFalse(service2.awaitingResponse() && service2.hasResponseTimeoutTimer());
    }

    @Test(timeout = 200)
    public void CheckTimeout() {
        received = false;
        timeout = false;

        ConfigCallback cb = new ConfigCallback() {

            @Override
            public void onTimeout(ConfigQuery configQuery) {
                synchronized (this) {
                    timeout = true;
                    this.notifyAll();
                }
            }

            @Override
            public void onSuccess(ConfigQuery configQuery, Response response) {
                synchronized (this) {
                    received = true;
                    this.notifyAll();
                }
            }

            @Override
            public void onError(ConfigQuery configQuery, Response response) {
                synchronized (this) {
                    received = true;
                    this.notifyAll();
                }
            }
        };

        Device device = new Device("0009E5001571");
        NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
        ConfigureParams configParams = new ConfigureParams(device, settings);

        synchronized (cb) {
            try {
                service.sendConfiguration(configParams, cb, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!timeout && !received) {
                try {
                    cb.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        assertTrue(!received && timeout);

    }

    @Test(timeout = 200)
    public void RemoveAwaitingEntryOnTimeoutTest() {
        // test if the HashMap entry is removed when no response is received within the timeout
        received = false;
        timeout = false;

        ConfigCallback cb = new ConfigCallback() {

            @Override
            public void onTimeout(ConfigQuery configQuery) {
                synchronized (this) {
                    timeout = true;
                    this.notifyAll();
                }
            }

            @Override
            public void onSuccess(ConfigQuery configQuery, Response response) {
                synchronized (this) {
                    received = true;
                    this.notifyAll();
                }
            }

            @Override
            public void onError(ConfigQuery configQuery, Response response) {
                synchronized (this) {
                    received = true;
                    this.notifyAll();
                }
            }
        };

        Device device = new Device("0009E5001571");
        NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
        ConfigureParams configParams = new ConfigureParams(device, settings);

        synchronized (cb) {
            try {
                service.sendConfiguration(configParams, cb, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!timeout && !received) {
                try {
                    cb.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        assertFalse(service.awaitingResponse() && service.hasResponseTimeoutTimer());
    }
*/
}
