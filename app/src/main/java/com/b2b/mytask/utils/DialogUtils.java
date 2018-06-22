package com.b2b.mytask.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.b2b.mytask.R;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.helper.ApplicationHelper;
import com.b2b.mytask.helper.HelperInterface;

public class DialogUtils implements HelperInterface {

    public static Dialog showFolderDeleteDialog(FolderEntity entity,Context context,
                                                View.OnClickListener saveListener) {
        Dialog alert = new Dialog(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertDlgView = inflater.inflate(R.layout.dialog_delete_folder, null);

        TextView txtFName = (TextView) alertDlgView.findViewById(R.id.txt_fname);
        Button btnCancel  = (Button) alertDlgView.findViewById(R.id.btn_cancel);
        Button btnDelete  = (Button) alertDlgView.findViewById(R.id.btn_delete);

        if(!TextUtils.isEmpty(entity.getFolderName())){
            txtFName.setText("Delete "+entity.getFolderName()+"?");
        }else {
            if(entity.getTaskDetails() != null
                    && !TextUtils.isEmpty(entity.getTaskDetails().getTaskName())){
                txtFName.setText("Delete "+entity.getTaskDetails().getTaskName()+"?");
            }
        }

        alert.setCancelable(true);
        btnCancel.setOnClickListener(v -> {
            alert.dismiss();
        });
        btnDelete.setOnClickListener(saveListener);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(alertDlgView);
        alert.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alert.show();

        return alert;
    }

    @Override
    public ApplicationHelper getHelper() {
        return ApplicationHelper.getInstance();
    }
}
