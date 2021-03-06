package in.calibrage.AkshayaFA.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import in.calibrage.AkshayaFA.Model.Product_new;
import in.calibrage.AkshayaFA.R;


public class fert_producut_Adapter extends RecyclerView.Adapter<fert_producut_Adapter.MyViewHolder> {
    private Context context;
    private ArrayList<Product_new> product_Listitems = new ArrayList<>();
    int  valueRounded;
    double cgst;
    double GST;
    DecimalFormat dec = new DecimalFormat("####0.00");
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name, quantity, amount, gst, item_cost,cgst,sgst;
        CardView card_view;

        public MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.name);
            quantity = view.findViewById(R.id.qun_tity);
            amount = view.findViewById(R.id.Value);
            // gst = view.findViewById(R.id.per_gst);
            cgst =view.findViewById(R.id.cgst);
//            sgst =view.findViewById(R.id.sgst);
            item_cost = view.findViewById(R.id.item_cost);
            card_view = view.findViewById(R.id.card_view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }


    public fert_producut_Adapter(Context context, ArrayList<Product_new> myProductsList) {
        this.context = context;

        this.product_Listitems = myProductsList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_fert, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.card_view.setBackgroundResource(R.drawable.button_bg2);
        final Product_new dataa = product_Listitems.get(position);
        holder.product_name.setText(dataa.getProductname());
        holder.quantity.setText(dataa.getQuandity() + "");
        Double amountt =(dataa.getWithGSTamount()) * dataa.getQuandity() ;


        valueRounded = (int)(amountt * 100) / 100;
        holder.amount.setText(dec.format(Math.round(amountt) ));
        // holder.gst.setText(dataa.getGst()+"");

        holder.item_cost.setText(dec.format(Math.round(dataa.getAmount())));
        if(" "+dataa.getGst()!=null){
            GST = dataa.getGst();
            holder.cgst.setText(GST+"");
            cgst =GST/2;
        }

        // holder.sgst.setText(cgst+"");
    }


    @Override
    public int getItemCount() {
        if (product_Listitems != null)
            return product_Listitems.size();
        else
            return 0;
    }


}

