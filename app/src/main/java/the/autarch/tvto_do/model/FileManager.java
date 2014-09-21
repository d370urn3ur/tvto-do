package the.autarch.tvto_do.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import the.autarch.tvto_do.model.database.Show;

public class FileManager {
	
	private static FileManager _instance = null;
	private Context _context = null;
	
	private MessageDigest _digest = null;
	
	public static void initialize(Context context) {
		if(_instance == null) {
			_instance = new FileManager(context.getApplicationContext());
		}
	}
	
	public static FileManager getInstance() {
		if(_instance == null) {
			Log.e("FileManager", "instance wasn't initialized");
		}
		return _instance;
	}
	
	private FileManager(Context context) {
		
		_context = context;
        try {
        	_digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
        	_digest = null;
            e.printStackTrace();
        }
	}
	
	public String writeBitmapToFileForShow(Bitmap image, Show show) {
		
		FileOutputStream out = null;
		ByteArrayOutputStream baos = null;
		String filename = null;
		
		try {
			baos = new ByteArrayOutputStream();  
			image.compress(Bitmap.CompressFormat.PNG, 100, baos);
			
			filename = hashStringForFilename(show.getPoster138Url());
			out = _context.openFileOutput(filename, Context.MODE_PRIVATE);
			baos.writeTo(out);
			
			return filename;
			
		} catch (Exception e) {
			
		    e.printStackTrace();
		    
		} finally {
			try {
				if(baos != null) {
					baos.close();
				}
				if(out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return filename;
	}
	
	public Bitmap getBitmapForFilename(String filename) {
		
		File file = _context.getFileStreamPath(filename);
		Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
		return b;
	}
	
	public void deleteBitmapForFilename(String filename) {
        if(TextUtils.isEmpty(filename)) {
            return;
        }
		File file = _context.getFileStreamPath(filename);
		file.delete();
	}
	
	private String hashStringForFilename(String stringToHash) {
		
		byte[] hashBytes = stringToHash.getBytes();
		
		 _digest.update(hashBytes, 0, hashBytes.length);
		String hash = new BigInteger(1, _digest.digest()).toString(16);
		return hash;
	}
}
