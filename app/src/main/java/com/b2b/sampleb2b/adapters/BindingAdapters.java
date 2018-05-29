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

package com.b2b.sampleb2b.adapters;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.b2b.sampleb2b.interfaces.Folder;


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
        }else if(folder.getTaskDetails() != null && folder.getTaskDetails().size() > 0
                && !TextUtils.isEmpty(folder.getTaskDetails().get(0).getTaskName())){
            name = folder.getTaskDetails().get(0).getTaskName();
        }
        ((TextView) view).setText(name);
    }

    @BindingAdapter("folderTaskSize")
    public static void setFolderTaskSize(View view, Folder folder) {
        int size = 0;
        if(folder.getTaskDetails() != null && folder.getTaskDetails().size() > 0){
            size = folder.getTaskDetails().size();
        }
        ((TextView) view).setText(""+size);
    }
}