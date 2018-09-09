package com.b2b.mytask.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b2b.mytask.R;
import com.b2b.mytask.databinding.SelectedContactsRowBinding;
import com.b2b.mytask.interfaces.iContactsSelected;
import com.b2b.mytask.models.MeetingPeopleDO;

import java.util.List;


/**
 * Created by Nihar.s on 4/7/18.
 */

public class SelectedContactsListAdapter extends RecyclerView.Adapter<SelectedContactsListAdapter.RecyclerViewHolders> {

    List<MeetingPeopleDO> strListContact;
    private Context context;
    private iContactsSelected contactsSelected;


    public SelectedContactsListAdapter(List<MeetingPeopleDO> strListContact, iContactsSelected contactsSelected) {
        this.strListContact = strListContact;
        this.contactsSelected = contactsSelected;
    }

    @NonNull
    @Override
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        SelectedContactsRowBinding contactsRowBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.selected_contacts_row,parent,false);

        return new RecyclerViewHolders(contactsRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolders holder, final int position) {

        final MeetingPeopleDO meetingPeopleDO = strListContact.get(position);

        holder.contactsRowBinding.setSelectedPeople(meetingPeopleDO);

        holder.contactsRowBinding.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strListContact.remove(meetingPeopleDO);
                contactsSelected.getSelectedContacts(strListContact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return strListContact.size();
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        SelectedContactsRowBinding contactsRowBinding;

        public RecyclerViewHolders(SelectedContactsRowBinding contactsRowBinding) {
            super(contactsRowBinding.getRoot());

            this.contactsRowBinding = contactsRowBinding;

        }
    }
}
