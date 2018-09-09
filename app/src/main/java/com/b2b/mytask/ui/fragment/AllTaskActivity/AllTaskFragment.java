package com.b2b.mytask.ui.fragment.AllTaskActivity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.R;
import com.b2b.mytask.adapters.AllTaskAdapter;
import com.b2b.mytask.constants.AllConstants;
import com.b2b.mytask.databinding.FragmentAllTaskBinding;
import com.b2b.mytask.db.entities.FolderEntity;
import com.b2b.mytask.db.entities.TaskDetailsEntity;
import com.b2b.mytask.interfaces.IEditDeletePopup;
import com.b2b.mytask.ui.AddTaskActivity;
import com.b2b.mytask.utils.ApplicationUtils;
import com.b2b.mytask.viewModel.TaskDetailsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Nihar.s on 30/7/18.
 *
 * AllTaskFragment : This fragemnt is called from AllTaskActivity.
 *
 * From Bundle it receives flag NAVIGATES_FROM. On basis of flag subscribeUi method retrieves all data From Room DB.
 */

public class AllTaskFragment extends Fragment implements AllConstants, View.OnClickListener, IEditDeletePopup {

    private static final String TAG = "AllTaskFragment";
    private FragmentAllTaskBinding binding;
    private List<TaskDetailsEntity> listDates;
    private AllTaskAdapter allTaskAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private String navigationFrom = "", startDateOfWeek = "", endDateOfWeek = "", currentDateOfWeek = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_task, container, false);
        binding.setLifecycleOwner(this);
        initializeResources();
        return binding.getRoot();
    }

    private void initializeResources() {
        Bundle bundle = getActivity().getIntent().getExtras();

        if (bundle.containsKey(NAVIGATES_FROM)) {
            navigationFrom = bundle.getString(NAVIGATES_FROM);
            if (navigationFrom.equalsIgnoreCase(FROM_HOME_THISWEEK)) {
                startDateOfWeek = bundle.getString(START_DATE_OF_WEEK);
                endDateOfWeek = bundle.getString(END_DATE_OF_WEEK);
            }
            if (navigationFrom.equalsIgnoreCase(FROM_HOME_TODAY)) {
                currentDateOfWeek = bundle.getString(CURRENT_DATE_OF_WEEK);
            }
        }

        listDates = new ArrayList<>();
        allTaskAdapter = new AllTaskAdapter(this);
        binding.recyclerTasks.setAdapter(allTaskAdapter);
        // initialize your RecyclerView and your LayoutManager
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerTasks.setLayoutManager(mLinearLayoutManager);
        // set a custom ScrollListner to your RecyclerView
        binding.recyclerTasks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // Get the first visible item
                int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                //Now you can use this index to manipulate your TextView
                if (firstVisibleItem != 0) {
                    binding.linearHeader.setVisibility(View.VISIBLE);
                    binding.txtHeader.setText(ApplicationUtils.formatDateAllTask(listDates.get(firstVisibleItem).getTaskDate(), 1));
                } else {
                    binding.linearHeader.setVisibility(View.GONE);
                    /*recyclerView.post(new Runnable() {
                        public void run() {
                            allTaskAdapter.notifyDataSetChanged();
                        }
                    });*/

                }

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final TaskDetailsViewModel viewModel =
                ViewModelProviders.of(this).get(TaskDetailsViewModel.class);

        subscribeUi(viewModel);
    }

    private void subscribeUi(TaskDetailsViewModel viewModel) {
        // Update the list when the data changes
        if (navigationFrom.equalsIgnoreCase(FROM_HOME_ALLTASK)) {
            viewModel.getAllTasks().observe(this, new Observer<List<TaskDetailsEntity>>() {
                @Override
                public void onChanged(@Nullable List<TaskDetailsEntity> myProducts) {
                    if (myProducts != null && myProducts.size() > 0) {
                        binding.setIsLoading(false);
                        binding.recyclerTasks.setVisibility(View.VISIBLE);
                        allTaskAdapter.setFolderList(myProducts);
                        listDates = myProducts;
                    } else {
                        binding.setIsLoading(true);
                        binding.recyclerTasks.setVisibility(View.GONE);
                    }
                    // espresso does not know how to wait for data binding's loop so we execute changes sync.
                    binding.executePendingBindings();
                }
            });
        } else if (navigationFrom.equalsIgnoreCase(FROM_HOME_THISWEEK)) {
            viewModel.getAllTaskDetailsbyWeek(startDateOfWeek, endDateOfWeek).observe(this, new Observer<List<TaskDetailsEntity>>() {
                @Override
                public void onChanged(@Nullable List<TaskDetailsEntity> myProducts) {
                    if (myProducts != null&& myProducts.size() > 0) {
                        binding.setIsLoading(false);
                        binding.recyclerTasks.setVisibility(View.VISIBLE);

                        Collections.sort(myProducts, new Comparator<TaskDetailsEntity>() {
                            public int compare(TaskDetailsEntity o1, TaskDetailsEntity o2) {
                                if (o1.getTaskDate() == null || o2.getTaskDate() == null) {
                                    return 0;
                                }
                                return o1.getTaskDate().compareTo(o2.getTaskDate());
                            }
                        });

                        allTaskAdapter.setFolderList(myProducts);
                        listDates = myProducts;
                    } else {
                        binding.setIsLoading(true);
                        binding.recyclerTasks.setVisibility(View.GONE);
                    }
                    // espresso does not know how to wait for data binding's loop so we execute changes sync.
                    binding.executePendingBindings();
                }
            });
        } else if (navigationFrom.equalsIgnoreCase(FROM_HOME_TODAY)) {
            viewModel.getAllTaskDetailsbyToday(currentDateOfWeek).observe(this, new Observer<List<TaskDetailsEntity>>() {
                @Override
                public void onChanged(@Nullable List<TaskDetailsEntity> myProducts) {
                    if (myProducts != null && myProducts.size() > 0) {
                        binding.setIsLoading(false);
                        binding.recyclerTasks.setVisibility(View.VISIBLE);

                        Collections.sort(myProducts, new Comparator<TaskDetailsEntity>() {
                            public int compare(TaskDetailsEntity o1, TaskDetailsEntity o2) {
                                if (o1.getTaskDate() == null || o2.getTaskDate() == null) {
                                    return 0;
                                }
                                return o1.getTaskDate().compareTo(o2.getTaskDate());
                            }
                        });

                        allTaskAdapter.setFolderList(myProducts);
                        listDates = myProducts;
                    } else {
                        binding.setIsLoading(true);
                        binding.recyclerTasks.setVisibility(View.GONE);
                    }

                    // espresso does not know how to wait for data binding's loop so we execute changes sync.
                    binding.executePendingBindings();
                }
            });
        } else if (navigationFrom.equalsIgnoreCase(FROM_HOME_COMPLETED)) {
            viewModel.getAllTaskDetailsbyCompleted().observe(this, new Observer<List<TaskDetailsEntity>>() {
                @Override
                public void onChanged(@Nullable List<TaskDetailsEntity> myProducts) {
                    if (myProducts != null && myProducts.size() > 0) {
                        binding.setIsLoading(false);
                        binding.recyclerTasks.setVisibility(View.VISIBLE);

                        Collections.sort(myProducts, new Comparator<TaskDetailsEntity>() {
                            public int compare(TaskDetailsEntity o1, TaskDetailsEntity o2) {
                                if (o1.getTaskDate() == null || o2.getTaskDate() == null) {
                                    return 0;
                                }
                                return o1.getTaskDate().compareTo(o2.getTaskDate());
                            }
                        });

                        allTaskAdapter.setFolderList(myProducts);
                        listDates = myProducts;
                    } else {
                        binding.setIsLoading(true);
                        binding.recyclerTasks.setVisibility(View.GONE);

                    }

                    // espresso does not know how to wait for data binding's loop so we execute changes sync.
                    binding.executePendingBindings();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void getClickEvent(String str_click, FolderEntity folderEntity) {
        if (folderEntity != null) {
            //Task
            Bundle bundle = new Bundle();
            if (folderEntity != null && folderEntity.getTaskDetails() != null
                    && !TextUtils.isEmpty(folderEntity.getTaskDetails().getTaskName())) {
                bundle.putString(TITLE, String.valueOf(folderEntity.getTaskDetails().getTaskName()));
                bundle.putString(getResources().getString(R.string.parent_column), folderEntity.getTaskDetails().getParentColumn());
                bundle.putParcelable(TASK, folderEntity.getTaskDetails());
                bundle.putParcelable(FOLDER_OBJ, folderEntity);
                ApplicationUtils.startActivityIntent(getActivity(), AddTaskActivity.class, bundle);

            }
        }

    }
}
