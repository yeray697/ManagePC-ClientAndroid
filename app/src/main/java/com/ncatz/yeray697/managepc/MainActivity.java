package com.ncatz.yeray697.managepc;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ncatz.yeray697.managepc.components.MouseView;
import com.ncatz.yeray697.managepc.socket.Client;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MouseListener {

    private Client client;
    private MouseView mouseView;
    private InputMethodManager keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mouseView = (MouseView) findViewById(R.id.mouseView);
        client = new Client();
        client.start();
        mouseView.setMouseListener(this);

    }

    @Override
    public void onClick(int key) {
        client.sendMessage(key,"");
    }


    @Override
    public void onMoveWheel(int y) {
        client.sendMessage(Client.ORDER_MOVE_WHEEL, String.valueOf(y));
    }

    @Override
    public void onMove(int x, int y) {
        client.sendMessage(Client.ORDER_MOUSE_MOVE,x + "," + y);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_show_keyboard:
                showKeyboard();
                result = true;
                break;
            case R.id.action_volume_down:
                client.sendMessage(Client.ORDER_VOLUME_DOWN,"");
                result = true;
                break;
            case R.id.action_volume_up:
                client.sendMessage(Client.ORDER_VOLUME_UP,"");
                result = true;
                break;
            case R.id.action_timer:
                showShutdownDialog();
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }

    public void showKeyboard() {
        if (keyboard == null)
            keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void hideKeyboard() {
        if (keyboard == null)
            keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent KEvent)
    {
        int keyaction = KEvent.getAction();
        if(keyaction == KeyEvent.ACTION_UP) {
            String keyPressed = "";
            int keycode = KEvent.getKeyCode();
            Log.d("asdf","keycode:"+keycode);
            switch (keycode) {
                case KeyEvent.KEYCODE_DEL:
                    keyPressed = Client.KEYCODE_DEL;
                    break;
                case KeyEvent.KEYCODE_SPACE:
                    keyPressed = Client.KEYCODE_SPACE;
                    break;
                case KeyEvent.KEYCODE_ENTER:
                    keyPressed = Client.KEYCODE_ENTER;
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                case KeyEvent.KEYCODE_VOLUME_UP:
                case KeyEvent.KEYCODE_SHIFT_LEFT:
                    break;
                case KeyEvent.KEYCODE_UNKNOWN:
                    keyPressed = KEvent.getCharacters();
                    break;
                default:
                    int keyunicode = KEvent.getUnicodeChar(KEvent.getMetaState());
                    keyPressed= String.valueOf((char) keyunicode);
                    break;
            }
            if (keyPressed.length() >= 1) {
                client.sendMessage(Client.ORDER_KEY_PRESSED,keyPressed);
            }
        }


        return super.dispatchKeyEvent(KEvent);
    }

    private void showShutdownDialog() {

        // custom dialog
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.rb_dialog_shutdown);
        List<String> stringList=new ArrayList<>();
        stringList.add("Cancelar");
        stringList.add("Ahora");
        stringList.add("X minutos");
        RadioGroup rg = (RadioGroup) d.findViewById(R.id.rgShutdownDialog);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb=new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));
            rg.addView(rb);
        }

        d.show();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        if (btn.getText().toString().equals("Cancelar")) {
                            client.sendMessage(Client.ORDER_TIMER,"-1");
                        } else if (btn.getText().toString().equals("Ahora")) {
                            client.sendMessage(Client.ORDER_TIMER,"0");
                        } else if (btn.getText().toString().equals("X minutos")) {
                            showShutdownTimeDialog();
                        }
                        d.dismiss();
                        break;
                    }
                }
            }
        });
    }

    private void showShutdownTimeDialog() {
        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle("Tiempo para apagar");
        d.setContentView(R.layout.rb_dialog_shutdown_timer);
        Button btOk = (Button) d.findViewById(R.id.btOkShutdownTimer);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.npShutdownTimer);
        np.setMinValue(1);
        np.setMaxValue(Integer.MAX_VALUE);
        np.setValue(30);
        np.setValue(30);
        np.setWrapSelectorWheel(false);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.sendMessage(Client.ORDER_TIMER, String.valueOf(np.getValue()));
                d.dismiss();
            }
        });

        // (That new View is just there to have something inside the dialog that can grow big enough to cover the whole screen.)

        d.show();
    }
}
