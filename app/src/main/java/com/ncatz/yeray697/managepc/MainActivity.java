package com.ncatz.yeray697.managepc;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.ncatz.yeray697.managepc.components.MouseView;
import com.ncatz.yeray697.managepc.socket.Client;

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
        if (id == R.id.action_show_keyboard) {
            showKeyboard();
            result = true;
        } else
            result = super.onOptionsItemSelected(item);
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
}
