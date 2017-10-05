package com.jerei.im.timchat.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jerei.im.timchat.R;
import com.jerei.im.timchat.model.GroupInfo;
import com.tencent.TIMValueCallBack;
import com.jerei.im.presentation.presenter.GroupManagerPresenter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;




/**
 * 创建群页面
 */
public class CreateGroupActivity extends Activity {
    TextView mAddMembers;
    EditText mInputView;
    TextView input_group_info_size;

    EditText input_group_info;
    String  type = GroupInfo.publicGroup;//公开群
    private final int CHOOSE_MEM_CODE = 100;

    ImageView group_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup);

        mInputView = (EditText) findViewById(R.id.input_group_name);
        mInputView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
                6) });
        mAddMembers = (TextView) findViewById(R.id.btn_add_group_member);
        input_group_info= (EditText) findViewById(R.id.input_group_info);
        input_group_info_size= (TextView) findViewById(R.id.input_group_info_size);
        mAddMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInputView.getText().toString().equals("")){
                    Toast.makeText(CreateGroupActivity.this, getString(R.string.create_group_need_name), Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(url)){
                    Toast.makeText(CreateGroupActivity.this, getString(R.string.create_group_need_image), Toast.LENGTH_SHORT).show();
                }else{
                    //选择好友页面
                    Intent intent = new Intent(CreateGroupActivity.this, ChooseFriendActivity.class);
                    startActivityForResult(intent, CHOOSE_MEM_CODE);
                }
            }
        });

        group_image = (ImageView) findViewById(R.id.group_image);
        group_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPictrues();
            }
        });
        input_group_info.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
                100) });
        input_group_info.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                    int size = 100-(input_group_info.getText()+"").length();
                input_group_info_size.setText("还可输入"+size+"字");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CHOOSE_MEM_CODE == requestCode) {
            if (resultCode == RESULT_OK){
                GroupManagerPresenter.createGroup(mInputView.getText().toString(),
                        type,
                        data.getStringArrayListExtra("select"),input_group_info.getText()+"",url,
                        new TIMValueCallBack<String>() {
                            @Override
                            public void onError(int i, String s) {
                                if (i == 80001){
                                    Toast.makeText(CreateGroupActivity.this, getString(R.string.create_group_fail_because_wording), Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(CreateGroupActivity.this, getString(R.string.create_group_fail), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onSuccess(String s) {
                                Toast.makeText(CreateGroupActivity.this, getString(R.string.create_group_succeed), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                );
            }
        }
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        Drawable drawable = new BitmapDrawable(getResources(), photo);
                        group_image.setImageDrawable(drawable);
                        submitIcon(photo);
                    }

                }
                break;

            case REQUESTCODE_CAMERA:
                startPhotoZoom(mOutPutFileUri);

                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 添加图片
     */
    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    private static final int REQUESTCODE_CAMERA = 3;
    private static String path = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + Environment.getExternalStorageDirectory().getPath();
    Uri mOutPutFileUri;
    private String fileName;
    public void addPictrues() {
        // TODO 打开选择或者拍照的面板
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("上传群组图片");
        builder.setItems(new String[] { "从相册选择", "拍照","取消" },
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK,null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            case 1:
                                Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                //文件夹aaaa
                                String path = Environment.getExternalStorageDirectory().toString()+"/aaaa";
                                File path1 = new File(path);
                                if(!path1.exists()){
                                    path1.mkdirs();
                                }
                                File file = new File(path1,System.currentTimeMillis()+".jpg");
                                mOutPutFileUri = Uri.fromFile(file);
                                imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
                                startActivityForResult(imageCaptureIntent,REQUESTCODE_CAMERA);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }



    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }


    String url="http://img3.imgtn.bdimg.com/it/u=696720809,4217856284&fm=27&gp=0.jpg";

    /**
     * 上传图片  想办法在主工程里实现  //TODO
     */
    public void submitIcon(Bitmap photo){

    }




    /**
     * 显示进度对话框
     */
    ProgressDialog progressDialog = null;

    public void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();

    }

    /**
     * 关闭进度对话框
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
