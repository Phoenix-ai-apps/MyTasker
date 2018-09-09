package com.b2b.mytask.interfaces;

import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.db.entities.TaskDetailsEntity;

/**
 * Created by root on 26/4/18.
 */

public interface IEditDeletePopup {

    void getClickEvent(String str_click, FolderEntity folderEntity);
}
