package com.example;


public class Main {

    public static void main(String[] args) {
        Worldmesh wm = new Worldmesh();
        double latitude = 35.590676;
        double longitude = 139.671488;
        long wmSample = wm.cal_meshcode3(latitude, longitude);
        System.out.println(String.format("Lat=%f, Lng=%f", latitude, longitude));
        System.out.println(wmSample);
        Worldmesh wmNW = wm.meshcode_to_latlong_NW(wmSample);
        Worldmesh wmSW = wm.meshcode_to_latlong_SW(wmSample);
        Worldmesh wmNE = wm.meshcode_to_latlong_NE(wmSample);
        Worldmesh wmSE = wm.meshcode_to_latlong_SE(wmSample);
        System.out.println(String.format("NW(%f, %f), SW(%f, %f), NE(%f, %f), SE(%f, %f)",
            wmNW.longitude, wmNW.latitude,
            wmSW.longitude, wmSW.latitude,
            wmNE.longitude, wmNE.latitude,
            wmSE.longitude, wmSE.latitude));
        long upperMesh = wm.cal_meshcode3(wmNE.latitude + 0.000001,wmNE.longitude);
        System.out.println(("Upper: " + upperMesh));
        long lowerMesh = wm.cal_meshcode3(wmSE.latitude - 0.000001,wmSE.longitude);
        System.out.println(("Lower: " + lowerMesh));
        long leftMesh = wm.cal_meshcode3(wmNW.latitude,wmNW.longitude - 0.000001);
        System.out.println(("Left: " + leftMesh));
        long rightMesh = wm.cal_meshcode3(wmNE.latitude,wmNE.longitude + 0.000001);
        System.out.println(("Right: " + rightMesh));
    }
}
