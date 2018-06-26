package com.b2b.mytask.ui.fragment.addTaskActivity;

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

import com.b2b.mytask.AppExecutors;
import com.b2b.mytask.MyTaskApp;
import com.b2b.mytask.R;
import com.b2b.mytask.adapters.MoveFolderAdapter;
import com.b2b.mytask.databinding.FragmentMoveFolderBinding;
import com.b2b.mytask.db.MyTaskDatabase;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.helper.ApplicationHelper;
import com.b2b.mytask.helper.HelperInterface;
import com.b2b.mytask.viewModel.FolderViewModel;

import java.util.ArrayList;
import java.util.List;

public class MoveFolderFragment extends Fragment implements HelperInterface{

    private static final String TAG = "MoveFolderFragment";

    // @formatter:on
    int                                folderColor;
    private int[]                      mColours = new int[0];
    private List<String>               task_list;
    private List<FolderEntity>         folderEntities;
    private Context                    context;
    private MoveFolderAdapter          moveFolderAdapter;
    private MyTaskDatabase             database    ;
    private AppExecutors               appExecutors;
    private FragmentMoveFolderBinding  mBinding;
    private String                     from = "";

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_move_folder, container, false);
        mBinding.setLifecycleOwner(this);
        initializeResources();
        return mBinding.getRoot();
    }

    private void initializeResources(){
        moveFolderAdapter = new MoveFolderAdapter();
        context          = MyTaskApp.getInstance();
        database         = ((MyTaskApp) context.getApplicationContext()).getDatabase();
        appExecutors     = new AppExecutors();
        mColours         = getResources().getIntArray(R.array.colours);
        mBinding.recyclerview.setAdapter(moveFolderAdapter);
        folderEntities   = new ArrayList<>();

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(MOVE_FROM)){
            from = bundle.getString(MOVE_FROM);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FolderViewModel viewModel =
                ViewModelProviders.of(this).get(FolderViewModel.class);
        subscribeUi(viewModel);
    }

    private void subscribeUi(FolderViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getAllFoldersByInsertedFolder(FROM_HOME_FRAGMENT).observe(this, new Observer<List<FolderEntity>>() {
            @Override
            public void onChanged(@Nullable List<FolderEntity> myProducts) {
                if (myProducts != null) {
                    mBinding.setIsLoading(false);
                    for(FolderEntity entity : myProducts){
                        if(!TextUtils.isEmpty(entity.getFolderName())){
                            folderEntities.add(entity);
                        }
                    }
                    moveFolderAdapter.setFolderList(folderEntities);
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
