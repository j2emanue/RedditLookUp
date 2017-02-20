package org.jefferyemanuel.mainStuff;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;


public class DiskLruImageCache {

    private DiskLruCache mDiskCache;
    private CompressFormat mCompressFormat = CompressFormat.JPEG;
    private int mCompressQuality = 70;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final String TAG = "DiskLruImageCache";
private Context context;
   
public DiskLruImageCache( Context context,String uniqueName, int diskCacheSize,
        CompressFormat compressFormat, int quality ) {
        try {
                final File diskCacheDir = getDiskCacheDir(context, uniqueName );
                mDiskCache = DiskLruCache.open( diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize );
                mCompressFormat = compressFormat;
                mCompressQuality = quality;
                this.context=context;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private synchronized boolean writeBitmapToFile( Bitmap bitmap, DiskLruCache.Editor editor )
        throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream( editor.newOutputStream( 0 ), Consts.IO_BUFFER_SIZE );
            return bitmap.compress( mCompressFormat, mCompressQuality, out );
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {

    // Check if media is mounted or storage is built-in, if so, try and use external cache dir
    // otherwise use internal cache dir
        final String cachePath =
            Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                    !Utils.isExternalStorageRemovable() ?
                    Utils.getExternalCacheDir(context).getPath() :
                    context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    
    
    
    public synchronized void put( String key, Bitmap data ) {

        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit( key );
            if ( editor == null ) {
                return;
            }

            if( writeBitmapToFile( data, editor ) ) {               
                mDiskCache.flush();
                editor.commit();
                if ( BuildConfig.DEBUG ) {
                   Log.d( "cache_test_DISK_", "image put on disk cache " + key );
                }
            } else {
                editor.abort();
                if ( BuildConfig.DEBUG ) {
                    Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
                }
            }   
        } catch (IOException e) {
            if ( BuildConfig.DEBUG ) {
                Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
            }
            try {
                if ( editor != null ) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }           
        }

    }

    public synchronized Bitmap getBitmap( final String key,final ImageView imageView ) {

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
        	
        	//jack is only allowing some chars in key, lets make key unique Each key must match the regex [a-z0-9_-]{1,64} as keys
        	final String safeKey=Integer.toString(key.hashCode());
            snapshot = mDiskCache.get( safeKey );
            if ( snapshot == null ) {
           
            	/*if there is no cached image we spawn a child thread to make a network call and update the main ui 
            	 * afterwards.  */
            	 new Thread() {

                    public synchronized  void run() {
                    
                    	InputStream in1 = null;
						try {
							in1 = new java.net.URL(key).openStream();
						} catch ( MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        				final Bitmap b = BitmapFactory.decodeStream(in1);      				
        				
                           ((Activity) context).runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                /* we have a bitmap lets update the imageview*/
                                	imageView.setImageBitmap(b);
                                }
                                }
                                );
                           /*save the bitmap into our cache for next fetch*/
                           if(b!=null)
        				put(safeKey,b);
                    }
                    }.start();

                
            
            
            return null;
            
            }
            InputStream in = snapshot.getInputStream( 0 );
            
            if ( in != null ) {
                final BufferedInputStream buffIn = 
                new BufferedInputStream( in, Consts.IO_BUFFER_SIZE );
                bitmap = BitmapFactory.decodeStream( buffIn );   
                imageView.setImageBitmap(bitmap);
                put(safeKey,bitmap);//TODO this call might not be necessary 
            }   
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        if ( BuildConfig.DEBUG ) {
            Log.d( "cache_test_DISK_", bitmap == null ? "" : "image read from disk " + key);
        }

        return bitmap;

    }

    public boolean containsKey( String key ) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get( key );
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        if ( BuildConfig.DEBUG ) {
            Log.d( "cache_test_DISK_", "disk cache CLEARED");
        }
        try {
            mDiskCache.delete();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }

    
}

