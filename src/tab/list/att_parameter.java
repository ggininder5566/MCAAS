package tab.list;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import softwareinclude.ro.portforwardandroid.network.UPnPPortMapper;

import net.sbbi.upnp.messages.UPNPResponseException;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class att_parameter {
    int count, id_this;
    int[] arrDuration, arrSize;
    Bitmap[] thumbnails;
    boolean[] thumbnailsselection;
    String[] arrPath, arrName, Path;
    Uri uri;
    
    
    public static int music = 0;
    public static int video = 1;
    public static int photo = 2;
    public static int filetype = 3;
    public static int content = 0;
    public static int reply = 1;
    public static int d2d = 2;
    public static int msgtype = 3;
    public static int port = 5000;
    public static boolean first = false;
    public static String out_ip ="0.0.0.0";
    public static String in_ip ="0.0.0.0";
	public static String latest_cookie;
	public static Boolean wifi = false;
	public static boolean nat = false;
	public static boolean ffmpeg_now = false;
	public static String selfid = new String();
	public static UPnPPortMapper uPnPPortMapper = new UPnPPortMapper();
    public void setcount(int count) {
	this.count = count;
    }

    public void setid(int id_this) {
	this.id_this = id_this;
    }

    public void setarrDuration(int[] arrDuration) {
	this.arrDuration = arrDuration;
    }

    public void setarrSize(int[] arrSize) {
	this.arrSize = arrSize;
    }

    public void setthumbnails(Bitmap[] thumbnails) {
	this.thumbnails = thumbnails;
    }

    public void setthumbnailsselection(boolean[] thumbnailsselection) {
	this.thumbnailsselection = thumbnailsselection;
    }

    public void setarrPath(String[] arrPath) {
	this.arrPath = arrPath;
    }

    public void setPath(String[] Path) {
	this.Path = Path;
    }

    public void setarrName(String[] arrName) {
	this.arrName = arrName;
    }

    public void seturi(Uri uri) {
	this.uri = uri;
    }

    // ///////////////////////////////////////////////////////////////
    public int getcount() {
	return count;
    }

    public int getid() {
	return id_this;
    }

    public int[] getarrDuration() {
	return arrDuration;
    }

    public int[] getarrSize() {
	return arrSize;
    }

    public Bitmap[] getthumbnails() {
	return thumbnails;
    }

    public boolean[] getthumbnailsselection() {
	return thumbnailsselection;
    }

    public String[] getarrPath() {
	return arrPath;
    }

    public String[] getPath() {
	return Path;
    }

    public String[] getarrName() {
	return arrName;
    }

    public Uri geturi() {
	return uri;
    }

    // /////////////////////////////////////////////////////////
    public static boolean[] checktype(String file) {
	boolean[] checktype = new boolean[filetype];
	Pattern patternmusic = Pattern.compile(".*.mp3$|.*.wma|.*.m4a|.*.3ga|.*.ogg|.*.wav"); // check
										       // file
										       // type
	Matcher matchermusic = patternmusic.matcher(file);
	Pattern patternvideo = Pattern.compile(".*.3gp$|.*.mp4|.*.wmv|.*.movie|.*.flv");
	Matcher matchervideo = patternvideo.matcher(file);
	Pattern patternphoto = Pattern.compile(".*.jpg$|.*.bmp|.*.jpeg|.*.gif|.*.png|.*.image");
	Matcher matcherphoto = patternphoto.matcher(file);

	if (matchermusic.find()) {
	    checktype[music] = true;
	} else {
	    checktype[music] = false;
	}
	if (matchervideo.find()) {
	    checktype[video] = true;
	} else {
	    checktype[video] = false;
	}
	if (matcherphoto.find()) {
	    checktype[photo] = true;
	} else {
	    checktype[photo] = false;
	}
	return checktype;
    }

    // //////////////////////////////////////////////////////////////
    public static boolean chechsuccess(String respone) {

	boolean result;
	Pattern pattern = Pattern.compile("ret=0.*"); // check file type
	Matcher matcher = pattern.matcher(respone);
	if (matcher.find()) {
	    result = true;
	} else {
	    result = false;
	}
	return result;

    }

    public static boolean[] checkCR(String value) {
	boolean[] checktype = new boolean[msgtype];
	Pattern cpattern = Pattern.compile("&content.*"); // check file type
	Matcher cmatcher = cpattern.matcher(value);
	Pattern rpattern = Pattern.compile("&reply.*"); // check file type
	Matcher rmatcher = rpattern.matcher(value);
	Pattern ppattern = Pattern.compile("&d2d.*"); // check file type
	Matcher pmatcher = ppattern.matcher(value);
	if (cmatcher.find()) {
	    checktype[content] = true;
	} else {
	    checktype[content] = false;
	}
	if (rmatcher.find()) {
	    checktype[reply] = true;
	} else {
	    checktype[reply] = false;
	}
	if (pmatcher.find()) {
	    checktype[d2d] = true;
	} else {
	    checktype[d2d] = false;
	}
	return checktype;
    }
    public static String getip(){
    	
    	String in_out_ip = "";
    	if(att_parameter.wifi){
        	try {
                String externalIP = uPnPPortMapper.findExternalIPAddress();
                String foundDeviceInternalIP = getDottedDecimalIP(getLocalIPAddress());
           	  //String externalIP = "140.138.150.22";
           	  //String foundDeviceInternalIP = "192.168.0.1";

           		System.out.println("eip:"+externalIP+"  iip:"+foundDeviceInternalIP);
               if(externalIP != null && !externalIP.isEmpty()) {
            	   if(externalIP.equals("No External IP Address Found")){
            		   out_ip=foundDeviceInternalIP;
            		   in_ip=foundDeviceInternalIP;
            	   }else{
            		  out_ip=externalIP;
           		   	  in_ip=foundDeviceInternalIP;
            	   } 	 
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UPNPResponseException e) {
                e.printStackTrace();
            }
    	}
    	in_out_ip ="in_ip="+in_ip+"&out_ip="+out_ip;
    	return in_out_ip;
    }
    private static String getDottedDecimalIP(byte[] ipAddr) {
        //convert to dotted decimal notation:
        String ipAddrStr = "";
        for (int i=0; i<ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipAddr[i]&0xFF;
        }
        return ipAddrStr;
    }
    private static byte[] getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress instanceof Inet4Address) { // fix for Galaxy Nexus. IPv4 is easy to use :-)
                            return inetAddress.getAddress();
                        }
                        //return inetAddress.getHostAddress().toString(); // Galaxy Nexus returns IPv6
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
        } catch (NullPointerException ex) {
            Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
        }
        return null;
    }
}