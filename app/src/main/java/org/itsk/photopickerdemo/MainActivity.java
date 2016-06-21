package org.itsk.photopickerdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.itsk.photopicker.PhotoPickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_PHOTO = 1;
    private GridView gridview;
    private List<SelectedPicture> selectedimgs=new ArrayList<>();
    private GridAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        bindEvents();
    }

    private void bindEvents() {
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedimgs.get(position).isShowDelete()) {
                    adapter.showDelete(false);
                }else{
                    adapter.showDelete(true);
                }
                return true;
            }
        });
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ("add".equals(selectedimgs.get(position).getPath())){
                    adapter.showDelete(false);
                    Intent intent = new Intent(MainActivity.this, PhotoPickerActivity.class);
                    intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);
                    intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_MULTI);
                    intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 9);
                    intent.putExtra(PhotoPickerActivity.EXTRA_CHANGE_FLODER, false);
                    intent.putExtra(PhotoPickerActivity.EXTRA_EXCLUDE_IMAGE_PATH, "_compress.");
                    if (selectedimgs.size()>1){
                        ArrayList<String> selected=new ArrayList<String>();
                        for (SelectedPicture p : selectedimgs) {
                            if (!"add".equalsIgnoreCase(p.getPath())) {
                                selected.add(p.getPath().replace("_compress",""));
                            }
                        }
                        intent.putStringArrayListExtra(PhotoPickerActivity.EXTRA_SELECTED_PHOTO, selected);
                    }
                    startActivityForResult(intent, PICK_PHOTO);
                }else{
                    if (selectedimgs.get(position).isShowDelete()){
                        //点击删除
                        selectedimgs.remove(position);
                        if (selectedimgs.size()<9){
                            if (!"add".equals(selectedimgs.get(selectedimgs.size()-1).getPath()))
                                selectedimgs.add(selectedimgs.size(),new SelectedPicture("add",false));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    private void initViews() {
        gridview= (GridView) findViewById(R.id.grid_pictures);
        selectedimgs.add(new SelectedPicture("add",false));
        adapter=new GridAdapter(this,selectedimgs);
        gridview.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_PHOTO:
                if(resultCode == RESULT_OK){
                    ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                    showResult(result);
                }
                break;
        }
    }
    private ProgressDialog dialog;
    private void showResult(final ArrayList<String> result) {
        if (dialog==null)
            dialog=new ProgressDialog(this);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String string : result) {
                    if (!string.contains("_compress")) {
                        String newPath = getNewFile(string);
                        File file = new File(newPath);
                        if (!file.exists())
                            BitmapUtils.saveImgae(string, file);
                        SelectedPicture picture = new SelectedPicture(newPath, false);
                        if (!selectedimgs.contains(picture))
                            selectedimgs.add(0, picture);
                    }else {
                        SelectedPicture picture = new SelectedPicture(string, false);
                        if (!selectedimgs.contains(picture))
                            selectedimgs.add(0, picture);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (selectedimgs.size()>9)
                            selectedimgs.remove(selectedimgs.size()-1);
                        adapter.notifyDataSetChanged();
                        if (dialog!=null && dialog.isShowing())
                            dialog.dismiss();
                    }
                });
            }
        }).start();
    }
    public String getNewFile(String path){
        return path.substring(0,path.lastIndexOf("."))+"_compress"+path.substring(path.lastIndexOf("."),path.length());
    }
}
