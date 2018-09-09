package com.b2b.mytask.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.b2b.mytask.R;
import com.b2b.mytask.customviews.CustomTextView;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.helper.ApplicationHelper;
import com.b2b.mytask.helper.HelperInterface;

public class DialogUtils implements HelperInterface {

    public static Dialog showFolderDeleteDialog(FolderEntity entity, TaskDetailsEntity taskDetailsEntity, Context context,
                                                View.OnClickListener saveListener) {
        Dialog alert = new Dialog(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertDlgView = inflater.inflate(R.layout.dialog_delete, null);

        TextView txtFName = (TextView) alertDlgView.findViewById(R.id.txt_fname);
        TextView txtMessage = (TextView) alertDlgView.findViewById(R.id.txt_message);
        CustomTextView btnCancel = (CustomTextView) alertDlgView.findViewById(R.id.btn_cancel);
        CustomTextView btnDelete = (CustomTextView) alertDlgView.findViewById(R.id.btn_delete);
        ImageView imgDialog = alertDlgView.findViewById(R.id.img_dialog);

        if (entity != null) {
            if (!TextUtils.isEmpty(entity.getFolderName())) {
                txtFName.setText("Delete " + entity.getFolderName() + "?");
            } else {
                if (entity.getTaskDetails() != null
                        && !TextUtils.isEmpty(entity.getTaskDetails().getTaskName())) {
                    txtFName.setText("Delete " + entity.getTaskDetails().getTaskName() + "?");
                }
            }
            imgDialog.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_delete));
            btnDelete.setText(R.string.delete);
            imgDialog.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_circle_red));
        } else if (taskDetailsEntity != null) {
            if (!TextUtils.isEmpty(taskDetailsEntity.getTaskName())) {
                txtFName.setText("Delete " + taskDetailsEntity.getTaskName() + "?");
            }
            imgDialog.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_delete));
            btnDelete.setText(R.string.delete);
            imgDialog.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_circle_red));


        } else {
            txtFName.setText(R.string.exit);
            txtMessage.setText(R.string.exit_app);
            imgDialog.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done));
            btnDelete.setText(R.string.exit);
            imgDialog.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient_circle_purple));


        }
        alert.setCancelable(false);
        btnCancel.setOnClickListener(v -> {
            alert.dismiss();
        });
        btnDelete.setOnClickListener(saveListener);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.setContentView(alertDlgView);
        alert.show();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        alert.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        return alert;
    }

    @Override
    public ApplicationHelper getHelper() {
        return ApplicationHelper.getInstance();
    }
}
