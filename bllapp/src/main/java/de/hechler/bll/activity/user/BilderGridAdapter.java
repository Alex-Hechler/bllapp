package de.hechler.bll.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.recyclerview.widget.RecyclerView;

import de.hechler.bll.R;


public class BilderGridAdapter extends RecyclerView.Adapter<BilderGridAdapter.ViewHolder>{
        Activity parentActivity;
        private int[] localDataSet;
        private View lastSelected = null;
        private int lastPosition = -1;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageView;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                imageView =  view.findViewById(R.id.imageView);
            }

            public ImageView getImageView() {
                return imageView;
            }
        }

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param dataSet String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        public BilderGridAdapter(int[] dataSet, Activity parentActivity) {
            localDataSet = dataSet;
            this.parentActivity = parentActivity;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.bild_item, viewGroup, false);

            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.getImageView().setImageResource(localDataSet[position]);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // open another activity on item click
                    if(lastSelected!=null){
                        lastSelected.setBackgroundResource(R.color.design_default_color_background);
                    }
                    if(lastSelected!=view) {
                        view.setBackgroundResource(R.color.colorPrimaryDark); // start Intent
                        lastSelected = view;
                        lastPosition = position;
                        ActionMenuItemView btn = parentActivity.findViewById(R.id.imagechooseok);
                        btn.setEnabled(true);
                    }else{
                        lastSelected = null;
                        lastPosition = -1;
                        ActionMenuItemView btn = parentActivity.findViewById(R.id.imagechooseok);
                        btn.setEnabled(false);
                    }
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return localDataSet.length;
        }

    public int getLastPosition() {
        return lastPosition;
    }
}

