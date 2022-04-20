package in.calibrage.AkshayaFA.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import in.calibrage.AkshayaFA.Model.GetPlotVillagesByFarmerCode;
import in.calibrage.AkshayaFA.Model.GetTypeCdDmt;
import in.calibrage.AkshayaFA.Model.Getdestinations;
import in.calibrage.AkshayaFA.Model.Hirecharges;
import in.calibrage.AkshayaFA.Model.Hirecharges_new;
import in.calibrage.AkshayaFA.Model.Product_new;
import in.calibrage.AkshayaFA.Model.QuickPayModel;
import in.calibrage.AkshayaFA.Model.VehicleTypes;
import in.calibrage.AkshayaFA.Model.VillagesData;
import in.calibrage.AkshayaFA.R;
import in.calibrage.AkshayaFA.Views.Activities.Transport_service_questioner_vendor;
import in.calibrage.AkshayaFA.service.APIConstantURL;
import in.calibrage.AkshayaFA.service.ApiService;
import in.calibrage.AkshayaFA.service.ServiceFactory;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HirechargesAdapter extends RecyclerView.Adapter<HirechargesAdapter.ViewHolder> implements VehiclebaseChargeAdapter.VehiclebaseChargeAdapterListener {
    public Context mContext;
    //private List<GetPlotVillagesByFarmerCode.ListResult>villagesList = new ArrayList<>();
    private HirechargesAdapterListener listener;
    String Destination1, Destionation2;
    Double price1, price2;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private Subscription mSubscription;
    private SpotsDialog mdilogue;
    public Spinner destination1, destination2,hiring_basics;
    String datetimevaluereq;
    List<String> get_ccs = new ArrayList<String>();
    List<Integer> get_ccsid = new ArrayList<Integer>();
    Getdestinations CCList;
    int HirebaseID;
    private List<VillagesData> villagesList = new ArrayList<>();
    GetTypeCdDmt gethirechargesdata;
    private ArrayList<Hirecharges_new> hirechargesmain = new ArrayList<>();
    private ArrayList<VehicleTypes> vehicletypelist = new ArrayList<>();


    //  private List<Getdestinations.Mill>MCCList = new ArrayList<>();
    public HirechargesAdapter(Context context, List<VillagesData> villagesList, Getdestinations cclist, ArrayList<VehicleTypes> vehicletypelist, GetTypeCdDmt gethirechargesdata, HirechargesAdapterListener listener) {
        this.villagesList = villagesList;
        this.mContext = context;
        this.CCList = cclist;
        this.listener = listener;
        this.vehicletypelist = vehicletypelist;
        this.gethirechargesdata = gethirechargesdata;

        for(int i =0; i < villagesList.size(); i ++){
             ArrayList<Hirecharges> hirecharges = new ArrayList<>();
            for(int j =0; j < vehicletypelist.size(); j++)
            {
                hirecharges.add(new Hirecharges(0,0,0,0,villagesList.get(i).getVillage_id(),vehicletypelist.get(j).getVehicleid(),0,0));

            }
            hirechargesmain.add(new Hirecharges_new(0,hirecharges));
        }

        // here we are adding dummy data


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {


        View rowView;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.hirecharges, parent, false);

        ViewHolder viewHolder = new ViewHolder(rowView);

        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.villagename.setText(villagesList.get(position).getVillage_name());


        LinearLayoutManager layoutManager = new LinearLayoutManager(
                viewHolder.rvSubItem.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        VehiclebaseChargeAdapter subItemAdapter = new VehiclebaseChargeAdapter(mContext,position,vehicletypelist, CCList, villagesList.get(position).getVillage_id(),gethirechargesdata, hirechargesmain,HirechargesAdapter.this);

        viewHolder.rvSubItem.setLayoutManager(layoutManager);
        viewHolder.rvSubItem.setAdapter(subItemAdapter);
        viewHolder.rvSubItem.setRecycledViewPool(viewPool);


    }


    // Return the size arraylist
    @Override
    public int getItemCount() {


        if (villagesList != null)
            return villagesList.size();
        else
            return 0;
    }

    @Override
    public void onSelected(int po, ArrayList<Hirecharges> hirecharges,int mainposition) {

        hirechargesmain.set(mainposition,new Hirecharges_new(mainposition,hirecharges));


        for(int i =0; i <hirecharges.size(); i++)
        {
            Log.d("---Analysis start--","-------------------------------Main Adapter-----------------------------------------------");
            Log.d("---Analysis--","===> Main Loop index :"+mainposition +"===>  sub Loop index :"+i);
            Log.d("---Analysis--","===> price :"+hirecharges.get(i).getPrice());
            Log.d("---Analysis--","===> CC_ID :"+hirecharges.get(i).getCcid());
            Log.d("---Analysis--end","------------------------------------------------------------------------------");

        }
        listener.onsSelected(po, hirechargesmain);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView villagename, vehiclename, vehiclename2;

        public EditText price1, price2;
        public TextView tvDate;
        public CheckBox chkSelected;
        Button add;
        public CardView card_view;
        public QuickPayModel singlestudent;
        private Subscription mSubscription;
        private SpotsDialog mdilogue;
        String datetimevaluereq;
        List<String> get_ccs = new ArrayList<String>();
        List<Integer> get_ccsid = new ArrayList<Integer>();
        private RecyclerView rvSubItem;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            villagename = (TextView) itemLayoutView.findViewById(R.id.villagename);


            rvSubItem = itemView.findViewById(R.id.rv_sub_item);

        }


//
    }


    public interface HirechargesAdapterListener {

        void onsSelected(int po, ArrayList<Hirecharges_new> hirecharges);
    }

    public abstract static class TextChangedListener<T> implements TextWatcher {
        private T target;

        public TextChangedListener(T target) {
            this.target = target;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            this.onTextChanged(target, s);
        }

        public abstract void onTextChanged(T target, Editable s);
    }
}









