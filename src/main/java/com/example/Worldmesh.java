package com.example;//
// Copyright (c) 2015-2020 Research Institute for World Grid Squares 
// Prof. Dr. Aki-Hiro Sato
// All rights reserved. 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
// java class to calculate the world grid square code.
// The world grid square code computed by this library is
// compatible to JIS X0410    
//
// Version 1.0: Released on 1 January 2019
// Version 1.6: Released on 14 October 2020
//
// Written by Prof. Dr. Aki-Hiro Sato    
// Department of Data Science, Graduate School of Data Science,
// Yokohama City University
//
// Contact:
// Address: 22-2, Seto, Kanazawa-ku, Yokohama, Kanagawa 236-0027 Japan
// E-mail: ahsato@yokohama-cu.ac.jp
// TEL: +81-45-787-2208
//
// Three types of methods are defined in this class library.
// 1. calculate representative geographical position(s) (latitude, longitude) of a grid square from a grid square code
// 2. calculate a grid square code from a geographical position (latitude, longitude)
// 3. calculate geodesic distance and size of grid square (representative lengths and area)
//
// 1.
//
// meshcode_to_latlong(meshcode, extension=false)
// : calculate northen western geographic position of the grid (latitude, longitude) from meshcode
// meshcode_to_latlong_NW(meshcode, extension=false)
// : calculate northen western geographic position of the grid (latitude, longitude) from meshcode
// meshcode_to_latlong_SW(meshcode, extension=false)
// : calculate sourthern western geographic position of the grid (latitude, longitude) from meshcode
// meshcode_to_latlong_NE(meshcode, extension=false)
// : calculate northern eastern geographic position of the grid (latitude, longitude) from meshcode
// meshcode_to_latlong_SE(meshcode, extension=false)
// : calculate sourthern eastern geographic position of the grid (latitude, longitude) from meshcode
// meshcode_to_latlong_grid(meshcode, extension=false)
// : calculate northern western and sourthern eastern geographic positions of the grid (latitude0, longitude0, latitude1, longitude1) from meshcode
//
// 2.
//
// : calculate a basic (1km) grid square code (10 digits) from a geographical position (latitude, longitude)
// cal_meshcode1(latitude,longitude)
// : calculate an 80km grid square code (6 digits) from a geographical position (latitude, longitude)
// cal_meshcode2(latitude,longitude)
// : calculate a 10km grid square code (8 digits) from a geographical position (latitude, longitude)
// cal_meshcode3(latitude,longitude)
// : calculate a 1km grid square code (10 digits) from a geographical position (latitude, longitude)
// cal_meshcode4(latitude,longitude)
// : calculate a 500m grid square code (11 digits) from a geographical position (latitude, longitude)
// cal_meshcode5(latitude,longitude)
// : calculate a 250m grid square code (12 digits) from a geographical position (latitude, longitude)
// cal_meshcode6(latitude,longitude)
// : calculate a 125m grid square code (13 digits) from a geographical position (latitude, longitude)
//
// This grid square code set is not included in JIS X0410 directly but useful.
//
// cal_meshcode_ex100(latitude,longitude)
// : calculate an extended 100m grid square code (13 digits) from a geographical position (latitude, longitude) - 3 arc-second for latitude and 4.5 arc-second for longitude 
//
// Structure of the world grid square code with compatibility to JIS X0410
// A : area code (1 digit) A takes 1 to 8
// ABBBBB : 80km grid square code (40 arc-minutes for latitude, 1 arc-degree for longitude) (6 digits)
// ABBBBBCC : 10km grid square code (5 arc-minutes for latitude, 7.5 arc-minutes for longitude) (8 digits)
// ABBBBBCCDD : 1km grid square code (30 arc-seconds for latitude, 45 arc-secondes for longitude) (10 digits)
// ABBBBBCCDDE : 500m grid square code (15 arc-seconds for latitude, 22.5 arc-seconds for longitude) (11 digits)
// ABBBBBCCDDEF : 250m grid square code (7.5 arc-seconds for latitude, 11.25 arc-seconds for longitude) (12 digits)
// ABBBBBCCDDEFG : 125m grid square code (3.75 arc-seconds for latitude, 5.625 arc-seconds for longitude) (13 digits)
// ABBBBBCCDDEHH : Extended 100m grid square code (3 arc-seconds for latitude, 4.5 arc-seconds for longitude) (13 digits)
//
// 3.
//
// Calculate geodesic distance and size of world grid square
//
// # T. Vincenty, ``Direct and Inverse Solutions of Geodesics
// on the Ellipsoid with application of nested equations'',
// Survey Review XXIII, Vol 176 (1975) Vol. 88-93.
//
// Vincenty(latitude1, longitude1, latitude2, longitude)
// : calculate geodesitc distance between two points (latitude1, longitude1) and (latitude2, longitude2) placed on the WGS84 Earth ellipsoid based on the Vincenty's formulae (1975)
// cal_area_from_meshcode(meshcode,extension=F)
// : calculate size (northern west-to-east span H1, sothern west-to-east span H2, north-to-south span W, and area approximated by trapezoide A) of world grid square indicated by meshcode
// cal_area_from_latlong(latlong)
// : calculate size (northern west-to-east span H1, sothern west-to-east span H2, north-to-south span W, and area approximated by trapezoid A) of a trapezoid on the WGS84 Earth ellipoid indicated by (latlong$lat0, latlong$long0, latlong$lat1, latlong$long1)
//
//
//Test Code 
//class TestWorldmesh{
//    public static void main(String args[]){
//	Worldmesh res = new Worldmesh();
//	long worldmeshcode = res.cal_meshcode(34.9773063,135.7402153);
//	System.out.println("worldmeshcode = " + worldmeshcode);
//      Worldmesh wm = res.meshcode_to_latlong(worldmeshcode);
//	System.out.println("lat0 = " + wm.lat0 + ", long0 = " + wm.long0);
//	System.out.println("lat1 = " + wm.lat1 + ", long1 = " + wm.long1);
//    }
//}

