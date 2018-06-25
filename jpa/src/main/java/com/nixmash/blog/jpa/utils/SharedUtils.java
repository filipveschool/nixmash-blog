package com.nixmash.blog.jpa.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class SharedUtils {

    public static Long randomNegativeId() {
        Random rand = new Random();
        return -1 * ((long) rand.nextInt(1000));
    }


    public static long timeMark() {
        return new Date().getTime();
    }

    public static String totalTime(long lStartTime, long lEndTime) {
        long duration = lEndTime - lStartTime;
        String totalTime = String.format("Milliseconds: %d", duration);
        return totalTime;
    }


    public static boolean isNixMashPC() {
        boolean isNixMashPC = false;
        try {
            for (Enumeration<NetworkInterface> e
                 = NetworkInterface.getNetworkInterfaces();
                 e.hasMoreElements(); ) {
                NetworkInterface ni = e.nextElement();
                if (ni.getDisplayName().equals("wlp5s0"))
                    isNixMashPC = formatMac(ni.getHardwareAddress()).equals("10-FE-ED-84-9E-A9");
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return isNixMashPC;
    }

    private static String formatMac(byte[] mac) {
        if (mac == null)
            return "UNKNOWN";
        String sb = IntStream.range(0, mac.length)
                .mapToObj(i -> String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""))
                .collect(Collectors.joining());
        /**
         *   for (int i = 0; i < mac.length; i++) {
         *             sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
         *         }
         */
        return sb;
    }

}
