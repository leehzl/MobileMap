package com.lilynlee.mobilemap.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lilynlee.mobilemap.R;
import com.lilynlee.mobilemap.activity.ReportActivity;
import com.lilynlee.mobilemap.object.ReportItem;
import com.lilynlee.mobilemap.adapter.StickyListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by admin on 2016/8/4.
 */
public class NoReportFragment extends Fragment implements View.OnClickListener{

    private StickyListHeadersListView mListView;
    private Button mModifyButton;
    private Button mReportButton;
    private TextView mDescribeTextView;
    private ImageView mImageView;
    private TextView mLocationTextView;

    /*点击的ReportItem*/
    private ReportItem mClickItem = null;

    private List<ReportItem> mReportItemList = new ArrayList<ReportItem>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.noreport_layout,container,false);
        mListView = (StickyListHeadersListView) view.findViewById(R.id.list);
        mDescribeTextView = (TextView) view.findViewById(R.id.reportlist_describe);
        mImageView = (ImageView) view.findViewById(R.id.reportlist_image);
        mLocationTextView = (TextView) view.findViewById(R.id.reportlist_location);
        mModifyButton = (Button) view.findViewById(R.id.reportlist_modify);
        mReportButton = (Button) view.findViewById(R.id.reportlist_report);
        mModifyButton.setOnClickListener(this);
        mReportButton.setOnClickListener(this);
        initList();
        return view;
    }

    private void initList() {
        /*读取文件数据，并初始化list的数据*/
        getListData();

        /*对listview设置adapter*/
        StickyListAdapter adapter1 = new StickyListAdapter(
                getContext(),R.layout.reportlist_item,
                mReportItemList);
        mListView.setAdapter(adapter1);

        /*设置listview的点击事件，将被点击的listItem中的数据，显示在右边*/
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mClickItem = mReportItemList.get(position);
                String describe = mClickItem.getDescribe();
                String location_name = mClickItem.getLocation();
                float latitude = mClickItem.getLatitude();
                float longitude = mClickItem.getLongitude();
                String image_uri = mClickItem.getImage_uri();
                mDescribeTextView.setText("描述:"+describe);
                mLocationTextView.setText("位置:\n"+location_name+"\n坐标：\n"+latitude+":"+longitude);
                Bitmap bmp = BitmapFactory.decodeFile(image_uri);
                mImageView.setImageBitmap(bmp);
            }
        });
    }

    /**
     * 遍历指定路径的文件夹，获取以“上报-”开头的文件
     * 将符合条件的文件信息读取，存放到List中
     */
    private void getListData() {
        File root_file = new File("/data/data/com.lilynlee.mobilemap/shared_prefs");
        mReportItemList.clear();
        File[] xml_files = root_file.listFiles();
        if(xml_files != null){
            for(File f : xml_files){
                ReportItem item = readFile(f);
                mReportItemList.add(item);
                if(item.getFile_name().substring(0, 2).equals("上报")){
                    mReportItemList.add(item);
                }
            }
        }else{
            Toast.makeText(getContext(),"没有上报文件",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 读取File中的信息，并解析成一个ReportIte对象
     * @param file 需要读取的文件
     * @return ReportItem对象
     */
    private ReportItem readFile(File file) {
        String path = file.getAbsolutePath();
        String xml_file_name = "";
        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            xml_file_name = path.substring(start+1,end);
        }else{
            xml_file_name = "";
        }
        SharedPreferences pref = getActivity().getSharedPreferences(xml_file_name, getActivity().MODE_PRIVATE);
        String describe = pref.getString(ReportItem.DESCRIBE, "无法显示");
        String location_name = pref.getString(ReportItem.LOCATION_NAME, "无法显示");
        float latitude = pref.getFloat(ReportItem.LATITUDE, 0);
        float longitude = pref.getFloat(ReportItem.LONGITUDE, 0);
        String image_uri = pref.getString(ReportItem.IMAGE_URI, "无法显示");
        String name = pref.getString(ReportItem.FILE_NAME,"xxxxxxxxxx");
        ReportItem item = new ReportItem(name,image_uri,describe,location_name, latitude, longitude);
        return item;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reportlist_modify:
                if (mClickItem != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item", mClickItem);
                    Intent intent = new Intent(getContext(), ReportActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.reportlist_report:
                break;
            default:
                break;
        }
    }
}