class Worldmesh{
  public double lat0;
  public double long0;
  public double lat1;
  public double long1;
  public double latitude;
  public double longitude;
  public double W1;
  public double W2;
  public double H;
  public double A;    
  public Worldmesh meshcode_to_latlong_grid(long meshcode){
      return this.meshcode_to_latlong_grid(meshcode, false);      
  }
  public Worldmesh meshcode_to_latlong_grid(long meshcode, boolean extension){
    Worldmesh wm = new Worldmesh();
    int code0,code12,code34,code5,code6,code7,code8,code9,code10,code11,codeex10,codeex11;
    code0=0;
    code12=0;
    code34=0;
    code5=0;
    code6=0;
    code7=0;
    code8=0;
    code9=0;
    code10=0;
    code11=0;
    codeex10=0;
    codeex11=0;
    double lat_width,long_width;
    lat_width=0.0; long_width=0.0;
    double dlat,dlong;
    dlat=0.0; dlong=0.0;
    String code = String.valueOf(meshcode);
    int ncode = code.length();
    if (ncode >= 6) { // more than 1st grid
	code0 = Integer.parseInt(code.substring(0, 0+1)); // code0 : 1 to 8
	code0 = code0 - 1; // transforming code0 from 0 to 7
	String s_code12 = code.substring(1, 1+3);
	if(s_code12.substring(0,0+2)=="00"){
            code12 = Integer.parseInt(code.substring(3, 3+1));
	}else{
            if(s_code12.substring(0,0+1)=="0"){
		code12 = Integer.parseInt(code.substring(2, 2+2));
            }
            else{
		code12 = Integer.parseInt(code.substring(1, 1+3));
            }
	}
	if(code.substring(4,4+1)=="0"){
            code34 = Integer.parseInt(code.substring(5, 5+1));
	}
	else{
            code34 = Integer.parseInt(code.substring(4, 4+2));
	}
	lat_width  = 2.0 / 3.0;
	long_width = 1.0;
    }
    else {
	return(null);
    }
    if (ncode >= 8) { // more than 2nd grid
	code5 = Integer.parseInt(code.substring(6, 6+1));
	code6 = Integer.parseInt(code.substring(7, 7+1));
	lat_width  = lat_width / 8.0;
	long_width = long_width / 8.0;
    }
    if (ncode >= 10) { // more than 3rd grid
	code7 = Integer.parseInt(code.substring(8, 8+1));
	code8 = Integer.parseInt(code.substring(9, 9+1));
	lat_width = lat_width / 10.0;
	long_width = long_width / 10.0;
    }
    if (ncode >= 11) { // more than 4th grid
	code9 = Integer.parseInt(code.substring(10, 10+1));
	lat_width = lat_width / 20.0;
	long_width = long_width / 20.0;
    }
    if (ncode >= 12) { // more than 5th grid
	code10 = Integer.parseInt(code.substring(11, 11+1));
	lat_width = lat_width / 40.0;
	long_width = long_width / 40.0;
    }
    if(!extension){
	if (ncode >= 13) { // more than 6th grid
	    code11 = Integer.parseInt(code.substring(12, 12+1));
	    lat_width = lat_width / 80.0;
	    long_width = long_width / 80.0;
	}
    }else{
	if (ncode >= 13) { // Extended 100m grid square code
	    codeex10 = Integer.parseInt(code.substring(11, 11+1));
	    codeex11 = Integer.parseInt(code.substring(12, 12+1));
	    lat_width = lat_width / 100.0;
	    long_width = long_width / 100.0;
	}
    }
    
    // 0'th grid
    int z = code0 % 2;
    int y = ((code0 - z)/2) % 2;
    int x = (code0 - 2*y - z)/4;

    switch(ncode){
    case 6: // 1st grid (6 digits)
	wm.lat0 = (code12-x+1) * 2.0 / 3.0;        
	wm.long0 = Double.valueOf((code34+y) + 100*z);
	wm.lat0 = Double.valueOf(1-2*x)*wm.lat0;        
	wm.long0 = Double.valueOf(1-2*y)*wm.long0;
	dlat = 2.0/3.0;
	dlong = 1.0;
	break;
    case 8: // 2nd grid (8 digits)
	wm.lat0 = Double.valueOf(code12) * 2.0 / 3.0;
	wm.long0 = Double.valueOf(code34 + 100*z);
	wm.lat0 = wm.lat0  + (Double.valueOf(code5-x+1) * 2.0 / 3.0) / 8.0; 
	wm.long0 = wm.long0 +  Double.valueOf(code6+y) / 8.0;
	wm.lat0 = Double.valueOf(1-2*x) * wm.lat0;
	wm.long0 = Double.valueOf(1-2*y) * wm.long0;
	dlat = 2.0/3.0/8.0;
	dlong = 1.0/8.0;
	break;
    case 10: // 3rd grid (10 digits)
	wm.lat0 = Double.valueOf(code12) * 2.0 / 3.0;  
	wm.long0 = Double.valueOf(code34 + 100*z);
	wm.lat0 = wm.lat0 + (Double.valueOf(code5) * 2.0 / 3.0) / 8.0; 
	wm.long0 = wm.long0 +  Double.valueOf(code6) / 8.0;
	wm.lat0 = wm.lat0 + (Double.valueOf(code7-x+1) * 2.0 / 3.0) / 8.0 / 10.0;
	wm.long0 = wm.long0 + Double.valueOf(code8+y) / 8.0 / 10.0;
	wm.lat0 = Double.valueOf(1-2*x)*wm.lat0;
	wm.long0 = Double.valueOf(1-2*y)*wm.long0;
	dlat = 2.0/3.0/8.0/10.0;
	dlong = 1.0/8.0/10.0;
	break;
    case 11: // 4th grid (11 digits)
	// code 9
	//     N
	//   3 | 4
	// W - + - E
	//   1 | 2
	//     S
	wm.lat0 = Double.valueOf(code12) * 2.0 / 3.0;  
	wm.long0 = Double.valueOf(code34 + 100*z);
	wm.lat0 = wm.lat0 + (Double.valueOf(code5) * 2.0 / 3.0) / 8.0; 
	wm.long0 = wm.long0 + Double.valueOf(code6) / 8.0;
	wm.lat0 = wm.lat0  + (Double.valueOf(code7-x+1) * 2.0 / 3.0) / 8.0 / 10.0;
	wm.long0 = wm.long0 + Double.valueOf(code8+y) / 8.0 / 10.0;
	wm.lat0 = wm.lat0  + (Math.floor(Double.valueOf((code9-1)/2+x-1))) * 2.0 / 3.0 / 8.0 / 10.0 / 2.0;
	wm.long0 = wm.long0 + Double.valueOf((code9-1)%2-y) / 8.0 / 10.0 / 2.0;
	wm.lat0 = Double.valueOf(1-2*x)*wm.lat0;
	wm.long0 = Double.valueOf(1-2*y)*wm.long0;
	dlat = 2.0/3.0/8.0/10.0/2.0;
	dlong = 1.0/8.0/10.0/2.0;
	break;
    case 12 : // 5th grid (12 digits)
	// code 10
	//     N
	//   3 | 4
	// W - + - E
	//   1 | 2
	//     S
	wm.lat0 = Double.valueOf(code12) * 2.0 / 3.0;  
	wm.long0 = Double.valueOf(code34 + 100*z);
	wm.lat0 = wm.lat0  + (Double.valueOf(code5) * 2.0 / 3.0) / 8.0; 
	wm.long0 = wm.long0 + Double.valueOf(code6) / 8.0;
	wm.lat0 = wm.lat0  + (Double.valueOf(code7-x+1) * 2.0 / 3.0) / 8.0 / 10.0;
	wm.long0 = wm.long0 +  Double.valueOf(code8+y) / 8.0 / 10.;
	wm.lat0 = wm.lat0 + (Math.floor(Double.valueOf((code9-1)/2)+x-1)) * 2.0 / 3.0 / 8.0 / 10.0 / 2.0;
	wm.long0 = wm.long0 + Double.valueOf((code9-1)%2-y) / 8.0 / 10.0 / 2.0;
	wm.lat0 = wm.lat0  + (Math.floor(Double.valueOf((code10-1)/2+x-1))) * 2.0 / 3.0 / 8.0 / 10.0 / 2.0 / 2.0;
	wm.long0 = wm.long0 + Double.valueOf((code10-1)%2-y) / 8.0 / 10.0 / 2.0 / 2.0;
	wm.lat0 = Double.valueOf(1-2*x)*wm.lat0;
	wm.long0 = Double.valueOf(1-2*y)*wm.long0;
	dlat = 2.0/3.0/8.0/10.0/2.0/2.0;
	dlong = 1.0/8.0/10.0/2.0/2.0;
	break;
    case 13:
	if(!extension){
	    // 6rd grid (13 digits)	
	    // code 11
	    //     N
	    //   3 | 4
	    // W - + - E
	    //   1 | 2
	    //     S
	    wm.lat0 = Double.valueOf(code12) * 2.0 / 3.0;  
	    wm.long0 = Double.valueOf(code34 + 100*z);
	    wm.lat0 = wm.lat0  + (Double.valueOf(code5) * 2.0 / 3.0) / 8.0; 
	    wm.long0 = wm.long0 + Double.valueOf(code6) / 8.0;
	    wm.lat0 = wm.lat0  + (Double.valueOf(code7-x+1) * 2.0 / 3.0) / 8.0 / 10.0;
	    wm.long0 = wm.long0 + Double.valueOf(code8+y) / 8.0 / 10.0;
	    wm.lat0 = wm.lat0  + (Math.floor(Double.valueOf((code9-1)/2+x-1))) * 2.0 / 3.0 / 8.0 / 10.0 / 2.0;
	    wm.long0 = wm.long0 + Double.valueOf((code9-1)%2-y) / 8.0 / 10.0 / 2.0;
	    wm.lat0 = wm.lat0 + (Math.floor(Double.valueOf((code10-1)/2+x-1))) * 2.0 / 3.0 / 8.0 / 10.0 / 2.0 / 2.0;
	    wm.long0 = wm.long0 + Double.valueOf((code10-1)%2-y) / 8.0 / 10.0 / 2.0 / 2.0;
	    wm.lat0 = wm.lat0 + (Math.floor(Double.valueOf((code11-1)/2+x-1))) * 2.0 / 3.0 / 8.0 / 10.0 / 2.0 / 2.0 / 2.0;
	    wm.long0 = wm.long0 + Double.valueOf((code11-1)%2-y) / 8.0 / 10.0 / 2.0 / 2.0 / 2.0;
	    wm.lat0 = Double.valueOf(1-2*x)*wm.lat0;
	    wm.long0 = Double.valueOf(1-2*y)*wm.long0;
	    dlat = 2.0/3.0/8.0/10.0/2.0/2.0/2.0;
	    dlong = 1.0/8.0/10.0/2.0/2.0/2.0;
	}else{ // Extended 100m grid square code
	    //    Code10
	    //      4*****
	    //      3*****
	    //      2*****
	    //      1*****
	    //      0*****
	    //       01234 Code11
	    wm.lat0 = Double.valueOf(code12) * 2.0 / 3.0;  
	    wm.long0 = Double.valueOf(code34 + 100*z);
	    wm.lat0 = wm.lat0 + (Double.valueOf(code5) * 2.0 / 3.0) / 8.0; 
	    wm.long0 = wm.long0 + Double.valueOf(code6) / 8.0;
	    wm.lat0 = wm.lat0 + (Double.valueOf(code7-x+1) * 2.0 / 3.0) / 8.0 / 10.0;
	    wm.long0 = wm.long0 + Double.valueOf(code8+y) / 8.0 / 10.0;
	    wm.lat0 = wm.lat0 + (Math.floor(Double.valueOf(code9-1)/2)+2*x-2) * 2.0 / 3.0 / 8.0 / 10.0 / 2.0;
	    wm.long0 = wm.long0 + Double.valueOf((code9-1)%2-2*y) / 8.0 / 10.0 / 2.0;
	    wm.lat0 = wm.lat0 + Double.valueOf(codeex10-x+1) * 2.0 / 3.0 / 8.0 / 10.0 / 2.0 / 5.0;
	    wm.long0 = wm.long0 + Double.valueOf(codeex11+y) / 8.0 / 10.0 / 2.0 / 5.0;
	    wm.lat0 = Double.valueOf(1-2*x)*wm.lat0;
	    wm.long0 = Double.valueOf(1-2*y)*wm.long0;
	    dlat = 2.0/3.0/8.0/10.0/2.0/5.0;
	    dlong = 1.0/8.0/10.0/2.0/5.0;
	}
    }
    wm.lat1 = wm.myformat8(wm.lat0-dlat);  
    wm.long1 = wm.myformat8(wm.long0+dlong);
    wm.lat0 = wm.myformat8(wm.lat0);  
    wm.long0 = wm.myformat8(wm.long0);
    return wm;
  }

