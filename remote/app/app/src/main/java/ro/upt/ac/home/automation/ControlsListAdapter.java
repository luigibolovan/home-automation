package ro.upt.ac.home.automation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ro.upt.ac.home.automation.models.DataUnit;

public class ControlsListAdapter extends RecyclerView.Adapter<ControlsListAdapter.DoorLockHolder> {
    private final Context mContext;
    private final List<DataUnit> listOfDataUnits;

    public ControlsListAdapter(Context context, List<DataUnit> doorLockDataUnits){
        mContext = context;
        listOfDataUnits = doorLockDataUnits;
    }

    @NonNull
    @Override
    public DoorLockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View holder = inflater.inflate(R.layout.viewholder_dataunit, parent, false);
        return new DoorLockHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ControlsListAdapter.DoorLockHolder holder, int position) {
        if (listOfDataUnits.get(position).getDataUnitValue() == 1) {
            holder.doorLockDataUnitValue.setText("ON");
        } else {
            holder.doorLockDataUnitValue.setText("OFF");
        }
        holder.doorLockDataUnitDate.setText(listOfDataUnits.get(position).getDataUnitDate());
        holder.doorLockDataUnitTime.setText(listOfDataUnits.get(position).getDataUnitTime());
    }

    @Override
    public int getItemCount() {
        return listOfDataUnits.size();
    }

    public class DoorLockHolder extends RecyclerView.ViewHolder {
        TextView doorLockDataUnitValue;
        TextView doorLockDataUnitDate;
        TextView doorLockDataUnitTime;

        public DoorLockHolder(@NonNull View itemView) {
            super(itemView);
            doorLockDataUnitValue = itemView.findViewById(R.id.tv_doorlockdataunit_value);
            doorLockDataUnitDate  = itemView.findViewById(R.id.tv_doorlockdataunit_date);
            doorLockDataUnitTime  = itemView.findViewById(R.id.tv_doorlockdataunit_time);
        }
    }
}
