/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.b2b.mytask.adapters;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.interfaces.Folder;
import com.b2b.mytask.models.AddTaskDetails;


public class BindingAdapters {
    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("folderNameText")
    public static void setFolderNameText(View view, Folder folder) {
        String name = "";
        if(folder != null && !TextUtils.isEmpty(folder.getFolderName())){
            name = folder.getFolderName();
        }else if(folder != null && folder.getTaskDetails() != null
                && !TextUtils.isEmpty(folder.getTaskDetails().getTaskName())){
            name = folder.getTaskDetails().getTaskName();
        }
        if(!TextUtils.isEmpty(name)){
            ((TextView) view).setText(name);
        }
    }

    @BindingAdapter("folderTaskSize")
    public static void setFolderTaskSize(View view, Folder folder) {
        int size = 0;
      /*  if(folder.getTaskDetails() != null ){
            size = folder.getTaskDetails();
        }*/
        ((TextView) view).setText(""+size);
    }

    @BindingAdapter("taskDate")
    public static void setTaskDate(View view, AddTaskDetails addTaskDetails) {
        if(addTaskDetails != null && !TextUtils.isEmpty(addTaskDetails.getTaskDate())){
            ((TextView) view).setText(addTaskDetails.getTaskDate());
        }else {
            ((TextView) view).setText("Select Task date");
        }
    }

    @BindingAdapter("taskTime")
    public static void setTaskTime(View view, AddTaskDetails addTaskDetails) {
        if(addTaskDetails != null && !TextUtils.isEmpty(addTaskDetails.getTaskTime())){
            ((TextView) view).setText(addTaskDetails.getTaskTime());
        }else {
            ((TextView) view).setText("Select Task Time");
        }
    }

    @BindingAdapter("taskNote")
    public static void setTaskNote(View view, AddTaskDetails addTaskDetails) {
        if(addTaskDetails != null && !TextUtils.isEmpty(addTaskDetails.getTaskNote())){
            ((TextView) view).setText(addTaskDetails.getTaskNote());
        }else {
            ((TextView) view).setText("");
        }
    }

    @BindingAdapter("deleteFolderName")
    public static void deleteFolderName(View view, FolderEntity folderEntity) {
        if(folderEntity != null && !TextUtils.isEmpty(folderEntity.getFolderName())){
            ((TextView) view).setText("Delete "+ folderEntity.getFolderName()+"?");
        }else {
            ((TextView) view).setText("Delete ");
        }
    }
}