  private double myformat8(double v){
    String s = String.format("%20.20f",v);
    String ss;
    if(v > 100.0) ss = s.substring(0,3+8);
    else if(v > 10.0) ss = s.substring(0,2+8);
    else ss = s.substring(0,1+8);
    return(Double.parseDouble(ss));
  }
  public Worldmesh meshcode_to_latlong(long meshcode){
      return this.meshcode_to_latlong(meshcode, false);
  }
  public Worldmesh meshcode_to_latlong(long meshcode, boolean extension){
    Worldmesh res = new Worldmesh();
    res = this.meshcode_to_latlong_grid(meshcode,extension);
    res.latitude = res.lat0;
    res.longitude = res.long0;
    return res;    
  }

  public Worldmesh meshcode_to_latlong_NW(long meshcode){
      return this.meshcode_to_latlong_NW(meshcode, false);
  }
    
  public Worldmesh meshcode_to_latlong_NW(long meshcode, boolean extension){
    Worldmesh res = new Worldmesh();
    res = this.meshcode_to_latlong_grid(meshcode, extension);
    res.latitude = res.lat0;
    res.longitude = res.long0;
    return res;
  }

  public Worldmesh meshcode_to_latlong_SW(long meshcode){
      return this.meshcode_to_latlong_SW(meshcode, false);
  }

