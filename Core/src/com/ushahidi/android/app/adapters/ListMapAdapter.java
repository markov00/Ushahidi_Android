
package com.ushahidi.android.app.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.ListMapModel;

public class ListMapAdapter extends BaseListAdapter<ListMapModel> implements
        Filterable {

    private int[] colors;

    private ListMapModel listMapModel;

    public ListMapAdapter(Context context) {
        super(context);
        listMapModel = new ListMapModel();
        colors = new int[] {
                R.drawable.odd_row_rounded_corners,
                R.drawable.even_row_rounded_corners
        };
    }

    @Override
    public void refresh() {
        final boolean loaded = listMapModel.load();
        if (loaded) {
            this.setItems(listMapModel.getMaps());
        }
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        Preferences.loadSettings(context);
        final int mapId = Preferences.activeDeployment;
        Widgets widget;
        int colorPosition = position % colors.length;
        if (view == null) {
            view = inflater.inflate(R.layout.list_map_item, null);
            widget = new Widgets(view);
            view.setTag(widget);
        }
        else {
            widget = (Widgets) view.getTag();
        }
        view.setBackgroundResource(colors[colorPosition]);

        // initialize view with content
        widget.mapName.setText(getItem(position).getName());
        widget.mapDesc.setText(getItem(position).getDesc());
        widget.mapUrl.setText(getItem(position).getUrl());
        widget.mapId.setText(String.valueOf(getItem(position).getId()));

        if (getItem(position).getId() == mapId) {
            widget.arrow.setImageResource(R.drawable.selected);
        } else {
            widget.arrow.setImageResource(R.drawable.arrow);
        }

        return view;

    }

    public class Widgets {

        TextView mapName;

        TextView mapDesc;

        TextView mapUrl;

        TextView mapId;

        ImageView arrow;

        public Widgets(View convertView) {

            mapName = (TextView) convertView.findViewById(R.id.map_list_name);
            mapDesc = (TextView) convertView.findViewById(R.id.map_list_desc);
            mapUrl = (TextView) convertView.findViewById(R.id.map_list_url);
            mapId = (TextView) convertView.findViewById(R.id.map_list_id);
            arrow = (ImageView) convertView.findViewById(R.id.map_arrow);
        }
    }

    // Implements fitering pattern for the list items.
    @Override
    public Filter getFilter() {
        return new MapFilter();
    }

    public class MapFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            results.values = items;
            results.count = items.size();

            if (constraint != null && constraint.toString().length() > 0) {
                constraint = constraint.toString().toLowerCase();
                ArrayList<ListMapModel> filteredItems = new ArrayList<ListMapModel>();
                ArrayList<ListMapModel> itemsHolder = new ArrayList<ListMapModel>();
                itemsHolder.addAll(items);
                for (ListMapModel map : itemsHolder) {
                    if (map.getName().toLowerCase().contains(constraint)
                            || map.getDesc().toLowerCase().contains(constraint)) {
                        filteredItems.add(map);
                    }
                }
                results.count = filteredItems.size();
                results.values = filteredItems;
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                FilterResults results) {
            List<ListMapModel> reports = (ArrayList<ListMapModel>) results.values;
            setItems(reports);

        }

    }
}
