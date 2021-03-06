/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.common.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 *
 * @author hungld
 */
public class HincUtils {

    // only IPv4
    public static String getEth0Address__Manual() {
        System.out.println("Geting eth0");
        Enumeration en;
        try {
            en = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            return null;
        }
        while (en.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            System.out.println("Interface name: " + i.getName());
            if (i.getName().equals("eth0") || i.getName().equals("wlan0")) {
                for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
                    InetAddress addr = (InetAddress) en2.nextElement();
                    if (!addr.isLoopbackAddress()) {
                        if (addr instanceof Inet4Address) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        }
        return "127.0.0.1";
    }
    
    public static String getEth0Address() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            return null;
        }
    }
    
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            return "unknown";
        }
    }
}
