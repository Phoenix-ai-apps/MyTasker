package com.b2b.mytask.ui.fragment.AddTaskActivity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.adapters.MoveFolderAdapter;
import com.b2b.mytask.databinding.FragmentMoveFolderBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.helper.ApplicationHelper;
import com.b2b.mytask.helper.HelperInterface;
import com.b2b.mytask.interfaces.iBottomSheetMoveTo;
import com.b2b.mytask.viewModel.FolderViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * MoveFolderFragment this fragment is called when binding.layoutMovetoHeader is clicked From AddTaskActivity.class
 * This will get all folders from db and displayed in list.User allowed to move Task from one folder to another.
 */
public class MoveFolderFragment extends Fragment implements HelperInterface {

    private static final String TAG = "MoveFolderFragment";
    public int CURR_FOLDER_ID = 0;
    // @formatter:on
    int folderColor;
    private int[] mColours = new int[0];
    private List<String> task_list;
    private List<FolderEntity> folderEntities;
    private Context context;
    private MoveFolderAdapter moveFolderAdapter;
    private MyTaskDatabase database;
    private FragmentMoveFolderBinding mBinding;
    private String from = "", folderType = "";
    private FolderEntity folderEntity = null;
    private iBottomSheetMoveTo bottomSheetMoveTo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_move_folder, container, false);
        mBinding.setLifecycleOwner(this);
        initializeResources();
        return mBinding.getRoot();
    }

    /**
     * initialization of all used resources.
     * Bundle specifies
     * From :- Always it will get all folders from HOME FRAGMENT (i.e 0 is passed).
     * folderEntity:- complete object.
     */
    private void initializeResources() {
        bottomSheetMoveTo = (iBottomSheetMoveTo) getActivity();

        moveFolderAdapter = new MoveFolderAdapter();
        context = MyTaskApp.getInstance();
        //   mBinding.imgBackArrow.setOnClickListener(this);
        database = ((MyTaskApp) context.getApplicationContext()).getDatabase();
        mColours = getResources().getIntArray(R.array.colours);
        mBinding.recyclerview.setAdapter(moveFolderAdapter);
        mBinding.recyclerview.setNestedScrollingEnabled(false);
        folderEntities = new ArrayList<>();


        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(MOVE_FROM)) {
                from = bundle.getString(MOVE_FROM);
            }
            if (bundle.containsKey(FOLDER_OBJ)) {
                folderEntity = bundle.getParcelable(FOLDER_OBJ);
                CURR_FOLDER_ID = folderEntity.getId();
            }
            bottomSheetMoveTo.getSelectedFolderDetails(from, folderEntity);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FolderViewModel viewModel =
                ViewModelProviders.of(this).get(FolderViewModel.class);
        subscribeUi(viewModel);
    }

    /** subscribeUi
     * This method is called whenever there is changes in Db, LiveData will immediately return fresh data from Room DB.
     * @param viewModel
     */
    private void subscribeUi(FolderViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getAllFoldersByInsertedFolder(from).observe(this, new Observer<List<FolderEntity>>() {
            @Override
            public void onChanged(@Nullable List<FolderEntity> myProducts) {
                if (myProducts != null && myProducts.size() > 0) {
                    mBinding.setIsLoading(false);
                    for (FolderEntity entity : myProducts) {
                        if (!TextUtils.isEmpty(entity.getFolderName())) {
                            folderEntities.add(entity);
                        }
                    }
                    if (folderEntities != null && folderEntities.size() > 0) {
                        moveFolderAdapter.setFolderList(folderEntities);
                    } else {
                        mBinding.setIsLoading(true);
                    }
                } else {
                    mBinding.setIsLoading(true);
                }
                // espresso does not know how to wait for data binding's loop so we execute changes
                // sync.
                mBinding.executePendingBindings();
            }
        });
    }


    @Override
    public ApplicationHelper getHelper() {
        return ApplicationHelper.getInstance();
    }


}
