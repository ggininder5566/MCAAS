package login.submit1;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import kissmediad2d.android.LoginActivity;
import kissmediad2d.android.logininput;
import kissmediad2d.android.writepage;
import tab.list.att_parameter;

import android.os.Environment;

import com.github.kevinsawicki.http.HttpRequest;

public class submit {
	private String aliveIp;
	private String aliveRes;
	private String filename = new String();
	private String attachfile, postLength;
	public static String cookie;
	public String token = new String();
	private String loginreadLine, submitreadLine, rackreadLine, rreqreadLine, rfilereadLine;
	private boolean response;
	public boolean retrieveRes = false;
	private static String requestString;
	public String[] retrieveFileCount;
	public CharSequence[] path;
	public String[] fileid;
	private String receivefilename;
	public boolean retreivereqfailed = false;
	public boolean LoginException;
	public int algocount, code;
	public String source;
	public submit() {
		requestString = new String();
		loginreadLine = new String();
		submitreadLine = new String();
		response = false;
		LoginException = false;
		aliveIp =  LoginActivity.Homeip;
	}

	public void setsource(int algocount1, CharSequence[] path1) {
		path = path1;
		algocount = algocount1;
	}

	public void settoken(String arg) {
		token = "token=" + arg;
	}

	public String gettoken() {
		return token;
	}

	public String getFilename() {
		return filename;
	}

	public void setRetrieveRes(boolean arg) {
		retrieveRes = arg;
	}

	public boolean getRetrieveRes() {
		return retrieveRes;
	}

	public boolean getLoginException() {
		return LoginException;
	}

	public boolean getresponse() {
		return response;
	}

	public String getreceivefilename() {
		return receivefilename;
	}

	public String getrfilereadLine() {
		return rfilereadLine;
	}

	public String getcookie() {
		return cookie;
	}

	public String getloginreadline() {
		return loginreadLine;
	}

	public String getsubmitreadline() {
		return submitreadLine;
	}

	public void setrequestString(String arg) {
		requestString = arg;
	}

	public void setattachfile(String arg) {
		attachfile = arg;
	}

	public String getRetreiveack() {
		return rackreadLine;
	}

	public String getRetreivereq() {
		return rreqreadLine;
	}

	public void setsubmit_file_readline(String readLine) {
		source = readLine;
	}

	public void setsubmit_file_responsecode(int readcode) {
		code = readcode;
	}

	public String getsubmit_file_readline() {
		return source;
	}

    // 上傳之前先做alive的動作，先判斷目前最近的server在哪
	public String submit1(String arg_cookie,File file) {
		HttpRequest request = null;
		try {
			Map<String, File> data = new HashMap<String, File>();
			data.put("firstfile",file);
			String pathUrl = "http://" + logininput.Homeip + "/cms/submit/";
			request = HttpRequest.post(pathUrl);
			token=request.header("cookie", Login.latest_cookie).send(file).send("&"+requestString).body();
			// 這邊requestString是subject、content、receiver、filecnt、length、file_name(至少一個)
//			File output = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "KM/aa.html");
//			//token=request.body();
//			request.receive(output);
			if(att_parameter.chechsuccess(token)){
				//retrieve.token = token.replaceFirst("ret=0&", "");
			}else{

			}
			
			
		}catch (Exception ex) {
			ex.printStackTrace();
			token="timeout";
			File output = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "KM/aa.html");
			//token=request.body();
			request.receive(output);
		}
		return token;

	}

    // 上傳之前先做alive的動作，先判斷目前最近的server在哪
	public String resubmit(String arg_cookie) {
		String respone = new String();
		try {

			String pathUrl = "http://" + logininput.Homeip + "/cms/resubmit/";
			HttpRequest request = HttpRequest.post(pathUrl);
			// 這邊requestString是subject、content、receiver、filecnt、length、file_name(至少一個)
			respone = request.header("cookie", Login.latest_cookie).connectTimeout(20000).send(requestString).body();

			
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return respone;

	}
	// 這邊就是正式的檔案上傳
	public void submit_file(String token, int index) {
		int attachSize = 0;
		String pathUrl;
		HttpRequest request;
		String response;
		int responsecode;
		File file = null;
		// 根據writepage傳入的<執行次數跟路徑>來決定要產生多大的byte(也就是這個metohd Submit.setsource(Algocount, path))
		boolean [] checktype=new boolean[att_parameter.filetype];
		checktype=att_parameter.checktype(attachfile);
		if (checktype[att_parameter.video]) {
			postLength = new String();
		    /*
		     * 這邊是動態上傳的一部分，根據algocount，來製作submitfile所需要的資訊，要告訴server目前要上傳的檔案大小
		     * ex. attach_file=file_length=20405
		     */
			for (int i = 0; i < algocount; i++) {
				// path[i]是裝每個切個後的檔案路徑,path[0]=/storage/emulated/0/KM/file_name0_0-MOV_0259.mp4
				String filepath = (String) path[i];
				file = new File(filepath);
				String fileName = file.getName().substring(0, file.getName().indexOf("-"));
				fileName = fileName.replace("name", "length") + "=";
				//fileName輸出結果為 file_length0_0=
				attachSize = attachSize + (int) file.length();
				/*
				 * %3b表示";"的意思，因為多個上傳的關係，server必須要逐一執行，所以使用";"的方式告知server總共有幾個檔案
				 * 而使用%3b 不使用";"的原因在於，post、get、put這三種方法都有各自的解讀方式
				 * 有些method看的懂%3b，但也有些看不懂，
				 * 有些method看的懂";"，但也有些看不懂，
				 * 所以根據不同的method，使用他們看的懂的%3b或者是";"，這樣到server才不會錯
				 */
				postLength = postLength + fileName + file.length() + "%3b";
			//postLength輸出結果為 file_length0_0=4477792%3b
			}
			pathUrl = "http://" + logininput.Homeip + "/cms/submit_file/?token=" + token + "&attach_file=" + postLength;
			request = HttpRequest.put(pathUrl);
			// 這邊是第三方資料庫所使用的multifile upload的方法，在未取得body之前，可以重複使用send的方式夾帶檔案
			for (int i = 0; i < algocount; i++) {
				String filepath = (String) path[i];
				file = new File(filepath);
				request.send(file);
			}
			response = request.body();
			responsecode = request.code();
			setsubmit_file_readline(response);
			setsubmit_file_responsecode(responsecode);

		}// matchervideo end
		else if (checktype[att_parameter.photo]|checktype[att_parameter.music]) {
			file = new File(attachfile);
			attachSize = (int) file.length();
			postLength = "file_length" + index + "_0=" + attachSize + "%3b";
			pathUrl = "http://" + aliveIp + ":8000/cms/submit_file/?token=" + token + "&attach_file=" + postLength;
			request = HttpRequest.put(pathUrl);
			request.send(file);
			response = request.body();
			responsecode = request.code();
			setsubmit_file_readline(response);
			setsubmit_file_responsecode(responsecode);
		}
	}

}
