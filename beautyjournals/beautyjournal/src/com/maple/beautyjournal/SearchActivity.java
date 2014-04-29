package com.maple.beautyjournal;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.entitiy.SearchArticleInfo;
import com.maple.beautyjournal.entitiy.SearchProductInfo;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.sina.weibo.sdk.api.share.Base;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mosl on 14-4-29.
 */
public class SearchActivity extends BaseActivity {

    private EditText searchEdit;
    private TextView searchArticle,searchGoods,searchCancle_2;
    private ImageView searchCancle_1;
    private ListView searchListView;
    private String Tag_choose;
    private Context context;
//    private ArticleSearchAdapter articleSearchAdapter;
//    private ProductSearchAdapter productSearchAdapter;
    private SearchDataAdapter searchDataAdapter;
    private ListView searchjListView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context=this;
        initCompoment();

    }

    private void initCompoment(){

        searchEdit=(EditText)findViewById(R.id.search_edit);
        searchArticle=(TextView)findViewById(R.id.search_article);
        searchGoods=(TextView)findViewById(R.id.search_goods);
        searchCancle_2=(TextView)findViewById(R.id.search_cancle_2);
        searchCancle_1=(ImageView)findViewById(R.id.search_cancle_1);
        Tag_choose="article";
        searchArticle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Tag_choose="article";
                searchArticle.setBackgroundResource(R.drawable.search_rect_background);
                searchGoods.setBackgroundColor(Color.WHITE);
                return false;
            }
        });
        searchGoods.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Tag_choose="product";
                searchGoods.setBackgroundResource(R.drawable.search_rect_background);
                searchArticle.setBackgroundColor(Color.WHITE);
                return false;
            }
        });
        searchCancle_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchEdit.setText("");
                return false;
            }
        });
        searchCancle_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchEdit.setText("");
                return false;
            }
        });
        searchEdit.addTextChangedListener(new SearchTextWatcher());
//        articleSearchAdapter=new ArticleSearchAdapter(context);
//        productSearchAdapter=new ProductSearchAdapter(context);
        searchDataAdapter=new SearchDataAdapter(context);
        searchjListView=(ListView)findViewById(R.id.choose_listview);
        searchjListView.setAdapter(searchDataAdapter);
//        if(Tag_choose.equals("article")){
//            searchjListView.setAdapter(articleSearchAdapter);
//        }else{
//            searchjListView.setAdapter(productSearchAdapter);
//        }
        Bundle bundle=getIntent().getBundleExtra("key");
        if(bundle!=null){
            String search=bundle.get("search").toString();
            searchEdit.setText(bundle.get("search").toString());
            new GetSearchTittle().execute(search);
        }
    }

    private class SearchTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(s.equals("")){
                searchProductInfos.clear();
                searchArticleInfos.clear();
                searchDataAdapter.notifyDataSetChanged();
            }
            new GetSearchTittle().execute(s.toString());
