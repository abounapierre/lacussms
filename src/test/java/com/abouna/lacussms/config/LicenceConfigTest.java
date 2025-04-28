package com.abouna.lacussms.config;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class LicenceConfigTest {
    private LicenceConfig licenceConfig;

    @Test
    public void testAddress() {
        // Test the addAddress method
        String exist = "8C-F8-C5-5C-A9-D7-00-FF-31-2E-AA-3F-8E-F8-C5-5C-A9-D6-00-FF-75-4B-04-57-8C-F8-C5-5C-A9-D6-44-A9-2C-58-14-D3-0A-00-27-00-00-13";
       String macAddress = LicenceConfig.getMacAddress();
        assertNotNull(macAddress);
        System.out.println("MAC Address: " + macAddress);
        System.out.println("Address mac transform: " + macAddress.replace("-", ""));
    }

}
