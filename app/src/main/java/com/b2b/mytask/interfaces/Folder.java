package com.b2b.mytask.interfaces;

import com.b2b.mytask.models.AddTaskDetails;
import com.b2b.mytask.models.FolderTask;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public interface Folder {
    int                  getId();
    String               getFolderName();
    int                  getColor();
    String getInsertedFrom();
    AddTaskDetails       getTaskDetails();
    List<FolderTask>     getFolderTaskList();

}
