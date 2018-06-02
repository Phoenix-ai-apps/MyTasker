package com.b2b.sampleb2b.interfaces;

import com.b2b.sampleb2b.models.AddTaskDetails;
import com.b2b.sampleb2b.models.FolderTask;

import java.util.List;

/**
 * Created by Abhishek Singh on 27/5/18.
 */
public interface Folder {
    int                  getId();
    String               getFolderName();
    int                  getColor();
    String getInsertedFrom();
    List<AddTaskDetails> getTaskDetails();
    List<FolderTask>     getFolderTaskList();

}
