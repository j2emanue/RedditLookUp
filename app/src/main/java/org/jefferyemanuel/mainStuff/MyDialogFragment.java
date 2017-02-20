package org.jefferyemanuel.mainStuff;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;


public class MyDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    Context mContext;
   

    @SuppressLint("ValidFragment")
    public MyDialogFragment(Context context) {
        mContext = context;

    }

    public MyDialogFragment() {
    }
    
    public static MyDialogFragment newInstance(Context c) {
 
    	return new MyDialogFragment(c);
    }

    
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		//setRetainInstance(true);
    }


	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	Bundle args = this.getArguments();
		String message = args.getString(Consts.MESSAGE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(true);
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("OK", this);
        //alertDialogBuilder.setNeutralButton("NO", this);
        //alertDialogBuilder.setNegativeButton("Maybe Later",this); 

        return alertDialogBuilder.create();
    }


	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

		  
		
		switch(which)
         {
		 
		 
		 	
         	case AlertDialog.BUTTON_POSITIVE:
         		
         		
         		break;
         	
         	case AlertDialog.BUTTON_NEGATIVE:
         		
        
          		
         		break;
             	
         	case AlertDialog.BUTTON_NEUTRAL: //"no" button
         	
         		break;
             	
         }
         
         	dialog.dismiss();
         }
	
	
	
	
	
}