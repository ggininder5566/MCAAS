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

    // �W�Ǥ��e����alive���ʧ@�A���P�_�ثe�̪�server�b��
	public String submit1(String arg_cookie,File file) {
		HttpRequest request = null;
		try {
			Map<String, File> data = new HashMap<String, File>();
			data.put("firstfile",file);
			String pathUrl = "http://" + logininput.Homeip + "/cms/submit/";
			request = HttpRequest.post(pathUrl);
			token=request.header("cookie", Login.latest_cookie).send(file).send("&"+requestString).body();
			// �o��requestString�Osubject�Bcontent�Breceiver�Bfilecnt�Blength�Bfile_name(�ܤ֤@��)
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

    // �W�Ǥ��e����alive���ʧ@�A���P�_�ثe�̪�server�b��
	public String resubmit(String arg_cookie) {
		String respone = new String();
		try {

			String pathUrl = "http://" + logininput.Homeip + "/cms/resubmit/";
			HttpRequest request = HttpRequest.post(pathUrl);
			// �o��requestString�Osubject�Bcontent�Breceiver�Bfilecnt�Blength�Bfile_name(�ܤ֤@��)
			respone = request.header("cookie", Login.latest_cookie).connectTimeout(20000).send(requestString).body();

			
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return respone;

	}
	// �o��N�O�������ɮפW��
	public void submit_file(String token, int index) {
		int attachSize = 0;
		String pathUrl;
		HttpRequest request;
		String response;
		int responsecode;
		File file = null;
		// �ھ�writepage�ǤJ��<���榸�Ƹ���|>�ӨM�w�n���ͦh�j��byte(�]�N�O�o��metohd Submit.setsource(Algocount, path))
		boolean [] checktype=new boolean[att_parameter.filetype];
		checktype=att_parameter.checktype(attachfile);
		if (checktype[att_parameter.video]) {
			postLength = new String();
		    /*
		     * �o��O�ʺA�W�Ǫ��@�����A�ھ�algocount�A�ӻs�@submitfile�һݭn����T�A�n�i�Dserver�ثe�n�W�Ǫ��ɮפj�p
		     * ex. attach_file=file_length=20405
		     */
			for (int i = 0; i < algocount; i++) {
				// path[i]�O�˨C�Ӥ��ӫ᪺�ɮ׸��|,path[0]=/storage/emulated/0/KM/file_name0_0-MOV_0259.mp4
				String filepath = (String) path[i];
				file = new File(filepath);
				String fileName = file.getName().substring(0, file.getName().indexOf("-"));
				fileName = fileName.replace("name", "length") + "=";
				//fileName��X���G�� file_length0_0=
				attachSize = attachSize + (int) file.length();
				/*
				 * %3b���";"���N��A�]���h�ӤW�Ǫ����Y�Aserver�����n�v�@����A�ҥH�ϥ�";"���覡�i��server�`�@���X���ɮ�
				 * �Өϥ�%3b ���ϥ�";"����]�b��Apost�Bget�Bput�o�T�ؤ�k�����U�۪���Ū�覡
				 * ����method�ݪ���%3b�A���]���Ǭݤ����A
				 * ����method�ݪ���";"�A���]���Ǭݤ����A
				 * �ҥH�ھڤ��P��method�A�ϥΥL�̬ݪ�����%3b�Ϊ̬O";"�A�o�˨�server�~���|��
				 */
				postLength = postLength + fileName + file.length() + "%3b";
			//postLength��X���G�� file_length0_0=4477792%3b
			}
			pathUrl = "http://" + logininput.Homeip + "/cms/submit_file/?token=" + token + "&attach_file=" + postLength;
			request = HttpRequest.put(pathUrl);
			// �o��O�ĤT���Ʈw�ҨϥΪ�multifile upload����k�A�b�����obody���e�A�i�H���ƨϥ�send���覡���a�ɮ�
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