  public Worldmesh meshcode_to_latlong_SW(long meshcode, boolean extension){
    Worldmesh res = new Worldmesh();
    res = this.meshcode_to_latlong_grid(meshcode, extension);
    res.latitude = res.lat1;
    res.longitude = res.long0;
    return res;
  }
  public Worldmesh meshcode_to_latlong_NE(long meshcode){
      return this.meshcode_to_latlong_NE(meshcode, false);
  }
  public Worldmesh meshcode_to_latlong_NE(long meshcode, boolean extension){
    Worldmesh res = new Worldmesh();
    res = this.meshcode_to_latlong_grid(meshcode);
    res.latitude = res.lat0;
    res.longitude = res.long1;
    return res;
  }

  public Worldmesh meshcode_to_latlong_SE(long meshcode){
      return this.meshcode_to_latlong_SE(meshcode, false);
  }
    
  public Worldmesh meshcode_to_latlong_SE(long meshcode, boolean extension){
    Worldmesh res = new Worldmesh();
    res = this.meshcode_to_latlong_grid(meshcode, extension);
    res.latitude = res.lat1;
    res.longitude = res.long1;
    return res;
  }

  public long cal_meshcode6(double latitude, double longitude){
    String mesh;
    int o;
    int x,y,z;
    if(latitude < 0.0){
          o = 4;
    }
    else{
          o = 0;
    }
    if(longitude < 0.0){
          o = o + 2;
    }
    if(Math.abs(longitude) >= 100.0) o = o + 1;
    z = o % 2;
    y = ((o-z)/2) % 2;
    x = (o - 2*y - z)/4;
    o = o + 1;
    latitude = Double.valueOf(1-2*x)*latitude;
    longitude = Double.valueOf(1-2*y)*longitude;
    int p = (int)Math.floor(latitude*60/40);
    double a = (latitude*60/40-p)*40;
    int q = (int)Math.floor(a/5);
    double b = (a/5-q)*5;
    int r = (int)Math.floor(b*60/30);
    double c = (b*60/30-r)*30;
    int s2u = (int)Math.floor(c/15);
    double d = (c/15-s2u)*15;
    int s4u = (int)Math.floor(d/7.5);
    double e = (d/7.5-s4u)*7.5;
    int s8u = (int)Math.floor(e/3.75);
    int u = (int)Math.floor(longitude-100*z);
    double f = longitude-100*z-u;
    int v = (int)Math.floor(f*60/7.5);
    double g = (f*60/7.5-v)*7.5;
    int w = (int)Math.floor(g*60/45);
    double h = (g*60/45-w)*45;
    int s2l = (int)Math.floor(h/22.5);
    double i = (h/22.5-s2l)*22.5;
    int s4l = (int)Math.floor(i/11.25);
    double j = (i/11.25-s4l)*11.25;
    int s8l = (int)Math.floor(j/5.625);
    int s2 = s2u*2+s2l+1;
    int s4 = s4u*2+s4l+1;
    int s8 = s8u*2+s8l+1;
    if(u < 10.0){
       if(p < 10.0){
           mesh = String.valueOf(o)+"00"+String.valueOf(p)+"0"+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(s4)+String.valueOf(s8);
       }else{
           if(p < 100.0){
               mesh = String.valueOf(o)+"0"+String.valueOf(p)+"0"+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(s4)+String.valueOf(s8);
           }
           else{
               mesh = String.valueOf(o)+String.valueOf(p)+"0"+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(s4)+String.valueOf(s8);
           }
       }
    }else{
       if(p < 10.0){
            mesh = String.valueOf(o)+"00"+String.valueOf(p)+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(s4)+String.valueOf(s8);
       }else{
           if(p < 100.0){
                mesh = String.valueOf(o)+"0"+String.valueOf(p)+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(s4)+String.valueOf(s8);
           }else{
                mesh = String.valueOf(o)+String.valueOf(p)+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(s4)+String.valueOf(s8);
           }
       }
    }
    return(Long.parseLong(mesh));
  }
// 
  public long cal_meshcode(double latitude, double longitude){
    return(this.cal_meshcode3(latitude,longitude));
  }
  public long cal_meshcode1(double latitude, double longitude){
    String mesh = String.valueOf(this.cal_meshcode6(latitude,longitude));
    return(Long.parseLong(mesh.substring(0,6)));
  }
  public long cal_meshcode2(double latitude, double longitude){
    String mesh = String.valueOf(this.cal_meshcode6(latitude,longitude));
    return(Long.parseLong(mesh.substring(0,8)));
  }
  public long cal_meshcode3(double latitude, double longitude){
    String mesh = String.valueOf(this.cal_meshcode6(latitude,longitude));
    return(Long.parseLong(mesh.substring(0,10)));
}
  public long cal_meshcode4(double latitude, double longitude){
    String mesh = String.valueOf(this.cal_meshcode6(latitude,longitude));
    return(Long.parseLong(mesh.substring(0,11)));
  }
  public long cal_meshcode5(double latitude, double longitude){
    String mesh = String.valueOf(this.cal_meshcode6(latitude,longitude));
    return(Long.parseLong(mesh.substring(0,12)));
  }
  // calculate an extended 100m grid square code (13 digits) from a geographical position (latitude, longitude) - 3 arc-second for latitude and 4.5 arc-second for longitude
  public long cal_meshcode_ex100(double latitude, double longitude){
      String mesh;
      int o;
      int x,y,z;
      if(latitude < 0.0){
	  o = 4;
      }
      else{
	  o = 0;
      }
      if(longitude < 0.0){
	  o = o + 2;
      }
      if(Math.abs(longitude) >= 100) o = o + 1;
      z = o % 2;
      y = ((o - z)/2) % 2;
      x = (o - 2*y - z)/4;
      o = o + 1;
      latitude = Double.valueOf(1-2*x)*latitude;
      longitude = Double.valueOf(1-2*y)*longitude;
      int p = (int)Math.floor(latitude*60/40);
      double a = (latitude*60/40-p)*40;
      int q = (int)Math.floor(a/5);
      double b = (a/5-q)*5;
      int r = (int)Math.floor(b*60/30);
      double c = (b*60/30-r)*30;
      int s2u = (int)Math.floor(c/15);
      double d = (c/15-s2u)*15;
      int et = (int)Math.floor(d/3);
      double e = (d/3-et)*3;
      int u = (int)Math.floor(longitude-100*z);
      double f = longitude-100*z-u;
      int v = (int)Math.floor(f*60/7.5);
      double g = (f*60/7.5-v)*7.5;
      int w = (int)Math.floor(g*60/45);
      double h = (g*60/45-w)*45;
      int s2l = (int)Math.floor(h/22.5);
      double i = (h/22.5-s2l)*22.5;
      int jt = (int)Math.floor(i/4.5);
      double j = (i/9-jt)*4.5;
      int s2 = s2u*2+s2l+1;
	  //
      if(u < 10.0){
        if(p < 10.0){
	    mesh = String.valueOf(o)+"00"+String.valueOf(p)+"0"+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(et)+String.valueOf(jt);
	}else{
	    if(p < 100.0){
		mesh = String.valueOf(o)+"0"+String.valueOf(p)+"0"+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(et)+String.valueOf(jt);
	    }
	    else{
		mesh = String.valueOf(o)+String.valueOf(p)+"0"+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(et)+String.valueOf(jt);
	    }
	}
      }
      else{
	  if(p < 10.0){
	      mesh = String.valueOf(o)+"00"+String.valueOf(p)+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(et)+String.valueOf(jt);
	  }else{
	      if(p < 100.0){
		  mesh = String.valueOf(o)+"0"+String.valueOf(p)+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(et)+String.valueOf(jt);
	      }
	      else{
		  mesh = String.valueOf(o)+String.valueOf(p)+String.valueOf(u)+String.valueOf(q)+String.valueOf(v)+String.valueOf(r)+String.valueOf(w)+String.valueOf(s2)+String.valueOf(et)+String.valueOf(jt);
	      }
	  }
      }
      return(Long.parseLong(mesh));
  }

