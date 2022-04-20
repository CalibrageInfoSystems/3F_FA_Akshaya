package in.calibrage.AkshayaFA.Views.Activities;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.calibrage.AkshayaFA.Adapter.ModelFertAdapterNew;
import in.calibrage.AkshayaFA.Model.ModelFert;
import in.calibrage.AkshayaFA.Model.Product_new;
import in.calibrage.AkshayaFA.R;
import in.calibrage.AkshayaFA.common.BaseActivity;
import in.calibrage.AkshayaFA.common.CircleAnimationUtil;
import in.calibrage.AkshayaFA.common.CommonUtil;
import in.calibrage.AkshayaFA.localData.SharedPrefsData;
import in.calibrage.AkshayaFA.service.APIConstantURL;

import static in.calibrage.AkshayaFA.Views.Activities.PoleActivity.myProductsList;
import static in.calibrage.AkshayaFA.common.CommonUtil.updateResources;

public class FertilizerActivity extends BaseActivity implements  ModelFertAdapterNew.listner  {

    private RecyclerView recyclerView;
    private ModelFertAdapterNew adapter;

    String amount;
    String dis_price, Farmer_code;
    final Context context = this;
    Button button, btn_next;
    TextView mealTotalText, txt_recomandations, txt_count,no_data;
    private String TAG = "FertilizerActivity";
    private List<ModelFert> product_list = new ArrayList<>();
    private ProgressDialog dialog;
    int SPLASH_DISPLAY_DURATION = 500;
Double total_amount;
    private ImageView cartButtonIV;
    Integer Id, quantity;
    int price_final;
    int Count=0;
    int Godown_id;
    String Godown_code,Godown_name;
    DecimalFormat dec = new DecimalFormat("####0.00");
    //code_godown
    private Toolbar toolbar;

