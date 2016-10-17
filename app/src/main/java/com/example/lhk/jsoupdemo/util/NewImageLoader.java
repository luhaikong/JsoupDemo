package com.example.lhk.jsoupdemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class NewImageLoader {
    private HashMap<String, SoftReference<Bitmap>> imageCache;
    
    public NewImageLoader() {
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }
 
    public Bitmap loadDrawable(final String imageUrl,  final ImageCallback imageCallback) {  
        if (imageCache.containsKey(imageUrl)) {  
            SoftReference<Bitmap> softReference = imageCache.get(imageUrl);  
            Bitmap bitmap = softReference.get();  
            if (bitmap != null) {  
                return bitmap;  
            }  
        }  
        final Handler handler = new Handler() {  
            @Override  
            public void handleMessage(Message message) {  
                imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);  
            }  
        };  
        	 new Thread() {
                 @Override
                 public void run() {
                	 Bitmap bitmap = loadImageFromUrl(imageUrl);
                     imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));
                     Message message = handler.obtainMessage(0, bitmap);
                     handler.sendMessage(message);
                 }
             }.start();
			return null;
       
    }
 
    private  Bitmap loadImageFromUrl(String imageUrl) {
    	Bitmap bitmap = null;
		InputStream is = null;
		try
		{
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			is = new BufferedInputStream(conn.getInputStream());

			bitmap = BitmapFactory.decodeStream(is);
			conn.disconnect();
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				is.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return bitmap;
    }
    
   
    private Bitmap comp(Bitmap image) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {//�ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���
            baos.reset();//����baos�����baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ��50%����ѹ��������ݴ�ŵ�baos��
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
        float reqHeight = 250f;//�������ø߶�Ϊ800f
        float reqWidth = 400f;//�������ÿ��Ϊ480f
        //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
        int be = 1;//be=1��ʾ������
        if (width > height && width > reqWidth) {//�����ȴ�Ļ����ݿ�ȹ̶���С����
            be = (int) (newOpts.outWidth / reqWidth);
        } else if (width < height && height > reqHeight) {//����߶ȸߵĻ����ݿ�ȹ̶���С����
            be = (int) (newOpts.outHeight / reqHeight);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//�������ű���
        //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//        return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��
        return bitmap;
    }
    
    private Bitmap compressImage(Bitmap image) {
    	  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) { //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
            baos.reset();//����baos�����baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��
            options -= 10;//ÿ�ζ�����10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
        return bitmap;
    }
    
    public interface ImageCallback {  
        public void imageLoaded(Bitmap imageDrawable, String imageUrl);  
    }  
 
}



