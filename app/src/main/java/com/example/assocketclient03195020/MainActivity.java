package com.example.assocketclient03195020;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
public class MainActivity extends AppCompatActivity {
    private EditText etname, etneirong;
    private TextView tvneirong;
    private InputStream inputStream;
    private OutputStream outputStream;
    private HandlerThread mHandlerThread;
    //子线程中的 Handler 实例。
    private Handler mSubThreadHandler;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String neirong = bundle.getString("neirong");
            tvneirong.append(neirong + "\n");
        }
        ;
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etname = (EditText) findViewById(R.id.edit_name);
        etneirong = (EditText)
                findViewById(R.id.myinternet_tcpclient_EditText02);
        tvneirong = (TextView)
                findViewById(R.id.myinternet_tcpclient_EditText01);
        initHandlerThraed();
    }
    public void lianjie(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String ip = "localhost";
                //String ip = "192.168.138.153";
                int duankou = 7777;
                try {
                    Socket socket = new Socket(ip, duankou);
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    byte[] jieshou = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(jieshou)) != -1) {
                        //将 byte 数组转换为 String 类型
                        String neirong = new String(jieshou, 0, len, "gbk");
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("neirong", neirong);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void fasong(View view) {
        // 得到昵称
        String name = etname.getText().toString();
        // 得到内容
        String neirong = etneirong.getText().toString();
        String all = name + ":" + neirong;
        tvneirong.append(all + "\n");
        etneirong.setText("");
        Message msg = new Message();
        msg.obj = all;
        mSubThreadHandler.sendMessage(msg);
    }
    private void initHandlerThraed() {
        //创建 HandlerThread 实例
        mHandlerThread = new HandlerThread("handler_thread");
        //开始运行线程
        mHandlerThread.start();
        //获取 HandlerThread 线程中的 Looper 实例
        Looper loop = mHandlerThread.getLooper();
        //创建 Handler 与该线程绑定。
        mSubThreadHandler = new Handler(loop) {
            public void handleMessage(Message msg) {
                writeMsg((String) msg.obj);
            }
        };
    }
    private void writeMsg(String msg) {
        try {
            outputStream.write(msg.getBytes("gbk"));//发送
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