  public double Vincenty(double latitude1, double longitude1, double latitude2, double longitude2){
      // WGS84
      double f = 1/298.257223563;
      double a = 6378137.0;
      double b = 6356752.314245;
      //      
      double cs = 0.0;
      double cscc = 0.0;
      double sinsigma = 0.0;
      double cossigma = 0.0;
      double sigma = 0.0;
      double sinalpha = 0.0;
      double cos2alpha = 0.0;
      double C = 0.0;
      double lambda0 = 0.0;
      double cos2sigma = 0.0;
      double A = 0.0;
      double dsigma = 0.0;
      double B = 0.0;
      double u2 = 0.0;
      double cos2sigmam = 0.0;
      //
      double L = (longitude1 - longitude2)/180.0*Math.PI;
      double U1 = Math.atan((1.0-f)*Math.tan(latitude1/180.0*Math.PI));
      double U2 = Math.atan((1.0-f)*Math.tan(latitude2/180.0*Math.PI));
      double lambda = L;
      double dlambda = 10.0;
      while(Math.abs(dlambda) > 1e-12){
	  cs = Math.cos(U2)*Math.sin(lambda);
	  cscc = Math.cos(U1)*Math.sin(U2)-Math.sin(U1)*Math.cos(U2)*Math.cos(lambda);
	  sinsigma = Math.sqrt(cs*cs + cscc*cscc);
	  cossigma = Math.sin(U1)*Math.sin(U2)+Math.cos(U1)*Math.cos(U2)*Math.cos(lambda);
	  sigma = Math.atan(sinsigma/cossigma);
	  sinalpha = Math.cos(U1)*Math.cos(U2)*Math.sin(lambda)/sinsigma;
	  cos2alpha = 1.0 - sinalpha*sinalpha;
	  if(cos2alpha == 0.0){
	      C = 0.0;
	      lambda0 = L + f*sinalpha*sigma;
	  }else{
	      cos2sigmam = cossigma - 2.0*Math.sin(U1)*Math.sin(U2)/cos2alpha;
	      C = f/16.0*cos2alpha*(4.0+f*(4.0-3.0*cos2alpha));
	      lambda0 = L + (1.0-C)*f*sinalpha*(sigma + C*sinsigma*(cos2sigmam + C*cossigma*(-1.0+2.0*cos2sigmam*cos2sigmam)));
	  }
	  dlambda = lambda0 - lambda;
	  lambda = lambda0;
      }
      if(C == 0.0){
	  A = 1.0;
	  dsigma = 0.0;
      }else{
	  u2 = cos2alpha * (a*a-b*b)/(b*b);
	  A = 1.0 + u2/16384.0*(4096.0 + u2 * (-768.0 + u2*(320.0-175.0*u2)));
	  B = u2/1024.0*(256.0+u2*(-128.0+u2*(74.0-47.0*u2)));
	  dsigma = B*sinsigma*(cos2sigmam + 1.0/4.0*B*(cossigma*(-1.0+2.0*cos2sigmam*cos2sigmam)-1.0/6.0*B*cos2sigmam*(-3.0+4.0*sinsigma*sinsigma)*(-3.0+4.0*cos2sigmam*cos2sigmam)));
      }
      double s = b*A*(sigma-dsigma);
      return(s);
  }

  public Worldmesh cal_area_from_meshcode(long meshcode){
      return this.cal_area_from_meshcode(meshcode,false);
  }

  public Worldmesh cal_area_from_meshcode(long meshcode, boolean extension){
      Worldmesh latlong = this.meshcode_to_latlong_grid(meshcode,extension);
      return(this.cal_area_from_latlong(latlong));
  }

  public Worldmesh cal_area_from_latlong(Worldmesh latlong){
      latlong.W1 = this.Vincenty(latlong.lat0,latlong.long0,latlong.lat0,latlong.long1);
      latlong.W2 = this.Vincenty(latlong.lat1,latlong.long0,latlong.lat1,latlong.long1);
      latlong.H = this.Vincenty(latlong.lat0,latlong.long0,latlong.lat1,latlong.long0);
      latlong.A = (latlong.W1+latlong.W2)*latlong.H*0.5;
      return(latlong);
  }
}