    LinearLayout lyt_cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int langID = SharedPrefsData.getInstance(this).getIntFromSharedPrefs("lang");
        if (langID == 2)
            updateResources(this, "te");
        else if (langID == 3)
            updateResources(this, "kan");
        else
            updateResources(this, "en-US");
        setContentView(R.layout.activity_fertilizer);
        dialog = new ProgressDialog(this);
        txt_recomandations = findViewById(R.id.txt_recomandations);
        txt_count = findViewById(R.id.txt_count);
        btn_next = findViewById(R.id.btn_next);
        no_data =findViewById(R.id.no_data);
        lyt_cart =findViewById(R.id.lyt_cart);
        cartButtonIV = findViewById(R.id.cartButtonIV);
        settoolbar();
//        ImageView backImg = (ImageView) findViewById(R.id.back);
//        backImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                finish();
//            }
//        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Godown_code = extras.getString("code_godown");
            Log.e("Godown_code===",Godown_code);
            Godown_id= extras.getInt("id_godown");
            Godown_name = extras.getString("name_godown");

        }
        SharedPreferences pref = getSharedPreferences("FARMER", MODE_PRIVATE);
        Farmer_code = pref.getString("farmerid", "");       // Saving string data of your editext

        recyclerView = (RecyclerView) findViewById(R.id.fer_recycler_view);
        mealTotalText = (TextView) findViewById(R.id.meal_total);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(4), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (isOnline())
            Getstate();
        else {
            showDialog(FertilizerActivity.this,getResources().getString(R.string.Internet));
            //Toast.makeText(LoginActivity.this, "Please Check Internet Connection ", Toast.LENGTH_SHORT).show();
        }
       // Getstate();
        cartButtonIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (myProductsList.size() > 0 & !TextUtils.isEmpty(mealTotalText.getText()) & mealTotalText.getText()!= "" ) {

                        Intent i = new Intent(FertilizerActivity.this, Fert_godown_list.class);
                        i.putExtra("Total_amount", mealTotalText.getText());
                        i.putExtra("godown_id",Godown_id);
                        i.putExtra("godown_code",Godown_code);
                        i.putExtra("godown_name",Godown_name);

                        startActivity(i);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    }
                    else{
                        showDialog(FertilizerActivity.this, getResources().getString(R.string.select_product_toast));
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("error==",e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }

        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (myProductsList.size() > 0 & !TextUtils.isEmpty(mealTotalText.getText()) & mealTotalText.getText()!= "" ) {

                        Intent i = new Intent(FertilizerActivity.this, Fert_godown_list.class);
                        i.putExtra("Total_amount", mealTotalText.getText());
                        i.putExtra("godown_id",Godown_id);
                        i.putExtra("godown_code",Godown_code);
                        i.putExtra("godown_name",Godown_name);

                        startActivity(i);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    }
                    else{
                        showDialog(FertilizerActivity.this, getResources().getString(R.string.select_product_toast));
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("error==",e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }

        });
        txt_recomandations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FertilizerActivity.this, RecommendationActivity.class));
            }
        });
    }

    private void settoolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Product Request");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Getstate() {
        dialog.setMessage("Loading, please wait....");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        String url = APIConstantURL.LOCAL_URL + "Products/GetProductsByGodown/1/"+ Godown_code;
        Log.e("url==Godown_code===",url);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RESPONSE======" + response);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "GetProductsByCategoryId ======" + jsonObject);
                    String success = jsonObject.getString("isSuccess");
                    Log.d(TAG, "success======" + success);
                    if(!jsonObject.getString("listResult").equals("null")) {

                        JSONArray kl = jsonObject.getJSONArray("listResult");
                        Log.d("kl==============", String.valueOf(kl));
                        parseData(kl);
                            recyclerView.setVisibility(View.VISIBLE);
                            no_data.setVisibility(View.GONE);
                            Log.e("no==data==208","No data");

                        // parseData(alsoKnownAsArray);


                        String affectedRecords = jsonObject.getString("affectedRecords");
                        Log.d(TAG, "GetProductsByCategoryId======" + affectedRecords);
                    }else {
                        no_data.setVisibility(View.VISIBLE);
                        lyt_cart.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        Log.d(TAG,"------ analysis ------ "+"Iam NUll");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error instanceof NetworkError) {
                    Log.i("one:" + TAG, error.toString());

                } else if (error instanceof ServerError) {
                    Log.i("two:" + TAG, error.toString());

                } else if (error instanceof AuthFailureError) {
                    Log.i("three:" + TAG, error.toString());

                } else if (error instanceof ParseError) {
                    Log.i("four::" + TAG, error.toString());

                } else if (error instanceof NoConnectionError) {
                    Log.i("five::" + TAG, error.toString());

                } else if (error instanceof TimeoutError) {
                    Log.i("six::" + TAG, error.toString());

                } else {
                    System.out.println("Checking error in else");
                }
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void parseData(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {

            ModelFert superHero = new ModelFert();
            JSONObject json = null;

            try {
                json = array.getJSONObject(i);
                superHero.setName(json.getString("name"));
//                superHero.setDiscountedPrice(json.getDouble("actualPrice"));
//                superHero.setmAmount(json.getString("discountedPrice"));
//                superHero.setPrice(json.getInt("price"));
                superHero.setDiscountedPrice(json.getDouble("actualPriceInclGST"));
                superHero.setmAmount(json.getString("discountedPriceInclGST"));
                superHero.setPrice(json.getInt("priceInclGST"));
                superHero.setImageUrl(json.getString("imageUrl"));
                superHero.setDescription(json.getString("description"));
                int size = json.getInt("size");
                Log.d(TAG, "--- Size ----" + size);
//
                superHero.setSize(size);


                superHero.setId(json.getInt("id"));
                superHero.setUomType(json.getString("uomType"));
                superHero.setAvail_quantity(json.getInt("availableQuantity"));
                superHero.setProduct_code(json.getString("code"));

                Log.e("uom===", json.getString("uomType"));
                int price_finall = json.getInt("price");
                Log.e("price_final====", String.valueOf(price_finall));
                String final_price = Integer.toString(price_finall);
                Log.e("final_price====", String.valueOf(final_price));
                dis_price = json.getString("discountedPrice");
                Log.e("dis_price====", dis_price);

                String gst =json.getString("gstPercentage");
                Log.e("gst====", String.valueOf(gst));

if( String.valueOf(gst)!= null) {
    superHero.setgst(gst);
}

            } catch (JSONException e) {
                e.printStackTrace();
            }

            product_list.add(superHero);

            adapter = new ModelFertAdapterNew(product_list, this, this);
            Log.d(TAG, "listSuperHeroes======" + product_list);

            recyclerView.setAdapter(adapter);

        }
    }






    @Override
    public void updated(int po, ArrayList<Product_new> myProducts) {
        SharedPrefsData.saveCartitems(context,myProducts);


        myProductsList = myProducts;
        CommonUtil.Productitems =myProductsList;
        Double allitemscost = 0.0;
        int allproducts = 0;

        for (Product_new product : myProducts) {
            Double oneitem = product.getQuandity() * (product.getWithGSTamount());
            allitemscost = oneitem + allitemscost;
            Log.d("Product", "total Proce :" + allitemscost);
            int onitem = product.getQuandity();
            allproducts = allproducts + onitem;
            Log.d("Product", "totalitems :" + allproducts);


        }
        txt_count.setText(allproducts + "");
        total_amount = (allitemscost * 100) / 100;

        Log.e("valueRounded===",total_amount+"");
        mealTotalText.setText(dec.format(total_amount));
    }



    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void makeFlyAnimation(ImageView targetView) {

        // RelativeLayout destView = (RelativeLayout) findViewById(R.id.cartRelativeLayout);
        ImageView destView = findViewById(R.id.cartButtonIV);

        new CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setCircleDuration(500).setMoveDuration(800).setDestView(destView).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //  addItemToCart();
                // Toast.makeText(MainActivity.this, "Continue Shopping...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();


    }

}
