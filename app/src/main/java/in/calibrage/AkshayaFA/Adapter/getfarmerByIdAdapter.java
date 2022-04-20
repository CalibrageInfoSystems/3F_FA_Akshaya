package in.calibrage.AkshayaFA.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.List;

public class getfarmerByIdAdapter extends ArrayAdapter {


    public getfarmerByIdAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

}
