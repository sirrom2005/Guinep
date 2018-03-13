package com.trafalgartmc.guinep.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.trafalgartmc.guinep.MainActivity;
import com.trafalgartmc.guinep.R;

/**
 * Created by: Rohan
 * Date: 5/29/2017
 */

public class AlertBox {
    private static AlertDialog alert;
    public enum Type {ERROR,NORMAL,TWO_BUTTON};
    public static Button action;

    public static void Show(Context c,String message,Type type){
        View view = View.inflate(c, R.layout.alert_box_layout, null);

        ImageView icon = view.findViewById(R.id.about);
        TextView mes = view.findViewById(R.id.message);

        int img = (type == Type.NORMAL) ? R.drawable.icon_success : R.drawable.icon_error;

        icon.setImageDrawable(ContextCompat.getDrawable(c,img));
        mes.setText(message);

        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        alert = dialog.create();
        alert.setView(view);
        alert.setCancelable(false);
        alert.show();

        if(type == Type.TWO_BUTTON){
            action = view.findViewById(R.id.action);
            action.setVisibility(View.VISIBLE);
        }

        final Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    public static void close(){
        alert.dismiss();
    }

    public static void ShowMsg(final Context c, String message){
        View view = View.inflate(c, R.layout.alert_box_msg_layout, null);

        TextView mes = view.findViewById(R.id.message);
        mes.setText(message);

        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        alert = dialog.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.setView(view);
        alert.setCancelable(false);
        alert.show();

        Button btn = view.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                c.startActivity(intent);
            }
        });
    }

    public static void success(final Context c){

    }
}