//            if( Tag_choose.equals("article")) {
//                searchArticleInfos.clear();
//                if (s.toString().equals(""))
//                    articleSearchAdapter.notifyDataSetChanged();
//                if (!s.toString().equals(""))
//                    new GetSearchTittle().execute(s.toString());
//            }else if(Tag_choose.equals("product")){
//
//            }
        }

    }
    private List<SearchArticleInfo> searchArticleInfos=new ArrayList<SearchArticleInfo>();
    private List<SearchProductInfo> searchProductInfos=new ArrayList<SearchProductInfo>();

    private class GetSearchTittle extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            String url = NetUtil.getSearchUrl(context);
            Map<String,String> map=new HashMap<String,String>();
            if(Tag_choose.equals("article")){
                map.put("keyword",params[0]);
                map.put("type","ARTICLE");
                map.put("/$size","8");
                NetUtil util = new HttpClientImplUtil(context,map,url);
                String result = util.doGet();
                try {
                    JSONObject obj = new JSONObject(result);
                    if (ServerDataUtils.isTaskSuccess(obj)) {
                        JSONObject info=obj.getJSONObject("info");
                        JSONArray array=info.getJSONArray("articles");
                        for(int i=0;i<array.length();i++){
                            JSONObject obj_article=array.getJSONObject(i);
                            SearchArticleInfo searchArticleInfo=SearchArticleInfo.fromJson(obj_article);
                            searchArticleInfos.add(searchArticleInfo);
                        }
                        Log.d("XXX",Integer.toString(searchArticleInfos.size()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                map.put("keyword",params[0]);
                map.put("type","PRODUCT");
                map.put("/$size","8");
                NetUtil util = new HttpClientImplUtil(context,map,url);
                String result = util.doGet();
                try {
                    JSONObject obj = new JSONObject(result);
                    if (ServerDataUtils.isTaskSuccess(obj)) {
                        JSONObject info=obj.getJSONObject("info");
                        JSONArray array=info.getJSONArray("products");
                        for(int i=0;i<array.length();i++){
                            JSONObject obj_product=array.getJSONObject(i);
                            SearchProductInfo searchProductInfo=SearchProductInfo.fromJson(obj_product);
                            searchProductInfos.add(searchProductInfo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        public void onPostExecute(String result){

            searchDataAdapter.notifyDataSetChanged();
        }
    }

    private class SearchDataAdapter extends BaseAdapter{

        private Context context;
        public SearchDataAdapter(Context context){
            this.context=context;
        }
        @Override
        public int getCount() {
            if(Tag_choose.equals("article")){
             return   searchArticleInfos.size();
            }else
                return searchProductInfos.size();
        }

        @Override
        public Object getItem(int position) {
            if(Tag_choose.equals("article")){
                return searchArticleInfos.get(position);
            }else{
                return searchProductInfos.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(Tag_choose.equals("article")){
                convertView = LayoutInflater.from(context).inflate(R.layout.choose_listview_item, null);
                if(searchArticleInfos.size()>0) {
                    TextView tittle = (TextView) convertView.findViewById(R.id.search_tittle);
                    tittle.setText(searchArticleInfos.get(position).item_tittle);
                    TextView theme = (TextView) convertView.findViewById(R.id.search_theme);
                    theme.setText(searchArticleInfos.get(position).item_category);
                    Log.d("XXX", Integer.toString(position));
                    Log.d("XXX", searchArticleInfos.get(position).item_tittle);
                }
                return convertView;
            }else{
                convertView = LayoutInflater.from(context).inflate(R.layout.search_product_listview_item, null);
                if(searchProductInfos.size()>0) {
                    ImageView productImage=(ImageView)convertView.findViewById(R.id.product_image_search);
                    TextView productTittle=(TextView)convertView.findViewById(R.id.tittle_product);
                    ImageView productStar=(ImageView)convertView.findViewById(R.id.starstar);
                    TextView productPrice=(TextView)convertView.findViewById(R.id.product_price);
                    TextView productComments=(TextView)convertView.findViewById(R.id.product_comments);
                }
                return convertView;
            }
        }
    }
    /*
    private class ProductSearchAdapter extends BaseAdapter{
        private Context context;

        public ProductSearchAdapter(Context ctext){
            this.context=context;
        }
        @Override
        public int getCount() {
            return searchProductInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(context).inflate(R.layout.search_product_listview_item, null);
            if(searchArticleInfos.size()>0) {
                ImageView productImage=(ImageView)convertView.findViewById(R.id.product_image_search);
                TextView productTittle=(TextView)convertView.findViewById(R.id.tittle_product);
                ImageView productStar=(ImageView)convertView.findViewById(R.id.starstar);
                TextView productPrice=(TextView)convertView.findViewById(R.id.product_price);
                TextView productComments=(TextView)convertView.findViewById(R.id.product_comments);
            }
            return convertView;
        }
    }
    private class ArticleSearchAdapter extends BaseAdapter{
        private Context context;
        public ArticleSearchAdapter(Context context){
            this.context=context;
        }
        @Override
        public int getCount() {
            Log.d("XXX",Integer.toString(searchArticleInfos.size()));
            return searchArticleInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return searchArticleInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(context).inflate(R.layout.choose_listview_item, null);
            if(searchArticleInfos.size()>0) {
                TextView tittle = (TextView) convertView.findViewById(R.id.search_tittle);
                tittle.setText(searchArticleInfos.get(position).item_tittle);
                TextView theme = (TextView) convertView.findViewById(R.id.search_theme);
                theme.setText(searchArticleInfos.get(position).item_category);
                Log.d("XXX", Integer.toString(position));
                Log.d("XXX", searchArticleInfos.get(position).item_tittle);
            }
            return convertView;
        }

    }

    */
}