package com.b2b.mytask.interfaces;

import com.b2b.mytask.db.entities.FolderEntity;

/**
 * Created by Nihar.s on 19/7/18.
 */

public interface iBottomSheetMoveTo {
    void getSelectedFolderDetails(String from, FolderEntity folderEntity);
}
