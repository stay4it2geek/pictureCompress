package me.iwf.PhotoPickerDemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.iwf.PhotoPickerDemo.luban.Luban;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PhotoAdapter photoAdapter;
    private PhotoAdapter2 photoAdapter2;

    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private ArrayList<String> selectedPhotos2 = new ArrayList<>();
    public boolean islogo_up = true;

    public boolean getCurrentStatus() {
        return islogo_up;
    }

    private Button buttonPhotoCompress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonPhotoCompress = (Button) findViewById(R.id.button_photo_compress);
        buttonPhotoCompress.setOnClickListener(this);
        /**  第一个  */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);
//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                islogo_up = true;
//                PhotoPicker.builder().setPhotoCount(4).setGridColumnCount(4).setSelected(selectedPhotos).start(MainActivity.this);
//            }
//        });
        /**  第二个  */
        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.recycler_view2);
        photoAdapter2 = new PhotoAdapter2(this, selectedPhotos2);
        recyclerView2.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView2.setAdapter(photoAdapter2);
//        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                islogo_up = false;
//                PhotoPicker.builder().setPhotoCount(9).setSelected(selectedPhotos2).start(MainActivity.this);
//            }
//        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        islogo_up = true;
                        if (photoAdapter.getItemViewType(position) == photoAdapter.TYPE_ADD) {
                            PhotoPicker.builder()
                                    .setPhotoCount(4)
                                    .setShowCamera(true)
                                    .setPreviewEnabled(false)
                                    .setSelected(selectedPhotos)

                                    .start(MainActivity.this);
                        } else {
                            PhotoPreview.builder()
                                    .setPhotos(selectedPhotos)
                                    .setCurrentItem(position)
                                    .start(MainActivity.this);
                        }
                    }

//                    @Override
//                    public void onItemLongClick(View view, int position) {
//                        if (islogo_up) {
//                            if(selectedPhotos != null){
//                                selectedPhotos.remove(position);
//                                photoAdapter.notifyDataSetChanged();
//                            }
//
//                        }
////                        else {
////                            selectedPhotos2.remove(position);
////                            photoAdapter2.notifyDataSetChanged();
////                        }
//                    }
                }));
        recyclerView2.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        islogo_up = false;
                        if (photoAdapter2.getItemViewType(position) == photoAdapter2.TYPE_ADD) {
                            PhotoPicker.builder()
                                    .setPhotoCount(9)
                                    .setShowCamera(true)
                                    .setPreviewEnabled(false)
                                    .setSelected(selectedPhotos2)
                                    .start(MainActivity.this);
                        } else {
                            PhotoPreview.builder()
                                    .setPhotos(selectedPhotos2)
                                    .setCurrentItem(position)
                                    .start(MainActivity.this);
                        }
                    }

                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }

            if (islogo_up) {
                selectedPhotos.clear();
                if (photos != null) {
                    selectedPhotos.addAll(photos);
                }
                photoAdapter.notifyDataSetChanged();
            } else {
                selectedPhotos2.clear();
                if (photos != null) {
                    selectedPhotos2.addAll(photos);
                }
                photoAdapter2.notifyDataSetChanged();
            }

        }


    }
    long between = 0;
    String start;
    SimpleDateFormat sdf=  new SimpleDateFormat("yyyy-MM-DD HH-mm-ss.SSS");
    @Override
    public void onClick(View v) {
        List<File> listAll = new ArrayList<>();
        for (int i = 0; i < selectedPhotos2.size(); i++) {
            listAll.add(new File(selectedPhotos2.get(i)));
        }
        start = sdf.format(new Date());
        Log.e("ceshi","开始了:"+start);
        if (listAll.size() > 0) {
            compressWithRx(listAll);
        }
    }

    private void compressWithRx(List<File> fileList) {
        Luban.get(this)
                .load(fileList)
                .putGear(Luban.THIRD_GEAR)
                .asList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<File>>>() {
                    @Override
                    public Observable<? extends List<File>> call(Throwable throwable) {
                        return Observable.empty();
                    }
                })
                .subscribe(new Action1<List<File>>() {
                    @Override
                    public void call(List<File> fileList) {
                        if (selectedPhotos2 != null) {
                            selectedPhotos2.clear();
                        }
                        for (int i = 0; i < fileList.size(); i++) {
                            selectedPhotos2.add(fileList.get(i).getAbsolutePath());
                        }
                        String end = sdf.format(new Date());
                        Log.e("ceshi","压缩好了:"+end);
                        try {
                            between =  sdf.parse(end).getTime()-sdf.parse(start).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.e("ceshi","一共用时:"+between);
                        Toast.makeText(MainActivity.this,between+"",Toast.LENGTH_LONG).show();
                        photoAdapter2.notifyDataSetChanged();
                    }
                });
    }
//    findViewById(R.id.button_no_camera).setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        PhotoPicker.builder()
//                .setPhotoCount(7)
//                .setShowCamera(false)
//                .setPreviewEnabled(false)
//                .start(MainActivity.this);
//      }
//    });
//
//    findViewById(R.id.button_one_photo).setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        PhotoPicker.builder()
//                .setPhotoCount(1)
//                .start(MainActivity.this);
//      }
//    });
//
//    findViewById(R.id.button_photo_gif).setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        PhotoPicker.builder()
//                .setShowCamera(true)
//                .setShowGif(true)
//                .start(MainActivity.this);
//      }
//    });
}
