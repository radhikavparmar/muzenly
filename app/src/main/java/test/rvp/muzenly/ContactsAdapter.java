package test.rvp.muzenly;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.Objects;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ContactsAdapter extends RealmRecyclerViewAdapter<Contacts, ContactsAdapter.ItemViewHolder> implements Filterable{

    public Context mContext;
    private CustomFilter filter;


    public ContactsAdapter(OrderedRealmCollection<Contacts> itemList) {

        super(itemList, true);

    }

    @Override
    public ContactsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mContext = parent.getContext();
        View view = inflater.inflate(R.layout.contacts_cardview, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view,mContext);

        return itemViewHolder;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onViewRecycled(ItemViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ItemViewHolder holder, int position) {
        Contacts item = Objects.requireNonNull(getData()).get(position);
        ItemViewHolder itemViewHolder = holder;

        itemViewHolder.loadItem(item);

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Nullable
    @Override
    public Contacts getItem(int index) {
        return super.getItem(index);
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
            filter=new CustomFilter(this);
        return filter;
    }

    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        //updateData();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        Context mContext;
        TextView name,number;

        public ItemViewHolder(View itemView,Context context) {
            super(itemView);
            mContext =context;
            name = itemView.findViewById(R.id.name_textView_in_card);
            number = itemView.findViewById(R.id.number_textView_in_card);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // send the text to the listener, i.e Activity.


                }
            });


        }



        public void loadItem(Contacts item) {

            name.setText(item.getName());
            number.setText(item.getNumber());

            //Change Font and Color of text Default

        }
    }

    // Define listener member variable
    private static OnRecyclerViewItemClickListener mListener;
    private static OnRecyclerViewItemLongClickListener mListenerLong;

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClicked(String text);
    }

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(String text,View v);
    }

    // Define the method that allows the parent activity or fragment to define the listener.
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    public void setOnRecyclerViewItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        mListenerLong = listener;
    }



    public class CustomFilter extends Filter {
        ContactsAdapter adapter;
        public CustomFilter(ContactsAdapter adapter)
        {
            this.adapter=adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterResults(constraint.toString());
        }
    }
}


