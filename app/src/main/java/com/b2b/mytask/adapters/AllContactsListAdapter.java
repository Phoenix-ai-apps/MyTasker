package com.b2b.mytask.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.b2b.mytask.R;
import com.b2b.mytask.databinding.AllContactsRowBinding;
import com.b2b.mytask.interfaces.iContactsSelected;
import com.b2b.mytask.models.MeetingPeopleDO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nihar.s on 4/7/18.
 */

public class AllContactsListAdapter extends RecyclerView.Adapter<AllContactsListAdapter.RecyclerViewHolders> {

    List<MeetingPeopleDO> strListContact;
    List<MeetingPeopleDO> selectedContactsList;
    private Context context;
    private iContactsSelected contactsSelected;

    public AllContactsListAdapter(List<MeetingPeopleDO> strListContact, iContactsSelected contactsSelected) {
        this.strListContact = strListContact;
        this.contactsSelected = contactsSelected;
        selectedContactsList = new ArrayList<>();

    }

    @NonNull
    @Override
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        AllContactsRowBinding allContactsRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.all_contacts_row, parent, false);
        return new RecyclerViewHolders(allContactsRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolders holder, final int position) {

        final MeetingPeopleDO meetingPeopleDO = strListContact.get(position);

        holder.allContactsRowBinding.setSelectedPeople(meetingPeopleDO);

        holder.allContactsRowBinding.contraintMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!selectedContactsList.contains(meetingPeopleDO)) {

                    selectedContactsList.add(meetingPeopleDO);
                    contactsSelected.getSelectedContacts(selectedContactsList);

                } else {
                    Toast.makeText(context, "Already selected", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return strListContact.size();
    }

    public void updateList(List<MeetingPeopleDO> list) {
        strListContact = list;
        notifyDataSetChanged();
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        AllContactsRowBinding allContactsRowBinding;

        public RecyclerViewHolders(AllContactsRowBinding allContactsRowBinding) {
            super(allContactsRowBinding.getRoot());

            this.allContactsRowBinding = allContactsRowBinding;
        }
    }
}
