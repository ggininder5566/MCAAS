package ffmpeg.process;

import java.io.File;

import kissmediad2d.android.VideoTrimmingService;
import tab.list.FileUtils;
import tab.list.fileContentProvider.UserSchema;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CopyOfffmpeg_service extends Service{
	String ffmpeg_path;
	int split_seq=0;
	int index=0;
	int total_time=0,duration;
    String selfid;
	FileUtils oname = new FileUtils();
	String outFileName;
	File file;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub


		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
	 public int onStartCommand(Intent intent, int flags, int startId) {

		 return startId; 
		 
	 }
		public void ffmpeg(String file_path, int start, int duration) {
			file = new File(file_path);
			String inputFileName = file_path;
			ffmpeg_path = file_path;
			outFileName = oname.getTargetFileName(file_path, split_seq, index);
			final String[] temp_up = {UserSchema._ID};
			//�����̫�@���������
			Cursor ffmpeg_exit = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), temp_up, "selfid='"+selfid +"'", null, null);
			if(ffmpeg_exit.getCount()>0){
				//��s���
				ffmpeg_exit.moveToFirst();
				ContentValues values = new ContentValues();
				values.put(UserSchema._FILEPATH, file_path);
				values.put(UserSchema._DURATION, duration);
				values.put(UserSchema._TOTAL_TIME, total_time);
				values.put(UserSchema._SELFID, selfid);
				values.put(UserSchema._SEQ_ID, split_seq);
				int id_this = Integer.parseInt(ffmpeg_exit.getString(0));
				String file_where = UserSchema._ID + " = " + id_this;
				getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), values, file_where, null);
			}else{
				//�s�W���
				ContentValues values = new ContentValues();
				values.put(UserSchema._FILEPATH, file_path);
				values.put(UserSchema._DURATION, duration);
				values.put(UserSchema._TOTAL_TIME, total_time);
				values.put(UserSchema._SELFID, selfid);
				values.put(UserSchema._SEQ_ID, split_seq);
				//�٭n�[�ۭqID(��SERVER)
				getContentResolver().insert(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), values);
				values=null;
			}

			
			split_seq++;
			File ch_file = new File(outFileName);
			// ���ˬd�Ӽv���b�L�h�O�_���Q���ιL�A�p�G�o�@���Q���ιL�N���L����
			if (ch_file.exists()) {
				Cursor file_exit = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), null, "selfid='"+selfid +"' and filerecord ='"+oname.getindex()+"'", null, null);
				if(file_exit.getCount()>0){
					//do nothing
				}else{
					ContentValues values = new ContentValues();
					values = new ContentValues();
					values.put(UserSchema._FILEPATH, outFileName);
					values.put(UserSchema._FILERECORD, oname.getindex());
					values.put(UserSchema._FILECHECK, 0);
					values.put(UserSchema._SELFID, selfid);
					values.put(UserSchema._FILENAME, file.getName());
					//�٭n�[�ۭqID(��SERVER)
					getContentResolver().insert(Uri.parse("content://tab.list.d2d/temp_file"), values);
				
				}

				
				if (split_seq < (total_time / duration)) {
					ffmpeg(file_path, split_seq * duration, duration);

				} else {
					Log.i("COPYofWIRTE", "���Ȧ��o���(���g���L)");
					//�R�����ά���(��ID��)
					split_seq = 0;
					String token,content_where,file_where,ffmpeg_where = null;
					String[] Form = { UserSchema._ID,UserSchema._MESSAGETOKEN ,};
					Cursor token_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), Form, "selfid='"+selfid+"'", null, null);
					if (token_cursor.getCount() > 0) {
						int id_this1 = 0;
						token_cursor.moveToFirst();
						id_this1 = Integer.valueOf(token_cursor.getString(0));
						token=token_cursor.getString(1);
						ContentValues values1 = new ContentValues();
						values1.put(UserSchema._FINISH, "yes");
						String where1 = UserSchema._ID + " = " + id_this1;
						getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_content"), values1, where1, null);
						//�Ĥ@���W�ǧ�  ��sFIRST��TRUE
						
						Cursor uptoken_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "selfid='"+selfid+"'", null, null);
						if (uptoken_cursor.getCount() > 0) {
							ContentValues values = new ContentValues();
							 values = new ContentValues();
							uptoken_cursor.moveToFirst();
							values.put(UserSchema._MESSAGETOKEN, token);
							for (int i = 0; i < uptoken_cursor.getCount(); i++) {
								int id_this = Integer.parseInt(uptoken_cursor.getString(0));
								file_where = UserSchema._ID + " = " + id_this;
								getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, file_where, null);
								uptoken_cursor.moveToNext();
							}
						}
						uptoken_cursor.close();
						
						Cursor ffmpeg_id = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), new String[] { UserSchema._ID } , "selfid='"+selfid+"'", null, null);
						if (ffmpeg_id.getCount() > 0) {
							ffmpeg_id.moveToFirst();
							ffmpeg_where =ffmpeg_id.getString(0);						
						}
						ffmpeg_id.close();
						
						int id_this =Integer.parseInt(ffmpeg_where);
						String where = UserSchema._ID + " = " +  id_this;
						getContentResolver().delete(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), where, null);
					}
					token_cursor.close();
					Toast.makeText(getApplicationContext(), "YA!�����F", Toast.LENGTH_LONG).show();

				}
			} else {
				// �ǤJ�|�ӰѼƵ�FFMPEG
				Bundle bundle = new Bundle();
				bundle.putString("inputFileName", inputFileName);
				bundle.putString("outFileName", outFileName);
				bundle.putInt("start", start);
				bundle.putInt("duration", duration);
				Intent intent = videoTrimmingServiceIntent();
				intent.putExtras(bundle);
				startService(intent);
			}

		}

		public Intent videoTrimmingServiceIntent() {
			return new Intent(CopyOfffmpeg_service.this, VideoTrimmingService.class);
		}


}